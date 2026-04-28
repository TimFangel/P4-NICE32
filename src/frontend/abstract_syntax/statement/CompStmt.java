package frontend.abstract_syntax.statement;

import lombok.Getter;
import lombok.ToString;

/* Composition */
@ToString
@Getter
public class CompStmt extends Stmt { 
    private Stmt statement1;
    private Stmt statement2;

    CompStmt(int lineNumber, Stmt statement1, Stmt statement2) {
        super(lineNumber);
        this.statement1 = statement1;
        this.statement2 = statement2;
    }
}
