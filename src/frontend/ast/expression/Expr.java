package com.mycompany.app.ast.expression;

import com.mycompany.app.ast.Node;

import lombok.Getter;
import lombok.ToString;

/* Expression */
@ToString
@Getter
public abstract class Expr extends Node {
    protected Expr(int lineNumber) {
        super(lineNumber);
    }
}
