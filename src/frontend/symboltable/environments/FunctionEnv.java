package frontend.symboltable.environments;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.enums.Category;

public class FunctionEnv {
    // Declares scope and bindings of EnvF
    // Symbol = binding, symboltable = scope
    // SymbolTable uses Symbol
    public SymbolTable symbolTable;

    public FunctionEnv(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    // Declares new function in function environment
    // Throws exception if function with given name already exist
    // Need name and type as input
    public Symbol funcDeclaration(String name, Type type) throws NameAlreadyBoundException {
        Symbol function = symbolTable.findId(name);
        // Check if the declared function with given name already exists
        if (function.getCategory() == Category.FUNCTION) {
            throw new NameAlreadyBoundException("Function name already exists");

        }
        return symbolTable.newSymbol(name, Category.FUNCTION, type);
    }

    // Looks up for function in function environment
    // Throws exception if not found
    public Symbol lookupFunction(String name) throws NameNotFoundException {
        Symbol functionSymbol = symbolTable.findId(name);

        if (functionSymbol.getCategory() == Category.FUNCTION) {
            return functionSymbol;
        }
        throw new NameNotFoundException("Function not found");
    }
}
