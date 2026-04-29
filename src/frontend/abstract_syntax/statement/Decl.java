package frontend.abstract_syntax.statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Ident;
import lombok.Getter;
import lombok.ToString;

/* Declaration Statement */
@ToString
@Getter
public class Decl extends Stmt {
    private Type type;
    private Ident identifier;
    private Expr value;

    public Decl(int lineNumber, Type type, Ident identifier, Expr value) {
        super(lineNumber);
        this.type = type;
        this.identifier = identifier;
        this.value = value;
    }
}
