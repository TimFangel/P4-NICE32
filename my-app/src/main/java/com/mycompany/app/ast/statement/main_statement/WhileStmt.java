package com.mycompany.app.ast.statement.main_statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.statement.Stmt;

import lombok.Getter;

/* While Statement */
@Getter
public class WhileStmt extends Stmt {
    private Expr condition;
    private Stmt whileBody;
    
    WhileStmt(int lineNumber, Expr condition, Stmt whileBody) {
        super(lineNumber);
        this.condition = condition;
        this.whileBody = whileBody;
    }
}
