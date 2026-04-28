package com.mycompany.app.ast.expression.arith_expression;

import com.mycompany.app.ast.expression.Expr;

import lombok.Getter;
import lombok.ToString;

/* Arithmetic Expression */
@ToString
@Getter
public class ArithExpr extends Expr {
    ArithExpr(int lineNumber) {
        super(lineNumber);
    }
}

