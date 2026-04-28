package frontend.abstract_syntax.statement;

import java.util.List;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class BlockStmt implements Stmt {
    public final List<Stmt> statements;

    public BlockStmt(List<Stmt> statements) {
        this.statements = statements;
    }
}