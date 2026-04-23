package com.mycompany.app.ast.expression;

import com.mycompany.app.ast.Node;

public abstract class Expr extends Node {
    protected Expr(int lineNumber) {
        super(lineNumber);
    }
}
