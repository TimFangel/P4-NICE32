package com.mycompany.app.ast.statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.type.Type;
import com.mycompany.app.ast.value.Var;

import lombok.Getter;

/* Declaration Statement */
@Getter
public class Decl extends Stmt {
    private Type type;
    private Var identifier;
    private Expr value;

    Decl(int lineNumber, Var identifier, Expr value) {
        super(lineNumber);
        this.identifier = identifier;
        this.value = value;
    }
}
