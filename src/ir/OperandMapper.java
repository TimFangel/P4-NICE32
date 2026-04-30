package ir;

import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.ArithUnaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolUnaryOp;

/* Map from frontend operand to IR operands */
public class OperandMapper {
    
    public Operand mapArithBin(ArithBinaryOp op) {
        switch (op) {
            case ADD:
                return Operand.ADD;

            case SUB:
                return Operand.SUB;

            case MUL:
                return Operand.MUL;

            case DIV:
                return Operand.DIV;

            case MOD:
                return Operand.MOD;

            default:
                return null;
        }
    }

    public Operand mapArithUna(ArithUnaryOp op) {
        if (op == ArithUnaryOp.NEG) {
            return Operand.NEG;
        }

        return null;
    }

    public Operand mapBoolBin(BoolBinaryOp op) {
        switch (op) {
            case AND:
                return Operand.AND;
            case OR:
                return Operand.OR;
            case LT:
                return Operand.LT;
            case GT:
                return Operand.GT;
            case EQ:
                return Operand.EQ;
            case LEQ:
                return Operand.LEQ;
            case GEQ:
                return Operand.GEQ;
            default:
                return null;
        }
    }

    public Operand mapBoolUna(BoolUnaryOp op) {
        if (op == BoolUnaryOp.NOT) {
            return Operand.NOT;
        }

        return null;
    }
}
