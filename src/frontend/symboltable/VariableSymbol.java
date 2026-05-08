package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class VariableSymbol extends Symbol {

    public VariableSymbol(String name, Type type) {
        super(name, type);
    }
}
