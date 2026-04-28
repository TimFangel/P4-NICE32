package com.mycompany.app.ast.function;

import com.mycompany.app.ast.Node;

import lombok.Getter;

/* Function Composition Node */
@Getter
public class CompFunc extends Node implements Func  {
    private FuncDecl func1;
    private FuncDecl func2;

    CompFunc(int lineNumber, FuncDecl func1, FuncDecl func2) {
        super(lineNumber);
        this.func1 = func1;
        this.func2 = func2;
    }
}
