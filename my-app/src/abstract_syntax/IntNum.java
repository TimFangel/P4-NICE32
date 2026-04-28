package src.abstract_syntax;

public class IntNum implements Expr {
    public final int value;

    public IntNum(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "IntNum(" + value + ")";
    }
}