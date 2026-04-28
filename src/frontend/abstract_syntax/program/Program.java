package frontend.abstract_syntax.program;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.app.ast.Node;
import com.mycompany.app.ast.function.Func;
import com.mycompany.app.ast.statement.Stmt;

import lombok.Getter;
import lombok.ToString;

/* Program Node */
@ToString
@Getter
public class Program extends Node {
    private List<Stmt> statements = new ArrayList<>();
    private List<Func> functions = new ArrayList<>();

    Program(int lineNumber) {
        super(lineNumber);
    }

    /**
     * Add statements to program list.
     * @param stmt statement to add.
     */
    public void addStmt(Stmt stmt) {
        this.statements.add(stmt);
    }

    /**
     * Add functions to program list.
     * @param func function to add.
     */
    public void addFunc(Func func) {
        this.functions.add(func);
    }
}

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