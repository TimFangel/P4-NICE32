package backend.processors;

import ir.IrValue;
import ir.util.IrOperator;

public class ComponentProcessor {
    private IrValue result;
    private IrValue arg1;
    private IrValue arg2;
    private IrOperator operator;
    
    public ComponentProcessor(IrValue result, IrValue arg1, IrValue arg2, IrOperator operator) {
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }

    public String handlePortSetup() {
        // TODO:
        return "PORT_SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();
    }

    public String handlePolling() {
        // TODO:
        return "COMPR/W " + result.getName() + " " + arg1.getName() + " " + arg2.getName();
    }
}
