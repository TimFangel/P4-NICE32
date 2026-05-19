package irTest;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.NoValueMatchException;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.Value;
import frontend.symbol_table.VariableSymbol;
import ir.IrFunction;
import ir.IrGenerator;
import ir.IrValue;

class IrGeneratorTest {
    private IrGenerator irGenerator;

    // Runs this method before each test
    @BeforeEach
    void setup() {
        irGenerator = new IrGenerator();
    }

    // Test counters start at 0
    @Test
    void testCounters() {
        Assertions.assertEquals(0, irGenerator.getTempCounter());
        Assertions.assertEquals(0, irGenerator.getLabelCounter());
    }

    // Test current function is null
    @Test
    void testCurrentFunction() {
        Assertions.assertNull(irGenerator.getCurrentFunction());
    }

    // Test that new labels gets created correctly. Followed medium guide
    @Test
    void testNewLabel() throws Exception {

        // Get the private method by name and parameter types
        Method newLabelMethod = IrGenerator.class.getDeclaredMethod("newLabel");

        // Make the private method accessible
        newLabelMethod.setAccessible(true);

        // Invoke the private method
        String label0 = (String) newLabelMethod.invoke(irGenerator);
        String label1 = (String) newLabelMethod.invoke(irGenerator);

        // Check that first label is named L0, second label is L1 and that they are not
        // the same
        Assertions.assertEquals("L0", label0);
        Assertions.assertEquals("L1", label1);
        Assertions.assertNotEquals("Label0:" + label0, "Label1:" + label1);

        System.out.println(label0 + label1);

    }

    // Connects type to t0, t1.. etc
    @Test
    void testNewTempType() throws Exception {

        // Get the private method by name and parameter types
        Method newTempTypeMethod = IrGenerator.class.getDeclaredMethod("newTemp", Type.class);

        // Make the private method accessible
        newTempTypeMethod.setAccessible(true);

        // Invoke the private method
        IrValue temp0 = (IrValue) newTempTypeMethod.invoke(irGenerator, Type.INT_T);
        IrValue temp1 = (IrValue) newTempTypeMethod.invoke(irGenerator, Type.FLOAT_T);

        // Check that first label is named L0, second label is L1 and that they are not
        // the same
        Assertions.assertEquals("t0", temp0.getName());
        Assertions.assertEquals(Type.INT_T, temp0.getType());
        Assertions.assertNotEquals("t8", temp0.getName());
        Assertions.assertNotEquals(Type.FLOAT_T, temp0.getType());

        Assertions.assertEquals("t1", temp1.getName());
        Assertions.assertEquals(Type.FLOAT_T, temp1.getType());

        Assertions.assertNotEquals(temp0, temp1);

    }

    // Connects t0, t1..., to symbols
    @Test
    void testNewTempSymbol() throws Exception {

        VariableSymbol testVariableSymbol = new VariableSymbol("x", Type.INT_T);

        // Get the private method by name and parameter types
        Method newTempSymbolMethod = IrGenerator.class.getDeclaredMethod("newTemp", VariableSymbol.class);

        // Make the private method accessible
        newTempSymbolMethod.setAccessible(true);

        // Invoke the private method
        IrValue temp0 = (IrValue) newTempSymbolMethod.invoke(irGenerator, testVariableSymbol);

        // Check that the name and type is correct
        Assertions.assertEquals("t0", temp0.getName());
        Assertions.assertEquals(Type.INT_T, temp0.getType());

        Assertions.assertNotEquals("t7", temp0.getName());
        Assertions.assertNotEquals(Type.BOOL_T, temp0.getType());

    }

    // Declare function first, then find scope
    @Test
    void testCreateIr() throws Exception {

        // Declare parameter for function
        IrValue paramIrValue = new IrValue("x", Type.INT_T);

        // Declare instance of IrFunction called newFunction with name, parameter and
        // return type
        IrFunction newFunction = new IrFunction("funcTest", paramIrValue,
                Type.INT_T);

        // Create field called currentFunctionField with the getDeclaredField method
        // getDeclaredField method makes access to currentFunction private field
        Field currentFunctionField = IrGenerator.class.getDeclaredField("currentFunction");

        // Make private field accessible
        currentFunctionField.setAccessible(true);

        // Sets the newFunction to currentFunctionField
        currentFunctionField.set(irGenerator, newFunction);

        // Store value of newCurrentFunctionField in
        IrFunction tempField = (IrFunction) currentFunctionField.get(irGenerator);

        // Verify that name and type of parameter is correct
        Assertions.assertEquals("x", paramIrValue.getName());
        Assertions.assertEquals(Type.INT_T, paramIrValue.getType());

        // Verify that function name and return type is correct
        Assertions.assertEquals("funcTest", tempField.getFuncName());
        Assertions.assertEquals(Type.INT_T, tempField.getRetType());

        Assertions.assertNotNull(irGenerator.getCurrentFunction());

    }

    @Test
    void testGenerateValue() throws Exception {

        IntNum intNum = new IntNum(10);
        FloatNum floatNum = new FloatNum(12.5f);
        Bool bool = new Bool(true);

        Method generateValueMethod = IrGenerator.class.getDeclaredMethod("generateValue", Value.class);
        generateValueMethod.setAccessible(true);

        IrValue resultInt = (IrValue) generateValueMethod.invoke(irGenerator, intNum);
        IrValue resultFloat = (IrValue) generateValueMethod.invoke(irGenerator, floatNum);
        IrValue resultBool = (IrValue) generateValueMethod.invoke(irGenerator, bool);

        Assertions.assertEquals("10", resultInt.getName());
        Assertions.assertEquals(Type.INT_T, resultInt.getType());
        Assertions.assertNotEquals("10.7", resultInt.getName());
        Assertions.assertNotEquals(Type.FLOAT_T, resultInt.getType());

        Assertions.assertEquals("12.5", resultFloat.getName());
        Assertions.assertEquals(Type.FLOAT_T, resultFloat.getType());

        Assertions.assertEquals("true", resultBool.getName());
        Assertions.assertEquals(Type.BOOL_T, resultBool.getType());

    }

    @Test
    void testGenerateValueExpectedFail() throws Exception {

        IntNum intNum = new IntNum(10);
        Method generateValueMethod = IrGenerator.class.getDeclaredMethod("generateValue", Value.class);
        generateValueMethod.setAccessible(true);

        IrValue resultInt = (IrValue) generateValueMethod.invoke(irGenerator, intNum);

        Assertions.assertThrows(NoValueMatchException.class, () -> {
            IntNum intNumFail = new IntNum(15);
        }, "No matching value found! Value:");

    }
}
