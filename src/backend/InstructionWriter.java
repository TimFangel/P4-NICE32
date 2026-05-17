package backend;

import java.util.Arrays;
import java.util.List;

import exception.InvalidOperatorException;
import exception.InvalidRegisterException;
import exception.NoValueMatchException;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import exception.RegisterException;
import exception.UnknownAssArgTypeException;
import exception.UnknownInstructionException;
import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;

// TODO: Scratch Register
// F9-15
// TODO REMOVE MAGIC NUMBERS


public class InstructionWriter {
    // Registers for temporary values
    static final String arithScratchReg = "a15";
    static final String boolScratchReg = "b15";
    List<String> floatScratchReg = Arrays.asList("f9", "f10", "f11", "f12", "f13", "f14", "f15");

    private IrOperator operator;
    private IrValue arg1;
    private IrValue arg2;
    private IrValue result;

    public InstructionWriter(IrInstruction ii) {
        this.operator = ii.getOperator();
        this.arg1 = ii.getArg1();
        this.arg2 = ii.getArg2();
        this.result = ii.getResult();
        updateTypes();
    }

    private void updateTypes() {
        if (result != null && isRegister(result.getName())) {
            result.setType(toRegister(result.getType()));
        }

        if (arg1 != null && isRegister(arg1.getName())) {
            arg1.setType(toRegister(arg1.getType()));
        }

        if (arg2 != null && isRegister(arg2.getName())) {
            arg2.setType(toRegister(arg2.getType()));
        }

    }

    private boolean isRegister(String name) {
        return name.matches("^[abf]\\d+$");
    }

    private Type toRegister(Type oldType) {
        switch (oldType) {
            case BOOL_T, B_REG:
                return Type.B_REG;
            case INT_T, A_REG:
                return Type.A_REG;
            case FLOAT_T, F_REG:
                return Type.F_REG;

            default:
                throw new RegisterException("Could not find register for " + oldType);
        }
    }

    private void switchArgs() {
        IrValue temp = arg1;
        arg1 = arg2;
        arg2 = temp;
    }

    public String write() {
        switch (operator) {
            case ASS:
                return assignment();

            case ADD, SUB, MUL, DIV, MOD:
                if (result.getType() == Type.A_REG) {
                    return arithExpression();
                } else if (result.getType() == Type.F_REG) {
                    return floatExpression();
                } else {
                    throw new RegisterException("Cannot do arithmetic operation on " + result.getType());
                }

            case AND, OR:
                return logicalComparison();

            case LEQ, LT, GT, GEQ, EQ, NEQ:
                if (result.getType() == Type.A_REG) {
                    return boolExpression();
                } else if (result.getType() == Type.F_REG) {
                    return floatBoolExpression();
                } else {
                    throw new RegisterException("Cannot do comparison operation on " + result.getType());
                }

            case IF_FALSE:
                return ifStatement();

            case GOTO:
                return jump();

            case LABEL:
                return label();

            case NOT:
                return notBool();

            case NEG:
                if (result.getType() == Type.A_REG) {
                    return negateArith();
                } else if (result.getType() == Type.F_REG) {
                    return negateFloat();
                } else {
                    throw new RegisterException("Cannot do arithmetic operation on " + result.getType());
                }

            case INT_TO_FLOAT, FLOAT_TO_INT:
                return typeCast();

            case RET:
                return "RET";

            case CALL:
                return callFunction();

            case PORT_SETUP:
                // TODO:
                return "PORT_SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();

            case COMPR, COMPW:
                // TODO:
                return "COMPR/W " + result.getName() + " " + arg1.getName() + " " + arg2.getName();

            case FUNC_INFO:
                return defineFunction();

            default:
                throw new UnrecognizedOperatorException("Unrecognized Operator: " + operator);
        }
    }

    private String assignment() {
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

    private String arithExpression() {
        String str = "";

        if (result.getType() != Type.A_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        switch (operator) {
            case ADD:
                str += "ADD ";
                break;
            case SUB:
                str += "SUB ";
                break;
            case MUL:
                str += "MULL ";
                break;
            case DIV:
                str += "QUOS ";
                break;
            case MOD:
                str += "REMS ";
                break;
            default:
                throw new InvalidOperatorException("Could not generate arithmetic expression for " + operator);
        }

        str += result.getName() + ", " + arg1.getName() + ", " + arg2.getName();

        return str;
    }

    private String floatExpression() {
        String str = "";

        if (result.getType() != Type.F_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.F_REG || arg2.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        switch (operator) {
            case ADD:
                str += "ADD.S ";
                break;
            case SUB:
                str += "SUB.S ";
                break;
            case MUL:
                str += "MULL.S ";
                break;
            case DIV:
                return divideFloat();
            case MOD:
                throw new InvalidOperatorException("Cannot use MOD in float expressions");
            default:
                throw new InvalidOperatorException("Could not generate float expression for " + operator);
        }

        str += result.getName() + ", " + arg1.getName() + ", " + arg2.getName();

        return str;
    }

    private String divideFloat() {
        if (floatScratchReg.contains(result.getName()) || floatScratchReg.contains(arg1.getName())
                || floatScratchReg.contains(arg2.getName())) {
            throw new InvalidRegisterException("Register f9-15 must be unused for float division");
        }

        return "DIV0.S f9, " + arg2.getName() + "\n" // .............. Divide: q = a/b with recip approximation
                + "NEXP01.S f10, " + arg2.getName() + "\n" // ........ Negative and Narrow fully accurate divisor
                + "CONST.S f11, 1\n" // .............................. Prepare for next instruction
                + "MADDN.S f11, f10, f9\n" // ........................ First error computation
                + "MOV.S f12, f9\n" // ............................... Avoid overwriting
                + "MOV.S f13, " + arg2.getName() + "\n" // ........... Copy of divisor needed later
                + "NEXP01.S f14, " + arg1.getName() + "\n" // ........ Negate and narrow dividend
                + "MADDN.S f12, f11, f9\n" // ........................ Second reciprocal approximation
                + "CONST.S f11, 1\n" // .............................. Prepare for first madd instruction below
                + "CONST.S " + result.getName() + ", 0\n" // ......... Prepare for second madd instruction below
                + "NEG.S f15, f14\n" // .............................. Positive of reduced range dividend
                + "MADDN.S f11, f10, f12\n" // ....................... Second error computation
                + "MADDN.S " + result.getName() + ", f15, f9\n" // ... First Quotient Approximation
                + "MKDADJ.S f13, " + arg1.getName() + "\n" // ........ Make adjustment bits
                + "MADDN.S f12, f11, f12\n" // ....................... Third reciprocal approximation
                + "MADDN.S f15, f10, " + result.getName() + "\n" // .. First Quotient error
                + "CONST.S f11, 1\n" // .............................. Prepare for next instruction
                + "MADDN.S f11, f10, f12\n" // ....................... Third reciprocal error
                + "MADDN.S " + result.getName() + ", f15, f12\n" // .. Second quotient approximation
                + "NEG.S f15, f14\n" // .............................. Positive of reduced range dividend
                + "MADDN.S f12, f11, f12\n" // ....................... Fourth reciprocal approximation
                + "MADDN.S f15, f10, " + result.getName() + "\n" // .. Second Quotient error
                + "ADDEXPM.S " + result.getName() + ", f13\n" // ..... Include adjustment bits
                + "ADDEXP.S f12, f13\n" // ........................... Include adjustment bits
                + "DIVN.S " + result.getName() + ", f15, f12\n"; // .. Third and final quotient is accurate
    }

    private String logicalComparison() {
        if (result.getType() != Type.B_REG || arg1.getType() != Type.B_REG || arg2.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate logical comparison for non-register args or result");
        }

        switch (operator) {
            case AND:
                return "ANDB " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case OR:
                return "ORB " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            default:
                throw new InvalidOperatorException("Could not generate logical comparison for " + operator);
        }
    }

    private String floatBoolExpression() {

        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate boolean expression for non-register result");
        }

        if (arg1.getType() != Type.F_REG || arg2.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Cannot generate boolean expression for non-register args");
        }

        switch (operator) {
            case GEQ:
                switchArgs();
            case LT:
                return "OLT.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case LEQ:
                return "OLE.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case GT:
                switchArgs();
            case EQ:
                return "OEQ.S" + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case NEQ:
                if (result.getName().compareTo(boolScratchReg) == 0) {
                    throw new InvalidRegisterException(
                            "Register " + boolScratchReg + " must be unused for extended immediate assignments");
                }

                // Calculate Equal and negate result
                StringBuilder str = new StringBuilder();
                str.append("OEQ.S" + result.getName() + ", " + arg1.getName() + ", " + arg2.getName());
                str.append("ORBC " + boolScratchReg + ", " + boolScratchReg + ", " + boolScratchReg + "");
                str.append("XORB " + result.getName() + ", " + result.getName() + ", " + boolScratchReg);
                return str.toString();
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

    }

    private String boolExpression() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        final String trueLabel = AssemblyGenerator.newLabel();
        final String falseLabel = AssemblyGenerator.newLabel();
        StringBuilder str = new StringBuilder();

        switch (operator) {
            case GEQ:
                switchArgs();
            case LT:
                str.append("BLT ");
                break;
            case LEQ:
                switchArgs();
            case GT:
                str.append("BGE ");
                break;
            case EQ:
                str.append("BEQ");
                break;
            case NEQ:
                str.append("BNE ");
                break;
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        // Generate if else branch which sets or clears bit
        str.append(arg1.getName() + "," + arg2.getName() + "," + trueLabel + "\n");
        str.append(clearBit + "\n");
        str.append("J " + falseLabel + "\n");
        str.append(trueLabel + "\n");
        str.append(setBit + "\n");
        str.append(falseLabel + "\n");

        return str.toString();
    }

    private String ifStatement() {
        if (arg1.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate if statement with non boolean register argument");
        }

        return "BF " + arg1.getName() + ", ." + result.getName();
    }

    private String jump() {
        if (result.getType() != Type.LABEL) {
            throw new RegisterException("Cannot do jump for non-label:" + result.getType());
        }
        return "J ." + result.getName();
    }

    private String label() {
        if (result.getType() != Type.LABEL) {
            throw new UnknownInstructionException("Expected a label but got: " + result.getType());
        }
        return "." + result.getName() + ":";
    }

    private String notBool() {
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

    private String negateArith() {
        if (result.getType() != Type.A_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic negation for non-register result");
        }

        if (arg1.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic negation for non-register args");
        }

        return "NEG" + " " + arg1.getName() + "," + result.getName();

    }

    private String negateFloat() {
        if (result.getType() != Type.F_REG) {
            throw new NonRegisterResultException("Cannot generate float negation for non-register result");
        }

        if (arg1.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Cannot generate float negation for non-register args");
        }

        return "NEG.S" + " " + arg1.getName() + "," + result.getName();

    }

    private String typeCast() {
        if (operator == IrOperator.INT_TO_FLOAT) {
            if (result.getType() != Type.F_REG || arg1.getType() != Type.A_REG) {
                throw new InvalidRegisterException(
                        "Cannot do int to float cast for registers " + arg1 + " to " + result);
            }

            return "FLOAT.S " + result.getName() + ", " + arg1.getName() + ", 0";

        } else if (operator == IrOperator.FLOAT_TO_INT) {
            if (result.getType() != Type.A_REG || arg1.getType() != Type.F_REG) {
                throw new InvalidRegisterException(
                        "Cannot do float to int cast for registers " + arg1 + " to " + result);
            }

            return "TRUNC.S " + result.getName() + ", " + arg1.getName() + ", 0"; // Round towards 0

        } else {
            throw new UnrecognizedOperatorException("Cannot typecast to " + operator);
        }
    }

    private String callFunction() {
        if (arg2.getType() != Type.FUNCTION) {
            throw new RegisterException("Expected a function but got: " + arg2.getName());
        }

        return "CALL0 .L" + arg2.getName() + "\n" + assignment();
    }

    private String defineFunction() {
        if (arg1.getType() != Type.INT_T && arg1.getType() != Type.BOOL_T && arg1.getType() != Type.FLOAT_T) {
            throw new RegisterException("Expected a function but got: " + arg1.getName());
        }

        return ".L" + arg1.getName() + ":";
    }
}
