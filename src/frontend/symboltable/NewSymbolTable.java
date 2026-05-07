package frontend.symboltable;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;

public class NewSymbolTable {
    Deque<HashMap<String, NewSymbol>> stack = new LinkedList<>();

    public NewSymbolTable() {
        enterScope(); // Creates root scope
    }

    // Creates and enter new sub scope
    public void enterScope() {
        HashMap<String, NewSymbol> newScope = new HashMap<>();
        stack.addFirst(newScope);
    }

    // Deletes current scope and returns to super scope
    public void exitScope() {
        stack.removeFirst();
    }

    // Create new variable symbol and add it to current scope
    public NewSymbol newVariableSymbol(String name, Type type) {
        NewSymbol symbol = new VariableSymbol(name, type);
        addSymbol(symbol);
        return symbol;
    }

    public NewSymbol lookup(String name) {
        for (HashMap<String, NewSymbol> scope : stack) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }

        throw new NameNotFoundException("Could not find symbol name '" + name + "'");
    }

    // add to current scope
    void addSymbol(NewSymbol symbol) {
        HashMap<String, NewSymbol> scope = stack.getFirst();

        if (scope.containsKey(symbol.getName())) {
            throw new NameAlreadyBoundException("Symbol " + symbol.getName() + " already exists");
        }

        scope.put(symbol.getName(), symbol);
    }
}