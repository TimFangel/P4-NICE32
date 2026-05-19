package ir;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public final class IrFunction implements IrInstructionInterface {
    private String funcName; // identifier of function.
    private IrValue parameter; // function parameter.
    private Type retType; // return type of function
    private List<IrInstruction> funcBody = new ArrayList<>(); // local function code

    public IrFunction(String funcName, IrValue parameter, Type retType) {
        this.funcName = funcName;
        this.parameter = parameter;
        this.retType = retType;
    }

    @Override
    public String toString() {
        return "func " + funcName + ":";
    }
}
