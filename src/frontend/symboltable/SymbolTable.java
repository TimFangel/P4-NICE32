package frontend.symboltable;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;

public class SymbolTable {
    Deque<HashMap<String, Symbol>> stack = new LinkedList<>();

    public SymbolTable() {
        enterScope(); // Creates root scope
    }

    // Creates and enter new sub scope
    public void enterScope() {
        HashMap<String, Symbol> newScope = new HashMap<>();
        stack.addFirst(newScope);
    }

    // Deletes current scope and returns to super scope
    public void exitScope() {
        stack.removeFirst();
    }

    // Create new variable symbol and add it to current scope
    public VariableSymbol newVariableSymbol(String name, Type type) {
        VariableSymbol symbol = new VariableSymbol(name, type);
        addSymbol(symbol);
        return symbol;
    }

    // Create new function symbol and add it to current scope
    public FunctionSymbol newFunctionSymbol(String name, Type type) {
        FunctionSymbol symbol = new FunctionSymbol(name, type);
        addSymbol(symbol);
        return symbol;
    }

    // Create new component symbol and add it to current scope
    public ComponentSymbol newComponentSymbol(String name, Type type) {
        ComponentSymbol symbol = new ComponentSymbol(name, type);
        addSymbol(symbol);
        return symbol;
    }

    // lookup symbol
    public Symbol lookup(String name) {
        for (HashMap<String, Symbol> scope : stack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }

        throw new NameNotFoundException("Could not find symbol name '" + name + "'");
    }

    // Add to current scope
    void addSymbol(Symbol symbol) {
        HashMap<String, Symbol> scope = stack.getFirst();

        if (scope.containsKey(symbol.getName())) {
            throw new NameAlreadyBoundException("Symbol " + symbol.getName() + " already exists");
        }

        scope.put(symbol.getName(), symbol);
    }

    public boolean symbolExistsLocal(Symbol symbol) {
        return stack.getFirst().containsKey(symbol.getName());
    }

    public Map<String, Symbol> getCurrentScope() {
        return stack.getFirst();
    }
}