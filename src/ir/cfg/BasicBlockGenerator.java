package ir.cfg;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

import ir.IrInstruction;
import ir.util.IrOperator;

@Getter
public class BasicBlockGenerator {
    private int blockIdCount = 0;

    private List<BasicBlock> blocks = new ArrayList<>();

    private boolean isLeader(IrInstruction instr) {
        return instr.getOperator() == IrOperator.LABEL;
    }

    private boolean isTerminator(IrInstruction instr) {
        switch (instr.getOperator()) {
            case GOTO, IF_FALSE, RET:
                return true;
            default:
                return false;
        }
    }

    public void generateBasicBlocks(List<IrInstruction> instructions) {
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

    public void generateRelations() {
        // find parents and children for basic blocks.
    }
}
