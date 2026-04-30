package ir;

public class TacInstruction {
    Operand operand;     // "if", "+", "*" etc.
    IrValue arg1;           
    IrValue arg2;
    IrValue result;


    public String toString() {
        switch (operand) {
            case ASS:
                return result.name + " = " + arg1.name;    

            case ADD: case SUB: case MUL: case DIV: case MOD:
            case LTE: case LT: case GT: case GTE: case EQ: case NEQ:
                return result.name + " = " + arg1.name + " " + operandToSymbol(operand) + " " + arg2.name;
        
            case IF:
                return "if " + arg1.name + " goto " + result.name; 

            case GOTO:
                return "goto " + result.name;

            case LABEL:
                return result.name + ":";

            default:
                return "NULL";
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
            case LTE:
                return "<=";
            case GTE:
                return ">=";
            case EQ:
                return "==";
            default:
                return operand.name();
        }
    }
}
