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

    private Type toRegister(Type oldType) throws RuntimeException {
        switch (oldType) {
            case INT_T, BOOL_T, A_REG:
                return Type.A_REG;
            case FLOAT_T, F_REG:
                return Type.F_REG;

            default:
                throw new RuntimeException("Could not find register for " + oldType);
        }
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
                return result.getName() + " := " + arg1.getName() + " " + "OP" + " "
                        + arg2.getName();

            case IF_FALSE:
                return "if_false " + arg1.getName() + " goto " + result.getName();

            case GOTO:
                return "goto " + result.getName();

            case LABEL:
                return result.getName() + ":";

            case NOT, NEG:
                return result.getName() + " := " + "OP" + arg1.getName();

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
            case INT_T, BOOL_T:
                return "MOVI " + result.getName() + ", " + arg1.getName();

            case A_REG:
                return "MOV " + result.getName() + ", " + arg1.getName();

            default:
                throw new UnknownAssArgTypeException("Could not generate assignment");
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

    private String logicalComparison() {
        if (result.getType() != Type.A_REG || arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new RuntimeException("Cannot generate logical comparison for non-register args or result");
        }

        switch (operator) {
            case AND:
                return "AND " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case OR:
                return "OR " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            default:
                throw new RuntimeException("Could not generate logical comparison for " + operator);
        }
    }
}
