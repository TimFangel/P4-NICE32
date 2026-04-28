package frontend.semantic_analysis;

import frontend.abstract_syntax.*;
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
            if (symbols.containsKey(d.name)) {
                throw new RuntimeException("Variable already declared: " + d.name);
            }

            Type valueType = checkExpr(d.value);

            if (d.type != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + d.type + " variable '" + d.name + "'");
            }

            symbols.put(d.name, d.type);
            return;
        }

        if (stmt instanceof BlockStmt b) {
            for (Stmt s : b.statements) {
                checkStmt(s);
            }
            return;
        }

        if (stmt instanceof If i) {
            Type condType = checkExpr(i.condition);

            if (condType != Type.BOOL) {
                throw new RuntimeException("If condition must be BOOL, got " + condType);
            }

            checkStmt(i.thenBranch);

            if (i.elseBranch != null) {
                checkStmt(i.elseBranch);
            }

            return;
        }

        if (stmt instanceof Assign a) {
            Type varType = symbols.get(a.name);

            if (varType == null) {
                throw new RuntimeException("Cannot assign to undeclared variable '" + a.name + "'");
            }

            Type valueType = checkExpr(a.value);

            if (varType != valueType) {
                throw new RuntimeException(
                        "Type error: cannot assign " + valueType + " to " + varType + " variable '" + a.name + "'");
            }

            return;
        }

        throw new RuntimeException("Unknown statement: " + stmt);
    }

    private Type checkExpr(Expr expr) {
        if (expr instanceof IntNum) {
            return Type.INT;
        }

        if (expr instanceof FloatNum) {
            return Type.FLOAT;
        }

        if (expr instanceof Var v) {
            Type type = symbols.get(v.name);

            if (type == null) {
                throw new RuntimeException("Undeclared variable: " + v.name);
            }

            return type;
        }

        if (expr instanceof Cast c) {
            Type sourceType = checkExpr(c.expr);
            Type targetType = c.targetType;

            if (sourceType == Type.BOOL || targetType == Type.BOOL) {
                throw new RuntimeException(
                        "Cannot cast between BOOL and other types: " + sourceType + " -> " + targetType);
            }

            if ((sourceType == Type.INT && targetType == Type.FLOAT) ||
                    (sourceType == Type.FLOAT && targetType == Type.INT)) {
                return targetType;
            }

            if (sourceType == targetType) {
                return targetType;
            }

            throw new RuntimeException("Invalid cast: " + sourceType + " -> " + targetType);
        }

        if (expr instanceof Bool) {
            return Type.BOOL;
        }

        if (expr instanceof BinaryOp b) {
            Type left = checkExpr(b.left);
            Type right = checkExpr(b.right);

            switch (b.op) {
                case ADD:
                case SUB:
                case MUL:
                case DIV:
                    if (left != right) {
                        throw new RuntimeException("Type mismatch: " + left + " and " + right);
                    }

                    if (left != Type.INT && left != Type.FLOAT) {
                        throw new RuntimeException("Arithmetic op on non-number: " + left);
                    }

                    return left;

                case AND:
                case OR:
                    if (left != Type.BOOL || right != Type.BOOL) {
                        throw new RuntimeException(
                                "Boolean operator " + b.op + " requires BOOL and BOOL, got " + left + " and " + right);
                    }

                    return Type.BOOL;

                case EQ:
                    if (left != right) {
                        throw new RuntimeException(
                                "Equality requires same types, got " + left + " and " + right);
                    }

                    return Type.BOOL;

                default:
                    throw new RuntimeException("Unknown op: " + b.op);
            }
        }

        throw new RuntimeException("Unknown expression: " + expr);
    }
}