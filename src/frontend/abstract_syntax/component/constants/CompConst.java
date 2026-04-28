package com.mycompany.app.ast.component.constants;

import com.mycompany.app.ast.Node;

import lombok.Getter;
import lombok.ToString;

/* Component Constant Superclass */
@ToString
@Getter
public class CompConst extends Node {
    CompConst(int lineNumber) {
        super(lineNumber);
    }
}
