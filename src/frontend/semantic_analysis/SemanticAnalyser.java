package frontend.semantic_analysis;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import exception.InvalidDirectionException;
import exception.InvalidNodeException;
import exception.InvalidPortException;
import exception.NameAlreadyBoundException;
import exception.NameNotFoundException;
import exception.NonMatchingSymbolException;
import exception.NoValueMatchException;
import exception.NonMatchingTypeException;
import exception.PortAlreadyAssignedException;
import exception.UnrecognizedOperatorException;
import exception.UnrecognizedTypeException;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.component.constants.component_types.DirectionType;
import frontend.abstract_syntax.component.constants.component_types.ProtocolType;
import frontend.abstract_syntax.expression.Cast;
import frontend.abstract_syntax.expression.FuncCall;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.VarExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.function.FuncDecl;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.ReturnStmt;
import frontend.abstract_syntax.statement.main_statement.WhileStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.ComponentSymbol;
import frontend.symboltable.FunctionSymbol;
import frontend.symboltable.Symbol;
import frontend.symboltable.SymbolTable;

public class SemanticAnalyser {
    private final SymbolTable symbolTable;
    private Type currentFunctionReturnType = null;
    private HashSet<Integer> usedPorts = new HashSet<>();
    private HashMap<Integer, EnumSet<DirectionType>> allowedPorts = new HashMap<>();

    public SemanticAnalyser() {
        this.symbolTable = new SymbolTable();

        allowedPorts.put(2, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));
        allowedPorts.put(4, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));
        allowedPorts.put(5, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));

        allowedPorts.put(12, EnumSet.of(DirectionType.OUTPUT));

        for (int i = 13; i <= 33; i++) {
            Set<Integer> unavailablePorts = Set.of(20, 24, 28, 29, 30, 31);
            if (unavailablePorts.contains(i)) {
                continue;
            }
            allowedPorts.put(i, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));
        }

        allowedPorts.put(34, EnumSet.of(DirectionType.INPUT));
        allowedPorts.put(35, EnumSet.of(DirectionType.INPUT));
        allowedPorts.put(36, EnumSet.of(DirectionType.INPUT));
        allowedPorts.put(39, EnumSet.of(DirectionType.INPUT));
    }

    public void traverse(Node ast) {
        visit(ast);
    }

    /* --- Visitors --- */
    void visit(Node n) {
        switch (n) {
            case Program p -> visit(p);
            case IfStmt is -> visit(is);
            case WhileStmt ws -> visit(ws);
            case BlockStmt bs -> visit(bs);
            case ReturnStmt rs -> visit(rs);
            case Decl d -> visit(d);
            case AssStmt as -> visit(as);
            case FuncDecl fd -> visit(fd);
            case Component c -> visit(c);
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
        symbolTable.enterScope();
        BlockStmt thenStatements = ifStmt.getThenStmt();
        if (thenStatements != null) {
            visit(thenStatements);
        }
        symbolTable.exitScope();

        // Else
        symbolTable.enterScope();
        BlockStmt elseStatement = ifStmt.getElseStmt();
        if (elseStatement != null) {
            visit(elseStatement);
        }
        symbolTable.exitScope();

    }

    void visit(WhileStmt ws) {
        Type conditionType = visitType(ws.getCondition());

        if (conditionType != Type.BOOL_T) {
            throw new NonMatchingTypeException(
                    "[" + ws.getLineNumber() + "] Type mismatch: cannot use " + conditionType
                            + " in while statement");
        }

        // Body
        symbolTable.enterScope();
        visit(ws.getWhileBody());
        symbolTable.exitScope();
    }

    void visit(FuncDecl fd) {
        FunctionSymbol symbol;

        try {
            symbol = symbolTable.newFunctionSymbol(fd.getIdentifier(), fd.getReturnType(), fd.getParamType());
            fd.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + fd.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + fd.getLineNumber() + "] " + e.getMessage());
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

    void visit(Component c) {
        ComponentSymbol symbol;
        int portNumber = -1;

        try {
            symbol = symbolTable.newComponentSymbol(c.getIdentifier(), Type.COMPONENT);
            c.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + c.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + c.getLineNumber() + "] " + e.getMessage());
        }

        Type portType = visitType(c.getPort());

        if (portType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + c.getLineNumber() + "] Port has to be of type int, got " + portType);
        }

        if (c.getPort() instanceof Operand o && o.getValue() instanceof IntNum n) {
            portNumber = n.value();

            if (usedPorts.contains(portNumber)) {
                throw new PortAlreadyAssignedException("[" + c.getLineNumber() + "] Port " + portNumber
                        + " has already been assigned to a different component");
            } else {
                usedPorts.add(portNumber);
            }
        }

        ProtocolComp protocolComp = c.getProtocol();
        ProtocolType protocolType = protocolComp == null ? null : protocolComp.getProtocol();

        if (protocolType == null) {
            throw new NonMatchingTypeException("[" + c.getLineNumber()
                    + "] Protocol must be one of the supported protocol values, got " + protocolType);
        }

        Type intervalType = visitType(c.getInterval());

        if (intervalType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + c.getLineNumber() + "] Interval has to be of type int, got " + intervalType);
        }

        if (c.getInterval() instanceof Operand o && o.getValue() instanceof IntNum n && n.value() < 0) {
            throw new NoValueMatchException(
                    "[" + c.getLineNumber() + "] Interval must be a positive integer, got " + n.value());
        }

        DirectionComp directionComp = c.getDirection();
        DirectionType directionType = directionComp == null ? null : directionComp.getDirection();

        if (directionType == null) {
            throw new NonMatchingTypeException("[" + c.getLineNumber()
                    + "] Direction must be one of the supported direction values, got " + directionType);
        }

        EnumSet<DirectionType> allowedDirections = allowedPorts.get(portNumber);

        if (allowedDirections == null) {
            throw new InvalidPortException("[" + c.getLineNumber() + "] Port " + portNumber + " cannot be used");
        }

        if (!allowedDirections.contains(directionType)) {
            throw new InvalidDirectionException("[" + c.getLineNumber() + "] Port " + portNumber + " only supports "
                    + allowedDirections + ", got " + directionType);
        }

        symbolTable.enterScope();
        for (Decl d : c.getVariables()) {
            visit(d);
        }
        symbolTable.exitScope();
    }

    /* Statement visitors */
    void visit(BlockStmt blockStmt) {
        for (Stmt stmt : blockStmt.getStatements()) {
            visit(stmt);
        }
    }

    void visit(Decl decl) {
        Symbol symbol;

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
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + decl.getLineNumber() + "] " + e.getMessage());
        }
    }

    void visit(AssStmt assStmt) {
        Symbol symbol;

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
        try {
            assStmt.setSymbolRef(symbol);
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + assStmt.getLineNumber() + "] " + e.getMessage());
        }
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
            case Cast c:
                return visitType(c);
            case FuncCall fc:
                return visitType(fc);
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
        Symbol symbol;

        // Get symbol
        try {
            symbol = symbolTable.lookup(varExpr.getName());
        } catch (NameNotFoundException e) {
            throw new NameNotFoundException("[" + varExpr.getLineNumber() + "] " + e.getMessage());
        }

        // Update ast
        try {
            varExpr.setSymbolRef(symbol);
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + varExpr.getLineNumber() + "] " + e.getMessage());
        }

        return symbol.getType();
    }

    Type visitType(ArithBinaryOpExpr binaryExpr) {
        Type leftType = visitType(binaryExpr.getExprLeft());
        Type rightType = visitType(binaryExpr.getExprRight());
        ArithBinaryOp operator = binaryExpr.getOp();

        switch (operator) {
            case DIV:
                if (binaryExpr.getExprRight() instanceof Operand o) {
                    Value v = o.getValue();
                    if (v instanceof IntNum n && n.value() == 0) {
                        throw new NoValueMatchException(
                                "[" + binaryExpr.getLineNumber() + "] Illegal division by zero");
                    }

                    if (v instanceof FloatNum n && n.value() == 0) {
                        throw new NoValueMatchException(
                                "[" + binaryExpr.getLineNumber() + "] Illegal division by zero");
                    }
                }

                // No return, continue to following checks.

            case ADD, SUB, MUL, MOD:
                if (leftType != rightType) {
                    throw new NonMatchingTypeException(
                            "[" + binaryExpr.getLineNumber() + "] Type mismatch: " + leftType + " and " + rightType);
                }

                if (leftType != Type.INT_T && leftType != Type.FLOAT_T) {
                    throw new NonMatchingTypeException(
                            "[" + binaryExpr.getLineNumber() + "] Arithmetic operation on non-number: " + leftType);
                }
                return leftType;

            default:
                throw new UnrecognizedOperatorException(
                        "[" + binaryExpr.getLineNumber() + "] Unrecognized operator: " + operator);
        }
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

    Type visitType(Cast cast) {
        Type initType = visitType(cast.getExpr());
        Type targetType = cast.getTargetType();

        // Type checking
        if (initType != Type.FLOAT_T && initType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + cast.getLineNumber() + "] Type mismatch: cannot type cast from " + initType);
        }

        if (targetType != Type.FLOAT_T && targetType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + cast.getLineNumber() + "] Type mismatch: cannot type cast to " + targetType);
        }

        return targetType;
    }

    Type visitType(FuncCall funcCall) {
        Symbol funcSymbol = symbolTable.lookup(funcCall.getIdentifier());

        try {
            funcCall.setSymbolRef(funcSymbol);
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + funcCall.getLineNumber() + "] " + e.getMessage());
        }

        Type parameterType = visitType(funcCall.getParameter());

        return funcSymbol.getType();
    }

}

// TODO: memberCall