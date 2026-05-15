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
import lombok.Getter;

@Getter
public class LivenessAnalyzer {
    private HashMap<String, Set<String>> interference = new HashMap<>();
    private ControlFlowGraph cfg;

    public LivenessAnalyzer(ControlFlowGraph cfg) {
        // Step 1: find gen/kill for instructions
        // Already on instruction when created using constructor.
        // Step 2: find gen/kill for blocks
        this.cfg = blockGenKill(cfg);
        // Step 3: do fixed-point analysis for blocks
        this.cfg = fixedPointAnalysis(this.cfg);
        // Step 4: do fixed-point analysis for instructions (?)
        this.cfg = instructionLevelLiveness(this.cfg);
        // Step 5: find interference
        computeInterference(cfg);
        System.out.println(interference);
    }

    private ControlFlowGraph blockGenKill(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();

        for (BasicBlock block : blocks) {
            // ensure each block is clear
            block.clearGen();
            block.clearKill();

            for (IrInstruction instr : block.getInstructions()) {
                // find gen for Block
                // block.gen = (instr.gen \ block.kill)
                Set<String> instrGen = new HashSet<>(instr.getGen());
                instrGen.removeAll(block.getKill());
                block.addGen(instrGen);

                // block.kill = instr.kill
                block.addKill(instr.getKill());
            }
        }

        cfg.setBlocks(blocks);

        return cfg;
    }

    private ControlFlowGraph fixedPointAnalysis(ControlFlowGraph cfg) {
        // reversed since we go bottom up.
        List<BasicBlock> blocks = cfg.getBlocks().reversed();

        // out = union of successor.in
        // in = gen[b] U (out[b] \ kill[b])

        // if current iteration has changed from last iteration. (SPO 12)
        boolean pointsHaveChanged = true;

        while (pointsHaveChanged) {
            pointsHaveChanged = false;

            for (BasicBlock b : blocks) {
                Set<String> oldIn = new HashSet<>(b.getIn());
                Set<String> oldOut = new HashSet<>(b.getOut());

                // Find live_out.
                b.clearOut();
                for (BasicBlock s : b.getChildren()) {
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

        // update cfg with blocks in normal order.
        cfg.setBlocks(blocks.reversed());

        return cfg;
    }

    private ControlFlowGraph instructionLevelLiveness(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();

        for (BasicBlock b : blocks) {
            List<IrInstruction> instructions = b.getInstructions();
            Set<String> live = new HashSet<>(b.getOut());

            // find in and out for each instruction.
            for (IrInstruction i : instructions.reversed()) {
                i.setOut(new HashSet<>(live));

                i.clearIn();
                i.addIn(i.getGen());
                Set<String> a = new HashSet<>(i.getOut());
                a.removeAll(i.getKill());
                i.addIn(a);

                live = new HashSet<>(i.getIn());
            }

            b.setInstructions(instructions);
        }

        cfg.setBlocks(blocks);

        return cfg;
    }

    private void computeInterference(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();

        for (BasicBlock b : blocks) {
            List<IrInstruction> instructions = b.getInstructions();

            for (IrInstruction i : instructions) {
                for (String k : i.getKill()) {
                    for (String o : i.getOut()) {
                        if (k.equals(o)) {
                            continue;
                        }

                        interference.computeIfAbsent(k, x -> new HashSet<>()).add(o);
                        interference.computeIfAbsent(o, x -> new HashSet<>()).add(k);
                    }
                }
            }
        }
    }
}
