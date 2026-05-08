package ir;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

import exception.NoExprMatchException;
import exception.NoStmtMatchException;
import exception.NoValueMatchException;
import exception.NonMatchingTypeException;
import exception.ScopeException;
import exception.TypeCastException;
import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.expression.Cast;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.FuncCall;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.VarExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.function.FuncDecl;
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
import frontend.symboltable.FunctionSymbol;
import frontend.symboltable.Symbol;
import frontend.symboltable.VariableSymbol;
import lombok.Getter;

/* Three Access Code Generator */
@Getter
public class IrGenerator {
    private int tempCounter = 0;
    private int labelCount = 0;

    // main+setup code
    private IrInstruction mainLoopGoto;
    private List<IrInstruction> code = new ArrayList<>();
    private List<IrComponent> components = new ArrayList<>();

    // functions
    private List<IrFunction> functions = new ArrayList<>();
    private IrFunction currentFunction = null; // start in global scope

    private OperatorMapper operatorMapper = new OperatorMapper();

    public IrGenerator() {
        // Empty constructor
    }

    private IrValue newTemp(Type type) {
        return new IrValue("t" + tempCounter++, type);
    }

    private String newLabel() {
        return "L" + labelCount++;
    }

    private void newTemp(VariableSymbol symbol) {
        symbol.setIrName("t" + tempCounter++);
    }

    /**
     * Creates IR depending on scope.
     * 
     * @param instruction IrInstruction to create.
     */
    private void createIR(IrInstruction instruction) {
        if (currentFunction != null) { // null -> global scope
            currentFunction.getFuncBody().add(instruction);
        } else {
            code.add(instruction);
        }
    }

    /**
     * Converts actual values into IrValues.
     * 
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
     * 
     * @param expr expression from the AST.
     * @return the temporary variable generated.
     */
    public IrValue generateExpr(Expr expr) {
        if (expr instanceof ArithBinaryOpExpr binOp) {
            IrValue left = generateExpr(binOp.getExprLeft());
            IrValue right = generateExpr(binOp.getExprRight());

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException(
                        "[" + expr.getLineNumber() + "] Type mismatch! Left: " + left.getType() + " Right: "
                                + right.getType());
            }

            // Create temporary value to hold result
            IrValue temp = newTemp(left.getType());

            // add instruction for temp var
            createIR(new IrInstruction(operatorMapper.mapArithBin(binOp.getOp()), left, right, temp));

            // return temp to be used in parent expr
            return temp;
        }

        if (expr instanceof ArithUnaryOpExpr unOp) {
            IrValue left = generateExpr(unOp.getExpr());

            IrValue temp = newTemp(left.getType());

            // add code for temp var
            createIR(new IrInstruction(operatorMapper.mapArithUna(unOp.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof BoolBinaryOpExpr binOp) {
            IrValue left = generateExpr(binOp.getExprLeft());
            IrValue right = generateExpr(binOp.getExprRight());

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException(
                        "Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
            }

            IrValue temp = newTemp(Type.BOOL_T);

            // add code for temp var
            createIR(new IrInstruction(operatorMapper.mapBoolBin(binOp.getOp()), left, right, temp));

            return temp;
        }

        if (expr instanceof BoolUnaryOpExpr unOp) {
            IrValue left = generateExpr(unOp.getExpr());

            if (left.getType() != Type.BOOL_T) {
                throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType());
            }

            IrValue temp = newTemp(left.getType());

            // add code for temp var
            createIR(new IrInstruction(operatorMapper.mapBoolUna(unOp.getOp()), left, null, temp));

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

        if (expr instanceof FuncCall func) {
            IrValue parameter = generateExpr(func.getParameter());
            String ident = func.getIdentifier();
            FunctionSymbol funcSymbol = func.getFunctionSymbolRef();
            IrValue result = new IrValue(funcSymbol.getName(), funcSymbol.getType());

            createIR(new IrInstruction(IrOperator.CALL, parameter, new IrValue(ident, Type.FUNCTION), result));

            // Return return symbol
            return new IrValue(funcSymbol.getReturnIrName(), funcSymbol.getType());
        }

        if (expr instanceof VarExpr varExpr) {
            VariableSymbol symbol = varExpr.getSymbolRef();

            return new IrValue(symbol.getIrName(), symbol.getType());
        }

        throw new NoExprMatchException("No matching expression found! Expression: " + expr.toString());
    }

    /**
     * Generates IR instructions from statements.
     * 
     * @param stmt statement to convert to IR
     */
    public void generateStmt(Stmt stmt) {
        // TODO: usikker på om temp variable i stmt bruges rigtigt, kan først testes
        // efter frontend.

        if (stmt instanceof Decl decl) {
            String name = decl.getIdentifier();

            try {
                // findId, since frontend has created it before.
                VariableSymbol symbol = decl.getSymbolRef();
                newTemp(symbol);
                IrValue result = new IrValue(symbol.getIrName(), symbol.getType());
                IrValue expr = generateExpr(decl.getValue());

                if (expr.getType() != result.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Left: " + expr.getType() + " Right: " + result.getType());
                }

                createIR(new IrInstruction(IrOperator.ASS, expr, null, result));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if (stmt instanceof AssStmt ass) {
            VariableSymbol symbol = ass.getSymbolRef();
            IrValue right = generateExpr(ass.getValue());

            try {
                IrValue left = new IrValue(symbol.getIrName(), symbol.getType());

                if (left.getType() != right.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
                }
                createIR(new IrInstruction(IrOperator.ASS, right, null, left));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
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
            createIR(new IrInstruction(IrOperator.IF_FALSE, condition, null, new IrValue(elseLabel, Type.LABEL)));

            // generate then statements
            generateStmt(ifStmt.getThenStmt());

            // jump to end, only relevant if else exists.
            if (ifStmt.getElseStmt() != null) {
                createIR(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(endLabel, Type.LABEL)));
            }

            // else label
            createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(elseLabel, Type.LABEL)));

            // generate else if exists
            if (ifStmt.getElseStmt() != null) {
                generateStmt(ifStmt.getElseStmt());

                // end label only needed on else stmt.
                createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(endLabel, Type.LABEL)));
            }

            return;
        }

        if (stmt instanceof WhileStmt whileStmt) {
            String startLabel = newLabel();
            String exitLabel = newLabel();

            // label before condition check
            createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(startLabel, Type.LABEL)));

            IrValue condition = generateExpr(whileStmt.getCondition());

            // exit if false
            createIR(new IrInstruction(IrOperator.IF_FALSE, condition, null, new IrValue(exitLabel, Type.LABEL)));

            // else do body and return to start
            generateStmt(whileStmt.getWhileBody());

            createIR(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(startLabel, Type.LABEL)));

            createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(exitLabel, Type.LABEL)));

            return;
        }

        if (stmt instanceof BlockStmt block) {
            for (Stmt statement : block.getStatements()) {
                generateStmt(statement);
            }

            return;
        }

        if (stmt instanceof ReturnStmt retStmt) {
            if (currentFunction == null) {
                throw new ScopeException("Return not allowed in setup/main!");
            }

            // Generate RET line
            IrValue returnedExpr = generateExpr(retStmt.getExprReturned());
            createIR(new IrInstruction(IrOperator.RET, null, null, returnedExpr));

            retStmt.getFunctionSymbol().setReturnIrName(returnedExpr.getName());

            return;
        }

        if (stmt instanceof FuncDecl funcDecl) {
            newTemp(funcDecl.getParamSymbolRef());

            IrValue parameter = new IrValue(funcDecl.getParamSymbolRef().getIrName(), funcDecl.getParamType());

            IrFunction function = new IrFunction(funcDecl.getIdentifier(), parameter, funcDecl.getReturnType());

            functions.add(function);
            currentFunction = function; // change scope

            // generate function body
            generateStmt(funcDecl.getStatements());

            currentFunction = null; // reset scope

            return;
        }

        if (stmt instanceof Component compDecl) {
            IrValue port = generateExpr(compDecl.getPort());
            IrValue interval = generateExpr(compDecl.getInterval());

            IrComponent component = new IrComponent(compDecl.getIdentifier(), compDecl.getProtocol(),
                    compDecl.getDirection(), port, interval);

            for (Decl decl : compDecl.getVariables()) {

                IrValue expr = generateExpr(decl.getValue());
                IrValue result = newTemp(expr.getType());

                if (expr.getType() != result.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Left: " + expr.getType() + " Right: " + result.getType());
                }

                IrInstruction variable = new IrInstruction(IrOperator.ASS, expr, null, result);

                component.addVariable(variable);
            }

            components.add(component);

            return;
        }

        throw new NoStmtMatchException(
                "No matching statement found! Statement: " + stmt.toString());
    }

    /**
     * Generate IR instructions for a NICE32 program.
     * 
     * @param program NICE32 AST's root node.
     */
    public void generateProgram(Program program) {
        generateStmt(program.getSetup());

        // Generate functions
        generateStmt(program.getFunctions());

        // make label to goto to start of main, instead of going to functions.
        String mainStart = newLabel();

        createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(mainStart, Type.LABEL)));

        generateStmt(program.getMain());

        // make last instruction of main return to start of main.
        mainLoopGoto = new IrInstruction(IrOperator.GOTO, null, null, new IrValue(mainStart, Type.LABEL));
    }

    /**
     * Type casts between integer and float.
     * 
     * @param value      value to type cast.
     * @param targetType type casted to.
     * @return the type casted variable.
     */
    public IrValue typeCast(IrValue value, Type targetType) {
        Type valueType = value.getType();
        if (valueType == targetType) {
            return value;
        }

        IrValue temp = newTemp(targetType);

        if (valueType == Type.INT_T && targetType == Type.FLOAT_T) {
            createIR(new IrInstruction(IrOperator.INT_TO_FLOAT, value, null, temp));
        } else if (valueType == Type.FLOAT_T && targetType == Type.INT_T) {
            createIR(new IrInstruction(IrOperator.FLOAT_TO_INT, value, null, temp));
        } else {
            throw new TypeCastException("Type cast not possible!");
        }

        return temp;
    }

    /**
     * Generate IR and output to text file?
     * 
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
 * TODO:
 * --- Generation ---
 * - Correct recursion??
 * - Component generation
 * 
 * --- After Generation ---
 * - Basic Blocks?
 * - Output strings, obj?
 * - Optimize?
 * - Tests
 */