package frontend.abstract_syntax.expression.bool_expression;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.enums.BoolUnaryOp;

import lombok.Getter;
import lombok.ToString;

/* Unary Boolean Operator */
@ToString
@Getter
public class BoolUnaryOpExpr extends BoolExpr {
    private BoolUnaryOp op;
    private Expr expr;

    public BoolUnaryOpExpr(int lineNumber, BoolUnaryOp op, Expr expr) {
        super(lineNumber);

        this.op = op;
        this.expr = expr;
    }

}
