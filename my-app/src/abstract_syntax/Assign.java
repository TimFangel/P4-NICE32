package src.abstract_syntax;

public class Assign implements Stmt {
    public final String name;
    public final Expr value;

    public Assign(String name, Expr value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Assign(" + name + " = " + value + ")";
    }
}