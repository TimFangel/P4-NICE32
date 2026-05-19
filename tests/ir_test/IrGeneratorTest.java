package ir_test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import exception.NoValueMatchException;
import exception.TypeCastException;
import frontend.abstract_syntax.type.Type;
import frontend.abstract_syntax.value.Bool;
import frontend.abstract_syntax.value.FloatNum;
import frontend.abstract_syntax.value.IntNum;
import frontend.abstract_syntax.value.TestValue;
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
    // Method uses a private field so it needs reflection
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

    // Public method, does not need reflection
    // Valid values: Int, float and bool
    @Test
    void testGenerateValueInt() throws Exception {

        IntNum intNum = new IntNum(10);

        IrValue resultInt = irGenerator.generateValue(intNum);

        Assertions.assertEquals("10", resultInt.getName());
        Assertions.assertEquals(Type.INT_T, resultInt.getType());

    }

    @Test
    void testGenerateValueFloat() throws Exception {

        FloatNum floatNum = new FloatNum(12.5f);

        IrValue resultFloat = irGenerator.generateValue(floatNum);
        Assertions.assertEquals("12.5", resultFloat.getName());
        Assertions.assertEquals(Type.FLOAT_T, resultFloat.getType());

    }

    @Test
    void testGenerateValueBool() throws Exception {

        Bool bool = new Bool(true);

        IrValue resultBool = irGenerator.generateValue(bool);
        Assertions.assertEquals("true", resultBool.getName());
        Assertions.assertEquals(Type.BOOL_T, resultBool.getType());

    }

    // LAV TEST FOR NÅR DET ER FORKERT VÆRDI

    @Test
    void testGenerateValueExpectedFail() throws Exception {
        TestValue failValue = new TestValue();

        Assertions.assertThrows(NoValueMatchException.class, () -> {
            irGenerator.generateValue(failValue);
        });
    }

    @Test
    void testGenerateExpr() throws Exception {

    }

    // LAV TEST FOR GENERATE STM

    // LAV TEST FOR GENERATE PROGRAM

    // Public method so it does not need reflection
    @Test
    void testTypeCastSameType() throws Exception {
        IrValue intValue = new IrValue("10", Type.INT_T);

        IrValue result = irGenerator.typeCast(intValue, Type.INT_T);

        Assertions.assertEquals(Type.INT_T, result.getType());
    }

    @Test
    void testTypeCastIntToFloat() throws Exception {
        IrValue intValue = new IrValue("10", Type.INT_T);

        IrValue result = irGenerator.typeCast(intValue, Type.FLOAT_T);

        Assertions.assertEquals(Type.FLOAT_T, result.getType());
    }

    @Test
    void testTypeCastFloatToInt() throws Exception {
        IrValue intValue = new IrValue("9.4", Type.FLOAT_T);

        IrValue result = irGenerator.typeCast(intValue, Type.INT_T);

        Assertions.assertEquals(Type.INT_T, result.getType());
    }

    @Test
    void testTypeCastExpectedFail() throws Exception {
        IrValue boolValue = new IrValue("true", Type.BOOL_T);

        Assertions.assertThrows(TypeCastException.class, () -> {
            irGenerator.typeCast(boolValue, Type.INT_T);
        });
    }

}
