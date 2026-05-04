package frontend.symboltabletest.enviromentsTest;

import javax.swing.text.html.HTMLEditorKit.Parser;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.type.Type;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.enums.Category;
import frontend.symboltable.environments.VariableEnv;

public class VariableEnvTest {

    TODO: //Ikke tested endnu 
    @Test
    public void varDeclarationTest() {
        // Arrange. Set up the env and symbol table
        SymbolTable symbolTable = new SymbolTable();
        VariableEnv variableEnv = new VariableEnv(symbolTable); // EnvV uses symbolTable

        // Declares variables named x, y and z with type different types
        variableEnv.varDeclaration("x", Type.INT_T);
        variableEnv.varDeclaration("y", Type.FLOAT_T);
        variableEnv.varDeclaration("z", Type.BOOL_T);

        // Saves lookup for variables as result
        Symbol resultX = variableEnv.lookupVariable("x");
        Symbol resultY = variableEnv.lookupVariable("y");
        Symbol resultZ = variableEnv.lookupVariable("Z");

        // Verify that the category is a variable
        Assertions.assertEquals(Category.VARIABLE, resultX.getCategory());
        Assertions.assertEquals(Category.VARIABLE, resultY.getCategory());
        Assertions.assertEquals(Category.VARIABLE, resultZ.getCategory());
    }
}
