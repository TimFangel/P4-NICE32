package src.abstract_syntax;

public class Program {
    public final BlockStmt setup;
    public final BlockStmt functions;
    public final BlockStmt main;

    public Program(BlockStmt setup, BlockStmt functions, BlockStmt main) {
        this.setup = setup;
        this.functions = functions;
        this.main = main;
    }

    @Override
    public String toString() {
        return "Program(" +
                "setup=" + setup +
                ", functions=" + functions +
                ", main=" + main +
                ")";
    }
}