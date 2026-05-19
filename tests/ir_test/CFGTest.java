package ir_test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.type.Type;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraphGenerator;
import ir.*;
import ir.util.*;

class CFGTest {
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

    @Test
    void singleBlock() throws Exception {
        Method generateBasicBlocks = ControlFlowGraphGenerator.class.getDeclaredMethod("generateBasicBlocks",
                List.class);
        generateBasicBlocks.setAccessible(true);

        // define list of instructions
        List<IrInstruction> ir = List.of(
                new IrInstruction(IrOperator.LABEL, null, null, new IrValue("L1", Type.LABEL)),
                new IrInstruction(IrOperator.ASS, new IrValue("5", Type.INT_T), null, new IrValue("t0", Type.INT_T)),
                new IrInstruction(IrOperator.ASS, new IrValue("8", Type.INT_T), null, new IrValue("t1", Type.INT_T)));

        // this updates blocks on cfgGen.
        generateBasicBlocks.invoke(cfgGen, ir);

        // assert only one block is created.
        assertEquals(1, cfgGen.getBlocks().size());
    }

    @Test
    void newBlockAfterReturn() throws Exception {
        Method generateBasicBlocks = ControlFlowGraphGenerator.class.getDeclaredMethod("generateBasicBlocks",
                List.class);
        generateBasicBlocks.setAccessible(true);

        // temps to use
        IrValue a = new IrValue("t1", Type.FLOAT_T);
        IrValue b = new IrValue("t2", Type.FLOAT_T);
        IrValue c = new IrValue("t3", Type.FLOAT_T);

        List<IrInstruction> ir = List.of(
                new IrInstruction(IrOperator.FUNC_INFO, new IrValue("funFunc", Type.FLOAT_T), null,
                        new IrValue("t0", Type.FLOAT_T)),
                new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null, a),
                new IrInstruction(IrOperator.ASS, new IrValue("3.0", Type.FLOAT_T), null, b),
                new IrInstruction(IrOperator.ADD, a, b, c),
                new IrInstruction(IrOperator.RET, null, null, c),
                new IrInstruction(IrOperator.ASS, new IrValue("3", Type.INT_T), null, new IrValue("t4", Type.INT_T)));

        // this updates blocks on cfgGen.
        generateBasicBlocks.invoke(cfgGen, ir);

        // expected results
        int expBlocks = 2;
        int expBlock1Instr = 5;
        int expBlock2Instr = 1;

        // last instr in 1. block
        IrInstruction lastInstr = cfgGen.getBlocks().get(0).getLastInstruction();

        assertEquals(expBlocks, cfgGen.getBlocks().size());

        assertEquals(expBlock1Instr, cfgGen.getBlocks().get(0).getInstructions().size());
        assertEquals(expBlock2Instr, cfgGen.getBlocks().get(1).getInstructions().size());

        assertEquals(IrOperator.RET, lastInstr.getOperator());
    }

    @Test
    void newBlockAfterLabel() throws Exception {
        Method generateBasicBlocks = ControlFlowGraphGenerator.class.getDeclaredMethod("generateBasicBlocks",
                List.class);
        generateBasicBlocks.setAccessible(true);

        List<IrInstruction> ir = List.of(
                new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null,
                        new IrValue("t0", Type.FLOAT_T)),
                new IrInstruction(IrOperator.ASS, new IrValue("3.4", Type.FLOAT_T), null,
                        new IrValue("t1", Type.FLOAT_T)),
                new IrInstruction(IrOperator.LABEL, null, null, new IrValue("L1", Type.LABEL)),
                new IrInstruction(IrOperator.ADD, new IrValue("t2", Type.INT_T), new IrValue("t3", Type.INT_T),
                        new IrValue("t4", Type.INT_T)));

        // this updates blocks on cfgGen.
        generateBasicBlocks.invoke(cfgGen, ir);

        // expected results
        int expBlocks = 2;

        // first instr in 2. block
        IrInstruction firstInstr = cfgGen.getBlocks().get(1).getInstructions().get(0);

        assertEquals(expBlocks, cfgGen.getBlocks().size());

        assertEquals(IrOperator.LABEL, firstInstr.getOperator());
    }

    @Test
    void noEmptyTrailingBlock() throws Exception {
        Method generateBasicBlocks = ControlFlowGraphGenerator.class.getDeclaredMethod("generateBasicBlocks",
                List.class);
        generateBasicBlocks.setAccessible(true);

        // temps to use
        IrValue a = new IrValue("t1", Type.FLOAT_T);
        IrValue b = new IrValue("t2", Type.FLOAT_T);
        IrValue c = new IrValue("t3", Type.FLOAT_T);

        List<IrInstruction> ir = List.of(
                new IrInstruction(IrOperator.FUNC_INFO, new IrValue("funFunc", Type.FLOAT_T), null,
                        new IrValue("t0", Type.FLOAT_T)),
                new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null, a),
                new IrInstruction(IrOperator.ASS, new IrValue("3.0", Type.FLOAT_T), null, b),
                new IrInstruction(IrOperator.ADD, a, b, c),
                new IrInstruction(IrOperator.RET, null, null, c));

        // this updates blocks on cfgGen.
        generateBasicBlocks.invoke(cfgGen, ir);

        // expected results
        int expBlocks = 1;

        assertEquals(expBlocks, cfgGen.getBlocks().size());
    }

    @Test
    void firstSeparatorMarksEntry() throws Exception {
        Method generateBasicBlocks = ControlFlowGraphGenerator.class.getDeclaredMethod("generateBasicBlocks",
                List.class);
        generateBasicBlocks.setAccessible(true);

        List<IrInstruction> ir = List.of(
                new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null,
                        new IrValue("t0", Type.FLOAT_T)),
                new IrInstruction(IrOperator.RET, null, null, new IrValue("t1", Type.FLOAT_T)),
                new IrInstruction(IrOperator.SEPARATOR, null, null, null),
                new IrInstruction(IrOperator.LABEL, null, null, new IrValue("L0", Type.LABEL)),
                new IrInstruction(IrOperator.ASS, new IrValue("8", Type.INT_T), null, new IrValue("t2", Type.INT_T)));

        // this updates blocks on cfgGen.
        generateBasicBlocks.invoke(cfgGen, ir);

        // expected results
        boolean expEntry = true;
        boolean expNotEntry = false;

        assertEquals(expNotEntry, cfgGen.getBlocks().get(0).getIsEntry());
        assertEquals(expEntry, cfgGen.getBlocks().get(1).getIsEntry());
    }

    @Test
    void createsFallthroughRelation() throws Exception {
        Method generateRelations = ControlFlowGraphGenerator.class.getDeclaredMethod("generateRelations");
        generateRelations.setAccessible(true);

        BasicBlock b1 = new BasicBlock(0);
        b1.addInstruction(new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null,
                new IrValue("t0", Type.FLOAT_T)));

        BasicBlock b2 = new BasicBlock(1);
        b2.addInstruction(new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null,
                new IrValue("t0", Type.FLOAT_T)));

        // update blocks
        cfgGen.getBlocks().addAll(List.of(b1, b2));

        generateRelations.invoke(cfgGen);

        assertTrue(b1.getChildren().contains(b2));
        assertTrue(b2.getParents().contains(b1));
    }

    @Test
    void createsGotoRelation() throws Exception {
        Method generateRelations = ControlFlowGraphGenerator.class.getDeclaredMethod("generateRelations");
        generateRelations.setAccessible(true);

        BasicBlock b1 = new BasicBlock(0);
        b1.addInstruction(new IrInstruction(IrOperator.GOTO, null, null,
                new IrValue("L0", Type.LABEL)));

        BasicBlock b2 = new BasicBlock(1);
        b2.addInstruction(new IrInstruction(IrOperator.ASS, new IrValue("5.2", Type.FLOAT_T), null,
                new IrValue("t0", Type.FLOAT_T)));

        BasicBlock b3 = new BasicBlock(2);
        b3.addInstruction(new IrInstruction(IrOperator.LABEL, null, null,
                new IrValue("L0", Type.LABEL)));

        // update blocks
        cfgGen.getBlocks().addAll(List.of(b1, b2, b3));

        generateRelations.invoke(cfgGen);

        assertTrue(b1.getChildren().contains(b3));
        assertTrue(b3.getParents().contains(b1));

        // assert b1 does not fallthrough to b2.
        assertTrue(b2.getParents().isEmpty());
    }

}