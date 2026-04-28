package src.abstract_syntax;

import java.util.List;

public class BlockStmt implements Stmt {
    public final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        return "Block" + statements;
    }
}