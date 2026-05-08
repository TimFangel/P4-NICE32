package frontend.abstract_syntax.component;

import java.util.ArrayList;
import java.util.List;

import exception.NonMatchingSymbolException;
import frontend.abstract_syntax.statement.Stmt;
import frontend.symboltable.ComponentSymbol;
import frontend.symboltable.Symbol;
import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Decl;
import lombok.Getter;
import lombok.ToString;

/* Component Node */
@ToString
@Getter
public final class Component extends Stmt {
    private String identifier;
    private Expr port;
    private ProtocolComp protocol;
    private Expr interval;
    private DirectionComp direction;
    private List<Decl> variables = new ArrayList<>();
    private ComponentSymbol symbolRef = null;

    public Component(int lineNumber, String identifier, Expr port, ProtocolComp protocol, Expr interval,
            DirectionComp direction, List<Decl> variables) {
        super(lineNumber);
        this.identifier = identifier;
        this.port = port;
        this.protocol = protocol;
        this.interval = interval;
        this.direction = direction;
        this.variables = variables;
    }

    public void setSymbolRef(Symbol symbolRef) {
        if (symbolRef instanceof ComponentSymbol cs) {
            this.symbolRef = cs;
        } else {
            throw new NonMatchingSymbolException("Symbol must be of type: component");
        }
    }
}
