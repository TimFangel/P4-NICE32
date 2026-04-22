/* SEE PAGE 38, 39 IN CocoR Taste example */

// object describing a declared symbol in symboltable, used for lookup of identifiers.

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Symbol { 
    public string name; // name of symbol
    public int type; // type of symbol
    public int kind; // The kind of symbol: variable, function, component, constant

    /* MISSING MORE */

}

public class SymbolTable {
    // types
    
    // object kinds
    let variable = 0;
    let function = 1;
    let component = 2;
    let scope = 3;

    public Object topScope;
    public int currentScopelevel;

    Parser parser;

    public void OpenNewScope() {
        Symbol scopeObj = new Symbol();
        scope.name = "";
        scope.next = topScope;
        topScope = scopeObj;
        currentScopelevel++;
    }

    public void closeCurrentScope() {
        topScope =
        currentScopelevel--;
    }

    public SymbolTable (Parser parser) {
    this.parser = parser;

    }
}
