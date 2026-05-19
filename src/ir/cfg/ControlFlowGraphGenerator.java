package ir.cfg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import exception.MissingLabelException;
import exception.UnknownInstructionException;
import lombok.Getter;
import ir.IrComponent;
import ir.IrFunction;
import ir.IrInstruction;
import ir.IrInstructionInterface;
import ir.IrValue;
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

    /**
     * Generates Basic Blocks from a list of IrInstructions
     * 
     * @param instructions the list of IrInstructions to make into basic blocks.
     */
    private void generateBasicBlocks(List<IrInstruction> instructions) {
        BasicBlock currentBlock = new BasicBlock(blockIdCount++); // start new block
        int separatorCount = 0; // 0 == in functions, hence not entry.

        // Iterate through each instruction.
        for (int i = 0; i < instructions.size(); i++) {
            IrInstruction instr = instructions.get(i);

            /*
             * ignore SEPARATOR used for pretty printing, but use first one to mark entry of
             * CFG (setup or main depending on whether setup has instructions)
             */
            if (instr.getOperator() == IrOperator.SEPARATOR) {
                if (separatorCount == 0) {
                    currentBlock.setEntry(true);
                    separatorCount++;
                }

                continue;
            }

            /*
             * if leader, create new block if current is not empty
             * (except first instruction)
             */
            if (i > 0 && isLeader(instr) && !currentBlock.getInstructions().isEmpty()) {
                // add block, then create new one.
                blocks.add(currentBlock);
                currentBlock = new BasicBlock(blockIdCount++);

            }

            // add instruction to block
            currentBlock.addInstruction(instr);

            // create new block after terminator
            if (isTerminator(instr)) {
                // add block and create new one if list contains more instructions.
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

    /**
     * Generates the relations between basic blocks
     */
    private void generateRelations() {
        // Map to match labels to blocks.
        Map<String, BasicBlock> labelToBlock = new HashMap<>();

        // match labels to their block
        for (BasicBlock block : blocks) {
            if (!block.getInstructions().isEmpty()) {
                // get the first instruction
                IrInstruction firstInstr = block.getInstructions().get(0);
                // if instr is label add it to map.
                if (firstInstr.getOperator() == IrOperator.LABEL) {
                    // label always stored in result, so put that in map.
                    labelToBlock.put(firstInstr.getResult().getName(), block);
                }
            }
        }

        // assign children based on last instruction in block and map.
        for (int i = 0; i < blocks.size(); i++) {
            BasicBlock block = blocks.get(i);
            IrInstruction lastInstr = block.getLastInstruction();

            // ignore empty blocks, if they should exist.
            if (lastInstr == null) {
                continue;
            }

            // switch assigning relations based on operation.
            // NOTE: addChild also assigns the child's parent.
            switch (lastInstr.getOperator()) {
                case GOTO:
                    // find target of GOTO
                    BasicBlock target = labelToBlock.get(lastInstr.getResult().getName());
                    if (target != null) {
                        // connect current block and target.
                        block.addChild(target);
                    } else {
                        throw new MissingLabelException(
                                "Missing CFG GOTO target label: " + lastInstr.getResult().getName());
                    }
                    break;

                case IF_FALSE:
                    // fallthrough case when condition is true
                    if (i + 1 < blocks.size()) {
                        block.addChild(blocks.get(i + 1));
                    }

                    // case when condition is false.
                    // jumpTarget -> thing after if body.
                    BasicBlock jumpTarget = labelToBlock.get(lastInstr.getResult().getName());
                    if (jumpTarget != null) {
                        // connect current block and target.
                        block.addChild(jumpTarget);
                    } else {
                        throw new MissingLabelException(
                                "Missing CFG IF_FALSE jump target label: " + lastInstr.getResult().getName());
                    }
                    break;

                case RET:
                    // end of function, therefore do nothing.
                    break;

                default:
                    // fallthrough to next block, if there are more.
                    if (i + 1 < blocks.size()) {
                        block.addChild(blocks.get(i + 1));
                    }
            }
        }
    }

    /**
     * Converts a List containing IrInstruction, IrComponent, and IrFunction into
     * one only containing IrInstruction.
     * 
     * @param list made up of IrInstructionInterface objects.
     * @return A list, where IrComponent and IrFunction has been made into
     *         IrInstruction.
     */
    private List<IrInstruction> convertInterfaceList(List<IrInstructionInterface> list) {
        // list of instructions to return
        List<IrInstruction> instructions = new ArrayList<>();
        int instructionCounter = 0;

        // Adapt each member to an IrInstruction.
        for (IrInstructionInterface i : list) {
            switch (i) {
                case IrInstruction instr -> {
                    // add regular instructions
                    if (instr.getOperator() != IrOperator.SEPARATOR) {
                        instr.setInstrNum(instructionCounter++);
                    }
                    instructions.add(instr);
                }

                case IrFunction instr -> {
                    // transform function identifier, param, and type to IrInstruction.
                    IrValue funcNameType = new IrValue(instr.getFuncName(), instr.getRetType());

                    IrInstruction funcInfo = new IrInstruction(
                            IrOperator.FUNC_INFO,
                            funcNameType,
                            null,
                            instr.getParameter());

                    funcInfo.setInstrNum(instructionCounter++);
                    instructions.add(funcInfo);

                    // add function body to list of instructions.
                    for (IrInstruction ii : instr.getFuncBody()) {
                        ii.setInstrNum(instructionCounter++);
                        instructions.add(ii);
                    }
                }

                case IrComponent instr -> {
                    // add direction/protocol instruction to list.
                    IrInstruction setup = instr.getSetup();
                    setup.setInstrNum(instructionCounter++);
                    instructions.add(setup);

                    // add component variables to list.
                    for (IrInstruction ii : instr.getVariables()) {
                        ii.setInstrNum(instructionCounter++);
                        instructions.add(ii);
                    }
                }

                default -> throw new UnknownInstructionException("Instruction Unknown: " + i);
            }
        }

        return instructions;
    }

    /**
     * Finds the block marked as entry and updates the CFG.
     * 
     * @param cfg to update with entry.
     * @return CFG with entry.
     */
    private ControlFlowGraph findEntry(ControlFlowGraph cfg) {

        // check each list of instructions of each block until marked block is found.
        List<BasicBlock> cfgBlocks = cfg.getBlocks();
        for (BasicBlock block : cfgBlocks) {
            if (block.getIsEntry()) {
                cfg.setEntry(block);
                break;
            }
        }

        return cfg;
    }

    /**
     * Generates a CFG with Basic Blocks containing instructions and relations.
     * 
     * @param instructions used to generate the CFG.
     * @return A CFG with Basic Blocks with relations.
     */
    public ControlFlowGraph generateCFG(List<IrInstructionInterface> instructions) {
        ControlFlowGraph cfg = new ControlFlowGraph();
        generateBasicBlocks(convertInterfaceList(instructions));
        generateRelations();
        cfg.setBlocks(blocks);
        cfg = findEntry(cfg);

        return cfg;
    }
}
