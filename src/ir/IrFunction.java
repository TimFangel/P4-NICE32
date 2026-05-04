package ir;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class IrFunction {
    private String funcName;
    private IrValue parameter;
    private Type retType;
    List<IrInstruction> funcBody = new ArrayList<>(); // local function code

    public IrFunction(String funcName, IrValue parameter, Type retType) {
        this.funcName = funcName;
        this.parameter = parameter;
        this.retType = retType;
    }

    @Override
    public String toString() {
        return "func " + funcName + "(" + parameter + "): " + retType.toString();
    }
}
