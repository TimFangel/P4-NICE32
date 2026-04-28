package com.mycompany.app.ast.expression;
import com.mycompany.app.ast.Node;

import lombok.Getter;
import lombok.ToString;

/* Operator */
@ToString
@Getter
public abstract class Operator extends Node {
    Operator(int lineNumber) {
        super(lineNumber);
    }
}
