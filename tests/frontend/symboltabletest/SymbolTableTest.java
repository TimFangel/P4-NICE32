// FIX package

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import frontend.abstract_syntax.type.Type;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.symboltable.enums.*;;

public class SymbolTableTest {

    @Test
    public void testNewSymbolReturnsCorrectSymbol() {
        
        // Expected
        Symbol battery = new Symbol();
        battery.setName("battery");
        battery.setCategory(Category.VARIABLE);
        battery.setType(Type.INT_T);

        // Result 
        SymbolTable symboltable = new SymbolTable(Parser parser);
        Symbol result = symboltable.newSymbol("x", Category.VARIABLE, Type.INT_T);
        Assertions.assertEquals(battery.getName(), result.getName());
        Assertions.assertEquals(battery.getCategory(), result.getCategory());
        Assertions.assertEquals(battery.getType(), result.getType());
    }

    @Test
    public void testNewSymbolIsDuplicate() {

        SymbolTable symboltable = new SymbolTable(Parser parser);

        Assertions.assertThrows(NameAlreadyBoundException.class, () -> {

            // Create variable 
            symboltable.newSymbol("battery", Category.VARIABLE, Type.INT_T);

            // Create varaible with duplicate name
            symboltable.newSymbol("battery", Category.VARIABLE, Type.FLOAT_T);
            }, "This error was expected");
        }

    public void testFindIdReturnsCorrectSymbol() {
        
        // Expected
        Symbol counter = new Symbol();
        counter.setName("counter");
        counter.setCategory(Category.COMPONENT);

        // Result
        SymbolTable symboltable = new SymbolTable(Parser parser);
        symboltable.newSymbol("counter", Category.COMPONENT);
        Symbol result = symboltable.findId("counter");

        Assertions.assertEquals(result, counter);
    }

    @Test 
    public void testFindIdNotDeclared() {

        SymbolTable symboltable = new SymbolTable(Parser parser);

        Assertions.assertThrows(NameNotFoundException.class, () -> {

            // New function symbol
            symboltable.newSymbol("sound", Category.VARIABLE, Type.FLOAT_T);
            
            // Look-up of undeclared variable.  
            symboltable.findId("switch");

            }, "This error was expected");

    }

}
