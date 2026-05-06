package ir;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import lombok.Getter;

@Getter
public class IrComponent {
    private String name;
    private IrValue port;
    private IrInstruction setup;
    private IrValue interval;
    private DirectionComp direction;
    private List<IrInstruction> variables = new ArrayList<>(); // local variables

    public IrComponent(String name, ProtocolComp protocol, DirectionComp direction, IrValue port, IrValue interval) {
        this.name = name;
        this.port = port;
        this.interval = interval;
        this.direction = direction;
        this.setup = new IrInstruction(IrOperator.SETUP, toIrValue(protocol), toIrValue(direction), port);
    }

    @Override
    public String toString() {
        if (variables.isEmpty()) {
            throw new NoSuchElementException("Could not find any variables in '" + name + "'");
        }

        IrValue startVar = variables.get(0).result;

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
        return new IrValue(pc.getProtocol().toString(), null);
    }

    IrValue toIrValue(DirectionComp dc) {
        return new IrValue(dc.getDirection().toString(), null);
    }

    void addVariable(IrInstruction ii) {
        variables.add(ii);
    }
}
