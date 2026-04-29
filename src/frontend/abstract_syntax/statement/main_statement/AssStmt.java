package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.value.Ident;
import lombok.Getter;
import lombok.ToString;

/* Assignment */
@ToString
@Getter
public class AssStmt extends Stmt {
    private Ident variable;
    private Expr value;

    public AssStmt(int lineNumber, Ident variable, Expr value) {
        super(lineNumber);
        this.variable = variable;
        this.value = value;
    }
}
