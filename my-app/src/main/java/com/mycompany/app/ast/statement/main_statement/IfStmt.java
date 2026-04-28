package com.mycompany.app.ast.statement.main_statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.statement.Stmt;

import lombok.Getter;

/* If Statement */
@Getter
public class IfStmt extends Stmt{
    private Expr condition;
    private Stmt thenStmt;
    private Stmt elseStmt;

    IfStmt(int linenumber, Expr condition, Stmt thenStmt, Stmt elseStmt) {
        super(linenumber);
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
}
