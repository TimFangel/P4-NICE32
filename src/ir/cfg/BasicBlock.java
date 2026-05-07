package ir.cfg;

import java.util.ArrayList;
import java.util.List;

import ir.IrInstruction;
import lombok.Getter;

@Getter
public class BasicBlock {
    private final int id;

    // code within block
    private List<IrInstruction> instructions = new ArrayList<>();

    // graph predessecor and successors
    private List<BasicBlock> parents = new ArrayList<>();
    private List<BasicBlock> children = new ArrayList<>();

    public BasicBlock(int id) {
        this.id = id;
    }

    public void addInstruction(IrInstruction instr) {
        instructions.add(instr);
    }

    public IrInstruction getLastInstruction() {
        if (instructions.isEmpty()) {
            return null;
        }

        return instructions.get(instructions.size() - 1);
    }

    public void addChild(BasicBlock child) {
        children.add(child);
        // remember to add this as parent to new child.
        child.parents.add(this);
    }
}
