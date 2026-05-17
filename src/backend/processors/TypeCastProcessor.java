package backend.processors;

import exception.InvalidRegisterException;
import exception.UnrecognizedOperatorException;
import frontend.abstract_syntax.type.Type;
import ir.IrValue;
import ir.util.IrOperator;

public class TypeCastProcessor {
    private IrValue result;
    private IrValue arg1;
    private IrOperator operator;

    public TypeCastProcessor(IrValue result, IrValue arg1, IrOperator operator) {
        this.result = result;
        this.result = arg1;
        this.operator = operator;
    }
    
    public String handleCast() {
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
