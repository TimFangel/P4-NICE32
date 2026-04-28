package src.abstract_syntax;

public class Bool implements Expr {
    public final boolean value;

    public Bool(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Bool(" + value + ")";
    }
}