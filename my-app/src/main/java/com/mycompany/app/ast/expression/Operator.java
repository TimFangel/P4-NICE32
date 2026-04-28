package com.mycompany.app.ast.expression;
import com.mycompany.app.ast.Node;

/* Operator */
public abstract class Operator extends Node {
    Operator(int lineNumber) {
        super(lineNumber);
    }
}
