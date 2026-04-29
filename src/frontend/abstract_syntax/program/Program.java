package frontend.abstract_syntax.program;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.statement.BlockStmt;

import lombok.Getter;
import lombok.ToString;

/* Program Node */
@ToString
@Getter
public class Program extends Node {
    public final BlockStmt setup;
    public final BlockStmt functions;
    public final BlockStmt main;

    public Program(int lineNumber, BlockStmt setup, BlockStmt functions, BlockStmt main) {
        super(lineNumber);
        this.setup = setup;
        this.functions = functions;
        this.main = main;
    }
}
