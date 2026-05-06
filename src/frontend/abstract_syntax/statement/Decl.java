package frontend.abstract_syntax.statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.type.Type;
import frontend.symboltable.NewSymbol;
import lombok.Getter;
import lombok.ToString;

/* Declaration Statement */
@ToString
@Getter
public class Decl extends Stmt {
    private Type type;
    private String identifier;
    private Expr value;
    private NewSymbol symbolRef = null;

    public Decl(int lineNumber, Type type, String identifier, Expr value) {
        super(lineNumber);
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }

    public void setSymbolRef(NewSymbol symbolRef) {
        this.symbolRef = symbolRef;
    }
}
