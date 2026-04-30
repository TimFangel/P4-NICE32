package ir;

import java.util.ArrayList;
import java.util.List;

import exception.NoExprMatchException;
import exception.NoStmtMatchException;
import exception.NoValueMatchException;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.expression.enums.BoolUnaryOp;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.ReturnStmt;
import frontend.abstract_syntax.statement.main_statement.WhileStmt;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.Deci;
import frontend.abstract_syntax.value.Num;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;

/* Three Access Code Generator */
public class IrGenerator {
    private int tempCounter = 0;
    private int labelCount = 0;
    private List<IrInstruction> code = new ArrayList<>();
    private SymbolTable symbolTable;
    private OperandMapper operandMapper = new OperandMapper();

    public IrGenerator(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    private List<IrInstruction> getCode() {
        return code;
    }

    private IrValue newTemp(int type) {
        return new IrValue("t" + tempCounter++, type);
    }

    private String newLabel() {
        return "L" + labelCount++;
    }

    /**
     * Converts actual values into IrValues.
     * @param value actual value object from frontend as input.
     * @return an actual value as an IrValue.
     */
    public IrValue generateValue(Value value) {
        if (value instanceof Num num) {
            return new IrValue(String.valueOf(num.value()), 0); // 0 -> integer in symbol table.
        }

        if (value instanceof Deci dec) {
            return new IrValue(String.valueOf(dec.value()), 1); // 1 -> float in symbol table.
        }

        if (value instanceof Bool bool) {
            return new IrValue(String.valueOf(bool.value()), 2); // 0 -> integer in symbol table.
        }

        throw new NoValueMatchException("No matching value found!");
    }

    /**
     * Generates IR instructions recursively AST expressions.
     * @param expr expression from the AST.
     * @return the temporary variable generated.
     */
    public IrValue generateExpr(Expr expr) {
        if (expr instanceof ArithBinaryOpExpr binop) {
            IrValue left = generateExpr(binop.getExprLeft());
            IrValue right = generateExpr(binop.getExprRight());

            // Create temporary value to hold result
            IrValue temp = newTemp(left.getType());

            code.add(new IrInstruction(operandMapper.mapArithBin(binop.getOp()), left, right, temp));

            return temp;
        }

        if (expr instanceof ArithUnaryOpExpr unop) {
            // assign expression  
            IrValue left = generateExpr(unop.getExpr());

            IrValue temp = newTemp(left.getType());

            code.add(new IrInstruction(operandMapper.mapArithUna(unop.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof BoolBinaryOpExpr binop) {
            IrValue left = generateExpr(binop.getExprLeft());
            IrValue right = generateExpr(binop.getExprRight());

            IrValue temp = newTemp(left.getType());

            code.add(new IrInstruction(operandMapper.mapBoolBin(binop.getOp()), left, right, temp));

            return temp;
        }

        if (expr instanceof BoolUnaryOpExpr unop) {
            IrValue left = generateExpr(unop.getExpr());

            IrValue temp = newTemp(left.getType());

            code.add(new IrInstruction(operandMapper.mapBoolUna(unop.getOp()), left, null, temp));

            return temp;
        }

        throw new NoExprMatchException("No matching expression found!");
    } 

    public void generateStmt(Stmt stmt) {
        // TODO: usikker på om temp variable i stmt bruges rigtigt, kan først testes efter frontend.

         if (stmt instanceof Decl decl) {
            // TODO: usikker på om dette virker, måske lav egen toString for at få variabel navn!
            String name = decl.getIdentifier().toString(); 

            try {
                // findId, since frontend has created it before.
                Symbol symbol = symbolTable.findId(name); 
                IrValue result = new IrValue(name, symbol.getType());
                IrValue expr = generateExpr(decl.getValue());
                code.add(new IrInstruction(Operand.ASS, expr, null, result));

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (stmt instanceof AssStmt ass) {
            // TODO: usikker på om dette virker, måske lav egen toString for at få variabel navn!
            String varName = ass.getVariable().toString();
            IrValue right = generateExpr(ass.getValue());

            try {
                Symbol sym = symbolTable.findId(varName);
                IrValue left = new IrValue(varName, sym.getType());

                code.add(new IrInstruction(Operand.ASS, right, null, left));

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (stmt instanceof IfStmt ifStmt) {
            IrValue condition = generateExpr(ifStmt.getCondition());

            String elseLabel = newLabel();
            String endLabel = newLabel();

            // add if condition
            code.add(new IrInstruction(Operand.IF, condition, null, new IrValue(elseLabel, -1)));

            // generate then statements
            generateStmt(ifStmt.getThenStmt());

            // jump to end
            code.add(new IrInstruction(Operand.GOTO, null, null, new IrValue(endLabel, -1)));

            // else label
            code.add(new IrInstruction(Operand.LABEL, null, null, new IrValue(elseLabel, -1)));

            // generate else if exists
            if (ifStmt.getElseStmt() != null) {
                generateStmt(ifStmt.getElseStmt());
            }

            // end label
            code.add(new IrInstruction(Operand.LABEL, null, null, new IrValue(endLabel, -1)));

            return;
        }

        if (stmt instanceof WhileStmt whileStmt) {
            IrValue condition = generateExpr(whileStmt.getCondition());
            String exitLabel = newLabel();
            String toStartLabel = newLabel();

            // add if condition to leave loop
            code.add(new IrInstruction(Operand.IF, condition, null, new IrValue(exitLabel, -1)));

            // create body of while loop
            generateStmt(whileStmt.getWhileBody());

            // goto start
            code.add(new IrInstruction(Operand.GOTO, null, null, new IrValue(toStartLabel, -1)));

            return;
        }

        if (stmt instanceof BlockStmt block) {
            for (Stmt statement : block.getStatements()) {
                generateStmt(statement);
            }

            return;
        }

        if (stmt instanceof ReturnStmt retStmt) {
            IrValue returnedExpr = generateExpr(retStmt.getExprReturned());

            code.add(new IrInstruction(Operand.RET, null, null, returnedExpr));

            return;
        }

        throw new NoStmtMatchException("No matching statement found!");
    }


    /**
     * Generate IR and output to text file?
     * @param ast abstract syntax tree generated by frontend
     */
    public void generateIR(AST ast) {
        // generate ir
        // create basic blocks?
        // (opt) optimize
        // output file
    }
}

/*
TODO:
- Basic Blocks?
- Correct recursion??
- Output strings, obj?
- :O
 */