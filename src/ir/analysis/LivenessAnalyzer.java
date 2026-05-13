package ir.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.IrInstruction;
import ir.IrInstructionInterface;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;

public class LivenessAnalyzer {
    public LivenessAnalyzer(ControlFlowGraph cfg) {
        // kald gen kill metode
        // Step 1: find gen/kill for instructions
        // Step 2: find gen/kill for blocks
        // Step 3: do fixed-point analysis for blocks
        cfg = fixedPointAnalysis(cfg);
        // Step 4: do fixed-point analysis for instructions (?)
        cfg = instructionLevelLiveness(cfg);
        // Step 5: find interference

    }

    private void blockGenKill(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();
    }

    private ControlFlowGraph fixedPointAnalysis(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks().reversed();

        boolean pointsHaveChanged = true;

        while (pointsHaveChanged) {
            pointsHaveChanged = false;

            for (BasicBlock b : blocks) {
                Set<String> oldIn = new HashSet<>(b.getIn());
                Set<String> oldOut = new HashSet<>(b.getOut());

                // Find live_out.
                b.clearOut();
                for (BasicBlock s : b.getChildren()) { // successor kan vel godt være i samme blok?
                    b.addOut(s.getIn());
                }

                // Find live_in.
                b.clearIn();
                b.addIn(b.getGen());
                Set<String> a = new HashSet<>(b.getOut());
                a.removeAll(b.getKill());
                b.addIn(a);

                // Compare old values to new values.
                if (!b.getIn().equals(oldIn) || !b.getOut().equals(oldOut)) {
                    pointsHaveChanged = true;
                }
            }
        }

        cfg.setBlocks(blocks.reversed());

        return cfg;
    }

    private ControlFlowGraph instructionLevelLiveness(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();

        for (BasicBlock b : blocks) {
            INSTRUCTIONS = b.getInstructions();
            Set<String> live = new HashSet<>(b.getOut());

            for (I : INSTRUCTIONS.reversed()) {
                i.setOut(live);

                i.clearIn();
                i.addIn(i.getGen());
                Set<String> a = new HashSet<>(i.getOut());
                a.removeAll(i.getKill());
                i.addIn(a);

                live = i.getIn();
            }

            b.setInstructions(INSTRUCTIONS);
        }

        cfg.setBlocks(blocks);

        return cfg;
    }
}
