package com.mycompany.app.ast.statement;

import com.mycompany.app.ast.Node;

import lombok.Getter;
import lombok.ToString;

/* Statement Node */
@ToString
@Getter
public abstract class Stmt extends Node {
    protected Stmt(int lineNumber) {
        super(lineNumber);
    }
}
