package frontend.abstract_syntax.expression.bool_expression;

import frontend.abstract_syntax.expression.Expr;

import lombok.Getter;
import lombok.ToString;

/* Boolean Expression */
@ToString
@Getter
public abstract sealed class BoolExpr extends Expr permits BoolBinaryOpExpr, BoolUnaryOpExpr {
    BoolExpr(int lineNumber) {
        super(lineNumber);
    }
}