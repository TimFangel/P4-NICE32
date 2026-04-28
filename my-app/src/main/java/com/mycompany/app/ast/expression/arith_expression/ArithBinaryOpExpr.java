package com.mycompany.app.ast.expression.arith_expression;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.expression.enums.*;

import lombok.Getter;

/* Binary Arithmetic Operator */
@Getter
public class ArithBinaryOpExpr extends ArithExpr {
    private ArithBinaryOp op;
    private Expr exprLeft;
    private Expr exprRight;
    
    ArithBinaryOpExpr(int lineNumber, ArithBinaryOp op, Expr exprLeft, Expr exprRight) {
        super(lineNumber);

        this.op = op;
        this.exprLeft = exprLeft;
        this.exprRight = exprRight;
    }

}