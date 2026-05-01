package frontend.abstract_syntax.expression;

import lombok.Getter;
import lombok.ToString;

/* Function Call Statement */
@ToString
@Getter
public class FuncCall extends Expr {
    private String identifier;
    private Expr parameter;

    public FuncCall(int lineNumber, String identifier, Expr parameter) {
        super(lineNumber);
        this.identifier = identifier;
        this.parameter = parameter;
    }
}
