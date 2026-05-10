package frontend.symbol_table;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public abstract class Symbol {
    private String name;
    private Type type;

    protected Symbol(String name, Type type) {
        this.name = name;
        this.type = type;
    }
}
