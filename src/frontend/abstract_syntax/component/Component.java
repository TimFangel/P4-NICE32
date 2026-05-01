package frontend.abstract_syntax.component;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.value.Ident;
import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.statement.Decl;
import lombok.Getter;
import lombok.ToString;

/* Component Node */
@ToString
@Getter
public class Component extends Stmt {
    private Ident identifier;
    private Expr port;
    private ProtocolComp protocol;
    private Expr interval;
    private DirectionComp direction;
    private List<Decl> variables = new ArrayList<>();

    public Component(int lineNumber, Ident identifier, Expr port, ProtocolComp protocol, Expr interval,
            DirectionComp direction, List<Decl> variables) {
        super(lineNumber);
        this.identifier = identifier;
        this.port = port;
        this.protocol = protocol;
        this.interval = interval;
        this.direction = direction;
        this.variables = variables;
    }
}
