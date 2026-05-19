package backend.assembly_generator;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import backend.InstructionGenerator;
import backend.processors.ArithmeticProcessor;
import backend.processors.IfStatementProcessor;
import backend.processors.JumpProcessor;
import exception.NonRegisterArgsException;
import exception.NonRegisterResultException;
import exception.RegisterException;
import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;


public class InstructionGeneratorTest {
    
    @Test
    public void testIfStatementReturnsCorrectXtensa() {

        Assertions.assertDoesNotThrow(() -> {

        IrValue Result = new IrValue("L1", Type.LABEL);
        IrValue Arg1 = new IrValue("b3", Type.B_REG);

        IfStatementProcessor processor = new IfStatementProcessor(Result, Arg1);

        String result = processor.handleIf();

        // ---- Expected ------------------------
        String expected = "BF b3, .L1";

        Assertions.assertEquals(expected,result);

        });
    }

    @Test
    public void testIfStatementThrowsExceptionOnInvalidRegister() {

        Assertions.assertThrows(NonRegisterArgsException.class, () -> {
        
        IrValue Result = new IrValue("L1", Type.LABEL);
        IrValue Arg1 = new IrValue("a3", Type.A_REG);

        IfStatementProcessor processor = new IfStatementProcessor(Result, Arg1);

        processor.handleIf();

        }, "This exception was expected");
    }

    @Test
    public void testUpdateTypesModifiesCorrect() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        Assertions.assertDoesNotThrow(() -> {

        IrValue arg1 = new IrValue("a4", Type.INT_T);
        IrValue arg2 = new IrValue("a5", Type.INT_T);
        IrValue result = new IrValue("b2", Type.BOOL_T);

        IrInstruction ii = new IrInstruction(IrOperator.GEQ, arg1, arg2, result);

        InstructionGenerator ig = new InstructionGenerator(ii);

        Method method = ig.getClass().getDeclaredMethod("updateTypes");
        method.setAccessible(true);
        method.invoke(ig);

        // Actual
        Type arg1Type = arg1.getType();
        Type arg2Type = arg2.getType();
        Type resultType = result.getType();


        Assertions.assertEquals(Type.A_REG, arg1Type);
        Assertions.assertEquals(Type.A_REG, arg2Type);
        Assertions.assertEquals(Type.B_REG, resultType);
        });
    }

    @Test
    public void testArithADDExpressionReturnsCorrectXtensa() {

        Assertions.assertDoesNotThrow(() -> {

        IrValue Result = new IrValue("a10", Type.A_REG);
        IrValue Arg1 = new IrValue("a3", Type.A_REG);
        IrValue Arg2 = new IrValue("a8", Type.A_REG);
        IrOperator op = IrOperator.ADD;

        ArithmeticProcessor processor = new ArithmeticProcessor(Result, Arg1, Arg2, op);

        String result = processor.handleExpression();

        // ---- Expected ------------------------
        String expected = "ADD a10, a3, a8";

        Assertions.assertEquals(expected,result);

        });
    }

    @Test
    public void testArithADDExpressionThrowsExceptionOnInvalidRegister() {

        Assertions.assertThrows(NonRegisterResultException.class,() -> {

        IrValue Result = new IrValue("b10", Type.B_REG);
        IrValue Arg1 = new IrValue("b3", Type.B_REG);
        IrValue Arg2 = new IrValue("b8", Type.B_REG);
        IrOperator op = IrOperator.ADD;

        ArithmeticProcessor processor = new ArithmeticProcessor(Result, Arg1, Arg2, op);

        processor.handleExpression();

        }, "This exception was expected");
    }

    @Test
    public void testIfHandleJumpReturnsCorrectXtensa() {

        Assertions.assertDoesNotThrow(() -> {

        IrValue result = new IrValue("L1", Type.LABEL);

        JumpProcessor processor = new JumpProcessor(result);

        String returnResult = processor.handleJump();

        // ---- Expected ------------------------
        String expected = "J .L1";

        Assertions.assertEquals(expected,returnResult);

        });
    }

    @Test
    public void testIfHandleJumpThrowsCorrectException() {

        Assertions.assertThrows( RegisterException.class,() -> {

        IrValue Result = new IrValue("a2", Type.A_REG);

        JumpProcessor processor = new JumpProcessor(Result);

        processor.handleJump();

        },"This exception was expected");
    }
}
