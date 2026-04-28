package frontend.abstract_syntax.expression.arith_expression;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.enums.*;

import lombok.Getter;
import lombok.ToString;

/* Unary Arithmetic Operator */
@ToString
@Getter
public class ArithUnaryOpExpr extends ArithExpr {
    private ArithUnaryOp op;
    private Expr expr;

    ArithUnaryOpExpr(int lineNumber, ArithUnaryOp op, Expr expr) {
        super(lineNumber);

        this.op = op;
        this.expr = expr;
    }

}