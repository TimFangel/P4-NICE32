package src.abstract_syntax;

public class Decl implements Stmt {
    public final Type type;
    public final String name;
    public final Expr value;

    public Decl(Type type, String name, Expr value) {
        this.type = type;
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        return "Decl(" + type + " " + name + " = " + value + ")";
    }
}