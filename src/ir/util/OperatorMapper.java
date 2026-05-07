package ir.util;

import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.ArithUnaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolUnaryOp;
import ir.IrOperator;

/* Map from frontend operand to IR operands */
public class OperatorMapper {

    public IrOperator mapArithBin(ArithBinaryOp op) {
        switch (op) {
            case ADD:
                return IrOperator.ADD;

            case SUB:
                return IrOperator.SUB;

            case MUL:
                return IrOperator.MUL;

            case DIV:
                return IrOperator.DIV;

            case MOD:
                return IrOperator.MOD;

            default:
                throw new RuntimeException("Cannot map binary arithmetic operator " + op);
        }
    }

    public IrOperator mapArithUna(ArithUnaryOp op) {
        if (op == ArithUnaryOp.NEG) {
            return IrOperator.NEG;
        }

        throw new RuntimeException("Cannot map unary arithmetic operator " + op);
    }

    public IrOperator mapBoolBin(BoolBinaryOp op) {
        switch (op) {
            case AND:
                return IrOperator.AND;
            case OR:
                return IrOperator.OR;
            case LT:
                return IrOperator.LT;
            case GT:
                return IrOperator.GT;
            case EQ:
                return IrOperator.EQ;
            case NEQ:
                return IrOperator.NEQ;
            case LEQ:
                return IrOperator.LEQ;
            case GEQ:
                return IrOperator.GEQ;
            default:
                throw new RuntimeException("Cannot map binary boolean operator " + op);
        }
    }

    public IrOperator mapBoolUna(BoolUnaryOp op) {
        if (op == BoolUnaryOp.NOT) {
            return IrOperator.NOT;
        }

        throw new RuntimeException("Cannot map unary boolean operator " + op);
    }
}
