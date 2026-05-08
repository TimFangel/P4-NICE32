package frontend.semantic_analyser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.expression.Expr;
import frontend.semantic_analysis.SemanticAnalyser;
import java.lang.reflect.*;
import frontend.symboltable.SymbolTable;
import frontend.symboltable.Symbol;

/* Documentation = https://www.youtube.com/watch?v=bhhMJSKNCQY */
/* The concept of reflection */
/* Change element of class at runtime */

public class SemanticAnalyserTest {

    @Test
    public void testArithBinaryOpExprReturnsCorrectType()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyser semanticAnalyser = new SemanticAnalyser();

        // Use reflection on method
        Method method = semanticAnalyser.getClass().getDeclaredMethod("visitType", Node.class);
        method.setAccessible(true);

        IntNum leftNum = new IntNum(5);
        IntNum rightNum = new IntNum(4);

        Expr exprLeft = new Operand(0, leftNum);
        Expr exprRight = new Operand(0, rightNum);

        ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

        // Invoking the now public method "checkExpr" on the typechecker with the
        // expression as argument.
        Type type = (Type) method.invoke(semanticAnalyser, arithBinOpexpr);

        Assertions.assertEquals(Type.INT_T, type);

    }

    @Test // Test to ensure that incorrect operands throws exception.
    public void testArithBinaryOpExprThrowsExceptionOnIncorrectOperands()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyser semanticAnalyser = new SemanticAnalyser();

        Assertions.assertThrows(InvocationTargetException.class, () -> {

            // Use reflection on method
            Method method = semanticAnalyser.getClass().getDeclaredMethod("visitType", Node.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            FloatNum rightNum = new FloatNum(4.2f);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

            // Invoking the now public method "checkExpr" on the typechecker with the
            // expression as argument.
            method.invoke(semanticAnalyser, arithBinOpexpr);

        }, "this exception was expected");
    }

    @Test // Test to ensure that division by zero throws exception
    public void testArithBinaryOpExprThrowsExceptionOnZeroDiv()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyser semanticAnalyser = new SemanticAnalyser();

        Assertions.assertThrows(InvocationTargetException.class, () -> {

            // Use reflection on method
            Method method = semanticAnalyser.getClass().getDeclaredMethod("visitType", Node.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(0);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.DIV, exprLeft, exprRight);

            // Invoking the now public method "checkExpr" on the typechecker with the
            // expression as argument.
            method.invoke(semanticAnalyser, arithBinOpexpr);

        }, "this exception was expected");
    }

    /* Waiting for this in semantic_analyser. */
    @Test
    public void testIfStatement()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        Assertions.assertDoesNotThrow(() -> {

            // Use reflection on method
            Method method = typeChecker.getClass().getDeclaredMethod("checkStmt", Stmt.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(4);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            BoolBinaryOpExpr boolBinOpexpr = new BoolBinaryOpExpr(0, BoolBinaryOp.GT, exprLeft, exprRight);

            Decl declOne = new Decl(0, Type.BOOL_T, "y", boolBinOpexpr);
            BlockStmt blockStmt = new BlockStmt(0, declOne);

            Decl declTwo = new Decl(0, Type.BOOL_T, "s", boolBinOpexpr);
            BlockStmt thenStmt = new BlockStmt(0, declTwo);

            IfStmt ifStmt = new IfStmt(0, boolBinOpexpr, thenStmt, blockStmt);

            // Invoking the now public method "checkStmt" on the typechecker with the
            // statement as argument.
            method.invoke(typeChecker, ifStmt);

        });
    }

    @Test
    public void testIfStatementThrowsExceptionOnInvalidCondition()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        Assertions.assertThrows(InvocationTargetException.class, () -> {

            // Use reflection on method
            Method method = typeChecker.getClass().getDeclaredMethod("checkStmt", Stmt.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(4);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

            Decl decl = new Decl(0, Type.INT_T, "y", arithBinOpexpr);
            BlockStmt blockStmt = new BlockStmt(0, decl);

            IfStmt ifStmt = new IfStmt(0, arithBinOpexpr, null, blockStmt);

            // Invoking the now public method "checkStmt" on the typechecker with the
            // statement as argument.
            method.invoke(typeChecker, ifStmt);

        }, "this exception was expected");
    }

}
