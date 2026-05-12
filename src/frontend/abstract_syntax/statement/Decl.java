package frontend.abstract_syntax.statement;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.type.Type;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

/* Declaration Statement */
@ToString
@Getter
public final class Decl extends Stmt {
    private Type type;
    private String identifier;
    private Expr value;
    private VariableSymbol symbolRef = null;

    public Decl(int lineNumber, Type type, String identifier, Expr value) {
        super(lineNumber);
        this.type = type;
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
