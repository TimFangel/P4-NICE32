package frontend.abstract_syntax.expression;

import exception.NonMatchingSymbolException;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class MemberAccess extends Expr {
    private final String component;
    private final String variable;
    private VariableSymbol symbolRef = null;

    public MemberAccess(int lineNumber, String component, String variable) {
        super(lineNumber);
        this.component = component;
        this.variable = variable;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof VariableSymbol vs) {
            this.symbolRef = vs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: variable");
        }
    }
}