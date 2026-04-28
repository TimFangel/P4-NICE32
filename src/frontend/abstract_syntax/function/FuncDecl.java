package frontend.abstract_syntax.function;

import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Ident;
import lombok.Getter;
import lombok.ToString;

/* Function Declaration */
@ToString
@Getter
public class FuncDecl extends Func {
    private Type returnType;
    private Ident identifier;
    private Type paramType;
    private BlockStmt statements;

    FuncDecl(int lineNumber, Type returnType, Ident identifier, Type paramType, BlockStmt statements) {
        super(lineNumber);
        this.returnType = returnType;
        this.identifier = identifier;
        this.paramType = paramType;
        this.statements = statements;
    }
}
