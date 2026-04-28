package src.abstract_syntax;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class Cast implements Expr {
    public final Type targetType;
    public final Expr expr;

    public Cast(Type targetType, Expr expr) {
        this.targetType = targetType;
        this.expr = expr;
    }
}