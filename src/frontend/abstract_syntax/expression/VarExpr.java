package frontend.abstract_syntax.expression;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VarExpr extends Expr {
    private final String name;

    public VarExpr(int lineNumber, String name) {
        super(lineNumber);
        this.name = name;
    }
}