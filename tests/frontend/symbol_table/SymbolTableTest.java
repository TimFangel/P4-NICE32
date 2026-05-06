// package frontend.symbol_table;
// // FIX package

// import org.junit.jupiter.api.Test;

// import java.nio.file.Paths;

// import org.junit.jupiter.api.Assertions;
// import org.junit.jupiter.api.BeforeEach;

// import frontend.abstract_syntax.type.Type;
// import frontend.coco.Parser;
// import frontend.symboltable.Symbol;
// import frontend.symboltable.SymbolTable;
// import exception.NameAlreadyBoundException;
// import exception.NameNotFoundException;
// import frontend.symboltable.enums.*;
// import frontend.coco.Scanner;

// class SymbolTableTest {
// Parser parser;
// String codeFile = Paths.get("tests", "resources", "frontend", "symbol_table",
// "code.NICE").toString();

// @BeforeEach
// void generateParser() {
// parser = new Parser(new Scanner(codeFile));
// parser.Parse();
// }

// @Test
// void testNewSymbolReturnsCorrectSymbol() {

// // Expected
// Symbol battery = new Symbol();
// battery.setName("battery");
// battery.setCategory(Category.VARIABLE);
// battery.setType(Type.INT_T);

// // Result
// SymbolTable symboltable = new SymbolTable(parser);
// Symbol result = symboltable.newSymbol("x", Category.VARIABLE, Type.INT_T,
// symboltable.getTopScope());
// Assertions.assertEquals(battery.getName(), result.getName());
// Assertions.assertEquals(battery.getCategory(), result.getCategory());
// Assertions.assertEquals(battery.getType(), result.getType());
// }

// @Test
// void testNewSymbolIsDuplicate() {

// SymbolTable symboltable = new SymbolTable(parser);

// // Create variable
// symboltable.newSymbol("battery", Category.VARIABLE, Type.INT_T,
// symboltable.getTopScope());

// Assertions.assertThrows(NameAlreadyBoundException.class, () -> {
// // Create variable with duplicate name
// symboltable.newSymbol("battery", Category.VARIABLE, Type.FLOAT_T,
// symboltable.getTopScope());
// }, "This error was expected");
// }

// @Test
// void testFindIdReturnsCorrectSymbol() {
// // Expected
// Symbol counter = new Symbol();
// counter.setName("counter");
// counter.setCategory(Category.COMPONENT);

// // Result
// SymbolTable symboltable = new SymbolTable(parser);
// symboltable.newSymbol("counter", Category.COMPONENT,
// symboltable.getTopScope());
// Symbol result = symboltable.findId("counter", symboltable.getTopScope());

// Assertions.assertEquals(result, counter);
// }

// @Test
// void testFindIdNotDeclared() {

// SymbolTable symboltable = new SymbolTable(parser);

// // New function symbol
// symboltable.newSymbol("sound", Category.VARIABLE, Type.FLOAT_T,
// symboltable.getTopScope());

// Assertions.assertThrows(NameNotFoundException.class, () -> {

// // Look-up of undeclared variable.
// symboltable.findId("switch", symboltable.getTopScope());

// }, "This error was expected");

// }

// }
