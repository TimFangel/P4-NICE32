package frontend.abstract_syntax.statement.main_statement;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

/* Assignment */
@ToString
@Getter
public final class AssStmt extends Stmt {
    private String identifier;
    private Expr value;
    private VariableSymbol symbolRef = null;

    public AssStmt(int lineNumber, String identifier, Expr value) {
        super(lineNumber);
        this.identifier = identifier;
        this.value = value;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof VariableSymbol vs) {
            this.symbolRef = vs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: variable");
        }
    }
}
