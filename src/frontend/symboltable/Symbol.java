package frontend.symboltable;

import frontend.abstract_syntax.type.Type;
import frontend.symboltable.enums.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Object describing a declared symbol in symboltable, used for lookup of identifiers.
@NoArgsConstructor
@Getter
@Setter
public class Symbol {
    public String name;
    public Type type;
    public Symbol next; // Next symbol in same scope
    public Category category; // The category of symbol: variable, constant, function, component, scope
    public Symbol locals; // The local symbols
    public int level; // 0 = global, 1 = local
    public Symbol symbolEnv;

    public Symbol(String name, Type type, Category category) {
        this.name = name;
        this.type = type;
        this.category = category;
    }

}