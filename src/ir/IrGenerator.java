package ir;

import java.util.ArrayList;
import java.util.List;

import exception.NoExprMatchException;
import exception.NoStmtMatchException;
import exception.NoValueMatchException;
import exception.NonMatchingTypeException;
import exception.TypeCastException;
import frontend.abstract_syntax.expression.Cast;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.ReturnStmt;
import frontend.abstract_syntax.statement.main_statement.WhileStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;

/* Three Access Code Generator */
public class IrGenerator {
    private int tempCounter = 0;
    private int labelCount = 0;
    private List<IrInstruction> code = new ArrayList<>();
    private SymbolTable symbolTable;
    private OperatorMapper operandMapper = new OperatorMapper();

    public IrGenerator(SymbolTable symbolTable){
        this.symbolTable = symbolTable;
    }

    public List<IrInstruction> getCode() {
        return code;
    }

    private IrValue newTemp(Type type) {
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
        if (value instanceof IntNum num) {
            return new IrValue(String.valueOf(num.value()), Type.INT_T);
        }

        if (value instanceof FloatNum num) {
            return new IrValue(String.valueOf(num.value()), Type.FLOAT_T); 
        }

        if (value instanceof Bool bool) {
            return new IrValue(String.valueOf(bool.value()), Type.BOOL_T); 
        }

        throw new NoValueMatchException("No matching value found! Value: " + value.toString());
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

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
            }

            // Create temporary value to hold result
            IrValue temp = newTemp(left.getType());

            // add instruction for temp var
            code.add(new IrInstruction(operandMapper.mapArithBin(binop.getOp()), left, right, temp));

            // return temp to be used in parent expr
            return temp;
        }

        if (expr instanceof ArithUnaryOpExpr unop) {
            IrValue left = generateExpr(unop.getExpr());

            IrValue temp = newTemp(left.getType());

            // add code for temp var
            code.add(new IrInstruction(operandMapper.mapArithUna(unop.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof BoolBinaryOpExpr binop) {
            IrValue left = generateExpr(binop.getExprLeft());
            IrValue right = generateExpr(binop.getExprRight());

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
            }

            IrValue temp = newTemp(left.getType());

            // add code for temp var
            code.add(new IrInstruction(operandMapper.mapBoolBin(binop.getOp()), left, right, temp));

            return temp;
        }

        if (expr instanceof BoolUnaryOpExpr unop) {
            IrValue left = generateExpr(unop.getExpr());

            if (left.getType() != Type.BOOL_T) {
                throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType());
            }

            IrValue temp = newTemp(left.getType());

            // add code for temp var
            code.add(new IrInstruction(operandMapper.mapBoolUna(unop.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof Operand operand) {
            return generateValue(operand.getValue());
        }

        if (expr instanceof Cast cast) {
            // get expr, then type cast it
            IrValue value = generateExpr(cast.getExpr());
            return typeCast(value, cast.getTargetType());
        }

        throw new NoExprMatchException("No matching expression found! Expression: " + expr.toString());
    } 

    /**
     * Generates IR instructions from statements.
     * @param stmt statement to convert to IR
     */
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

                if (expr.getType() != result.getType()) {
                    throw new NonMatchingTypeException("Type mismatch! Left: " + expr.getType() + " Right: " + result.getType());
                }

                code.add(new IrInstruction(IrOperator.ASS, expr, null, result));

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

                if (left.getType() != right.getType()) {
                    throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
                }
                code.add(new IrInstruction(IrOperator.ASS, right, null, left));

                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (stmt instanceof IfStmt ifStmt) {
            IrValue condition = generateExpr(ifStmt.getCondition());

            if (condition.getType() != Type.BOOL_T) {
                throw new NonMatchingTypeException("Condition is not a boolean! Type: " + condition.getType());
            }

            String elseLabel = newLabel();
            // only 
            String endLabel = (ifStmt.getElseStmt() != null) ? newLabel() : null;

            // add if condition
            code.add(new IrInstruction(IrOperator.IF_FALSE, condition, null, new IrValue(elseLabel, Type.LABEL)));

            // generate then statements
            generateStmt(ifStmt.getThenStmt());

            // jump to end, only relevant if else exists.
            if (ifStmt.getElseStmt() != null) {
                code.add(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(endLabel, Type.LABEL)));
            }

            // else label
            code.add(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(elseLabel, Type.LABEL)));

            // generate else if exists
            if (ifStmt.getElseStmt() != null) {
                generateStmt(ifStmt.getElseStmt());

                
                // end label only needed on else stmt.
                code.add(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(endLabel, Type.LABEL)));
            }


            return;
        }

        if (stmt instanceof WhileStmt whileStmt) {
            String startLabel = newLabel();
            String exitLabel = newLabel();

            // label before condition check
            code.add(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(startLabel, Type.LABEL)));

            IrValue condition = generateExpr(whileStmt.getCondition());

            // exit if false
            code.add(new IrInstruction(IrOperator.IF_FALSE, condition, null, new IrValue(exitLabel, Type.LABEL)));

            // else do body and return to start
            generateStmt(whileStmt.getWhileBody());

            code.add(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(startLabel, Type.LABEL)));

            code.add(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(exitLabel, Type.LABEL)));

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

            code.add(new IrInstruction(IrOperator.RET, null, null, returnedExpr));

            return;
        }

        throw new NoStmtMatchException("No matching statement found! Statement: " + stmt.toString());
    }

    /**
     * Generate IR instructions for a NICE32 program.
     * @param program NICE32 AST's root node.
     */
    public void generateProgram(Program program) {
        generateStmt(program.getSetup());
        generateStmt(program.getFunctions());

        // make label at start of main.
        String mainStart = newLabel();
        code.add(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(mainStart, Type.LABEL)));

        generateStmt(program.getMain());

        // make last instruction of main return to start of main.
        code.add(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(mainStart, Type.LABEL)));
    }

    /**
     * Type casts between integer and float.
     * @param value value to type cast.
     * @param targetType type casted to.
     * @return the type casted variable.
     */
    public IrValue typeCast(IrValue value, Type targetType) {
        Type valueType = value.getType();
        if (valueType == targetType) {
            return value;
        }

        IrValue temp = newTemp(targetType);

        if(valueType == Type.INT_T && targetType == Type.FLOAT_T) {
            code.add(new IrInstruction(IrOperator.INT_TO_FLOAT, value, null, temp));
        } else if (valueType == Type.FLOAT_T && targetType == Type.INT_T) {
            code.add(new IrInstruction(IrOperator.FLOAT_TO_INT, value, null, temp));
        } else {
            throw new TypeCastException("Type cast not possible!");
        }

        return temp;
    }

    /**
     * Generate IR and output to text file?
     * @param ast abstract syntax tree generated by frontend
     */
    public void generateIR(Program program) {
        // maybe just run generateProgram, then after pass it to optimizer class?
        // generate ir
        // create basic blocks?
        // (opt) optimize
        // output file or pass to backend
    }
}

/*
TODO:
--- Generation ---
- Correct recursion??
- Function call generation
- Function generation
- Component generation

--- After Generation ---
- Basic Blocks?
- Output strings, obj?
- Optimize?
- Tests
 */