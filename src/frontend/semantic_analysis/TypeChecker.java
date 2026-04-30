package frontend.semantic_analysis;

import frontend.abstract_syntax.component.Component;
import frontend.abstract_syntax.component.constants.component_types.ProtocolType;
import frontend.abstract_syntax.expression.Cast;
import frontend.abstract_syntax.expression.Expr;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolExpr;
import frontend.abstract_syntax.program.Program;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.Ident;
import frontend.abstract_syntax.value.IdentDual;
import frontend.abstract_syntax.value.IdentSingle;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;

import java.util.*;

public class TypeChecker {
    // TODO: Lige p.t. er dette vores symboltable. Har ikke fået kigget på det som
    // Skipper arbejdede på.
    private final Map<String, Type> symbols = new HashMap<>();

    public void check(Program program) {
        checkStmt(program.setup);
        checkStmt(program.functions);
        checkStmt(program.main);
    }

    private void checkStmt(Stmt stmt) {
        if (stmt instanceof Decl d) {
            Ident identifier = d.getIdentifier();
            String name = null;

            if (identifier instanceof IdentSingle single) {
                name = single.name();
            } else if (identifier instanceof IdentDual dual) {
                name = dual.parentName() + "." + dual.childName();
            }

            if (symbols.containsKey(name)) {
                throw new RuntimeException("Variable already declared: " + d.getIdentifier());
            }

            Type valueType = checkExpr(d.getValue());

            if (d.getType() != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + d.getType() + " variable '"
                                + d.getIdentifier()
                                + "'");
            }

            symbols.put(name, d.getType());
            return;
        }

        if (stmt instanceof BlockStmt b) {
            for (Stmt s : b.statements) {
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
            Ident identifier = a.getIdentifier();
            String name = null;

            if (identifier instanceof IdentSingle single) {
                name = single.name();
            } else if (identifier instanceof IdentDual dual) {
                name = dual.parentName() + "." + dual.childName();
            }

            Type varType = symbols.get(name);

            if (varType == null) {
                throw new RuntimeException("Cannot assign to undeclared variable '" + name + "'");
            }

            Type valueType = checkExpr(a.getValue());

            if (varType != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + varType + " variable '" + name
                                + "'");
            }

            return;
        }

        if (stmt instanceof Component c) {
            Type portType = checkExpr(c.getPort());

            if (portType != Type.INT_T) {
                throw new RuntimeException("Port must be of type INT, got " + portType);
            }

            if (c.getProtocol() == null || c.getProtocol().getProtocol() == null) {
                throw new RuntimeException("Protocol must be of type INT, got " +
                        c.getProtocol().getProtocol());
            }

            if (c.getInterval() instanceof Operand o && o.getValue() instanceof IntNum n && n.value() >= 0) {
                // Valid interval.
            } else {
                throw new RuntimeException("Interval must be of type INT, got " + c.getInterval());
            }

            if (c.getDirection() == null || c.getDirection().getDirection() == null) {
                throw new RuntimeException("Direction must be INPUT or OUTPUT, got " + c.getDirection().getDirection());
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

            if (value instanceof Ident id) {
                String name = null;

                if (id instanceof IdentSingle single) {
                    name = single.name();
                } else if (id instanceof IdentDual dual) {
                    name = dual.parentName() + "." + dual.childName();
                }

                Type type = symbols.get(name);

                if (type == null) {
                    throw new RuntimeException("Undeclared variable: " + name);
                }

                return type;
            }

            throw new RuntimeException("Unknown operand value: " + value);
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

        // Check boolean expressions.
        if (expr instanceof BoolExpr) {
            return Type.BOOL_T;
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

                case EQ:
                    if (left != right) {
                        throw new RuntimeException(
                                "Equality requires same types, got " + left + " and " + right);
                    }

                    return Type.BOOL_T;

                default:
                    throw new RuntimeException("Unknown op: " + b.getOp());
            }
        }

        // Check arithmetic binary expressions.
        if (expr instanceof ArithBinaryOpExpr a) {
            Type left = checkExpr(a.getExprLeft());
            Type right = checkExpr(a.getExprRight());

            switch (a.getOp()) {
                case ADD, SUB, MUL, DIV:
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

        throw new RuntimeException("Unknown expression: " + expr);
    }
}