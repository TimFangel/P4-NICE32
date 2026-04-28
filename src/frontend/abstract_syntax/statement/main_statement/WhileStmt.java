package frontend.abstract_syntax.statement.main_statement;

import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Stmt;

import lombok.Getter;
import lombok.ToString;

/* While Statement */
@ToString
@Getter
public class WhileStmt extends Stmt {
    private Expr condition;
    private BlockStmt  whileBody;
    
    WhileStmt(int lineNumber, Expr condition, BlockStmt  whileBody) {
        super(lineNumber);
        this.condition = condition;
        this.whileBody = whileBody;
    }
}