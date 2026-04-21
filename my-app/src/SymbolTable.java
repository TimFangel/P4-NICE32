/* SEE PAGE 38, 39 IN CocoR Taste example */
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
