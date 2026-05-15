package ir;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.type.Type;
import ir.util.IrOperator;
import lombok.Getter;

@Getter
public final class IrComponent implements IrInstructionInterface {
    private String name; // identifier of component
    private IrValue port;
    private IrInstruction setup; // IrInstruction containing protocol and direction.
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

        // first variable of component, which is written/read to/from.
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

    /**
     * Converts the protocol to an IrValue.
     * 
     * @param pc protocol of component.
     * @return IrValue of the protocol.
     */
    IrValue toIrValue(ProtocolComp pc) {
        return new IrValue(pc.getProtocol().toString(), Type.COMPONENT);
    }

    /**
     * Converts the direction to an IrValue.
     * 
     * @param dc direction of component.
     * @return IrValue of the direction.
     */
    IrValue toIrValue(DirectionComp dc) {
        return new IrValue(dc.getDirection().toString(), Type.COMPONENT);
    }

    void addVariable(IrInstruction ii) {
        variables.add(ii);
    }
}
