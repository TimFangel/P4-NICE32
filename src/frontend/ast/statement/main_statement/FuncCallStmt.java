package com.mycompany.app.ast.statement.main_statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.statement.Stmt;
import com.mycompany.app.ast.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Function Call Statement */
@ToString
@Getter
public class FuncCallStmt extends Stmt { // Unsure if should extend Func interface.
    private Var identifier; 
    private Expr parameter;

    FuncCallStmt(int lineNumber, Var identifier, Expr parameter) {
        super(lineNumber);
        this.identifier = identifier;
        this.parameter = parameter;
    }
}
