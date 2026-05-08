package frontend.symboltable;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class FunctionSymbol extends Symbol {
    private VariableSymbol parameterSymbolRef;
    private String returnIrName;

    FunctionSymbol(String name, Type type) {
        super(name, type);
    }

    public void setParameterSymbolRef(Symbol symbol) {
        if (symbol instanceof VariableSymbol vs) {
            parameterSymbolRef = vs;
        } else {
            throw new NonMatchingSymbolException("Parameter symbol must be of type: variable");
        }
    }

    public void setReturnIrName(String irName) {
        returnIrName = irName;
    }
}
