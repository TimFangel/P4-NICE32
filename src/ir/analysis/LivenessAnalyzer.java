package ir.analysis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import ir.IrInstruction;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;

public class LivenessAnalyzer {
    public LivenessAnalyzer(ControlFlowGraph cfg) {
        // kald gen kill metode

        // Do fixed-point iterations.
        fixedPointIteration(cfg);
    }

    private void fixedPointIteration(ControlFlowGraph cfg) {
        List<BasicBlock> blocks = cfg.getBlocks();
        IrInstruction nextInstruction = null;

        // Iterate through every block in the CFG, starting with the last block.
        for (BasicBlock b : blocks.reversed()) {
            // Update previous in/out and reset current lists.
            b.setInPrev(b.getInCurrent());
            b.setOutPrev(b.getOutCurrent());
            b.setInCurrent(new HashMap<String, List<String>>());
            b.setOutCurrent(new HashMap<String, List<String>>());

            List<IrInstruction> instructions = b.getInstructions();

            // Iterate through the block's instructions, starting with the last instruction.
            for (IrInstruction i : instructions.reversed()) {
                // Update current OUT list for the block.

                // Get
                // j er næste instruktions successor ID'er tal ting
                // hvordan ved man om næste instruktion er i samme blok eller child blok?
                List<IrInstruction> successorInstructions = new ArrayList<>();

                if (i == b.getLastInstruction()) {
                    for (BasicBlock bb : b.getChildren()) {
                        successorInstructions.add(bb.getInstructions().get(0));
                    }
                } else {
                    successorInstructions.add(nextInstruction);
                }

                // så out += nuværende instruktion's in[j]
                b.setOutCurrent(b.getOutCurrent());

                // Update current IN list for the block.

                // føj til block's out
                // if successor != null, så er out = out + in

                // out +=

                // føj til blocks in

                nextInstruction = i;
            }
        }

        // Check if previous iteration's in/out matches the current iteration. If they
        // don't match, do another iteration.
        for (BasicBlock b : cfg.getBlocks()) {
            if (b.getInPrev() != b.getInCurrent() || b.getOutPrev() != b.getOutCurrent()) {
                cfg.setBlocks(blocks);
                fixedPointIteration(cfg);
            }
        }
    }
}
