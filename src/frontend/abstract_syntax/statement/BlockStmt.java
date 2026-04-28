package frontend.abstract_syntax.statement;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BlockStmt extends Stmt {
    public final List<Stmt> statements;

    public BlockStmt(int lineNumber, List<Stmt> statements) {
        super(lineNumber);
        this.statements = statements;
    }
}