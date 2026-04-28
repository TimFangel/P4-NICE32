package com.mycompany.app.ast.component;

import java.util.ArrayList;
import java.util.List;

import com.mycompany.app.ast.Node;
import com.mycompany.app.ast.component.constants.DirectionComp;
import com.mycompany.app.ast.component.constants.IntervalComp;
import com.mycompany.app.ast.component.constants.PortComp;
import com.mycompany.app.ast.component.constants.ProtocolComp;
import com.mycompany.app.ast.statement.Decl;
import com.mycompany.app.ast.value.Var;

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
