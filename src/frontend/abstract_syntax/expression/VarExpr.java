package frontend.abstract_syntax.expression;

import frontend.symboltable.NewSymbol;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public final class VarExpr extends Expr {
    private final String name;
    private NewSymbol symbolRef = null;

    public VarExpr(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }

    public void setSymbolRef(NewSymbol symbolRef) {
        this.symbolRef = symbolRef;
    }
}