package com.mycompany.app.ast.expression;
import com.mycompany.app.ast.Node;

public abstract class Operator extends Node {
    Operator(int lineNumber) {
        super(lineNumber);
    }
}
