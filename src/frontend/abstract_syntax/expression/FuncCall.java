package frontend.abstract_syntax.expression;

import frontend.abstract_syntax.value.Ident;
import lombok.Getter;
import lombok.ToString;

/* Function Call Statement */
@ToString
@Getter
public class FuncCall extends Expr {
    private Ident identifier; 
    private Expr parameter;

    FuncCall(int lineNumber, Ident identifier, Expr parameter) {
        super(lineNumber);
        this.identifier = identifier;
        this.parameter = parameter;
    }
}
