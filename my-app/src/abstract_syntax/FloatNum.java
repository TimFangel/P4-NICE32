package src.abstract_syntax;

public class FloatNum implements Expr {
    public final float value;

    public FloatNum(float value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "FloatNum(" + value + ")";
    }
}