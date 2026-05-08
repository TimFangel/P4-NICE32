package frontend.abstract_syntax.statement.main_statement;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Stmt;
import frontend.symboltable.FunctionSymbol;
import frontend.symboltable.Symbol;
import lombok.Getter;
import lombok.ToString;

/* Return Statement */
@ToString
@Getter
public final class ReturnStmt extends Stmt {
    private Expr exprReturned;
    private FunctionSymbol functionSymbol = null;

    public ReturnStmt(int lineNumber, Expr exprReturned) {
        super(lineNumber);
        this.exprReturned = exprReturned;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof FunctionSymbol fs) {
            this.functionSymbol = fs;
        } else {
            throw new NonMatchingSymbolException("Function symbol must be of type: function");
        }
    }
}
