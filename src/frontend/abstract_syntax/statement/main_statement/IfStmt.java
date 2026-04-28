package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;

import lombok.Getter;
import lombok.ToString;

/* If Statement */
@ToString
@Getter
public class IfStmt extends Stmt{
    private Expr condition;
    private Stmt thenStmt;
    private Stmt elseStmt;

    IfStmt(int linenumber, Expr condition, Stmt thenStmt, Stmt elseStmt) {
        super(linenumber);
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
}
