package frontend.abstract_syntax.expression.bool_expression;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;

import lombok.Getter;
import lombok.ToString;

/* Binary Boolean Operator */
@ToString
@Getter
public final class BoolBinaryOpExpr extends BoolExpr {
    private BoolBinaryOp op;
    private Expr exprLeft;
    private Expr exprRight;

    public BoolBinaryOpExpr(int lineNumber, BoolBinaryOp op, Expr exprLeft, Expr exprRight) {
        super(lineNumber);

        this.op = op;
        this.exprLeft = exprLeft;
        this.exprRight = exprRight;
    }

}