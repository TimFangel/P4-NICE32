package backend.processors;

import java.util.List;

import exception.InvalidOperatorException;
import exception.InvalidRegisterException;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;
import ir.util.IrOperator;

public class FloatProcessor {
    private final List<String> arithScratchRegs;

    private IrValue result;
    private IrValue arg1;
    private IrValue arg2;
    private IrOperator operator;
    
    public FloatProcessor(List<String> arithScratchRegs, IrValue result, IrValue arg1, IrValue arg2, IrOperator operator) {
        this.arithScratchRegs = arithScratchRegs;
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }

    public String handleExpression() {
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
        if (arithScratchRegs.contains(result.getName()) || arithScratchRegs.contains(arg1.getName())
                || arithScratchRegs.contains(arg2.getName())) {
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

    public String handleNegation() {
        if (result.getType() != Type.F_REG) {
            throw new NonRegisterResultException("Cannot generate float negation for non-register result");
        }

        if (arg1.getType() != Type.F_REG) {
            throw new NonRegisterArgsException("Cannot generate float negation for non-register args");
        }

        return "NEG.S" + " " + arg1.getName() + "," + result.getName();
    }
}
