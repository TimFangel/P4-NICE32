package frontend.abstract_syntax.function;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.type.Type;
import frontend.symbol_table.FunctionSymbol;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.VariableSymbol;
import lombok.Getter;
import lombok.ToString;

/* Function Declaration */
@ToString
@Getter
public final class FuncDecl extends Func {
    private Type returnType;
    private String identifier;
    private Type paramType;
    private String paramName;
    private BlockStmt statements;
    private FunctionSymbol symbolRef = null;

    public FuncDecl(int lineNumber, Type returnType, String identifier, Type paramType, String paramName,
            BlockStmt statements) {
        super(lineNumber);
        this.returnType = returnType;
        this.identifier = identifier;
        this.paramType = paramType;
        this.paramName = paramName;
        this.statements = statements;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof FunctionSymbol fs) {
            this.symbolRef = fs;
        } else {
            throw new NonMatchingSymbolException("Function symbol must be of type: function");
        }
    }

    public VariableSymbol getParamSymbolRef() {
        return symbolRef.getParameterSymbolRef();
    }
}
