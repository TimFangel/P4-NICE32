// package frontend.symboltable.environments;

// import exception.NameAlreadyBoundException;
// import exception.NameNotFoundException;
// import frontend.abstract_syntax.type.Type;
// import frontend.symboltable.Symbol;
// import frontend.symboltable.SymbolTable;
// import frontend.symboltable.enums.Category;

// // Connects category VARIABLE with variable environment
// public class VariableEnv {
// // Declares scopes and binding for EnvV
// // Symbol = binding, symboltable = scope
// // SymbolTable uses Symbol
// public SymbolTable symbolTable;

// public VariableEnv(SymbolTable symbolTable) {
// this.symbolTable = symbolTable;
// }

// // Declares new variable in variable environment
// // Needs name and type as input parameter
// // Returns a new variable with name, type and category VAR
// // Function newSymbol from symbolTable
// public Symbol varDeclaration(String name, Type type) throws
// NameAlreadyBoundException {
// // Uses findID function from symboltable with parameter name and saves as
// // variable
// Symbol variable = symbolTable.findId(name);

// // Check if the declared variable with given name already exists
// if (variable.getCategory() == Category.VARIABLE) {
// throw new NameAlreadyBoundException("Variable name already exists");

// }
// // Creates a new variable if the given name and type does not already exist
// return symbolTable.newSymbol(name, Category.VARIABLE, type);
// }

// // Looks up for specific variable in variable environment
// // Throws exception if variable is not found
// public Symbol lookupVariable(String name) throws NameNotFoundException {
// Symbol variableSymbol = symbolTable.findId(name);

// if (variableSymbol.getCategory() == Category.VARIABLE) {
// return variableSymbol;
// }

// throw new NameNotFoundException("Variable not found");
// }
// }
