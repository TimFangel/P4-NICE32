package backend.processors;

import exception.InvalidRegisterException;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;

public class BooleanProcessor {
    private final String boolScratchReg;

    private IrValue result;
    private IrValue arg1;

    public BooleanProcessor(String boolScratchReg, IrValue result, IrValue arg1) {
        this.boolScratchReg = boolScratchReg;
        this.result = result;
        this.arg1 = arg1;
    }

    public String handleNot() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate bool expression for non-register result");
        }

        if (arg1.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate bool expression for non-register args");
        }

        if (result.getName().compareTo(boolScratchReg) == 0) {
            throw new InvalidRegisterException("Register " + boolScratchReg + " must be unused for extended immediate assignments");
        }

        String str = "ORBC " + boolScratchReg + ", " + boolScratchReg + ", " + boolScratchReg + " \n"; // Set bit
        str += "XORB " + result.getName() + ", " + boolScratchReg + ", " + arg1.getName(); // Negate with XOR
        return str;
    }
}
