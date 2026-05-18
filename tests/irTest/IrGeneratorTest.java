package irTest;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.type.Type;
import frontend.symbol_table.VariableSymbol;
import ir.IrFunction;
import ir.IrGenerator;
import ir.IrInstructionInterface;
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
    void testNewLabel() {
        try {
            // Get the private method by name and parameter types
            Method newLabelMethod = IrGenerator.class.getDeclaredMethod("newLabel", String.class);

            // Make the private method accessible
            newLabelMethod.setAccessible(true);

            // Invoke the private method
            String label0 = (String) newLabelMethod.invoke(irGenerator, "label0");
            String label1 = (String) newLabelMethod.invoke(irGenerator, "label1");

            // Check that first label is named L0, second label is L1 and that they are not
            // the same
            Assertions.assertEquals("L0", label0);
            Assertions.assertEquals("L1", label1);
            Assertions.assertNotEquals(label0, label1);

            System.out.println(label0 + label1);

            // Standard exception messages
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Cannot access method: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Method threw an exception: " + e.getCause());
        }

    }

    // Connects type to t0, t1.. etc
    @Test
    void testNewTempType() {
        try {
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
            Assertions.assertEquals("t1", temp1.getName());
            Assertions.assertEquals(Type.FLOAT_T, temp1.getType());
            Assertions.assertNotEquals(temp0, temp1);

            // Standard exception messages
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Cannot access method: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Method threw an exception: " + e.getCause());
        }

    }

    // Connects t0, t1..., to symbols
    @Test
    void testNewTempSymbol() {
        try {
            VariableSymbol testVariableSymbol = new VariableSymbol("x", Type.INT_T);

            // Get the private method by name and parameter types
            Method newTempSymbolMethod = IrGenerator.class.getDeclaredMethod("newTemp", VariableSymbol.class);

            // Make the private method accessible
            newTempSymbolMethod.setAccessible(true);

            // Invoke the private method
            IrValue temp0 = (IrValue) newTempSymbolMethod.invoke(irGenerator, testVariableSymbol);

            // Check that first label is named L0, second label is L1 and that they are not
            // the ½
            Assertions.assertEquals("t0", temp0.getName());
            Assertions.assertEquals(Type.INT_T, temp0.getType());

            // Standard exception messages
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Cannot access method: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Method threw an exception: " + e.getCause());
        }

    }

    @Test
    void testCreateIr() {
        try {

            IrFunction testCurrentFunction = new IrFunction("funcTest", null, Type.INT_T);

            // Get the private field
            Field[] newCurrentFunctionField = IrGenerator.class.getDeclaredField("currentFunction");

            newCurrentFunctionField.setAccessible(true);

            // Get the private method by name and parameter types
            Method newCreateIrMethod = IrGenerator.class.getDeclaredMethod("createIr", IrInstructionInterface.class);

            // Make the private method accessible
            newCreateIrMethod.setAccessible(true);

            IrFunction currenFunction = new IrFunction(null, null, null);

            // Invoke the private method
            IrInstructionInterface temp0 = (IrInstructionInterface) newCreateIrMethod.invoke(irGenerator);

            Assertions.assertEquals(null, temp0.get());

            // Standard exception messages
        } catch (NoSuchMethodException e) {
            System.err.println("Method not found: " + e.getMessage());
        } catch (IllegalAccessException e) {
            System.err.println("Cannot access method: " + e.getMessage());
        } catch (InvocationTargetException e) {
            System.err.println("Method threw an exception: " + e.getCause());
        }

    }

    @Test
    void testGenerateValue() {

    }
}
