package frontend.symboltabletest;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.type.Type;
import frontend.symboltable.Environment;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.enums.Category;

public class EnviromentTest {

    public Parser parser;

    TODO: // Ikke tested endnu

    @Test
    public void varDeclarationTest() {

   

    // Arrange. Set up the env and symbol table
    SymbolTable symbolTable = new SymbolTable();
    Environment environment = new Environment(symbolTable); // EnvV uses symbolTable
    Symbol symbolEnv = new Symbol();
    symbolEnv.setLocals(symbolEnv);

    // Declares variables named x, y and z with type different types
    environment.varDeclaration("x",Type.INT_T,symbolEnv);environment.varDeclaration("y",Type.FLOAT_T,symbolEnv);environment.varDeclaration("z",Type.BOOL_T,symbolEnv);

    // Saves lookup for variables as result
    Symbol resultX = environment.lookupVariable("x", symbolEnv);
    Symbol resultY = environment.lookupVariable("y", symbolEnv);
    Symbol resultZ = environment.lookupVariable("Z", symbolEnv);

    // Verify that the category is a variable
    Assertions.assertEquals(Category.VARIABLE,resultX.getCategory());
    Assertions.assertEquals(Category.VARIABLE,resultY.getCategory());
    Assertions.assertEquals(Category.VARIABLE,resultZ.getCategory());
}
}
