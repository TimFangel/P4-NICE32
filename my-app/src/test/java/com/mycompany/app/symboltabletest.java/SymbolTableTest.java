// FIX package

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import com.mycompany.app.symboltable.*;;

// Ongoing
public class SymbolTableTest {
    @Test
    public void testNewSymbol() {
        Symbol symbol = new Symbol("x");
        SymbolTable symboltable = new SymbolTable(Parser parser);
        Symbol result = symboltable.newSymbol("x", 0, 0);
        Assert.assertEquals(symbol, result);
    }
}
