/* SEE PAGE 38, 39 IN CocoR Taste example */

// object describing a declared symbol in symboltable, used for lookup of identifiers.
@RequiredArgsConstructor 
public class symbol { 
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

    Parser parser;

    public SymbolTable (Parser parser) {
    this.parser = parser;

    }
}
