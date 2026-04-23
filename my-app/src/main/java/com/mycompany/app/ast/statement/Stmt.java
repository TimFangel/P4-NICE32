package com.mycompany.app.ast.statement;

import com.mycompany.app.ast.Node;

/* Statement Node */
public abstract class Stmt extends Node {
    protected Stmt(int lineNumber) {
        super(lineNumber);
    }
}
