package frontend.symboltable;

import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import frontend.abstract_syntax.type.Type;
import frontend.coco.Parser;
import frontend.symboltable.enums.Category;

public class SymbolTable {
    public Symbol topScope; // Current scope
    public int currentScopelevel;

    // Missing import parser class.
    private Parser parser;

    public void OpenNewScope() {
        Symbol scopeObj = new Symbol();
        scopeObj.setName("");
        scopeObj.setCategory(Category.SCOPE);
        scopeObj.setNext(topScope);
        scopeObj.setLocals(null); // New scope has no locals initially
        topScope = scopeObj; // Topscope is now the new scope
        currentScopelevel++;
    }

    public void closeCurrentScope() {
        if (topScope == null) {
            throw new NullPointerException();
        }
        topScope = topScope.next;
        currentScopelevel--;
    }

    /* Create new component symbol in current scope
     * Compile time check 
     * Component has no type therefore two newSymbol methods*/
    public Symbol newSymbol(String name, Category category) throws NameAlreadyBoundException {
        Symbol topScopeLocals;
        Symbol last;
        Symbol symbol = new Symbol();
        symbol.setName(name);
        symbol.setCategory(category);
        symbol.setLevel(currentScopelevel);
        topScopeLocals = topScope.locals; // Object holding local symbols in current scope
        last = null;
        /*
         * Handle instance when a new Id of symbol in a scope is already declared in the
         * scope. (Duplicate Id)
         * The loop is only executed while we have local symbols, otherwise the symbol
         * will be the first therefore
         * not need checking.
         * It iterates through all locals and check if their Id is different from the
         * new Id of the symbol.
         */
        while (topScopeLocals != null) {
            if (topScopeLocals.getName().equals(name)) {
                throw new NameAlreadyBoundException("Duplicate: Name already exists!");
            }
            last = topScopeLocals;
            topScopeLocals = topScopeLocals.next;
        }
        /* Assign the new symbol to the locals in topscope (if no locals were found) */
        if (last == null) {
            topScope.locals = symbol;
        } else {
            last.next = symbol; // Assign the new symbol as the next member (Most recent - if locals were found)
        }
        return symbol;
    }

    // Create new symbol in current scope - compile time check (All symbols except component)
    public Symbol newSymbol(String name, Category category, Type type) throws NameAlreadyBoundException {
        Symbol topScopeLocals;
        Symbol last;
        Symbol symbol = new Symbol();
        symbol.setName(name);
        symbol.setCategory(category);
        symbol.setType(type);
        symbol.setLevel(currentScopelevel);
        topScopeLocals = topScope.locals; // Object holding local symbols in current scope
        last = null;
        /*
         * Handle instance when a new Id of symbol in a scope is already declared in the
         * scope. (Duplicate Id)
         * The loop is only executed while we have local symbols, otherwise the symbol
         * will be the first therefore
         * not need checking.
         * It iterates through all locals and check if their Id is different from the
         * new Id of the symbol.
         */
        while (topScopeLocals != null) {
            if (topScopeLocals.getName().equals(name)) {
                throw new NameAlreadyBoundException("Duplicate: Name already exists!");
            }
            last = topScopeLocals;
            topScopeLocals = topScopeLocals.next;
        }
        /* Assign the new symbol to the locals in topscope (if no locals were found) */
        if (last == null) {
            topScope.locals = symbol;
        } else {
            last.next = symbol; // Assign the new symbol as the next member (Most recent - if locals were found)
        }
        return symbol;
    }

    /*
     * Use this function when:
     * 1. Calling a function
     * 2. Accessing a variable in component
     * 3. Control statements
     * I think it is run-time check.
     */
    // TODO: fix exception to be custom.
    public Symbol findId(String name) throws NameNotFoundException { // Search for a symbol and return the symbol
        Symbol symbol;
        Symbol symbolScope;
        symbolScope = topScope;
        while (symbolScope != null) { // Iterate through all scopes
            symbol = symbolScope.locals;

            while (symbol != null) { // Iterate through all names in that scope
                if (symbol.name != null ? symbol.name.equals(name) : name == null) {
                    return symbol;
                }
                symbol = symbol.next;
            }
            symbolScope = symbolScope.next;
        }
        throw new NameNotFoundException("Not found: " + name);
    }

    // Maybe make lombok take care of this constructor?
    public SymbolTable(Parser parser) {
        this.parser = parser;
        topScope = null;
        currentScopelevel = -1;
    }
}
