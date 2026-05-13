package backend;

import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;

public class InstructionWriter {
    private IrOperator operator;
    private IrValue arg1;
    private IrValue arg2;
    private IrValue result;

    public InstructionWriter(IrInstruction ii) {
        this.operator = ii.getOperator();
        this.arg1 = ii.getArg1();
        this.arg2 = ii.getArg2();
        this.result = ii.getResult();
        updateTypes();
    }

    private void updateTypes() {
        if (isRegister(result.getName()))
            result.setType(toRegister(result.getType()));

        if (arg1 == null) {
            return;
        }

        if (isRegister(arg1.getName()))
            arg1.setType(toRegister(arg1.getType()));

        if (arg2 == null) {
            return;
        }

        if (isRegister(arg2.getName()))
            arg2.setType(toRegister(arg2.getType()));
    }

    private boolean isRegister(String name) {
        return name.matches("[a-z]\\d*");
    }

    private int getRegisterIndex(String name) throws RegisterException {
        if (!isRegister(name)) {
            throw new RegisterException("Could not find register index for " + name);
        }

        int index = Integer.parseInt(name.substring(1));

        if (index > 15 || index < 0) {
            throw new RegisterException("Register out of bounds " + name);
        }

        return index;
    }

    private Type toRegister(Type oldType) throws RegisterException {
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

    private int setBit(int index) {
        return 1 << index;
    }

    private void switchArgs() {
        IrValue temp = arg1;
        arg1 = arg2;
        arg2 = temp;
    }

    public String write() {
        switch (operator) {
            case ASS:
                return assignment();

            case ADD, SUB, MUL, DIV, MOD:
                return arithExpression();

            case AND, OR:
                return logicalComparison();

            case LEQ, LT, GT, GEQ, EQ, NEQ:
                return boolExpression();

            case IF_FALSE:
                return "if_false " + arg1.getName() + " goto " + result.getName();

            case GOTO:
                return "goto " + result.getName();

            case LABEL:
                return result.getName() + ":";

            case NOT:
                notBool();

            case NEG:
                negateArith();

            case INT_TO_FLOAT, FLOAT_TO_INT:
                return result.getName() + " := " + "(" + operator.toString() + ") " + arg1.getName();

            case RET:
                return "RET " + result.getName();

            case CALL:
                return "CALL " + result.getName() + ", " + arg1.getName();

            case SETUP:
                return "SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();

            default:
                throw new UnrecognizedOperatorException("Unrecognized Operator (toString): " + operator.toString());
        }
    }

    private String assignment() throws UnknownAssArgTypeException {
        switch (arg1.getType()) {
            case BOOL_T:
                return boolAssignment();
            case INT_T:
                return "MOVI " + result.getName() + ", " + arg1.getName();
            case A_REG:
                return "MOV " + result.getName() + ", " + arg1.getName();

            default:
                throw new UnknownAssArgTypeException("Could not generate assignment");
        }
    }

    private String boolAssignment() {
        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        switch (arg1.getName()) {
            case "true":
                return setBit;
            case "false":
                return clearBit;

            default:
                throw new UnknownAssArgTypeException(
                        "Could not generate boolean assignment for value " + arg1.getName());
        }

    }

    private String arithExpression()
            throws NonRegisterResultException, NonRegisterArgsException, InvalidOperatorException {
        String str = "";

        if (result.getType() != Type.A_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        switch (operator) {
            case ADD:
                str += "ADD ";
                break;
            case SUB:
                str += "SUB ";
                break;
            case MUL:
                str += "MULL ";
                break;
            case DIV:
                str += "QUOS ";
                break;
            case MOD:
                str += "REMS ";
                break;
            default:
                throw new InvalidOperatorException("Could not generate arithmetic expression for " + operator);
        }

        str += result.getName() + ", " + arg1.getName() + ", " + arg2.getName();

        return str;
    }

    private String logicalComparison() throws NonRegisterArgsException, InvalidOperatorException {
        if (result.getType() != Type.B_REG || arg1.getType() != Type.B_REG || arg2.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate logical comparison for non-register args or result");
        }

        switch (operator) {
            case AND:
                return "ANDB " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case OR:
                return "ORB " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            default:
                throw new InvalidOperatorException("Could not generate logical comparison for " + operator);
        }
    }

    private String boolExpression()
            throws NonRegisterResultException, NonRegisterArgsException, InvalidOperatorException {

        final String trueLabel = AssemblyGenerator.newLabel();
        final String falseLabel = AssemblyGenerator.newLabel();
        String str;

        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        switch (operator) {
            case GEQ:
                switchArgs();
            case LT:
                str = "BLT ";
                break;
            case LEQ:
                switchArgs();
            case GT:
                str = "BGE ";
                break;
            case EQ:
                str = "BEQ";
                break;
            case NEQ:
                str = "NEQ ";
                break;
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        str += arg1.getName() + "," + arg2.getName() + "," + trueLabel + "\n";
        str += clearBit + "\n";
        str += "J " + falseLabel + "\n";
        str += trueLabel + "\n";
        str += setBit + "\n";
        str += falseLabel + "\n";

        return str;
    }

    private String notBool() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        String str = "ORBC b0, b0, b0 \n";
        str += "XORB " + result.getName() + ", b0, " + arg1.getName();
        return str;
    }

    private String negateArith() {

    }
}
