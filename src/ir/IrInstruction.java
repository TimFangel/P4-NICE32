package ir;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.util.IrOperator;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class IrInstruction implements IrInstructionInterface {
    private IrOperator operator; // enum specifying the operation
    private IrValue arg1; // first argument of operation (null -> not present)
    private IrValue arg2; // second argument of operation (null -> not present)
    private IrValue result; // where to store result of operation

    private int instrNum; // instruction number used in liveness/register alloc

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
                return "IF_FALSE " + arg1.getName() + " GOTO " + result.getName();

            case GOTO:
                return "GOTO " + result.getName();

            case LABEL:
                return result.getName() + ":";

            case NOT, NEG:
                return result.getName() + " := " + operandToSymbol(operator) + arg1.getName();

            case INT_TO_FLOAT, FLOAT_TO_INT:
                return result.getName() + " := " + "(" + operator.toString() + ") " + arg1.getName();

            case RET:
                return "RET " + result.getName();

            case CALL:
                return result.getName() + " := " + "CALL " + arg2.getName() + ", " + arg1.getName();

            case PORT_SETUP:
                return "PORT_SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();

            case COMPR:
                return "COMPR " + result.getName() + " " + arg1.getName() + " " + arg2.getName();

            case COMPW:
                return "COMPW " + result.getName() + " " + arg1.getName() + " " + arg2.getName();

            case COMP_INTS:
                return "PORT: " + arg1.getName() + "INTERVAL: " + arg2.getName();

            case FUNC_INFO:
                return "FUNC " + arg1.getName() + ":\n" + "  PARAM " + arg2.getName();

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

    private void findGen() {
        Set<Type> set = EnumSet.of(Type.BOOL_T, Type.FLOAT_T, Type.INT_T, Type.FUNCTION, Type.COMPONENT);

        // add arg1 and arg2 to gen, if valid type.
        if (arg1 != null && set.contains(arg1.getType())) {
            String name = arg1.getName();
            if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                gen.add(name);
            }
        }

        if (arg2 != null && set.contains(arg2.getType())) {
            String name = arg2.getName();
            if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {
                if (operator == IrOperator.FUNC_INFO) {
                    kill.add(name);
                } else {
                    gen.add(name);
                }
            }
        }
    }

    private void findKill() {
        Set<Type> set = EnumSet.of(Type.BOOL_T, Type.FLOAT_T, Type.INT_T, Type.FUNCTION, Type.COMPONENT);

        // add result to kill, if valid type.
        if (result != null && set.contains(result.getType())) {
            String name = result.getName();
            if (name.charAt(0) == 't' && Character.isDigit(name.charAt(1))) {

                if (operator == IrOperator.RET || operator == IrOperator.COMPW) {
                    gen.add(name);

                } else {
                    kill.add(name);
                }
            }
        }
    }

    public void addIn(Set<String> s) {
        this.in.addAll(s);
    }

    public void addOut(Set<String> s) {
        this.out.addAll(s);
    }

    public void clearIn() {
        this.in.clear();
    }

    public void clearOut() {
        this.out.clear();
    }
}
