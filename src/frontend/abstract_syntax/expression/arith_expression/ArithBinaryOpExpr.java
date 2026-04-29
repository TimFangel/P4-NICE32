package frontend.abstract_syntax.expression.arith_expression;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.enums.*;

import lombok.Getter;
import lombok.ToString;

/* Binary Arithmetic Operator */
@ToString
@Getter
public class ArithBinaryOpExpr extends ArithExpr {
    private ArithBinaryOp op;
    private Expr exprLeft;
    private Expr exprRight;
    
    public ArithBinaryOpExpr(int lineNumber, ArithBinaryOp op, Expr exprLeft, Expr exprRight) {
        super(lineNumber);

        this.op = op;
        this.exprLeft = exprLeft;
        this.exprRight = exprRight;
    }

}