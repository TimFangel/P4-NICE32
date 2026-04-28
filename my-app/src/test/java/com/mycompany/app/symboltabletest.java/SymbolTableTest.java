// FIX package

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.mycompany.app.symboltable.*;;

// Ongoing
public class SymbolTableTest {
    @Test
    public void testNewSymbol() {
        Symbol symbol = new Symbol();
        symbol.setName("x");
        symbol.setCategory(1);
        symbol.setType(3);
        SymbolTable symboltable = new SymbolTable(Parser parser);
        Symbol result = symboltable.newSymbol("x", 1, 3);
        Assertions.assertEquals(symbol, result);
    }
}
