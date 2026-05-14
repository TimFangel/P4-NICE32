package backend;

import exception.InvalidRegisterException;
import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;

// TODO: Scratch Register
// A14, A15
// B15

public class InstructionWriter {
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
        if (isRegister(result.getName()))
            result.setType(toRegister(result.getType()));

        if (arg1 == null) {
            return;
        }

        if (isRegister(arg1.getName()))
            arg1.setType(toRegister(arg1.getType()));

        if (arg2 == null) {
            return;
        }

        if (isRegister(arg2.getName()))
            arg2.setType(toRegister(arg2.getType()));
    }

    private boolean isRegister(String name) {
        return name.matches("[a-z]\\d*");
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
                return "if_false " + arg1.getName() + " goto " + result.getName();

            case GOTO:
                return "J ." + result.getName();

            case LABEL:
                return "." + result.getName() + ":";

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
                return "RET " + result.getName();

            case CALL:
                return "CALL " + result.getName() + ", " + arg1.getName();

            case SETUP:
                return "SETUP " + arg1.getName() + " " + arg2.getName() + " " + result.getName();

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
        // TODO: remove t
        if (!result.startsWith("a") && !result.startsWith("t")) {
            throw new RegisterException("Cannot use register for arithmetic operations " + result);
        }

        if (imm >= -2048 && imm <= 2047) {
            return "MOVI " + result + ", " + (imm & 0xfff);
        } else if (imm >= -2147483648 && imm <= 2147483647) {
            if (result.compareTo("a15") == 0) {
                throw new InvalidRegisterException("Register a15 must be unused for extended immediate assignments");
            }

            // Split number in 4x8bit
            int imm0 = imm & 0xff;
            int imm1 = (imm & 0xff00) >> 8;
            int imm2 = (imm & 0xff0000) >> 16;
            int imm3 = (imm & 0xff000000) >> 24;

            // Move parts into result and shift it continuously
            String str = "";
            str += "MOVI " + "a15" + ", " + imm3 + "\n";
            str += "MOVI " + result + ", " + imm2 + "\n";
            str += "ADDMI " + result + ", a15" + "\n"; // result += a15<<8, imm3 imm2
            str += "MOVI " + result + ", " + imm1 + "\n";
            str += "ADDMI " + "a15, " + result + "\n"; // a15 += result<<8, imm3 imm2 imm1
            str += "MOVI " + "a15" + ", " + imm0 + "\n";
            str += "ADDMI " + result + ", a15" + "\n"; // result += a15<<8, imm3 imm2 imm1 imm0

            return str;
        } else {
            throw new IllegalArgumentException(
                    "Number out of bounds " + imm + ", must be between -2147483648 and 2147483647");
        }
    }

    private String boolAssignment() {
        if (result.getType() != Type.B_REG) {
            throw new InvalidRegisterException("Register a15 must be unused for extended immediate assignments");
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
        if (result.getType() != Type.F_REG || arg1.getType() != Type.FLOAT_T) {
            throw new UnknownAssArgTypeException("Could not generate float assignment for " + arg1.getName());
        }

        // Insert float as bits in A_REG (extended immediate assignment) before
        // transferring it into F_REG
        int bits = Float.floatToIntBits(Float.parseFloat(arg1.getName()));
        String str = immediateAssignment(bits, "a14") + "\n";
        str += "WFR " + result.getName() + ", a14";
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
        throw new RuntimeException("Not yet supported");
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
                // Calculate Equal and negate result
                String str = "OEQ.S" + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
                str += "ORBC b15, b15, b15";
                str += "XORB " + result.getName() + ", " + result.getName() + ", b15";
                return str;
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
        String str;

        switch (operator) {
            case GEQ:
                switchArgs();
            case LT:
                str = "BLT ";
                break;
            case LEQ:
                switchArgs();
            case GT:
                str = "BGE ";
                break;
            case EQ:
                str = "BEQ";
                break;
            case NEQ:
                str = "BNE ";
                break;
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        // Generate if else branch which sets or clears bit
        str += arg1.getName() + "," + arg2.getName() + "," + trueLabel + "\n";
        str += clearBit + "\n";
        str += "J " + falseLabel + "\n";
        str += trueLabel + "\n";
        str += setBit + "\n";
        str += falseLabel + "\n";

        return str;
    }

    private String notBool() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate bool expression for non-register result");
        }

        if (arg1.getType() != Type.B_REG) {
            throw new NonRegisterArgsException("Cannot generate bool expression for non-register args");
        }

        String str = "ORBC b15, b15, b15 \n"; // Set bit b15
        str += "XORB " + result.getName() + ", b15, " + arg1.getName(); // Negate with XOR
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
}
