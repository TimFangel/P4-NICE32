package com.mycompany.app.ast.program;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.app.ast.Node;
import com.mycompany.app.ast.function.Func;
import com.mycompany.app.ast.statement.Stmt;

import lombok.Getter;

/* Program Node */
@Getter
public class Program extends Node {
    private List<Stmt> statements = new ArrayList<>();
    private List<Func> functions = new ArrayList<>();

    Program(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Add statements to program list.
     * @param stmt statement to add.
     */
    public void addStmt(Stmt stmt) {
        this.statements.add(stmt);
    }

    /**
     * Add functions to program list.
     * @param func function to add.
     */
    public void addFunc(Func func) {
        this.functions.add(func);
    }
}
