package com.mycompany.app.ast.statement.main_statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.statement.Stmt;

import lombok.Getter;

/* Return Statement */
@Getter
public class ReturnStmt extends Stmt {
    private Expr exprReturned;

    ReturnStmt(int lineNumber, Expr exprReturned) {
        super(lineNumber);
        this.exprReturned = exprReturned;
    }
}
