package frontend.abstract_syntax.statement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BlockStmt extends Stmt {
    private final List<Stmt> statements;

    public BlockStmt(int lineNumber, List<Stmt> statements) {
        super(lineNumber);
        this.statements = statements;
    }

    public BlockStmt(int lineNumber, Stmt statement) {
        super(lineNumber);
        
        this.statements = new ArrayList<>(Arrays.asList(statement));
    }
}