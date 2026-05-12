package ir.cfg;

import java.util.ArrayList;
import java.util.List;

import ir.IrInstruction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ControlFlowGraph {
    private BasicBlock entry;
    private List<BasicBlock> blocks = new ArrayList<>();

    public ControlFlowGraph() {
        // empty since data is unknown on creation
    }

    public void printCFG() {
        for (BasicBlock block : blocks) {
            System.out.println("B" + block.getId());

            System.out.print(" Parents: ");
            for (BasicBlock parent : block.getParents()) {
                System.out.print(parent.getId() + " ");
            }

            System.out.println();

            System.out.print(" Children: ");
            for (BasicBlock child : block.getChildren()) {
                System.out.print(child.getId() + " ");
            }

            System.out.println();
            System.out.println();

            System.out.println(" Instructions: ");
            for (IrInstruction instr : block.getInstructions()) {
                System.out.println("  " + instr.toString());
            }

            System.out.println();
            System.out.println();
        }
    }

    public List<BasicBlock> getBlocks() {
        return blocks;
    }
}
