package com.mycompany.app.ast.function;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.app.ast.Node;
import com.mycompany.app.ast.statement.Stmt;
import com.mycompany.app.ast.type.Type;
import com.mycompany.app.ast.value.Var;

import lombok.Getter;

/* Function Declaration */
@Getter
public class FuncDecl extends Node implements Func {
    private Type returnType;
    private Var identifier;
    private Type paramType;
    private List<Stmt> statements = new ArrayList<>();

    FuncDecl(int lineNumber, Type returnType, Var identifier, Type paramType) {
        super(lineNumber);
        this.returnType = returnType;
        this.identifier = identifier;
        this.paramType = paramType;
    }

    /**
     * Add statement to function's stmt list.
     * @param stmt statement to add to list.
     */
    void addStmt(Stmt stmt) {
        if(stmt != null) {
            this.statements.add(stmt);
        }
    }
}
