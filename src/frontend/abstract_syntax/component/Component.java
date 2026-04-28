package frontend.abstract_syntax.component;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.IntervalComp;
import frontend.abstract_syntax.component.constants.PortComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.value.Var;

import lombok.Getter;
import lombok.ToString;

/* Component Node */
@ToString
@Getter
public class Component extends Node {
    private Var identifier;
    private PortComp port;
    private ProtocolComp protocol;
    private IntervalComp interval;
    private DirectionComp direction;
    private List<Decl> variables = new ArrayList<>();

    Component(int lineNumber, Var identifier, PortComp port, ProtocolComp protocol, IntervalComp interval, DirectionComp direction) {
        super(lineNumber);
        this. identifier = identifier;
        this.port = port;
        this.protocol = protocol;
        this.interval = interval;
        this.direction = direction;
    }


    // TODO: Write test for addVariable?
    /**
     * Adds a declaration to a component
     * @param decl a declaration to be added.
     */
    public void addVariable(Decl decl) {
        if(decl != null) {
            this.variables.add(decl);
        }
    }
}
