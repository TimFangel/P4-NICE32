package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Function Call Statement */
@ToString
@Getter
public class FuncCallStmt extends Stmt { // Unsure if should extend Func interface.
    private Var identifier; 
    private Expr parameter;

    FuncCallStmt(int lineNumber, Var identifier, Expr parameter) {
        super(lineNumber);
        this.identifier = identifier;
        this.parameter = parameter;
    }
}
