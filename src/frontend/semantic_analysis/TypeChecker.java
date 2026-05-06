package frontend.semantic_analysis;

import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.component.constants.DirectionComp;
import frontend.abstract_syntax.component.constants.ProtocolComp;
import frontend.abstract_syntax.component.constants.component_types.DirectionType;
import frontend.abstract_syntax.component.constants.component_types.ProtocolType;
import frontend.abstract_syntax.expression.Cast;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.FuncCall;
import frontend.abstract_syntax.expression.MemberAccess;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.VarExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.arith_expression.ArithUnaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolUnaryOpExpr;
import frontend.abstract_syntax.function.FuncDecl;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.statement.main_statement.WhileStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symboltable.SymbolTable;

import java.util.*;

public class TypeChecker {
    SymbolTable symbolTable;
    private final Map<String, Type> symbols = new HashMap<>();

    public TypeChecker(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void check(Program program) {
        checkStmt(program.setup);
        checkStmt(program.functions);
        checkStmt(program.main);
    }

    private void checkStmt(Stmt stmt) {
        if (stmt instanceof Decl d) {
            String identifier = d.getIdentifier();

            if (identifier == null) {
                throw new RuntimeException("Could not find identifier for " + identifier);
            }

            if (symbols.containsKey(identifier)) {
                throw new RuntimeException("Variable already declared: " + identifier);
            }

            Type valueType = checkExpr(d.getValue());

            if (d.getType() != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + d.getType() + " variable '"
                                + d.getIdentifier()
                                + "'");
            }

            symbols.put(identifier, d.getType());
            return;
        }

        if (stmt instanceof FuncDecl f) {
            String identifier = f.getIdentifier();

            if (identifier == null) {
                throw new RuntimeException("Could not find identifier for " + identifier);
            }

            // TODO: fix symboltable tjek
            // if (symboltable.functions.containsKey(identifier)) {
            // throw new RuntimeException("Function already declared: " + identifier);
            // }

            Type type = f.getReturnType();

            if (type != Type.BOOL_T && type != Type.FLOAT_T && type != Type.INT_T) {
                throw new RuntimeException("Invalid return type for function " + f.getIdentifier());
            }

            Type paramType = f.getParamType();

            if (paramType != Type.BOOL_T && paramType != Type.FLOAT_T && paramType != Type.INT_T) {
                throw new RuntimeException("Invalid function parameter type " + paramType);
            }

            BlockStmt stmts = f.getStatements();

            for (Stmt s : stmts.getStatements()) {
                checkStmt(s);
            }

            return;
        }

        if (stmt instanceof BlockStmt b) {
            for (Stmt s : b.getStatements()) {
                checkStmt(s);
            }
            return;
        }

        if (stmt instanceof IfStmt i) {
            Type condType = checkExpr(i.getCondition());

            if (condType != Type.BOOL_T) {
                throw new RuntimeException("If condition must be BOOL, got " + condType);
            }

            checkStmt(i.getThenStmt());

            if (i.getElseStmt() != null) {
                checkStmt(i.getElseStmt());
            }

            return;
        }

        if (stmt instanceof AssStmt a) {
            String identifier = a.getIdentifier();

            Type varType = symbols.get(identifier);

            if (varType == null) {
                throw new RuntimeException("Cannot assign to undeclared variable '" + identifier + "'");
            }

            Type valueType = checkExpr(a.getValue());

            if (varType != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + varType + " variable '" + identifier
                                + "'");
            }

            return;
        }

        if (stmt instanceof Component c) {
            String identifier = c.getIdentifier();

            if (identifier == null) {
                throw new RuntimeException("Component is missing identifier");
            }

            if (symbolTable.components.containsKey(identifier)) {
                throw new RuntimeException("Component already declared");
            }

            symbolTable.components.put(identifier, c);

            Type portType = checkExpr(c.getPort());

            if (portType != Type.INT_T) {
                throw new RuntimeException("Port must be of type INT, got " + portType);
            }

            ProtocolComp protocolComp = c.getProtocol();
            ProtocolType protocolType = protocolComp == null ? null : protocolComp.getProtocol();

            if (protocolType == null) {
                throw new RuntimeException("Protocol must be one of the supported protocol values, got " +
                        protocolType);
            }

            Type intervalType = checkExpr(c.getInterval());

            if (intervalType != Type.INT_T) {
                throw new RuntimeException("Interval must be of type INT, got " + intervalType);
            }

            if (c.getInterval() instanceof Operand o && o.getValue() instanceof IntNum n && n.value() < 0) {
                throw new RuntimeException("Interval must be of type INT, got " + c.getInterval());
            }

            DirectionComp directionComp = c.getDirection();
            DirectionType directionType = directionComp == null ? null : directionComp.getDirection();

            if (directionType == null) {
                throw new RuntimeException(
                        "Direction must be one of the supported direction values, got " + directionType);
            }

            return;
        }

        if (stmt instanceof WhileStmt w) {
            Expr condition = w.getCondition();
            checkExpr(condition);

            BlockStmt body = w.getWhileBody();

            for (Stmt s : body.getStatements()) {
                checkStmt(s);
            }

            return;
        }

        throw new RuntimeException("Unknown statement: " + stmt);
    }

    private Type checkExpr(Expr expr) {
        if (expr instanceof Operand o) {
            Value value = o.getValue();

            if (value instanceof IntNum) {
                return Type.INT_T;
            }

            if (value instanceof FloatNum) {
                return Type.FLOAT_T;
            }

            if (value instanceof Bool) {
                return Type.BOOL_T;
            }

            throw new RuntimeException("Unknown operand value: " + value);
        }

        if (expr instanceof VarExpr v) {
            Type type = symbols.get(v.getName());

            if (type == null) {
                throw new RuntimeException("Undeclared variable: " + v.getName());
            }

            return type;
        }

        if (expr instanceof FuncCall fc) {
            String identifier = fc.getIdentifier();

            // TODO: fix symboltable tjek
            // if (!symbolTable.functions.containsKey(identifier)) {
            // throw new RuntimeException("Cannot call undeclared function " + identifier);
            // }

            Type paramType = checkExpr(fc.getParameter());

            if (paramType != Type.BOOL_T && paramType != Type.FLOAT_T && paramType != Type.INT_T) {
                throw new RuntimeException("Cannot call function with parameter type " + paramType);
            }

            // TODO: fix symboltable tjek
            // FuncDecl f = symbolTable.functions.get(identifier);

            // return f.getReturnType();
            return Type.INT_T;
        }

        // Check member access.
        if (expr instanceof MemberAccess m) {
            String componentName = m.getComponent();
            String variableName = m.getVariable();

            Component c = symbolTable.components.get(componentName);

            if (c == null) {
                throw new RuntimeException("Unknown component: " + componentName);
            }

            for (Decl d : c.getVariables()) {
                if (d.getIdentifier().equals(variableName)) {
                    return d.getType();
                }
            }

            throw new RuntimeException(
                    "Component '" + componentName + "' has no field '" + variableName + "'");
        }

        // Check type casting.
        if (expr instanceof Cast c) {
            Type sourceType = checkExpr(c.getExpr());
            Type targetType = c.getTargetType();

            if (sourceType == Type.BOOL_T || targetType == Type.BOOL_T) {
                throw new RuntimeException(
                        "Cannot cast between BOOL and other types: " + sourceType + " -> " + targetType);
            }

            if ((sourceType == Type.INT_T && targetType == Type.FLOAT_T) ||
                    (sourceType == Type.FLOAT_T && targetType == Type.INT_T)) {
                return targetType;
            }

            if (sourceType == targetType) {
                return targetType;
            }

            throw new RuntimeException("Invalid cast from " + sourceType + " to " + targetType);
        }

        // Check boolean binary expressions.
        if (expr instanceof BoolBinaryOpExpr b) {
            Type left = checkExpr(b.getExprLeft());
            Type right = checkExpr(b.getExprRight());

            switch (b.getOp()) {
                case AND, OR:
                    if (left != Type.BOOL_T || right != Type.BOOL_T) {
                        throw new RuntimeException(
                                "Boolean operator " + b.getOp() + " requires BOOL and BOOL, got " + left + " and "
                                        + right);
                    }

                    return Type.BOOL_T;

                case EQ, NEQ:
                    if (left != right) {
                        throw new RuntimeException(
                                "Equality requires same types, got " + left + " and " + right);
                    }

                    return Type.BOOL_T;

                case LEQ, GEQ, GT, LT:
                    if (left == Type.BOOL_T || right == Type.BOOL_T || left != right) {
                        throw new RuntimeException(
                                "Comparison requires ints or floats, got " + left + " and " + right);
                    }

                    return Type.BOOL_T;

                default:
                    throw new RuntimeException("Unknown op: " + b.getOp());
            }
        }

        // Check boolean unary expressions.
        if (expr instanceof BoolUnaryOpExpr b) {
            Type type = checkExpr(b.getExpr());

            if (type != Type.BOOL_T) {
                throw new RuntimeException("Negation requires type bool, got " + type);
            }

            return Type.BOOL_T;
        }

        // Check arithmetic binary expressions.
        if (expr instanceof ArithBinaryOpExpr a) {
            Type left = checkExpr(a.getExprLeft());
            Type right = checkExpr(a.getExprRight());

            switch (a.getOp()) {
                case ADD, SUB, MUL, DIV, MOD:
                    if (left != right) {
                        throw new RuntimeException("Type mismatch: " + left + " and " + right);
                    }

                    if (left != Type.INT_T && left != Type.FLOAT_T) {
                        throw new RuntimeException("Arithmetic op on non-number: " + left);
                    }

                    return left;

                default:
                    throw new RuntimeException("Unknown op: " + a.getOp());

            }
        }

        // Check arithmetic unary expressions.
        if (expr instanceof ArithUnaryOpExpr a) {
            Type type = checkExpr(a.getExpr());

            if (type == Type.INT_T) {
                return Type.INT_T;
            }

            if (type == Type.FLOAT_T) {
                return Type.FLOAT_T;
            }

            throw new RuntimeException("Unary minus requires ints or floats, got " + type);
        }

        throw new RuntimeException("Unknown expression: " + expr);
    }
}