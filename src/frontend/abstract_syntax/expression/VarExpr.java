package frontend.abstract_syntax.expression;

import frontend.symboltable.NewSymbol;
import frontend.symboltable.VariableSymbol;
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

    public void setSymbolRef(NewSymbol symbolRef) {
        this.symbolRef = symbolRef;
    }
}