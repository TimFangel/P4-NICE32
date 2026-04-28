package src.abstract_syntax;

public class BinaryOp implements Expr {
    public final Expr left;
    public final Op op;
    public final Expr right;

    public BinaryOp(Expr left, Op op, Expr right) {
        this.left = left;
        this.op = op;
        this.right = right;
    }

    @Override
    public String toString() {
        return "BinaryOp(" + left + ", " + op + ", " + right + ")";
    }
}