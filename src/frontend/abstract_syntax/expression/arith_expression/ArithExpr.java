package frontend.abstract_syntax.expression.arith_expression;

import frontend.abstract_syntax.expression.Expr;

import lombok.Getter;
import lombok.ToString;

/* Arithmetic Expression */
@ToString
@Getter
public abstract class ArithExpr extends Expr {
    ArithExpr(int lineNumber) {
        super(lineNumber);
    }
}

