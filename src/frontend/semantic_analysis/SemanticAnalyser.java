package frontend.semantic_analysis;

import exception.InvalidNodeException;
import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import exception.NonMatchingTypeException;
import exception.UnrecognizedOperatorException;
import exception.UnrecognizedTypeException;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.VarExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.function.FuncDecl;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.ReturnStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.NewSymbol;
import frontend.symboltable.NewSymbolTable;

public class SemanticAnalyser {
    private final NewSymbolTable symbolTable;
    private Type currentFunctionReturnType = null;

    public SemanticAnalyser() {
        this.symbolTable = new NewSymbolTable();
    }

    public void traverse(Node ast) {
        visit(ast);
    }

    /* --- Visitors --- */
    void visit(Node n) {
        switch (n) {
            case Program p -> visit(p);
            case IfStmt is -> visit(is);
            case BlockStmt bs -> visit(bs);
            case ReturnStmt rs -> visit(rs);
            case Decl d -> visit(d);
            case AssStmt as -> visit(as);
            case FuncDecl fd -> visit(fd);
            default ->
                throw new InvalidNodeException(
                        "[" + n.getLineNumber() + "] Could not visit node '" + n.toString() + "'");
        }
    }

    void visit(Program program) {
        visit(program.getFunctions());
        visit(program.getSetup());
        visit(program.getMain());
    }

    /* Scope visitors */
    void visit(IfStmt ifStmt) {
        // Type checking
        Type conditionType = visitType(ifStmt.getCondition());
        if (conditionType != Type.BOOL_T) {
            throw new NonMatchingTypeException(
                    "[" + ifStmt.getLineNumber() + "] Type mismatch: cannot use " + conditionType
                            + " in if statement");
        }

        // Body
        BlockStmt thenStatements = ifStmt.getThenStmt();
        if (thenStatements != null) {
            visit(thenStatements);
        }

        // Else
        BlockStmt elseStatement = ifStmt.getElseStmt();
        if (elseStatement != null) {
            visit(elseStatement);
        }
    }

    void visit(FuncDecl fd) {
        NewSymbol symbol;

        try {
            symbol = symbolTable.newFunctionSymbol(fd.getIdentifier(), fd.getReturnType(), fd.getParamType());
            fd.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + fd.getLineNumber() + "] " + e.getMessage());
        }

        // Set current function return type.
        currentFunctionReturnType = fd.getReturnType();

        if (currentFunctionReturnType != Type.BOOL_T && currentFunctionReturnType != Type.FLOAT_T
                && currentFunctionReturnType != Type.INT_T) {
            throw new NonMatchingTypeException("Invalid return type for function " + fd.getIdentifier());
        }

        Type paramType = fd.getParamType();

        if (paramType != Type.BOOL_T && paramType != Type.FLOAT_T && paramType != Type.INT_T) {
            throw new NonMatchingTypeException("Invalid function parameter type " + paramType);
        }

        symbolTable.enterScope();
        symbolTable.newVariableSymbol(fd.getParamName(), fd.getParamType());
        visit(fd.getStatements());
        symbolTable.exitScope();

        // Reset current function return type.
        currentFunctionReturnType = null;
    }

    /* Statement visitors */
    void visit(BlockStmt blockStmt) {
        for (Stmt stmt : blockStmt.getStatements()) {
            visit(stmt);
        }
    }

    void visit(Decl decl) {
        NewSymbol symbol;

        // Type checking
        Type valueType = visitType(decl.getValue());
        if (valueType != decl.getType()) {
            throw new NonMatchingTypeException(
                    "[" + decl.getLineNumber() + "] Type mismatch: " + decl.getType() + " and " + valueType);
        }

        // Update st and ast
        try {
            symbol = symbolTable.newVariableSymbol(decl.getIdentifier(), decl.getType());
            decl.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + decl.getLineNumber() + "] " + e.getMessage());
        }
    }

    void visit(AssStmt assStmt) {
        NewSymbol symbol;

        // Get symbol
        try {
            symbol = symbolTable.lookup(assStmt.getIdentifier());
        } catch (NameNotFoundException e) {
            throw new NameNotFoundException("[" + assStmt.getLineNumber() + "] " + e.getMessage());
        }

        // Type checking
        Type valueType = visitType(assStmt.getValue());
        if (valueType != symbol.getType()) {
            throw new NonMatchingTypeException(
                    "[" + assStmt.getLineNumber() + "] Type mismatch: " + symbol.getType() + " and " + valueType);
        }

        // Update ast
        assStmt.setSymbolRef(symbol);
    }

    void visit(ReturnStmt rs) {
        Type actualReturnType = visitType(rs.getExprReturned());

        if (currentFunctionReturnType == null) {
            throw new NonMatchingTypeException(
                    "[" + rs.getLineNumber() + "] Return statement outside function");
        }

        if (actualReturnType != currentFunctionReturnType) {
            throw new NonMatchingTypeException(
                    "[" + rs.getLineNumber() + "] Return type mismatch: expected "
                            + currentFunctionReturnType + " but got " + actualReturnType);
        }
    }

    /* --- Type returning visitors --- */
    Type visitType(Node n) {
        switch (n) {
            case Operand o:
                return visitType(o);
            case VarExpr ve:
                return visitType(ve);
            case ArithBinaryOpExpr ae:
                return visitType(ae);
            case ArithUnaryOpExpr ae:
                return visitType(ae);
            case BoolBinaryOpExpr be:
                return visitType(be);
            case BoolUnaryOpExpr be:
                return visitType(be);
            default:
                throw new InvalidNodeException(
                        "[" + n.getLineNumber() + "] Could not visit node '" + n.toString() + "'");
        }
    }

    Type visitType(Operand operand) {
        Value value = operand.getValue();

        switch (value) {
            case IntNum in:
                return Type.INT_T;
            case FloatNum fn:
                return Type.FLOAT_T;
            case Bool b:
                return Type.BOOL_T;

            default:
                throw new UnrecognizedTypeException(
                        "[" + operand.getLineNumber() + "] Could not find type of '" + operand.toString() + "'");
        }
    }

    /* Expr visitors */
    Type visitType(VarExpr varExpr) {
        NewSymbol symbol;

        // Get symbol
        try {
            symbol = symbolTable.lookup(varExpr.getName());
        } catch (NameNotFoundException e) {
            throw new NameNotFoundException("[" + varExpr.getLineNumber() + "] " + e.getMessage());
        }

        // Update ast
        varExpr.setSymbolRef(symbol);

        return symbol.getType();
    }

    Type visitType(ArithBinaryOpExpr binaryExpr) {
        Type leftType = visitType(binaryExpr.getExprLeft());
        Type rightType = visitType(binaryExpr.getExprRight());

        // Type checking
        if (leftType != rightType) {
            throw new NonMatchingTypeException(
                    "[" + binaryExpr.getLineNumber() + "] Type mismatch: " + leftType + " and " + rightType);
        }
        if (leftType != Type.FLOAT_T && leftType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + binaryExpr.getLineNumber() + "] Type mismatch: cannot use " + leftType
                            + " in arithmetic expressions");
        }

        return leftType;
    }

    Type visitType(ArithUnaryOpExpr unaryExpr) {
        Type type = visitType(unaryExpr.getExpr());

        // Type checking
        if (type != Type.FLOAT_T && type != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + unaryExpr.getLineNumber() + "] Type mismatch: cannot use " + type
                            + " in arithmetic expressions");
        }

        return type;
    }

    Type visitType(BoolBinaryOpExpr binaryExpr) {
        Type leftType = visitType(binaryExpr.getExprLeft());
        Type rightType = visitType(binaryExpr.getExprRight());
        BoolBinaryOp operator = binaryExpr.getOp();

        switch (operator) {
            case AND, OR:
                if (leftType != Type.BOOL_T || rightType != Type.BOOL_T) {
                    throw new NonMatchingTypeException(
                            "[" + binaryExpr.getLineNumber() + "] Type mismatch: " + leftType + " and " + rightType);
                }

                return Type.BOOL_T;

            case EQ, NEQ:
                if (leftType != rightType) {
                    throw new NonMatchingTypeException(
                            "[" + binaryExpr.getLineNumber() + "] Type mismatch: " + leftType + " and " + rightType);
                }

                return Type.BOOL_T;

            case LEQ, GEQ, LT, GT:
                if (leftType == Type.BOOL_T || rightType == Type.BOOL_T || leftType != rightType) {
                    throw new NonMatchingTypeException(
                            "[" + binaryExpr.getLineNumber()
                                    + "] Comparison requires both operands to be of type int or float, got " + leftType
                                    + " and " + rightType);
                }

                return Type.BOOL_T;

            default:
                throw new UnrecognizedOperatorException(
                        "[" + binaryExpr.getLineNumber() + "] Unknown operator: " + operator);
        }
    }

    Type visitType(BoolUnaryOpExpr unaryExpr) {
        Type type = visitType(unaryExpr.getExpr());

        // Type checking
        if (type != Type.BOOL_T) {
            throw new NonMatchingTypeException(
                    "[" + unaryExpr.getLineNumber() + "] Negation requires type bool, got " + type);
        }

        return Type.BOOL_T;
    }
}