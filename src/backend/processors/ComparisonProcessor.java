package backend.processors;

import backend.AssemblyGenerator;
import exception.InvalidOperatorException;
import exception.InvalidRegisterException;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;
import ir.util.IrOperator;

public class ComparisonProcessor {
    private final String boolScratchReg;

    private IrValue result;
    private IrValue arg1;
    private IrValue arg2;
    private IrOperator operator;

    public ComparisonProcessor(String boolScratchReg, IrValue result, IrValue arg1, IrValue arg2, IrOperator operator) {
        this.boolScratchReg = boolScratchReg;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }

    public String handleLogicalComparison() {
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

    public String handleFloatComparisons() {

        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate boolean expression for non-register result");
        }

        if (arg1.getType() != Type.F_REG || arg2.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Cannot generate boolean expression for non-register args");
        }

        // Use idioms to get instruction as a > b = b < a and a >= b = b <= a
        switch (operator) {
            case GT:
                switchArgs();
            case LT:
                return "OLT.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case GEQ:
                switchArgs();
            case LEQ:
                return "OLE.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case EQ:
                return "OEQ.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName();
            case NEQ:
                if (result.getName().compareTo(boolScratchReg) == 0) {
                    throw new InvalidRegisterException(
                            "Register " + boolScratchReg + " must be unused for extended immediate assignments");
                }

                // Calculate Equal and negate result
                StringBuilder str = new StringBuilder();
                str.append("OEQ.S " + result.getName() + ", " + arg1.getName() + ", " + arg2.getName());
                str.append("ORBC " + boolScratchReg + ", " + boolScratchReg + ", " + boolScratchReg + "");
                str.append("XORB " + result.getName() + ", " + result.getName() + ", " + boolScratchReg);
                return str.toString();
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

    }

    public String handleArithmeticComparison() {
        if (result.getType() != Type.B_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic expression for non-register result");
        }

        if (arg1.getType() != Type.A_REG || arg2.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic expression for non-register args");
        }

        final String trueLabel = AssemblyGenerator.newLabel();
        final String falseLabel = AssemblyGenerator.newLabel();
        StringBuilder str = new StringBuilder();

        // Use idioms to get instruction as a > b = b < a and a >= b = b <= a
        switch (operator) {
            case LEQ:
                switchArgs();
            case GEQ:
                str.append("BGE ");
                break;
            case GT:
                switchArgs();
            case LT:
                str.append("BLT ");
                break;
            case EQ:
                str.append("BEQ ");
                break;
            case NEQ:
                str.append("BNE ");
                break;
            default:
                throw new InvalidOperatorException("Could not generate boolean expression for " + operator);
        }

        final String setBit = "ORBC " + result.getName() + ", " + result.getName() + ", " + result.getName();
        final String clearBit = "XORB " + result.getName() + ", " + result.getName() + ", " + result.getName();

        // Generate if else branch which sets (if true) or clears bit (if false)
        str.append(arg1.getName() + "," + arg2.getName() + "," + trueLabel + "\n");
        str.append(clearBit + "\n");
        str.append("J " + falseLabel + "\n");
        str.append(trueLabel + ":\n");
        str.append(setBit + "\n");
        str.append(falseLabel + ":\n");

        return str.toString();
    }

    private void switchArgs() {
        IrValue temp = arg1;
        arg1 = arg2;
        arg2 = temp;
    }
}
