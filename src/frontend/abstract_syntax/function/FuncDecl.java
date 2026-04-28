package frontend.abstract_syntax.function;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Function Declaration */
@ToString
@Getter
public class FuncDecl extends Node implements Func {
    private Type returnType;
    private Var identifier;
    private Type paramType;
    private List<Stmt> statements = new ArrayList<>();

    FuncDecl(int lineNumber, Type returnType, Var identifier, Type paramType) {
        super(lineNumber);
        this.returnType = returnType;
        this.identifier = identifier;
        this.paramType = paramType;
    }

    /**
     * Add statement to function's stmt list.
     * @param stmt statement to add to list.
     */
    void addStmt(Stmt stmt) {
        if(stmt != null) {
            this.statements.add(stmt);
        }
    }
}
