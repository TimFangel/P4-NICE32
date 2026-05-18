package ir;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import exception.*;
import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.expression.*;
import frontend.abstract_syntax.expression.arith_expression.*;
import frontend.abstract_syntax.expression.bool_expression.*;
import frontend.abstract_syntax.function.FuncDecl;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.*;
import frontend.abstract_syntax.statement.main_statement.*;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.*;
import frontend.symbol_table.*;
import ir.util.*;
import lombok.Getter;

/**
 * Generates IR in TAC form from a NICE32 AST.
 */
@Getter
public class IrGenerator {
    private int tempCounter = 0;
    private int labelCounter = 0;

    // used to scope between function body and not.
    private IrFunction currentFunction = null; // null -> global scope

    // list of all code in TAC IR form.
    private List<IrInstructionInterface> code = new ArrayList<>();

    private OperatorMapper operatorMapper = new OperatorMapper();

    public IrGenerator() {
        // Empty constructor
    }

    /**
     * Creates a new label
     */
    private String newLabel() {
        return "L" + labelCounter++;
    }

    /**
     * Creates a new temporary with the given type.
     * 
     * @param type to give new temporary variable.
     * @return Temporary variable with the input type.
     */
    private IrValue newTemp(Type type) {
        return new IrValue("t" + tempCounter++, type);
    }

    /**
     * Creates a new temporary based on given symbol.
     * 
     * @param symbol to get the type of.
     * @return Temporary variable with the symbols type.
     */
    private IrValue newTemp(VariableSymbol symbol) {
        String name = "t" + tempCounter++;
        symbol.setIrName(name);
        return new IrValue(name, symbol.getType());
    }

    /**
     * Creates IR depending on scope.
     * 
     * @param instruction IrInstruction to create.
     */
    private void createIR(IrInstructionInterface instruction) {
        // add code to function body or global scope
        if (currentFunction != null && instruction instanceof IrInstruction instr) { // null -> global scope
            currentFunction.getFuncBody().add(instr);
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
            // generate left and right argument of expression.
            IrValue left = generateExpr(binOp.getExprLeft());
            IrValue right = generateExpr(binOp.getExprRight());

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException(
                        "[" + expr.getLineNumber() + "] Type mismatch! Left: " + left.getType() + " Right: "
                                + right.getType());
            }

            // Create temporary value to hold result
            IrValue temp = newTemp(left.getType());

            // add instruction with temp var as result
            createIR(new IrInstruction(operatorMapper.mapArithBin(binOp.getOp()), left, right, temp));

            // return temp to be used in parent expr
            return temp;
        }

        if (expr instanceof ArithUnaryOpExpr unOp) {
            if (unOp.getExpr() instanceof Operand o) {
                if (o.getValue() instanceof IntNum in) {
                    return new IrValue(String.valueOf(in.value()), Type.INT_T);
                } else if (o.getValue() instanceof FloatNum fn) {
                    return new IrValue(String.valueOf(fn.value()), Type.FLOAT_T);
                }
            }

            IrValue left = generateExpr(unOp.getExpr());

            IrValue temp = newTemp(left.getType());

            // add instruction with temp var as result, and left as first argument.
            createIR(new IrInstruction(operatorMapper.mapArithUna(unOp.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof BoolBinaryOpExpr binOp) {
            // generate left and right subexpressions.
            IrValue left = generateExpr(binOp.getExprLeft());
            IrValue right = generateExpr(binOp.getExprRight());

            if (left.getType() != right.getType()) {
                throw new NonMatchingTypeException(
                        "Type mismatch! Left: " + left.getType() + " Right: " + right.getType());
            }

            IrValue temp = newTemp(Type.BOOL_T);

            /*
             * add instruction with temp var as result, and left(1) and right(2) as
             * arguments.
             */
            createIR(new IrInstruction(operatorMapper.mapBoolBin(binOp.getOp()), left, right, temp));

            return temp;
        }

        if (expr instanceof BoolUnaryOpExpr unOp) {
            // generate subexpression.
            IrValue left = generateExpr(unOp.getExpr());

            if (left.getType() != Type.BOOL_T) {
                throw new NonMatchingTypeException("Type mismatch! Left: " + left.getType());
            }

            // new temporary variable to hold result.
            IrValue temp = newTemp(left.getType());

            // add instruction with temp var as result, and left as first argument.
            createIR(new IrInstruction(operatorMapper.mapBoolUna(unOp.getOp()), left, null, temp));

            return temp;
        }

        if (expr instanceof Operand operand) {
            // operands only contain a value.
            return generateValue(operand.getValue());
        }

        if (expr instanceof Cast cast) {
            // get expr, then type cast it
            IrValue value = generateExpr(cast.getExpr());
            return typeCast(value, cast.getTargetType());
        }

        if (expr instanceof FuncCall func) {
            // generate parameter, get name, symbol and return type.
            IrValue parameter = generateExpr(func.getParameter());
            String ident = func.getIdentifier();
            FunctionSymbol funcSymbol = func.getFunctionSymbolRef();
            Type returnType = funcSymbol.getType();

            // make new temp of function return type.
            IrValue temp = newTemp(returnType);
            createIR(new IrInstruction(IrOperator.CALL, parameter, new IrValue(ident, Type.FUNCTION), temp));

            return temp;
        }

        if (expr instanceof VarExpr varExpr) {
            // find symbolRef of variable containing needed information.
            VariableSymbol symbol = varExpr.getSymbolRef();

            return new IrValue(symbol.getIrName(), symbol.getType());
        }

        if (expr instanceof MemberAccess memberAccess) {
            // find needed information of variable.
            VariableSymbol symbol = memberAccess.getSymbolRef();

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
        if (stmt instanceof Decl decl) {
            try {
                // Create resulting temp and evaluate expression
                VariableSymbol symbol = decl.getSymbolRef();
                IrValue temp = newTemp(symbol);
                IrValue expr = generateExpr(decl.getValue());

                if (expr.getType() != temp.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Expr: " + expr.getType() + " Result: " + temp.getType());
                }

                createIR(new IrInstruction(IrOperator.ASS, expr, null, temp));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if (stmt instanceof AssStmt ass) {
            // get symbol with relevant info and create subexpression.
            VariableSymbol symbol = ass.getSymbolRef();
            IrValue expr = generateExpr(ass.getValue());

            try {
                // create an IrValue for the result.
                IrValue result = new IrValue(symbol.getIrName(), symbol.getType());

                if (result.getType() != expr.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Result: " + result.getType() + " Expr: " + expr.getType());
                }
                createIR(new IrInstruction(IrOperator.ASS, expr, null, result));
            } catch (Exception e) {
                e.printStackTrace();
            }

            return;
        }

        if (stmt instanceof IfStmt ifStmt) {
            // generate the condition of the if stmt.
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
            IrValue parameter = newTemp(funcDecl.getParamSymbolRef());

            IrFunction function = new IrFunction(funcDecl.getIdentifier(), parameter, funcDecl.getReturnType());

            createIR(function);
            currentFunction = function; // change scope

            // generate function body
            generateStmt(funcDecl.getStatements());

            currentFunction = null; // reset scope

            return;
        }

        if (stmt instanceof Component compDecl) {
            // generate IrValues for the integer constants.
            IrValue port = generateExpr(compDecl.getPort());
            IrValue interval = generateExpr(compDecl.getInterval());

            IrComponent component = new IrComponent(compDecl.getIdentifier(), compDecl.getProtocol(),
                    compDecl.getDirection(), port, interval);

            // create IrInstructions for all comp variables and add it to the comp.
            for (Decl decl : compDecl.getVariables()) {
                VariableSymbol symbol = decl.getSymbolRef();

                IrValue expr = generateExpr(decl.getValue());
                IrValue result = newTemp(symbol);

                if (expr.getType() != result.getType()) {
                    throw new NonMatchingTypeException(
                            "Type mismatch! Left: " + expr.getType() + " Right: " + result.getType());
                }

                IrInstruction variable = new IrInstruction(IrOperator.ASS, expr, null, result);

                component.addVariable(variable);
            }

            // Add the component to the list of IR.
            createIR(component);

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

        // Generate functions
        generateStmt(program.getFunctions());

        // SEPARATOR to distinguish between functions and setup
        createIR(new IrInstruction(IrOperator.SEPARATOR, null, null, null));

        // Generate setup
        generateStmt(program.getSetup());

        // SEPARATOR to distinguish between main and setup
        createIR(new IrInstruction(IrOperator.SEPARATOR, null, null, null));

        // make label at start of main.
        String mainStart = newLabel();
        createIR(new IrInstruction(IrOperator.LABEL, null, null, new IrValue(mainStart, Type.LABEL)));

        // Generate main
        generateStmt(program.getMain());

        // create polling for components
        List<IrInstructionInterface> temp = new ArrayList<>();
        for (IrInstructionInterface c : code) {
            if (c instanceof IrComponent comp && !comp.getVariables().isEmpty()) {
                // first variable in comp is always the one written/read to/from.
                IrValue startVar = comp.getVariables().get(0).getResult();
                if (startVar == null) {
                    throw new NoSuchElementException("Could not read first variable in '" + comp.getName() + "'");
                }
                // create IR depending on component direction
                switch (comp.getDirection().getDirection()) {
                    case INPUT:
                        temp.add(new IrInstruction(IrOperator.COMPR, comp.getPort(), comp.getInterval(), startVar));
                        break;

                    case OUTPUT:
                        temp.add(new IrInstruction(IrOperator.COMPW, comp.getPort(), comp.getInterval(), startVar));
                        break;

                    default:
                        throw new IllegalArgumentException(
                                "Could not create component polling for '" + comp.getName() + "'");
                }
            }
        }
        // add the temp list to code list.
        code.addAll(temp);

        // make last instruction of main return to start of main for infinite loop.
        createIR(new IrInstruction(IrOperator.GOTO, null, null, new IrValue(mainStart, Type.LABEL)));
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

        // create temp of desired type.
        IrValue temp = newTemp(targetType);

        // create IR depending on type cast to.
        if (valueType == Type.INT_T && targetType == Type.FLOAT_T) {
            createIR(new IrInstruction(IrOperator.INT_TO_FLOAT, value, null, temp));
        } else if (valueType == Type.FLOAT_T && targetType == Type.INT_T) {
            createIR(new IrInstruction(IrOperator.FLOAT_TO_INT, value, null, temp));
        } else {
            throw new TypeCastException("Type cast not possible!");
        }

        return temp;
    }
}
