package frontend.abstract_syntax;

import frontend.abstract_syntax.component.constants.CompConst;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.Stmt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/* Node Superclass */
@ToString
@Getter
@AllArgsConstructor
public abstract sealed class Node permits CompConst, Expr, Program, Stmt {
    protected final int lineNumber;
}
