package src.abstract_syntax;

public class Cast implements Expr {
    public final Type targetType;
    public final Expr expr;

    public Cast(Type targetType, Expr expr) {
        this.targetType = targetType;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return "Cast(" + targetType + ", " + expr + ")";
    }
}