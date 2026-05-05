package frontend.symboltable;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;
import frontend.symboltable.enums.Category;

public class Environment {
    // Declares scope and bindings of EnvF
    // Symbol = binding, symboltable = scope
    // SymbolTable uses Symbol
    public SymbolTable symbolTable;

    public Environment(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    // Declares new variable in environment
    // Needs name and type as input parameter
    // Returns a new variable with name, type and category VAR
    // Function newSymbol from symbolTable
    public Symbol varDeclaration(String name, Type type, Symbol symbolEnv) throws NameAlreadyBoundException {
        // Uses findID function from symboltable with parameter name and saves as
        // variable
        Symbol variable = symbolTable.findId(name, symbolEnv);

        // Check if the declared variable with given name already exists
        if (variable.getCategory() == Category.VARIABLE) {
            throw new NameAlreadyBoundException("Variable name already exists");

        }
        // Creates a new variable if the given name and type does not already exist
        return symbolTable.newSymbol(name, Category.VARIABLE, type, symbolEnv);
    }

    // Looks up for specific variable in environment
    // Throws exception if variable is not found
    public Symbol lookupVariable(String name, Symbol symbolEnv) throws NameNotFoundException {
        Symbol variableSymbol = symbolTable.findId(name, symbolEnv);

        if (variableSymbol.getCategory() == Category.VARIABLE) {
            return variableSymbol;
        }

        throw new NameNotFoundException("Variable not found");
    }

    // Declares new function in environment
    // Throws exception if function with given name already exist
    // Need name and type as input
    /**
     * Looks up for function in environment
     *
     * @param name, type, symbolEnv
     * @return function
     * @throws NameNotFoundException if the function is not found
     */
    public Symbol funcDeclaration(String name, Type type, Symbol symbolEnv) throws NameAlreadyBoundException {
        Symbol function = symbolTable.findId(name);
        // Check if the declared function with given name already exists
        if (function.getCategory() == Category.FUNCTION) {
            throw new NameAlreadyBoundException("Function name already exists");

        }
        return symbolTable.newSymbol(name, Category.FUNCTION, type, symbolEnv);
    }

    /**
     * Looks up for function in environment
     *
     * @param name and symbolEnv
     * @return function
     * @throws NameNotFoundException if the function is not found
     */
    public Symbol lookupFunction(String name, Symbol symbolEnv) throws NameNotFoundException {
        Symbol functionSymbol = symbolTable.findId(name, symbolEnv);

        if (functionSymbol.getCategory() == Category.FUNCTION) {
            return functionSymbol;
        }
        throw new NameNotFoundException("Function not found");
    }

    /**
     * Function variable declaration (local scope)
     *
     * @param name, type, symbolEnv
     * @return
     * @throws
     */
    public Symbol funcVarDeclaration(String name, Type type, Symbol symbolEnv) throws NameAlreadyBoundException {
        Symbol funcVarSymbol = symbolTable.findId(name, symbolEnv);

        if (funcVarSymbol.getCategory() == Category.FUNCTION && funcVarSymbol.getCategory() == Category.VARIABLE) {

            throw new NameNotFoundException("Function variable already exist");
        }
        return symbolTable.newSymbol(name, Category.VARIABLE, type, symbolEnv);
    }

    // Declares new component in environment
    // Only needs name as input parameter
    public Symbol compDeclaration(String name, Symbol symbolEnv) throws NameAlreadyBoundException {
        Symbol component = symbolTable.findId(name, symbolEnv);
        if (component.getCategory() == Category.COMPONENT) {
            throw new NameAlreadyBoundException("Component name already exists");

        }
        return symbolTable.newSymbol(name, Category.COMPONENT, symbolEnv);
    }

    // Looks up for component in environment
    public Symbol lookupComponent(String name, Symbol symbolEnv) throws NameNotFoundException {
        Symbol componentSymbol = symbolTable.findId(name, symbolEnv);

        if (componentSymbol.getCategory() == Category.COMPONENT) {
            return componentSymbol;
        }

        throw new NameNotFoundException("Component not found");
    }

    public Symbol constantVarDeclaration(String name, Type type, Category category, Symbol symbolEnv)
            throws NameAlreadyBoundException {
        Symbol constVarSymbol = symbolTable.findId(name, symbolEnv);

        if (constVarSymbol.getCategory() == Category.COMPONENT && constVarSymbol.getCategory() == Category.VARIABLE) {
            throw new NameAlreadyBoundException("Constant variable already exists");
        }

        return symbolTable.newSymbol(name, Category.VARIABLE, type, symbolEnv);
    }

    // public SymbolTable symbolTable;

    // public VariableEnv variableEnv;
    // public FunctionEnv functionEnv;
    // public ComponentEnv componentEnv;

    // public void environment(SymbolTable symbolTable) {
    // this.symbolTable = symbolTable;

    // this.variableEnv = new VariableEnv(symbolTable);
    // this.functionEnv = new FunctionEnv(symbolTable);
    // this.componentEnv = new ComponentEnv(symbolTable);
    // }

}
