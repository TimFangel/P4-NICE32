package backend;

import java.util.Arrays;
import java.util.List;

import backend.processors.*;
import exception.RegisterException;
import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;

public class InstructionGenerator {
    // Registers for temporary values
    static final String ARITH_SCRATCH_REG = "a15";
    static final String BOOL_SCRATCH_REG = "b15";
    static final List<String> FLOAT_SCRATCH_REGS = Arrays.asList("f9", "f10", "f11", "f12", "f13", "f14", "f15");

    // Helper classes
    AssignmentProcessor assignmentProcessor;
    ArithmeticProcessor arithmeticProcessor;
    FloatProcessor floatProcessor;
    BooleanProcessor booleanProcessor;
    ComparisonProcessor comparisonProcessor;
    JumpProcessor jumpProcessor;
    IfStatementProcessor ifStatementProcessor;
    TypeCastProcessor typeCastProcessor;
    FunctionProcessor functionProcessor;
    ComponentProcessor componentProcessor;

    // Values
    private IrOperator operator;
    private IrValue arg1;
    private IrValue arg2;
    private IrValue result;

    public InstructionGenerator(IrInstruction ii) {
        this.operator = ii.getOperator();
        this.arg1 = ii.getArg1();
        this.arg2 = ii.getArg2();
        this.result = ii.getResult();
        updateTypes();

        // Helper classes
        assignmentProcessor = new AssignmentProcessor(ARITH_SCRATCH_REG, result, arg1);
        arithmeticProcessor = new ArithmeticProcessor(result, arg1, arg2, operator);
        floatProcessor = new FloatProcessor(FLOAT_SCRATCH_REGS, result, arg1, arg2, operator);
        booleanProcessor = new BooleanProcessor(BOOL_SCRATCH_REG, result, arg1);
        comparisonProcessor = new ComparisonProcessor(BOOL_SCRATCH_REG, result, arg1, arg2, operator);
        jumpProcessor = new JumpProcessor(result);
        ifStatementProcessor = new IfStatementProcessor(result, arg1);
        typeCastProcessor = new TypeCastProcessor(result, arg1, operator);
        functionProcessor = new FunctionProcessor(ARITH_SCRATCH_REG, result, arg1, arg2);
        componentProcessor = new ComponentProcessor(result, arg1, arg2, operator);
    }

    private void updateTypes() {
        if (result != null && isRegister(result.getName())) {
            result.setType(toRegister(result.getType()));
        }

        if (arg1 != null && isRegister(arg1.getName())) {
            arg1.setType(toRegister(arg1.getType()));
        }

        if (arg2 != null && isRegister(arg2.getName())) {
            arg2.setType(toRegister(arg2.getType()));
        }

    }

    private boolean isRegister(String name) {
        return name.matches("^[abf]\\d+$");
    }

    private Type toRegister(Type oldType) {
        switch (oldType) {
            case BOOL_T, B_REG:
                return Type.B_REG;
            case INT_T, A_REG:
                return Type.A_REG;
            case FLOAT_T, F_REG:
                return Type.F_REG;

            default:
                throw new RegisterException("Could not find register for " + oldType);
        }
    }

    public String write() {
        switch (operator) {
            case ASS:
                return assignmentProcessor.handleAssignment();

            case ADD, SUB, MUL, DIV, MOD:
                if (result.getType() == Type.A_REG) {
                    return arithmeticProcessor.handleExpression();
                } else if (result.getType() == Type.F_REG) {
                    return floatProcessor.handleExpression();
                } else {
                    throw new RegisterException("Cannot do arithmetic operation on " + result.getType());
                }

                case AND, OR:
                return comparisonProcessor.handleLogicalComparison();

            case LEQ, LT, GT, GEQ, EQ, NEQ:
                if (result.getType() == Type.A_REG) {
                    return comparisonProcessor.handleArithmeticComparison();
                } else if (result.getType() == Type.F_REG) {
                    return comparisonProcessor.handleFloatComparisons();
                } else {
                    throw new RegisterException("Cannot do comparison operation on " + result.getType());
                }

            case IF_FALSE:
                return ifStatementProcessor.handleIf();

            case GOTO:
                return jumpProcessor.handleJump();

            case LABEL:
                return jumpProcessor.handleLabel();

            case NOT:
                return booleanProcessor.handleNot();

            case NEG:
                if (result.getType() == Type.A_REG) {
                    return arithmeticProcessor.handleNegation();
                } else if (result.getType() == Type.F_REG) {
                    return floatProcessor.handleNegation();
                } else {
                    throw new RegisterException("Cannot do arithmetic operation on " + result.getType());
                }

            case INT_TO_FLOAT, FLOAT_TO_INT:
                return typeCastProcessor.handleCast();

            case RET:
                return jumpProcessor.handleReturn();

            case CALL:
                return functionProcessor.handleCall();

            case PORT_SETUP:
                return componentProcessor.handlePortSetup();

            case COMPR, COMPW:
                return componentProcessor.handlePolling();

            case FUNC_INFO:
                return functionProcessor.handleDefinition();

            default:
                throw new UnrecognizedOperatorException("Unrecognized Operator: " + operator);
        }
    }
}
