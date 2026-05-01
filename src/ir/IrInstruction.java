package ir;

public class IrInstruction {
    IrOperator operand;     // "if", "+", "*" etc.
    IrValue arg1;           
    IrValue arg2;
    IrValue result;

    public IrInstruction(IrOperator operand, IrValue arg1, IrValue arg2, IrValue result) {
        this.operand = operand;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    @Override
    public String toString() {
        switch (operand) {
            case ASS:
                return result.getName() + " := " + arg1.getName();    

            case ADD, SUB, MUL, DIV, MOD, LEQ, LT, GT, GEQ, EQ, NEQ:
                return result.getName() + " := " + arg1.getName() + " " + operandToSymbol(operand) + " " + arg2.getName();
        
            case IF:
                return "if " + arg1.getName() + " goto " + result.getName(); 

            case GOTO:
                return "goto " + result.getName();

            case LABEL:
                return result.getName() + ":";

            default:
                return ""; // not recognized
        }
    }

    public String operandToSymbol(IrOperator operand) {
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
