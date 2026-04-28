package com.mycompany.app.ast.expression.arith_expression;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.expression.enums.*;

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