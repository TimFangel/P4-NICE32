package backend.processors;

import exception.InvalidOperatorException;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;
import ir.util.IrOperator;

public class ArithmeticProcessor {
    private IrValue result;
    private IrValue arg1;
    private IrValue arg2;
    private IrOperator operator;
    
    public ArithmeticProcessor(IrValue result, IrValue arg1, IrValue arg2, IrOperator operator) {
        this.result = result;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.operator = operator;
    }

    public String handleExpression() {
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

    public String handleNegation() {
        if (result.getType() != Type.A_REG) {
            throw new NonRegisterResultException("Cannot generate arithmetic negation for non-register result");
        }

        if (arg1.getType() != Type.A_REG) {
            throw new NonRegisterArgsException("Cannot generate arithmetic negation for non-register args");
        }

        return "NEG" + " " + arg1.getName() + "," + result.getName();

    }
}
