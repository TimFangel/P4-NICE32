package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public abstract class Symbol {
    private String name;
    private Type type;
    private String irName;

    protected Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }
}
