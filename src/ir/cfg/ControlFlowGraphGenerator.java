package ir.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.MissingLabelException;
import lombok.Getter;

import ir.IrInstruction;
import ir.util.IrOperator;

// Class generating basic blocks, then relations between the blocks, and then returning a cfg.
@Getter
public class ControlFlowGraphGenerator {
    private int blockIdCount = 0;

    // list to contain all basic blocks of the program.
    private List<BasicBlock> blocks = new ArrayList<>();

    /**
     * returns true, if instruction is possible leader of basic block.
     * 
     * @param instr IrInstruction to check.
     * @return true for leader, false for anything else.
     */
    private boolean isLeader(IrInstruction instr) {
        return instr.getOperator() == IrOperator.LABEL;
    }

    /**
     * returns true, if instruction is possible terminator of basic block.
     * 
     * @param instr IrInstruction to check.
     * @return true for terminator, false for anything else.
     */
    private boolean isTerminator(IrInstruction instr) {
        switch (instr.getOperator()) {
            case GOTO, IF_FALSE, RET:
                return true;
            default:
                return false;
        }
    }

    private void generateBasicBlocks(List<IrInstruction> instructions) {
        BasicBlock currentBlock = new BasicBlock(blockIdCount++);

        for (int i = 0; i < instructions.size(); i++) {
            IrInstruction instr = instructions.get(i);

            // if leader, create new block (except first instruction)
            if (i > 0 && isLeader(instr)) {
                if (!currentBlock.getInstructions().isEmpty()) {
                    blocks.add(currentBlock);
                }
                currentBlock = new BasicBlock(blockIdCount++);
            }

            currentBlock.addInstruction(instr);

            // create new block after terminator
            if (isTerminator(instr)) {
                blocks.add(currentBlock);

                // avoid empty block at end
                if (i + 1 < instructions.size()) {
                    currentBlock = new BasicBlock(blockIdCount++);
                }
            }
        }

        // ensure final block is added
        if (!blocks.contains(currentBlock) && !currentBlock.getInstructions().isEmpty()) {
            blocks.add(currentBlock);
        }
    }

    private void generateRelations() {
        Map<String, BasicBlock> labelToBlock = new HashMap<>();

        // match labels to their block
        for (BasicBlock block : blocks) {
            if (!block.getInstructions().isEmpty()) {
                IrInstruction firstInstr = block.getInstructions().get(0);
                if (firstInstr.getOperator() == IrOperator.LABEL) {
                    // label always stored in result
                    labelToBlock.put(firstInstr.getResult().getName(), block);
                }
            }
        }

        // assign children based on last instruction
        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            IrInstruction lastInstr = block.getLastInstruction();

            // ignore empty blocks, if they should exist.
            if (lastInstr == null) {
                continue;
            }

            switch (lastInstr.getOperator()) {
                case GOTO:
                    BasicBlock target = labelToBlock.get(lastInstr.getResult().getName());
                    if (target != null) {
                        block.addChild(target);
                    } else {
                        throw new MissingLabelException(
                                "Missing CFG GOTO target label: " + lastInstr.getResult().getName());
                    }
                    break;

                case IF_FALSE:
                    // fallthrough if true
                    if (i + 1 < blocks.size()) {
                        block.addChild(blocks.get(i + 1));
                    }
                    // jump to target label
                    BasicBlock jumpTarget = labelToBlock.get(lastInstr.getResult().getName());
                    if (jumpTarget != null) {
                        block.addChild(jumpTarget);
                    } else {
                        throw new MissingLabelException(
                                "Missing CFG IF_FALSE jump target label: " + lastInstr.getResult().getName());
                    }
                    break;

                case RET:
                    // end of function
                    break;

                default:
                    // fallthrough to next block
                    if (i + 1 < blocks.size()) {
                        block.addChild(blocks.get(i + 1));
                    }
            }
        }
    }

    public ControlFlowGraph generateCFG(List<IrInstruction> instructions) {
        ControlFlowGraph cfg = new ControlFlowGraph();
        generateBasicBlocks(instructions);
        generateRelations();
        cfg.setEntry(blocks.get(0));
        cfg.setBlocks(blocks);

        return cfg;
    }
}
