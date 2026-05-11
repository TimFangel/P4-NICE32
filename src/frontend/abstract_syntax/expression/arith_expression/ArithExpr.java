package frontend.abstract_syntax.expression.arith_expression;

import frontend.abstract_syntax.expression.Expr;

import lombok.Getter;
import lombok.ToString;

/* Arithmetic Expression */
@ToString
@Getter
public abstract sealed class ArithExpr extends Expr permits ArithBinaryOpExpr, ArithUnaryOpExpr {
    ArithExpr(int lineNumber) {
        super(lineNumber);
    }
}
