package frontend.symboltable;

import java.util.HashMap;
import java.util.Map;

import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;
import frontend.symboltable.enums.Category;

public class SymbolTable2 {
    public String name;

    // id mappes til binding/symbol
    public Map<String, Symbol> table;

    // id mappes til SymbolTable
    public Map<String, SymbolTable2> scopes;

    // reference to a higher scope
    public SymbolTable2 prev;

    // depth of symbol table, not sure if relevant
    private int depth;

    public SymbolTable2(SymbolTable2 prev) {
        table = new HashMap<String, Symbol>();
        scopes = new HashMap<String, SymbolTable2>();
        this.prev = prev;
    }

    public void put(String name, Symbol b) {
        if (table.get(name) != null) {
            System.out.println(name + "is already declared");
        }
        table.put(name, b);
    }

    public void put(String name, SymbolTable2 table) {
        if (scopes.get(name) != null) {
            System.out.println(name + " is already declared!");
        }
        scopes.put(name, table);
    }

    public Symbol get(String name) throws NameNotFoundException {
        for (SymbolTable2 t = this; t != null; t = t.prev) {
            Symbol found = t.table.get(name);
            if (found != null)
                return found;
        }
        throw new NameNotFoundException(name + "not found");

    }
}
