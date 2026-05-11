package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.BlockStmt;

import lombok.Getter;
import lombok.ToString;

/* If Statement */
@ToString
@Getter
public final class IfStmt extends Stmt{
    private Expr condition;
    private BlockStmt  thenStmt;
    private BlockStmt  elseStmt;

    public IfStmt(int linenumber, Expr condition, BlockStmt  thenStmt, BlockStmt  elseStmt) {
        super(linenumber);
        this.condition = condition;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
}
