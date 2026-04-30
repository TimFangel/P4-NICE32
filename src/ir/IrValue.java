package ir;

public class IrValue {
    String name;    // t1
    int type;       // type from symbol table

    public IrValue(String name, int type) {
        this.name = name;
        this.type = type;
    }
}
