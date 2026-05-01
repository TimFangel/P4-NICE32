// FIX ackage

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;

public class SymbolTableTest {

    @Test
    public void testNewSymbolReturnsCorrectSymbol() {
        
        // Expected
        Symbol battery = new Symbol();
        battery.setName("battery");
        battery.setCategory(1);
        battery.setType(1);

        // Result 
        SymbolTable symboltable = new SymbolTable(Parser parser);
        Symbol result = symboltable.newSymbol("x", 1, 1);
        Assertions.assertEquals(battery.getName(), result.getName());
        Assertions.assertEquals(battery.getCategory(), result.getCategory());
        Assertions.assertEquals(battery.getType(), result.getType());
    }

    @Test
    public void testNewSymbolIsDuplicate() {

        SymbolTable symboltable = new SymbolTable(Parser parser);

        Assertions.assertThrows(NameAlreadyBoundException.class, () -> {

            // Create variable 
            symboltable.newSymbol("battery", 0, 0);

            // Create varaible with duplicate name
            symboltable.newSymbol("battery", 0, 1);
            }, "This error was expected");
        }
    

    public void testFindIdReturnsCorrectSymbol() {
        
        // Expected
        Symbol counter = new Symbol();
        counter.setName("counter");
        counter.setCategory(0);
        counter.setType(0);

        // Result
        SymbolTable symboltable = new SymbolTable(Parser parser);
        symboltable.newSymbol("counter", 0, 0);
        Symbol result = symboltable.findId("counter");

        Assertions.assertEquals(result, counter);
    }

    @Test 
    public void testFindIdNotDeclared() {

        SymbolTable symboltable = new SymbolTable(Parser parser);

        Assertions.assertThrows(NameNotFoundException.class, () -> {

            // Look-up of undeclared variable.  
            symboltable.findId("something");

            }, "This error was expected");

    }

}
 

