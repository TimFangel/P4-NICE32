package com.mycompany.app.ast.expression.bool_expression;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.expression.enums.BoolUnaryOp;

import lombok.Getter;
import lombok.ToString;

/* Unary Boolean Operator */
@ToString
@Getter
public class BoolUnaryOpExpr extends BoolExpr {
    private BoolUnaryOp op;
    private Expr expr;

    BoolUnaryOpExpr(int lineNumber, BoolUnaryOp op, Expr expr) {
        super(lineNumber);

        this.op = op;
        this.expr = expr;
    }

}
