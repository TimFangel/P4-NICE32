package backend.processors;

import java.util.Arrays;
import java.util.List;

import exception.InvalidRegisterException;
import exception.NoValueMatchException;
import exception.NonRegisterArgsException;
import exception.RegisterException;
import exception.UnknownAssArgTypeException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;

public class AssignmentProcessor {
    final String arithScratchReg;

    private IrValue result;
    private IrValue arg1;

    public AssignmentProcessor(String arithScratchReg, IrValue result, IrValue arg1) {
        this.arithScratchReg = arithScratchReg;
        this.result = result;
        this.arg1 = arg1;
    }

    public String handleAssignment() {
        if (result.getType() != Type.A_REG && result.getType() != Type.B_REG && result.getType() != Type.F_REG) {
            throw new RegisterException("Invalid result register type " + result.getType());
        }

        switch (arg1.getType()) {
            case BOOL_T:
                return boolAssignment();
            case INT_T:
                return immediateAssignment();
            case FLOAT_T:
                return floatAssignment();
            case A_REG:
                if (result.getType() != Type.A_REG) {
                    throw new RegisterException("Cannot use register for arithmetic operations " + result);
                }
                return "MOV " + result.getName() + ", " + arg1.getName();
            case B_REG:
                if (result.getType() != Type.B_REG) {
                    throw new RegisterException("Cannot use register for bool operations " + result);
                }
                return "ORB " + result.getName() + ", " + arg1.getName() + ", " + arg1.getName();
            case F_REG:
                if (result.getType() != Type.F_REG) {
                    throw new RegisterException("Cannot use register for float operations " + result);
                }
                return "MOV.S " + result.getName() + ", " + arg1.getName();
            default:
                throw new UnknownAssArgTypeException("Could not generate assignment with args type " + arg1.getType());
        }
    }

    private String immediateAssignment() {
        return immediateAssignment(Integer.parseInt(arg1.getName()), result.getName());
    }

    private String immediateAssignment(int imm, String result) {
        if (!result.startsWith("a")) {
            throw new RegisterException("Cannot use register for arithmetic operations " + result);
        }

        if (imm >= -2048 && imm <= 2047) {
            return "MOVI " + result + ", " + (imm & 0xfff);
        } else if (imm >= -2147483648 && imm <= 2147483647) {
            String str = "";

            // Split number in MSB and LSB by masking
            int immLSB = imm & 0xffff;
            int immMSB = (imm & 0xffff0000) >> 16;

            // Move parts into result implicitly shifted
            str += "CONST16 " + result + ", " + immMSB + "\n";
            str += "CONST16 " + result + ", " + immLSB;

            return str;
        } else {
            throw new IllegalArgumentException(
                    "Number out of bounds " + imm + ", must be between -2147483648 and 2147483647");
        }
    }

    private String boolAssignment() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Could not generate bool assignment for " + result.getName());
        }

        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        switch (arg1.getName()) {
            case "true":
                return setBit;
            case "false":
                return clearBit;

            default:
                throw new UnknownAssArgTypeException(
                        "Could not generate boolean assignment for value " + arg1.getName());
        }

    }

    private String floatAssignment() {
        List<Float> defaultValues = Arrays.asList(0.0f, 1.0f, 2.0f, 0.5f, -1.0f, -2.0f, -0.5f);

        if (result.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Could not generate float assignment for " + result.getName());
        }

        if (defaultValues.contains(Float.parseFloat(arg1.getName()))) {
            float f = Float.parseFloat(arg1.getName());
            String returnStr = "CONST.S " + result.getName() + ", ";

            switch (Float.toString(f)) {
                case "0.0":
                    returnStr += "0";
                    break;
                case "1.0", "-1.0":
                    returnStr += "1";
                    break;
                case "2.0", "-2.0":
                    returnStr += "2";
                    break;
                case "0.5", "-0.5":
                    returnStr += "3";
                    break;
            
                default:
                    throw new NoValueMatchException("Could not convert value to float, got: " + arg1.getName());
            }

            if (f < 0f) {
                returnStr += "\nNEG.S " + result.getName() + ", " + result.getName();
            }

            return returnStr;
        }

        if (result.getName().compareTo(arithScratchReg) == 0) {
            throw new InvalidRegisterException("Register " + arithScratchReg + " must be unused for float assignments");
        }

        // Insert float as bits in A_REG (extended immediate assignment) before
        // transferring it into F_REG
        int bits = Float.floatToIntBits(Float.parseFloat(arg1.getName()));
        String str = immediateAssignment(bits, arithScratchReg) + "\n";
        str += "WFR " + result.getName() + ", " + arithScratchReg;
        return str;
    }
}
