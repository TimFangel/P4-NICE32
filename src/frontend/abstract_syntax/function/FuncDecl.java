package frontend.abstract_syntax.function;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.type.Type;
import frontend.symboltable.FunctionSymbol;
import frontend.symboltable.NewSymbol;
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

    public void setSymbolRef(NewSymbol symbolRef) {
        if (symbolRef instanceof FunctionSymbol fs) {
            this.symbolRef = fs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: function");
        }
    }
}
