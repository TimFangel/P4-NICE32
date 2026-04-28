package com.mycompany.app.ast.statement.main_statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.statement.Stmt;
import com.mycompany.app.ast.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Assignment */
@ToString
@Getter
public class AssStmt extends Stmt {
    private Var variable;
    private Expr value;

    AssStmt(int lineNumber, Var variable, Expr value) {
        super(lineNumber);
        this.variable = variable;
        this.value = value;
    }
}
