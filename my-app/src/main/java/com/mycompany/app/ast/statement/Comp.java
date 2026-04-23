package com.mycompany.app.ast.statement;

import lombok.Getter;

/* Composition */
@Getter
public class Comp extends Stmt { 
    private Stmt statement1;
    private Stmt statement2;

    Comp(int lineNumber, Stmt statement1, Stmt statement2) {
        super(lineNumber);
        this.statement1 = statement1;
        this.statement2 = statement2;
    }
}
