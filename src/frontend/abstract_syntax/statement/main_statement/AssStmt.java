package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Assignment */
@ToString
@Getter
public class AssStmt extends Stmt {
    private Var variable;
    private Expr value;

    AssStmt(int lineNumber, Var variable, Expr value) {
        super(lineNumber);
        this.variable = variable;
        this.value = value;
    }
}
