package com.mycompany.app.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

/* Node Superclass */
@Getter @AllArgsConstructor
public abstract class Node {
    protected final int lineNumber;
}
