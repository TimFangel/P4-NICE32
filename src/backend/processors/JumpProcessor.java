package backend.processors;

import exception.RegisterException;
import exception.UnknownInstructionException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;

public class JumpProcessor {
    private IrValue result;

    public JumpProcessor(IrValue result) {
        this.result = result;
    }

    public String handleJump() {
        if (result.getType() != Type.LABEL) {
            throw new RegisterException("Cannot do jump for non-label:" + result.getType());
        }
        return "J ." + result.getName();
    }

    public String handleLabel() {
        if (result.getType() != Type.LABEL) {
            throw new UnknownInstructionException("Expected a label but got: " + result.getType());
        }
        return "." + result.getName() + ":";
    }

    public String handleReturn() {
        return "RET";
    }
}
