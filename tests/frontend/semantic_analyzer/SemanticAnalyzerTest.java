package frontend.semantic_analyzer;

import org.junit.jupiter.api.Test;

import exception.NonMatchingTypeException;

import org.junit.jupiter.api.Assertions;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.expression.arith_expression.ArithBinaryOpExpr;
import frontend.abstract_syntax.expression.bool_expression.BoolBinaryOpExpr;
import frontend.abstract_syntax.expression.enums.ArithBinaryOp;
import frontend.abstract_syntax.expression.enums.BoolBinaryOp;
import frontend.abstract_syntax.statement.BlockStmt;
import frontend.abstract_syntax.statement.Decl;
import frontend.abstract_syntax.statement.Stmt;
import frontend.abstract_syntax.statement.main_statement.AssStmt;
import frontend.abstract_syntax.statement.main_statement.IfStmt;
import frontend.abstract_syntax.expression.Operand;
import frontend.abstract_syntax.expression.VarExpr;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.Node;
import frontend.abstract_syntax.expression.Expr;
import frontend.semantic_analysis.SemanticAnalyzer;
import frontend.symbol_table.Symbol;
import frontend.symbol_table.SymbolTable;

import java.lang.reflect.*;

/* Documentation = https://www.youtube.com/watch?v=bhhMJSKNCQY */
/* Documentation = https://www.baeldung.com/java-lang-reflect-invocationtargetexception */
/* The concept of reflection */
/* Change element of class at runtime */

class SemanticAnalyzerTest {

    @Test
    public void testArithBinaryOpExprReturnsCorrectType()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        // Use reflection on method
        Method method = semanticAnalyzer.getClass().getDeclaredMethod("visitType", Node.class);
        method.setAccessible(true);

        IntNum leftNum = new IntNum(5);
        IntNum rightNum = new IntNum(4);

        Expr exprLeft = new Operand(0, leftNum);
        Expr exprRight = new Operand(0, rightNum);

        ArithBinaryOpExpr arithBinOpExpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

        // Invoking
        Type type = (Type) method.invoke(semanticAnalyzer, arithBinOpExpr);

        Assertions.assertEquals(Type.INT_T, type);

    }

    @Test // Test to ensure that incorrect operands throws correct exception.
    public void testArithBinaryOpExprThrowsExceptionOnIncorrectOperands()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        Assertions.assertThrows(NonMatchingTypeException.class, () -> {
            try {
                // Use reflection on method
                Method method = semanticAnalyzer.getClass().getDeclaredMethod("visitType", Node.class);
                method.setAccessible(true);

                IntNum leftNum = new IntNum(5);
                FloatNum rightNum = new FloatNum(4.2f);

                Expr exprLeft = new Operand(0, leftNum);
                Expr exprRight = new Operand(0, rightNum);

                ArithBinaryOpExpr arithBinOpExpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

                // Invoking
                method.invoke(semanticAnalyzer, arithBinOpExpr);

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NonMatchingTypeException) {
                    throw (NonMatchingTypeException) cause;
                }
                throw e;
            }

        }, "this exception was expected");
    }

    @Test // Test to ensure that division by zero throws exception Not yet implemented in
          // SemanticAnalyzer
    public void testArithBinaryOpExprThrowsExceptionOnZeroDiv()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        Assertions.assertThrows(InvocationTargetException.class, () -> {

            // Use reflection on method
            Method method = semanticAnalyzer.getClass().getDeclaredMethod("visitType", Node.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(0);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpExpr = new ArithBinaryOpExpr(0, ArithBinaryOp.DIV, exprLeft, exprRight);

            // Invoking
            method.invoke(semanticAnalyzer, arithBinOpExpr);

        }, "this exception was expected");
    }

    @Test
    public void testIfStatementOnLiteralCondition()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        Assertions.assertDoesNotThrow(() -> {

            // Use reflection on method
            Method method = semanticAnalyzer.getClass().getDeclaredMethod("visit", IfStmt.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(4);

            Operand operandLeft = new Operand(0, leftNum);
            Operand operandRight = new Operand(0, rightNum);

            BoolBinaryOpExpr boolBinOpExpr = new BoolBinaryOpExpr(0, BoolBinaryOp.GT, operandLeft, operandRight);

            Decl declOne = new Decl(0, Type.BOOL_T, "y", boolBinOpExpr);
            BlockStmt blockStmt = new BlockStmt(0, declOne);

            Decl declTwo = new Decl(0, Type.BOOL_T, "s", boolBinOpExpr);
            BlockStmt thenStmt = new BlockStmt(0, declTwo);

            IfStmt ifStmt = new IfStmt(0, boolBinOpExpr, thenStmt, blockStmt);

            // Invoking
            method.invoke(semanticAnalyzer, ifStmt);

        });
    }

    @Test
    public void testIfStatementOnVarCondition()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        Assertions.assertDoesNotThrow(() -> {

            /*
             * BUILD first declaration
             * -----------------------------------------------------------------------------
             * ------
             */
            // Use reflection on method
            Method methodDeclLeft = semanticAnalyzer.getClass().getDeclaredMethod("visit", Decl.class);
            methodDeclLeft.setAccessible(true);

            IntNum leftNum = new IntNum(5);

            Operand operandLeft = new Operand(0, leftNum);

            Decl leftDec = new Decl(0, Type.INT_T, "l", operandLeft);

            // Invoking
            methodDeclLeft.invoke(semanticAnalyzer, leftDec);
            /*
             * END first declaration
             * -----------------------------------------------------------------------------
             * ------
             */

            /*
             * BUILD second declaration
             * -----------------------------------------------------------------------------
             * ------
             */
            // Use reflection on method
            Method methodDeclRight = semanticAnalyzer.getClass().getDeclaredMethod("visit", Decl.class);
            methodDeclRight.setAccessible(true);

            IntNum rightNum = new IntNum(4);

            Operand operandRight = new Operand(0, rightNum);

            Decl rightDec = new Decl(0, Type.INT_T, "r", operandRight);

            // Invoking
            methodDeclRight.invoke(semanticAnalyzer, rightDec);
            /*
             * END second declaration
             * -----------------------------------------------------------------------------
             * ------
             */

            /*
             * BUILD first variable expression
             * -----------------------------------------------------------------------------
             * ------
             */
            Method methodDeclVarLeft = semanticAnalyzer.getClass().getDeclaredMethod("visitType", VarExpr.class);
            methodDeclVarLeft.setAccessible(true);

            VarExpr leftVar = new VarExpr(0, "l");

            // Invoking
            methodDeclVarLeft.invoke(semanticAnalyzer, leftVar);
            /*
             * END first variable expression
             * -----------------------------------------------------------------------------
             * ------
             */

            /*
             * BUILD second variable expression
             * -----------------------------------------------------------------------------
             * ------
             */
            Method methodDeclVarRight = semanticAnalyzer.getClass().getDeclaredMethod("visitType", VarExpr.class);
            methodDeclVarRight.setAccessible(true);

            VarExpr rightVar = new VarExpr(0, "r");

            // Invoking
            methodDeclVarRight.invoke(semanticAnalyzer, rightVar);
            /*
             * END second variable expression
             * -----------------------------------------------------------------------------
             * ------
             */

            /* BUILD the if statement */

            // Use reflection on method
            Method method = semanticAnalyzer.getClass().getDeclaredMethod("visit", IfStmt.class);
            method.setAccessible(true);

            BoolBinaryOpExpr boolBinOpExpr = new BoolBinaryOpExpr(0, BoolBinaryOp.GT, leftVar, rightVar);

            Decl declOne = new Decl(0, Type.BOOL_T, "y", boolBinOpExpr);
            BlockStmt blockStmt = new BlockStmt(0, declOne);

            Decl declTwo = new Decl(0, Type.BOOL_T, "s", boolBinOpExpr);
            BlockStmt thenStmt = new BlockStmt(0, declTwo);

            IfStmt ifStmt = new IfStmt(0, boolBinOpExpr, thenStmt, blockStmt);

            // Invoking
            method.invoke(semanticAnalyzer, ifStmt);

        });
    }

    @Test
    public void testIfStatementThrowsExceptionOnInvalidCondition()
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();

        Assertions.assertThrows(NonMatchingTypeException.class, () -> {
            try {
                // Use reflection on method
                Method method = semanticAnalyzer.getClass().getDeclaredMethod("visit", Node.class);
                method.setAccessible(true);

                IntNum leftNum = new IntNum(5);
                IntNum rightNum = new IntNum(4);

                Operand exprLeft = new Operand(0, leftNum);
                Operand exprRight = new Operand(0, rightNum);

                ArithBinaryOpExpr arithBinOpExpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);

                Decl decl = new Decl(0, Type.INT_T, "y", arithBinOpExpr);
                BlockStmt blockStmt = new BlockStmt(0, decl);

                IfStmt ifStmt = new IfStmt(0, arithBinOpExpr, null, blockStmt);

                // Invoking
                method.invoke(semanticAnalyzer, ifStmt);

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof NonMatchingTypeException) {
                    throw cause;
                } else {
                    throw cause;
                }
            }

        }, "this exception was expected");
    }

}
