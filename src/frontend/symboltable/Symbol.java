package frontend.symboltable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Object describing a declared symbol in symboltable, used for lookup of identifiers.
@NoArgsConstructor
@Getter
@Setter
public class Symbol { 
    public String name; // Name of symbol
    public int type; // Type of symbol: int, float, bool
    public Symbol next; // Next symbol in same scope
    public int category; // The category of symbol: variable, constant, function, component, constant, scope
    public Symbol locals; // The local symbols 
    public int level; // 0 = global, 1 = local
}
