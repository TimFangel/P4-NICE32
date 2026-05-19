package ir_test;

import java.lang.reflect.Method;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import ir.cfg.ControlFlowGraphGenerator;
import ir.*;
import ir.util.*;

class ControlFlowGraphGeneratorTest {
    private ControlFlowGraphGenerator cfgGen;

    @BeforeEach
    void setup() {
        cfgGen = new ControlFlowGraphGenerator();
    }

    @Test
    void isLeaderTrue() throws Exception {
        Method isLeader = ControlFlowGraphGenerator.class.getDeclaredMethod("isLeader", IrInstruction.class);
        isLeader.setAccessible(true);
        IrInstruction instr = new IrInstruction(IrOperator.LABEL, null, null, null);

        boolean expected = true;
        boolean result = (boolean) isLeader.invoke(cfgGen, instr);

        Assertions.assertEquals(expected, result);
    }

    @Test
    void isLeaderFalse() throws Exception {
        Method isLeader = ControlFlowGraphGenerator.class.getDeclaredMethod("isLeader", IrInstruction.class);

        isLeader.setAccessible(true);

        for (IrOperator op : IrOperator.values()) {

            // Skip the only operator expected to return true
            if (op == IrOperator.LABEL) {
                continue;
            }

            IrInstruction instr = new IrInstruction(op, null, null, null);

            boolean result = (boolean) isLeader.invoke(cfgGen, instr);

            Assertions.assertFalse(
                    result,
                    "Expected false for operator: " + op);
        }
    }

    @Test
    void isTerminatorTrue() throws Exception {
        Method isTerminator = ControlFlowGraphGenerator.class.getDeclaredMethod("isTerminator", IrInstruction.class);
        isTerminator.setAccessible(true);
        IrInstruction instr1 = new IrInstruction(IrOperator.RET, null, null, null);
        IrInstruction instr2 = new IrInstruction(IrOperator.IF_FALSE, null, null, null);
        IrInstruction instr3 = new IrInstruction(IrOperator.GOTO, null, null, null);

        boolean expected = true;
        boolean result1 = (boolean) isTerminator.invoke(cfgGen, instr1);
        boolean result2 = (boolean) isTerminator.invoke(cfgGen, instr2);
        boolean result3 = (boolean) isTerminator.invoke(cfgGen, instr3);

        Assertions.assertEquals(expected, result1);
        Assertions.assertEquals(expected, result2);
        Assertions.assertEquals(expected, result3);
    }

    @Test
    void isTerminatorFalse() throws Exception {
        Method isTerminator = ControlFlowGraphGenerator.class.getDeclaredMethod("isTerminator", IrInstruction.class);

        isTerminator.setAccessible(true);

        for (IrOperator op : IrOperator.values()) {

            // Skip operators expected to return true.
            if (op == IrOperator.IF_FALSE || op == IrOperator.GOTO || op == IrOperator.RET) {
                continue;
            }

            IrInstruction instr = new IrInstruction(op, null, null, null);

            boolean result = (boolean) isTerminator.invoke(cfgGen, instr);

            Assertions.assertFalse(
                    result,
                    "Expected false for operator: " + op);
        }
    }
}