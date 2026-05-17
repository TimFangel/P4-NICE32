package backend.processors;

import exception.NonRegisterArgsException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;

public class IfStatementProcessor {
    private IrValue result;
    private IrValue arg1;

    public IfStatementProcessor(IrValue result, IrValue arg1) {
        this.result = result;
        this.result = arg1;
    }
    
    public String handleIf() {
        if (arg1.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate if statement with non boolean register argument");
        }

        return "BF " + arg1.getName() + ", ." + result.getName();
    }
}