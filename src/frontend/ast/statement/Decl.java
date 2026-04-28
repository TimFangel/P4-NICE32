package com.mycompany.app.ast.statement;

import com.mycompany.app.ast.expression.Expr;
import com.mycompany.app.ast.type.Type;
import com.mycompany.app.ast.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Declaration Statement */
@ToString
@Getter
public class Decl extends Stmt {
    private Type type;
    private Var identifier;
    private Expr value;

    Decl(int lineNumber, Type type, Var identifier, Expr value) {
        super(lineNumber);
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }
}
