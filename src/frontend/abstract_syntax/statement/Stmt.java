package frontend.abstract_syntax.statement;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.function.Func;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.ReturnStmt;
import frontend.abstract_syntax.statement.main_statement.WhileStmt;
import lombok.Getter;
import lombok.ToString;

/* Statement Node */
@ToString
@Getter
public abstract sealed class Stmt extends Node
        permits Component, Func, BlockStmt, Decl, AssStmt, IfStmt, ReturnStmt, WhileStmt {
    protected Stmt(int lineNumber) {
        super(lineNumber);
    }
}
