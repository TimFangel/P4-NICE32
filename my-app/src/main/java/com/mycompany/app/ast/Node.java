package com.mycompany.app.ast;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public abstract class Node {
    protected final int lineNumber;
}
