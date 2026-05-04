package frontend.symboltable.environments;

import frontend.symboltable.SymbolTable;

public class EnvironmentManagement {
    public SymbolTable symbolTable;

    public VariableEnv variableEnv;
    public FunctionEnv functionEnv;
    public ComponentEnv componentEnv;

    public void environment(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;

        this.variableEnv = new VariableEnv(symbolTable);
        this.functionEnv = new FunctionEnv(symbolTable);
        this.componentEnv = new ComponentEnv(symbolTable);
    }

}
