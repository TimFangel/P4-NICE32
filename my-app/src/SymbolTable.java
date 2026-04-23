/* SEE PAGE 38, 39 IN CocoR Taste example */

import lombok.RequiredArgsConstructor;

// Object describing a declared symbol in symboltable, used for lookup of identifiers.
@RequiredArgsConstructor
public class Symbol { 
    public string name; // Name of symbol
    public int type; // Type of symbol: int, float, bool
    public Symbol next; // Next symbol in same scope
    public int kind; // The kind of symbol: variable, function, component, constant, scope
    public Symbol locals; // The local symbols 
    public int level; // O = global, 1 = local
}

public class SymbolTable {
    // types
    final int integer = 1;


    // object kinds
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
        scopeObj.kind = scope; 
        scopeObj.next = topScope; 
        scopeObj.locals = null; // New scope has no locals initially
        scopeObj.nextAdr = 0;  
        topScope = scopeObj; // Topscope is now the new scope
        currentScopelevel++; 
    }

    public void closeCurrentScope() {
        topScope = topScope.next;
        currentScopelevel--; 
    }

    // Create new symbol in current scope. 
    public Symbol newSymbol(string name, int kind, int type) {
        Symbol topScopeLocals = new Symbol(); // Object holding local symbols in current scope
        Symbol last = new Symbol();
        Symbol symbol = new Symbol();
        symbol.name = name;
        symbol.kind = kind;
        symbol.type = type;
        symbol.level = currentScopelevel;
        topScopeLocals = topScope.locals;
        last = null; 
        /* Handle instance when a new Id of symbol in a scope is already declared in the scope. (Duplicate Id) 
         * The loop is only executed while we have local symbols, otherwise the symbol will be the first therefore
         * not need checking.
         * It iterates through all locals and check if their Id is different from the new Id of the symbol. 
        */
        while (topScopeLocals != null) {
            if (topScopeLocals.name == name) {return 0;} 
            last = topScopeLocals;
            topScopeLocals = topScopeLocals.next();
        }
        /* Assign the new symbol to the locals in topscope */
        if (last = null) {
            topscope.locals = symbol; 
        } else {
            last.next = symbol; // Assign the new symbol to the last object as the next (Most recent)
        }
        return symbol;
    }

    // Maybe make lombok take care of this constructor?
    public SymbolTable(Parser parser) {
        this.parser = parser;
        topScope = null;
        currentScopelevel = -1; 
    }
}
