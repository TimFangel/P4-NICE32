package frontend.abstract_syntax.expression;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public final class Cast extends Expr {
    public final Type targetType;
    public final Expr expr;

    public Cast(int lineNumber, Type targetType, Expr expr) {
        super(lineNumber);
        this.targetType = targetType;
        this.expr = expr;
    }
}