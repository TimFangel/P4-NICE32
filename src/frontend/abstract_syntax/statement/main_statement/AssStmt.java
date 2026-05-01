package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import lombok.Getter;
import lombok.ToString;

/* Assignment */
@ToString
@Getter
public class AssStmt extends Stmt {
    private String identifier;
    private Expr value;

    public AssStmt(int lineNumber, String identifier, Expr value) {
        super(lineNumber);
        this.identifier = identifier;
        this.value = value;
    }
}
