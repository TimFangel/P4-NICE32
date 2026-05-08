package frontend.abstract_syntax.expression;

import exception.NonMatchingSymbolException;
import frontend.symboltable.FunctionSymbol;
import frontend.symboltable.NewSymbol;
import lombok.Getter;
import lombok.ToString;

/* Function Call Statement */
@ToString
@Getter
public final class FuncCall extends Expr {
    private String identifier;
    private Expr parameter;
    private FunctionSymbol symbolRef = null;

    public FuncCall(int lineNumber, String identifier, Expr parameter) {
        super(lineNumber);
        this.identifier = identifier;
        this.parameter = parameter;
    }

    public void setSymbolRef(NewSymbol symbolRef) {
        if (symbolRef instanceof FunctionSymbol fs) {
            this.symbolRef = fs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: function");
        }
    }
}
