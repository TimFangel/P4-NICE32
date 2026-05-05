package ir;

import exception.UnrecognizedOperatorException;

public class IrInstruction {
    IrOperator operator; // enum specifying the operation
    IrValue arg1; // first argument of operation (null -> not present)
    IrValue arg2; // second argument of operation (null -> not present)
    IrValue result; // where to store result of operation

    /**
     * Constructor for an IrInstruction. null -> not present in instruction.
     * 
     * @param operator what sort of instruction it is.
     * @param arg1
     * @param arg2
     * @param result
     */
    public IrInstruction(IrOperator operator, IrValue arg1, IrValue arg2, IrValue result) {
        this.operator = operator;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        switch (operator) {
            case ASS:
                return result.getName() + " := " + arg1.getName();

            case ADD, SUB, MUL, DIV, MOD, LEQ, LT, GT, GEQ, EQ, NEQ, AND, OR:
                return result.getName() + " := " + arg1.getName() + " " + operandToSymbol(operator) + " "
                        + arg2.getName();

            case IF_FALSE:
                return "if_false " + arg1.getName() + " goto " + result.getName();

            case GOTO:
                return "goto " + result.getName();

            case LABEL:
                return result.getName() + ":";

            case NOT, NEG:
                return result.getName() + " := " + operandToSymbol(operator) + arg1.getName();

            case INT_TO_FLOAT, FLOAT_TO_INT:
                return result.getName() + " := " + "(" + operator.toString() + ") " + arg1.getName();

            case RET:
                return "RET " + result.getName();

            case CALL:
                return "CALL " + result.getName() + ", " + arg1.getName();

            default:
                throw new UnrecognizedOperatorException("Unrecognized Operator (toString): " + operator.toString());
        }
    }

    public String operandToSymbol(IrOperator operator) {
        switch (operator) {
            case ADD:
                return "+";
            case SUB:
                return "-";
            case MUL:
                return "*";
            case DIV:
                return "/";
            case MOD:
                return "%";
            case LT:
                return "<";
            case GT:
                return ">";
            case LEQ:
                return "<=";
            case GEQ:
                return ">=";
            case EQ:
                return "==";
            case NEQ:
                return "!=";
            case AND:
                return "&&";
            case OR:
                return "||";
            case NOT:
                return "!";
            case NEG:
                return "-";
            default:
                throw new UnrecognizedOperatorException(
                        "Unrecognized Operator (operatorToSymbol): " + operator.toString());
        }
    }
}
