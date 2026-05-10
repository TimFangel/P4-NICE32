package frontend.symbol_table;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class VariableSymbol extends Symbol {
    private String irName;

    public VariableSymbol(String name, Type type) {
        super(name, type);
    }

    public void setIrName(String irName) {
        this.irName = irName;
    }
}
