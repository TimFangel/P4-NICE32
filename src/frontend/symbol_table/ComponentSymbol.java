package frontend.symbol_table;

import java.util.HashMap;
import java.util.Map;

import exception.ScopeException;
import frontend.abstract_syntax.type.Type;
import lombok.Getter;

@Getter
public class ComponentSymbol extends Symbol {
    private HashMap<String, Symbol> localScope = null;

    public ComponentSymbol(String name, Type type) {
        super(name, type);
    }

    public void setLocalScope(Map<String, Symbol> localScope) {
        if (localScope instanceof HashMap<String, Symbol> ls) {
            this.localScope = ls;
        } else {
            throw new ScopeException("Could not connect scope to component");
        }
    }
}
