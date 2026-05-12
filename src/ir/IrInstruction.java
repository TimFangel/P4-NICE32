package ir;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.util.IrOperator;
import lombok.Getter;

@Getter
public final class IrInstruction implements IrInstructionInterface {
    private IrOperator operator; // enum specifying the operation
    private IrValue arg1; // first argument of operation (null -> not present)
    private IrValue arg2; // second argument of operation (null -> not present)
    private IrValue result; // where to store result of operation

    // sets used for liveness analysis.
    private Set<String> gen = new HashSet<>(); // read
    private Set<String> kill = new HashSet<>(); // write
    private Set<String> in = new HashSet<>();
    private Set<String> out = new HashSet<>();

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

        findGen();
        findKill();
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

            case SETUP:
                return "SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();

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

    public void findGen() {
        Set<Type> set = EnumSet.of(Type.BOOL_T, Type.FLOAT_T, Type.INT_T);

        // add arg1 and arg2 to gen, if valid type.
        if (set.contains(arg1.getType())) {
            gen.add(arg1.getName());
        }

        if (set.contains(arg2.getType())) {
            gen.add(arg2.getName());
        }
    }

    public void findKill() {
        Set<Type> set = EnumSet.of(Type.BOOL_T, Type.FLOAT_T, Type.INT_T);

        // add result to kill, if valid type.
        if (set.contains(result.getType())) {
            kill.add(result.getName());
        }
    }
}
