package ir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.type.Type;
import ir.util.IrOperator;
import lombok.Getter;

@Getter
public final class IrComponent implements IrInstructionInterface {
    private String name;
    private IrValue port;
    private IrInstruction setup;
    private IrValue interval;
    private DirectionComp direction;
    private List<IrInstruction> variables = new ArrayList<>(); // local variables
    private IrInstruction portInterval; // used in liveness.

    public IrComponent(String name, ProtocolComp protocol, DirectionComp direction, IrValue port, IrValue interval) {
        this.name = name;
        this.port = port;
        this.interval = interval;
        this.direction = direction;
        this.setup = new IrInstruction(IrOperator.PORT_SETUP, toIrValue(protocol), toIrValue(direction), port);
    }

    @Override
    public String toString() {
        if (variables.isEmpty()) {
            throw new NoSuchElementException("Could not find any variables in '" + name + "'");
        }

        IrValue startVar = variables.get(0).getResult();

        if (startVar == null) {
            throw new NoSuchElementException("Could not read first variable in '" + name + "'");
        }

        switch (direction.getDirection()) {
            case INPUT:
                return "COMPR " + startVar.getName() + " " + port.getName() + " " + interval.getName();

            case OUTPUT:
                return "COMPW " + startVar.getName() + " " + port.getName() + " " + interval.getName();
            default:
                throw new IllegalArgumentException("Could not create component polling for '" + name + "'");
        }
    }

    IrValue toIrValue(ProtocolComp pc) {
        return new IrValue(pc.getProtocol().toString(), Type.PROTOCOL);
    }

    IrValue toIrValue(DirectionComp dc) {
        return new IrValue(dc.getDirection().toString(), Type.DIRECTION);
    }

    void addVariable(IrInstruction ii) {
        variables.add(ii);
    }
}
