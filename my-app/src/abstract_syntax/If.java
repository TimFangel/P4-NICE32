package src.abstract_syntax;

public class If implements Stmt {
    public final Expr condition;
    public final BlockStmt thenBranch;
    public final Stmt elseBranch; // null, BlockStmt eller If

    public If(Expr condition, BlockStmt thenBranch, Stmt elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    public String toString() {
        return "If(" + condition + ", then=" + thenBranch + ", else=" + elseBranch + ")";
    }
}