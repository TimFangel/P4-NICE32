package frontend.semantic_analysis;

import java.util.*;

import exception.*;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.component.constants.*;
import frontend.abstract_syntax.component.constants.component_types.*;
import frontend.abstract_syntax.expression.*;
import frontend.abstract_syntax.expression.arith_expression.*;
import frontend.abstract_syntax.expression.bool_expression.*;
import frontend.abstract_syntax.expression.enums.*;
import frontend.abstract_syntax.function.FuncDecl;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.*;
import frontend.abstract_syntax.statement.main_statement.*;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.*;
import frontend.symbol_table.*;

/**
 * The semantic analyzer class aims to perform type checking on the AST as well
 * as put symbols into the appropriate scopes for the symbol table.
 */
public class SemanticAnalyzer {
    private final SymbolTable symbolTable;

    // Ports which have been used in component declarations.
    private HashSet<Integer> usedPorts = new HashSet<>();

    // Ports which are allowed to be used for component declarations.
    private HashMap<Integer, EnumSet<DirectionType>> allowedPorts = new HashMap<>();

    // A reference to the current function being analyzed.
    private FunctionSymbol currentFunctionSymbol = null;

    /**
     * During class instantiation the symbol table is initialized and allowed ports
     * are defined.
     */
    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();

        allowedPorts.put(2, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));
        allowedPorts.put(4, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));
        allowedPorts.put(5, EnumSet.of(DirectionType.INPUT, DirectionType.OUTPUT));

        allowedPorts.put(12, EnumSet.of(DirectionType.OUTPUT));

        Set<Integer> unavailablePorts = Set.of(20, 24, 28, 29, 30, 31);

        for (int i = 13; i <= 33; i++) {
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

    // Public method from which the AST is passed from the Main class.
    public void traverse(Node ast) {
        visit(ast);
    }

    // Switch case for visiting and handling specific nodes.
    void visit(Node n) {
        switch (n) {
            case Program p -> visit(p);
            case IfStmt is -> visit(is);
            case WhileStmt ws -> visit(ws);
            case BlockStmt bs -> visit(bs);
            case ReturnStmt rs -> visit(rs);
            case MemberAssStmt mas -> visit(mas);
            case Decl d -> visit(d);
            case AssStmt as -> visit(as);
            case FuncDecl fd -> visit(fd);
            case Component c -> visit(c);
            default ->
                throw new InvalidNodeException(
                        "[" + n.getLineNumber() + "] Could not visit node '" + n.toString() + "'");
        }
    }

    // Visit the program's three main blocks.
    void visit(Program p) {
        visit(p.getFunctions());
        visit(p.getSetup());
        visit(p.getMain());
    }

    /**
     * The following visitor methods handle visiting of specific nodes, and will
     * throw exceptions if there are type errors.
     */
    void visit(IfStmt is) {
        // Get if-statement condition type.
        Type conditionType = visitType(is.getCondition());

        // If the condition is not a boolean expression, then throw an exception.
        if (conditionType != Type.BOOL_T) {
            throw new NonMatchingTypeException(
                    "[" + is.getLineNumber() + "] Type mismatch: cannot use " + conditionType
                            + " in if statement");
        }

        // Enter a new scope for the body of the if-statement.
        symbolTable.enterScope();

        // Handle body statements by visiting them.
        BlockStmt thenStatements = is.getThenStmt();

        if (thenStatements != null) {
            visit(thenStatements);
        }

        // Exit scope once body has been processed.
        symbolTable.exitScope();

        BlockStmt elseStatement = is.getElseStmt();

        // In case the if-statement has an else block.
        if (elseStatement != null) {
            // Enter new scope.
            symbolTable.enterScope();

            // Visit statements.
            visit(elseStatement);

            // Exit scope.
            symbolTable.exitScope();
        }
    }

    void visit(WhileStmt ws) {
        // Get while-statement condition type.
        Type conditionType = visitType(ws.getCondition());

        // If the condition is not a boolean expression, then throw an exception.
        if (conditionType != Type.BOOL_T) {
            throw new NonMatchingTypeException(
                    "[" + ws.getLineNumber() + "] Type mismatch: cannot use " + conditionType
                            + " in while statement");
        }

        // Enter a new scope for the body of the while-statement.
        symbolTable.enterScope();

        // Handle body statements by visiting them.
        visit(ws.getWhileBody());

        // Exit scope once body has been processed.
        symbolTable.exitScope();
    }

    void visit(FuncDecl fd) {
        // Attempt to create a new symbol for the declared function.
        try {
            currentFunctionSymbol = symbolTable.newFunctionSymbol(fd.getIdentifier(), fd.getReturnType());
            fd.setSymbolRef(currentFunctionSymbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + fd.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + fd.getLineNumber() + "] " + e.getMessage());
        }

        // Throw an exception if function return type is not bool, int, or float.
        if (currentFunctionSymbol.getType() != Type.BOOL_T && currentFunctionSymbol.getType() != Type.FLOAT_T
                && currentFunctionSymbol.getType() != Type.INT_T) {
            throw new NonMatchingTypeException("Invalid return type for function " + fd.getIdentifier());
        }

        // Throw an exception if function parameter is not of type bool, int, or float.
        Type paramType = fd.getParamType();
        if (paramType != Type.BOOL_T && paramType != Type.FLOAT_T && paramType != Type.INT_T) {
            throw new NonMatchingTypeException("Invalid function parameter type " + paramType);
        }

        // Enter new scope.
        symbolTable.enterScope();

        // Add function parameter to the symbol table for the current scope.
        try {
            VariableSymbol paramSymbol = symbolTable.newVariableSymbol(fd.getParamName(), fd.getParamType());
            currentFunctionSymbol.setParameterSymbolRef(paramSymbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + fd.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + fd.getLineNumber() + "] " + e.getMessage());
        }

        // Visit function body statements.
        visit(fd.getStatements());

        // Exit scope once body has been processed.
        symbolTable.exitScope();

        // Reset current function return type.
        currentFunctionSymbol = null;
    }

    void visit(Component c) {
        // Variable for chosen port number.
        int portNumber = -1;

        // Attempt to create a new symbol for the declared component.
        try {
            ComponentSymbol symbol = symbolTable.newComponentSymbol(c.getIdentifier(), Type.COMPONENT);
            c.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + c.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + c.getLineNumber() + "] " + e.getMessage());
        }

        // Get the type of the port expression.
        Type portType = visitType(c.getPort());

        // If the port is not of type int, throw an exception.
        if (portType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + c.getLineNumber() + "] Port has to be of type int, got " + portType);
        }

        // Check if the port is already in use by a previously declared component.
        if (c.getPort() instanceof Operand o && o.getValue() instanceof IntNum n) {
            portNumber = n.value();

            // If in use, throw an exception.
            if (usedPorts.contains(portNumber)) {
                throw new PortAlreadyAssignedException("[" + c.getLineNumber() + "] Port " + portNumber
                        + " has already been assigned to a different component");
            } else {
                usedPorts.add(portNumber);
            }
        }

        // Get component protocol.
        ProtocolComp protocolComp = c.getProtocol();
        ProtocolType protocolType = protocolComp == null ? null : protocolComp.getProtocol();

        // Throw an exception if protcol is not one of the supported types.
        if (protocolType == null) {
            throw new NonMatchingTypeException("[" + c.getLineNumber()
                    + "] Protocol must be one of the supported protocol values, got " + protocolType);
        }

        // Get component interval type.
        Type intervalType = visitType(c.getInterval());

        // If interval is not of type int, throw an exception.
        if (intervalType != Type.INT_T) {
            throw new NonMatchingTypeException(
                    "[" + c.getLineNumber() + "] Interval has to be of type int, got " + intervalType);
        }

        // If the provided interval is less than zero, throw an exception.
        if (c.getInterval() instanceof Operand o && o.getValue() instanceof IntNum n && n.value() < 0) {
            throw new NoValueMatchException(
                    "[" + c.getLineNumber() + "] Interval must be a positive integer, got " + n.value());
        }

        // Get component direction type.
        DirectionComp directionComp = c.getDirection();
        DirectionType directionType = directionComp == null ? null : directionComp.getDirection();

        // If direction is not one of the supported types, throw an exception.
        if (directionType == null) {
            throw new NonMatchingTypeException("[" + c.getLineNumber()
                    + "] Direction must be one of the supported direction values, got " + directionType);
        }

        // Get allowed directions for the provided port.
        EnumSet<DirectionType> allowedDirections = allowedPorts.get(portNumber);

        // If no directions could be found for the specified port, then throw an
        // exception.
        if (allowedDirections == null) {
            throw new InvalidPortException("[" + c.getLineNumber() + "] Port " + portNumber + " cannot be used");
        }

        // If the chosen port does not support the specified direction, throw an
        // exception.
        if (!allowedDirections.contains(directionType)) {
            throw new InvalidDirectionException("[" + c.getLineNumber() + "] Port " + portNumber + " only supports "
                    + allowedDirections + ", got " + directionType);
        }

        // Enter new scope.
        symbolTable.enterScope();

        // Attach scope to component.
        try {
            c.getSymbolRef().setLocalScope(symbolTable.getCurrentScope());
        } catch (ScopeException e) {
            throw new ScopeException("[" + c.getLineNumber() + "] " + e.getMessage());
        }

        // Visit component variable declarations.
        for (Decl d : c.getVariables()) {
            visit(d);
        }

        // Exit scope.
        symbolTable.exitScope();
    }

    void visit(BlockStmt bs) {
        // Visit each statement in the block.
        for (Stmt stmt : bs.getStatements()) {
            visit(stmt);
        }
    }

    void visit(Decl d) {
        // Get declaration type.
        Type valueType = visitType(d.getValue());

        // If actual type does not match expected type, throw an exception.
        if (valueType != d.getType()) {
            throw new NonMatchingTypeException(
                    "[" + d.getLineNumber() + "] Type mismatch: " + d.getType() + " and " + valueType);
        }

        // Attempt to create a new symbol for the declaration.
        try {
            Symbol symbol = symbolTable.newVariableSymbol(d.getIdentifier(), d.getType());
            d.setSymbolRef(symbol);
        } catch (NameAlreadyBoundException e) {
            throw new NameAlreadyBoundException("[" + d.getLineNumber() + "] " + e.getMessage());
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + d.getLineNumber() + "] " + e.getMessage());
        }
    }

    void visit(AssStmt as) {
        Symbol symbol;

        // Get symbol
        try {
            symbol = symbolTable.lookup(as.getIdentifier());
        } catch (NameNotFoundException e) {
            throw new NameNotFoundException("[" + as.getLineNumber() + "] " + e.getMessage());
        }

        // Get type of assigned expression.
        Type valueType = visitType(as.getValue());

        // If type does not match expected type, throw an exception.
        if (valueType != symbol.getType()) {
            throw new NonMatchingTypeException(
                    "[" + as.getLineNumber() + "] Type mismatch: " + symbol.getType() + " and " + valueType);
        }

        // Attempt to update the symbol reference for the variable being assigned to.
        try {
            as.setSymbolRef(symbol);
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + as.getLineNumber() + "] " + e.getMessage());
        }
    }

    void visit(ReturnStmt rs) {
        // Get the type of the return statement.
        Type actualReturnType = visitType(rs.getExprReturned());

        // Check if the use of return is legal.
        if (currentFunctionSymbol == null) {
            throw new NonMatchingTypeException(
                    "[" + rs.getLineNumber() + "] Return statement outside function");
        }

        // Check that the expected type matches the returned type.
        if (actualReturnType != currentFunctionSymbol.getType()) {
            throw new NonMatchingTypeException(
                    "[" + rs.getLineNumber() + "] Return type mismatch: expected "
                            + currentFunctionSymbol.getType() + " but got " + actualReturnType);
        }

        // Update the symbol reference for the return statement.
        rs.setSymbolRef(currentFunctionSymbol);
    }

    void visit(MemberAssStmt mas) {
        Type expressionType = visitType(mas.getValue());
        // TODO
        // findes component?
        // findes memberen?
        // member field type
        // expression type
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
            case MemberAccess ma:
                return visitType(ma);
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
            funcCall.setFunctionSymbolRef(funcSymbol);
        } catch (NonMatchingSymbolException e) {
            throw new NonMatchingSymbolException("[" + funcCall.getLineNumber() + "] " + e.getMessage());
        }

        Type parameterType = visitType(funcCall.getParameter());
        if (parameterType != funcCall.getParameterSymbolRef().getType()) {
            throw new NonMatchingTypeException(
                    "[" + funcCall.getLineNumber() + "] Type mismatch: " + parameterType + " and "
                            + funcCall.getParameterSymbolRef().getType());
        }

        return funcSymbol.getType();
    }

    Type visitType(MemberAccess memberAccess) {
        Symbol component = null;
        Symbol variable;

        // Get component
        try {
            component = symbolTable.lookup(memberAccess.getComponent());

        } catch (NameNotFoundException e) {
            throw new NameNotFoundException("[" + memberAccess.getLineNumber() + "]" + e.getMessage());
        }

        // Get variable
        if (component instanceof ComponentSymbol cs) {
            HashMap<String, Symbol> variableScope = cs.getLocalScope();

            if (variableScope.isEmpty()) {
                throw new NameNotFoundException("[" + memberAccess.getLineNumber() + "] could not find component");
            }

            variable = variableScope.get(memberAccess.getVariable());
        } else {
            throw new NameNotFoundException("[" + memberAccess.getLineNumber() + "] could not find component");
        }

        memberAccess.setSymbolRef(variable);

        return variable.getType();
    }
}