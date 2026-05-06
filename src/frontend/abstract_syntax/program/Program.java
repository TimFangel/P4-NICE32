package frontend.abstract_syntax.program;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.statement.BlockStmt;

import lombok.Getter;
import lombok.ToString;

/* Program Node */
@ToString
@Getter
public class Program extends Node {
    private final BlockStmt setup;
    private final BlockStmt functions;
    private final BlockStmt main;

    public Program(int lineNumber, BlockStmt setup, BlockStmt functions, BlockStmt main) {
        super(lineNumber);
        this.setup = setup;
        this.functions = functions;
        this.main = main;
    }
}
