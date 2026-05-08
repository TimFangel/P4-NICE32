package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class VariableSymbol extends NewSymbol {

    public VariableSymbol(String name, Type type) {
        super(name, type);
    }
}
