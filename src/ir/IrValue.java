package ir;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class IrValue {
    private String name;    // t1, 2, 2.5, true
    private Type type;       // type from symbol table. -1 = label, -2 = goto

    public IrValue(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
