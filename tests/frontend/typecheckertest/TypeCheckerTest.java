package frontend.typecheckertest;

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
import frontend.abstract_syntax.expression.Expr;
import frontend.semantic_analysis.TypeChecker;
import java.lang.reflect.*;

/* Documentation = https://www.youtube.com/watch?v=bhhMJSKNCQY */
/* The concept of reflection */
/* Change element of class at runtime */

public class TypeCheckerTest {

    @Test
    public void testCheckExprReturnsCorrectTypeOnArithBinaryOpExpr() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        // Use reflection on method 
        Method method = typeChecker.getClass().getDeclaredMethod("checkExpr", Expr.class);
        method.setAccessible(true);

        IntNum leftNum = new IntNum(5);
        IntNum rightNum = new IntNum(4);

        Expr exprLeft = new Operand(0, leftNum);
        Expr exprRight = new Operand(0, rightNum);

        ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);
        
        // Invoking the now public method "checkExpr" on the typechecker with the expression as argument.
        Type type = (Type)method.invoke(typeChecker, arithBinOpexpr);

        Assertions.assertEquals(Type.INT_T, type);

    }

    @Test // Test to ensure that incorrect operands throws exception.
    public void testCheckExprThrowsExceptionOnArithBinaryOpExpr() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        Assertions.assertThrows(RuntimeException.class, () -> {

            // Use reflection on method 
            Method method = typeChecker.getClass().getDeclaredMethod("checkExpr", Expr.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            FloatNum rightNum = new FloatNum(4.2f);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.ADD, exprLeft, exprRight);
        
            // Invoking the now public method "checkExpr" on the typechecker with the expression as argument.
            method.invoke(typeChecker, arithBinOpexpr);

        }, "this exception was expected");
    }

    @Test // Test to ensure that division by zero throws exception
    public void testCheckExprThrowsExceptionOnArithBinaryOpExprZeroDiv() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        Assertions.assertThrows(RuntimeException.class, () -> {

            // Use reflection on method 
            Method method = typeChecker.getClass().getDeclaredMethod("checkExpr", Expr.class);
            method.setAccessible(true);

            IntNum leftNum = new IntNum(5);
            IntNum rightNum = new IntNum(0);

            Expr exprLeft = new Operand(0, leftNum);
            Expr exprRight = new Operand(0, rightNum);

            ArithBinaryOpExpr arithBinOpexpr = new ArithBinaryOpExpr(0, ArithBinaryOp.DIV, exprLeft, exprRight);
        
            // Invoking the now public method "checkExpr" on the typechecker with the expression as argument.
            method.invoke(typeChecker, arithBinOpexpr);

        }, "this exception was expected");
    }
    
    @Test
    public void TestCheckExprReturnsCorrectTypeOnOperand() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        // Use reflection on method 
        Method method = typeChecker.getClass().getDeclaredMethod("checkExpr", Expr.class);
        method.setAccessible(true);

        FloatNum floatVal = new FloatNum(2.2f);

        Operand op = new Operand(0, floatVal);

        // Invoking the now public method "checkExpr" on the typechecker with the expression as argument.
        Type type = (Type)method.invoke(typeChecker, op);

        Assertions.assertEquals(Type.FLOAT_T, type);

    }

    @Test
    public void testIfStatement() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        Assertions.assertDoesNotThrow(() -> {

        // Use reflection on method 
        Method method = typeChecker.getClass().getDeclaredMethod("checkStmt", Stmt.class);
        method.setAccessible(true);

        IntNum leftNum = new IntNum(5);
        IntNum rightNum = new IntNum(4);

        Expr exprLeft = new Operand(0, leftNum);
        Expr exprRight = new Operand(0, rightNum);

        BoolBinaryOpExpr boolBinOpexpr = new BoolBinaryOpExpr(0, BoolBinaryOp.GT, exprLeft, exprRight);

        Decl decl = new Decl(0, Type.BOOL_T, "y", boolBinOpexpr);
        BlockStmt blockStmt = new BlockStmt(0, decl);

        IfStmt ifStmt = new IfStmt(0, boolBinOpexpr, null, blockStmt);

        // Invoking the now public method "checkStmt" on the typechecker with the statement as argument.
        method.invoke(typeChecker, ifStmt);

        });
    }

    @Test
    public void testIfStatementThrowsExceptionOnInvalidCondition() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {

        TypeChecker typeChecker = new TypeChecker();

        Assertions.assertThrows(RuntimeException.class, () -> {

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

        // Invoking the now public method "checkStmt" on the typechecker with the statement as argument.
        method.invoke(typeChecker, ifStmt);

        }, "this exception was expected");
    }


   
}
    
