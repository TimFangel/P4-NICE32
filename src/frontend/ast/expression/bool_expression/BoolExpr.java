package com.mycompany.app.ast.expression.bool_expression;

import com.mycompany.app.ast.expression.Expr;

import lombok.Getter;
import lombok.ToString;

/* Boolean Expression */
@ToString
@Getter
public class BoolExpr extends Expr {
    BoolExpr(int lineNumber) {
        super(lineNumber);
    }
}