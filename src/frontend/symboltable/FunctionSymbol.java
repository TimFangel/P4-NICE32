package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class FunctionSymbol extends Symbol {
    private Type parameterType;

    FunctionSymbol(String name, Type type, Type parameterType) {
        super(name, type);
        this.parameterType = parameterType;
    }
}
