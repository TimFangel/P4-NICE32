package ir_test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.type.Type;
import ir.IrInstruction;
import ir.IrValue;
import ir.util.IrOperator;

class IrInstructionTest {

    @Test
    void findGenKillLabel() {
        IrValue label = new IrValue("L1", Type.LABEL);
        // calls findGenKill in constructor.
        IrInstruction instr = new IrInstruction(IrOperator.LABEL, null, null, label);
        Set<String> emptySet = new HashSet<>();

        // Assert gen and kill are empty, since label should not generate any.
        assertEquals(instr.getGen(), emptySet);
        assertEquals(instr.getKill(), emptySet);
    }

    @Test
    void findGenKillCompw() {
        IrValue variable = new IrValue("t1", Type.INT_T);
        IrValue port = new IrValue("t2", Type.INT_T);
        IrValue interval = new IrValue("t3", Type.INT_T);

        // calls findGenKill in constructor.
        IrInstruction instr = new IrInstruction(IrOperator.COMPW, port, interval, variable);
        // gen should contain all temps in instruction
        Set<String> gen = new HashSet<>();
        gen.add("t1");
        gen.add("t2");
        gen.add("t3");

        // kill should be empty
        Set<String> kill = new HashSet<>();

        assertEquals(instr.getGen(), gen);
        assertEquals(instr.getKill(), kill);
    }

    @Test
    void findGenKillRet() {
        // same case as COMPW
        IrValue variable = new IrValue("t1", Type.INT_T);
        IrValue arg1 = new IrValue("t2", Type.INT_T);
        IrValue arg2 = new IrValue("t3", Type.INT_T);

        // calls findGenKill in constructor.
        IrInstruction instr = new IrInstruction(IrOperator.RET, arg1, arg2, variable);
        // gen should contain all temps in instruction
        Set<String> gen = new HashSet<>();
        gen.add("t1");
        gen.add("t2");
        gen.add("t3");

        // kill should be empty
        Set<String> kill = new HashSet<>();

        assertEquals(instr.getGen(), gen);
        assertEquals(instr.getKill(), kill);
    }

    @Test
    void findGenKillNormalInstruction() {
        IrValue arg1 = new IrValue("t1", Type.INT_T);
        IrValue arg2 = new IrValue("t2", Type.FLOAT_T);
        IrValue result = new IrValue("t3", Type.BOOL_T);

        // Any normal operator that is not RET, COMPW, or FUNC_INFO
        IrInstruction instr = new IrInstruction(IrOperator.ADD, arg1, arg2, result);

        Set<String> gen = new HashSet<>();
        gen.add("t1");
        gen.add("t2");

        Set<String> kill = new HashSet<>();
        kill.add("t3");

        assertEquals(gen, instr.getGen());
        assertEquals(kill, instr.getKill());
    }

    @Test
    void findGenKillFuncInfoSpecialCase() {
        IrValue arg1 = new IrValue("t1", Type.INT_T);
        IrValue arg2 = new IrValue("t2", Type.FUNCTION);
        IrValue result = new IrValue("t3", Type.INT_T);

        IrInstruction instr = new IrInstruction(IrOperator.FUNC_INFO, arg1, arg2, result);

        Set<String> gen = new HashSet<>();
        gen.add("t1");

        Set<String> kill = new HashSet<>();
        kill.add("t2"); // special FUNC_INFO case
        kill.add("t3");

        assertEquals(gen, instr.getGen());
        assertEquals(kill, instr.getKill());
    }

    @Test
    void findGenKillIgnoresNonTemps() {
        IrValue arg1 = new IrValue("x", Type.INT_T);
        IrValue arg2 = new IrValue("temp", Type.INT_T);
        IrValue result = new IrValue("result", Type.INT_T);

        IrInstruction instr = new IrInstruction(IrOperator.ADD, arg1, arg2, result);

        Set<String> emptySet = new HashSet<>();

        assertEquals(emptySet, instr.getGen());
        assertEquals(emptySet, instr.getKill());
    }

    @Test
    void findGenKillIgnoresUnsupportedTypes() {
        IrValue arg1 = new IrValue("t1", Type.LABEL);
        IrValue arg2 = new IrValue("t2", Type.LABEL);
        IrValue result = new IrValue("t3", Type.LABEL);

        IrInstruction instr = new IrInstruction(IrOperator.ADD, arg1, arg2, result);

        Set<String> emptySet = new HashSet<>();

        assertEquals(emptySet, instr.getGen());
        assertEquals(emptySet, instr.getKill());
    }

    @Test
    void findGenKillHandlesNullOperands() {
        IrValue result = new IrValue("t1", Type.INT_T);

        IrInstruction instr = new IrInstruction(IrOperator.ADD, null, null, result);

        Set<String> gen = new HashSet<>();

        Set<String> kill = new HashSet<>();
        kill.add("t1");

        assertEquals(gen, instr.getGen());
        assertEquals(kill, instr.getKill());
    }

    @Test
    void findGenKillRequiresDigitAfterT() {
        IrValue arg1 = new IrValue("ta", Type.INT_T);
        IrValue arg2 = new IrValue("t_", Type.INT_T);
        IrValue result = new IrValue("tb", Type.INT_T);

        IrInstruction instr = new IrInstruction(IrOperator.ADD, arg1, arg2, result);

        Set<String> emptySet = new HashSet<>();

        assertEquals(emptySet, instr.getGen());
        assertEquals(emptySet, instr.getKill());
    }
}