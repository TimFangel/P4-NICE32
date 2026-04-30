package ir;

public class TacInstruction {
    Operand operand;     // "if", "+", "*" etc.
    IrValue arg1;           
    IrValue arg2;
    IrValue result;

    public TacInstruction(Operand operand, IrValue arg1, IrValue arg2, IrValue result) {
        this.operand = operand;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        switch (operand) {
            case ASS:
                return result.name + " := " + arg1.name;    

            case ADD, SUB, MUL, DIV, MOD, LEQ, LT, GT, GEQ, EQ, NEQ:
                return result.name + " := " + arg1.name + " " + operandToSymbol(operand) + " " + arg2.name;
        
            case IF:
                return "if " + arg1.name + " goto " + result.name; 

            case GOTO:
                return "goto " + result.name;

            case LABEL:
                return result.name + ":";

            default:
                return ""; // not recognized
        }
    }

    public String operandToSymbol(Operand operand) {
        switch (operand) {
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
            default:
                return operand.name();
        }
    }
}
