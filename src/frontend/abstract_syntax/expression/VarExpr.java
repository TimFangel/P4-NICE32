package frontend.abstract_syntax.expression;

import exception.NonMatchingSymbolException;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class VarExpr extends Expr {
    private final String name;
    private VariableSymbol symbolRef = null;

    public VarExpr(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof VariableSymbol vs) {
            this.symbolRef = vs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: variable");
        }
    }
}