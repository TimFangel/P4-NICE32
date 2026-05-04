package frontend.symboltable.environments;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.enums.Category;

public class ComponentEnv {
    // Declares scopes and binding for EnvC
    // Symbol = binding, symboltable = scope
    // SymbolTable uses Symbol
    public SymbolTable symbolTable;

    public ComponentEnv(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    // Declares new component in component environment
    // Only needs name as input parameter
    public Symbol CompDeclaration(String name) throws NameAlreadyBoundException {
        Symbol component = symbolTable.findId(name);
        if (component.getCategory() == Category.COMPONENT) {
            throw new NameAlreadyBoundException("Component name already exists");

        }

        return symbolTable.newSymbol(name, Category.COMPONENT);
    }

    // Looks up for component in component environment
    public Symbol lookupSymbol(String name) throws NameNotFoundException {
        Symbol componentSymbol = symbolTable.findId(name);

        if (componentSymbol.getCategory() == Category.COMPONENT) {
            return componentSymbol;
        }

        throw new NameNotFoundException("Component not found");
    }

}
