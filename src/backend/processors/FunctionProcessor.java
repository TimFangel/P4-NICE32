package backend.processors;

import exception.RegisterException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;

public class FunctionProcessor {
    private IrValue arg1;
    private IrValue arg2;

    // Helper class
    AssignmentProcessor assignmentProcessor;
    
    public FunctionProcessor(String arithScratchReg, IrValue result, IrValue arg1, IrValue arg2) {
        this.arg1 = arg1;
        this.arg2 = arg2;

        // Helper class
        assignmentProcessor = new AssignmentProcessor(arithScratchReg, result, arg1);
    }

    public String handleCall() {
        if (arg2.getType() != Type.FUNCTION) {
            throw new RegisterException("Expected a function but got: " + arg2.getName());
        }

        return "CALL0 .L" + arg2.getName() + "\n" + assignmentProcessor.handleAssignment();
    }

    public String handleDefinition() {
        if (arg1.getType() != Type.INT_T && arg1.getType() != Type.BOOL_T && arg1.getType() != Type.FLOAT_T) {
            throw new RegisterException("Expected a function but got: " + arg1.getName());
        }

        return ".L" + arg1.getName() + ":";
    }
}
