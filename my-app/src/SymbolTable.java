/* SEE PAGE 38, 39 IN CocoR Taste example */

import lombok.RequiredArgsConstructor;

// object describing a declared symbol in symboltable, used for lookup of identifiers.
@RequiredArgsConstructor
public class Symbol { 
    public string name; // Name of symbol
    public int type; // Type of symbol: int, float, bool
    public int kind; // The kind of symbol: variable, function, component, constant
    public Object locals; // The local scopes within functions and components
    /* MISSING MORE */

}

public class SymbolTable {
    // types
    
    // object kinds
    let variable = 0;
    let constant = 1;
    let function = 2;
    let component = 3;
    let scope = 4;

    public Object topScope;
    public int currentScopelevel;

    Parser parser;

    public void OpenNewScope() {
        Symbol scopeObj = new Symbol();
        scopeObj.name = "";
        scopeObj.kind = scope;
        scopeObj.next = topScope;
        scopeObj.locals = null;
        topScope = scopeObj; // Topscope is now the new scope
        currentScopelevel++; // 
    }

    public void closeCurrentScope() {
        currentScopelevel--; //
    }

    public SymbolTable(Parser parser) {
    this.parser = parser;

    }
}
