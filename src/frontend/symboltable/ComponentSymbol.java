package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class ComponentSymbol extends NewSymbol {
    ComponentSymbol(String name, Type type) {
        super(name, type);
    }
}
