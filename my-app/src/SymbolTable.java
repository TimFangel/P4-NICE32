/* SEE PAGE 38, 39 IN CocoR Taste example */
import lombok.RequiredArgsConstructor;

// Object describing a declared symbol in symboltable, used for lookup of identifiers.
@RequiredArgsConstructor
public class Symbol { 
    public @Getter @Setter string name; // Name of symbol
    public @Getter @Setter int type; // Type of symbol: int, float, bool
    public @Getter @Setter Symbol next; // Next symbol in same scope
    public @Getter @Setter int category; // The category of symbol: variable, constant, function, component, constant, scope
    public @Getter @Setter Symbol locals; // The local symbols 
    public @Getter @Setter int level; // O = global, 1 = local
}

public class SymbolTable {
    // Types
    final int integer = 0;
    final int floatNumber = 1;
    final int bool = 2;
    final int port = 3;
    final int interval = 4;
    final int direction = 5;
    final int procedure = 6;

    // Symbol kinds
    final int variable = 0;
    final int constant = 1;
    final int function = 2;
    final int component = 3;
    final int scope = 4;

    public Symbol topScope; // Current scope 
    public int currentScopelevel;

    private Parser parser;

    public void OpenNewScope() {
        Symbol scopeObj = new Symbol();
        scopeObj.name = ""; 
        scopeObj.category = scope; 
        scopeObj.next = topScope; 
        scopeObj.locals = null; // New scope has no locals initially 
        topScope = scopeObj; // Topscope is now the new scope
        currentScopelevel++; 
    }

    public void closeCurrentScope() {
        topScope = topScope.next;
        currentScopelevel--; 
    }

    // Create new symbol in current scope - compile time check
    public Symbol newSymbol(string name, int category, int type) {
        Symbol topScopeLocals = new Symbol(); 
        Symbol last = new Symbol();
        Symbol symbol = new Symbol();
        symbol.name = name;
        symbol.category = category;
        symbol.type = type;
        symbol.level = currentScopelevel;
        topScopeLocals = topScope.locals; // Object holding local symbols in current scope
        last = null; 
        /* Handle instance when a new Id of symbol in a scope is already declared in the scope. (Duplicate Id) 
         * The loop is only executed while we have local symbols, otherwise the symbol will be the first therefore
         * not need checking.
         * It iterates through all locals and check if their Id is different from the new Id of the symbol. 
        */
        while (topScopeLocals != null) {
            if (topScopeLocals.name == name) {system.out.println("duplicate Id"); return 0;} 
            last = topScopeLocals;
            topScopeLocals = topScopeLocals.next();
        }
        /* Assign the new symbol to the locals in topscope (if no locals were found) */
        if (last == null) {
            topscope.locals = symbol; 
        } else {
            last.next = symbol; // Assign the new symbol as the next member (Most recent - if locals were found)
        }
        return symbol;
    }

    /* Use this function when:
     * 1. Calling a function
     * 2. Accessing a variable in component
     * 3. Control statements
     * I think it is run-time check. 
    */
    public Symbol findId(string name) { // Search for a symbol and return the symbol
        Symbol symbol = new Symbol();
        Symbol scope = new Symbol();
        scope = topScope;
        while (scope != null) { // Iterate through all scopes
            symbol = scope.locals;

            while (symbol != null) { // Iterate through all names in that scope
                if (symbol.name == name) { 
                    return symbol;
                }
                symbol = symbol.next;
            }
        scope = scope.next;
        }
        System.out.println(name + "name is not declared");
    }
    
    // Maybe make lombok take care of this constructor?
    public SymbolTable(Parser parser) {
        this.parser = parser;
        topScope = null;
        currentScopelevel = -1; 
    }
}
