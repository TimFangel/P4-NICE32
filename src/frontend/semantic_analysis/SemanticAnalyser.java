package frontend.semantic_analysis;

import exception.InvalidNodeException;
import exception.NonMatchingTypeException;
import exception.UnrecognizedTypeException;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.NewSymbol;
import frontend.symboltable.NewSymbolTable;

public class SemanticAnalyser {
    private final NewSymbolTable symbolTable;

    public SemanticAnalyser() {
        this.symbolTable = new NewSymbolTable();
    }

    public void traverse(Node ast) {
        visit(ast);
    }

    /*  --- Visitors --- */
    void visit(Node n) {
        // TODO: Change to switch and create sealed classes
        if (n instanceof Program p) visit(p);
        else if (n instanceof BlockStmt bs) visit(bs);
        else if (n instanceof Decl d) visit(d);
        else if (n instanceof AssStmt as) visit(as);
        else throw new InvalidNodeException("[" + n.getLineNumber() + "] Could not visit node '" + n.toString() + "'");
    }
    
    void visit(Program program) {
        visit(program.getMain());
    }

    void visit(BlockStmt blockStmt) {
        for (Stmt stmt : blockStmt.getStatements()) {
            visit(stmt);
        }
    }

    void visit(Decl decl) {
        // Type checking
        Type valueType = visitType(decl.getValue());
        if (valueType != decl.getType()) {
            throw new NonMatchingTypeException("[" + decl.getLineNumber() + "] Type mismatch: " + decl.getType() + " and " + valueType);
        }
        
        // Update st and ast
        NewSymbol symbol = symbolTable.newVariableSymbol(decl.getIdentifier(), decl.getType());
        decl.setSymbolRef(symbol);
    }

    void visit(AssStmt assStmt) {
        // Presence check
        NewSymbol symbol = symbolTable.lookup(assStmt.getIdentifier());

        // Type checking
        Type valueType = visitType(assStmt.getValue());
        if (valueType != symbol.getType()) {
            throw new NonMatchingTypeException("[" + assStmt.getLineNumber() + "] Type mismatch: " + symbol.getType() + " and " + valueType);
        }

        // Update ast
        assStmt.setSymbolRef(symbol);
    }

    /* --- Type returning visitors --- */
    Type visitType(Node n) {
        if (n instanceof Operand o) return visitType(o);
        else throw new InvalidNodeException("[" + n.getLineNumber() + "] Could not visit node '" + n.toString() + "'");
    }

    Type visitType(Operand operand) {
        Value value = operand.getValue();

        if (value instanceof IntNum) {
            return Type.INT_T;
        } else {
            throw new UnrecognizedTypeException("[" + operand.getLineNumber() + "] Could not find type of '" + operand.toString() + "'");
        }
    }
}