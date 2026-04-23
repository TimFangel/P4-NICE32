package com.mycompany.app.ast.expression.bool_expression;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.expression.enums.BoolBinaryOp;

import lombok.Getter;

@Getter
public class BoolBinaryOpExpr extends BoolExpr {
    private BoolBinaryOp op;
    private Expr exprLeft;
    private Expr exprRight;
    
    BoolBinaryOpExpr(int lineNumber, BoolBinaryOp op, Expr exprLeft, Expr exprRight) {
        super(lineNumber);

        this.op = op;
        this.exprLeft = exprLeft;
        this.exprRight = exprRight;
    }

}