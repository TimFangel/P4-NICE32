package frontend.abstract_syntax.expression;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.statement.Stmt;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberAssStmt extends Stmt {
    private final String component;
    private final String variable;
    private final Expr value;
    VariableSymbol symbolRef = null;

    public MemberAssStmt(int lineNumber, String component, String variable, Expr value) {
        super(lineNumber);
        this.component = component;
        this.variable = variable;
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
