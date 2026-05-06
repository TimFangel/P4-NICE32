package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;

import lombok.Getter;
import lombok.ToString;

/* Return Statement */
@ToString
@Getter
public class ReturnStmt extends Stmt {
    private Expr exprReturned;

    public ReturnStmt(int lineNumber, Expr exprReturned) {
        super(lineNumber);
        this.exprReturned = exprReturned;
    }
}
