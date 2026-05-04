package ir;

import java.util.ArrayList;
import java.util.List;

import frontend.abstract_syntax.type.Type;

public class IrFunction {
    private String funcName;
    private IrValue parameter;
    private Type retType;
    List<IrInstruction> code = new ArrayList<>(); // local function code

    public IrFunction(String funcName, IrValue parameter, Type retType) {
        this.funcName = funcName;
        this.parameter = parameter;
        this.retType = retType;
    }

}
