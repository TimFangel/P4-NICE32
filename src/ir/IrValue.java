package ir;

import lombok.Getter;

@Getter
public class IrValue {
    private String name;    // t1
    private int type;       // type from symbol table. -1 = label, -2 = goto

    public IrValue(String name, int type) {
        this.name = name;
        this.type = type;
    }
}
