package ir.cfg;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ir.IrInstruction;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BasicBlock {
    private final int id; // block number
    private boolean isEntry = false; // whether it is the entry to the CFG or not.

    // code within block
    private List<IrInstruction> instructions = new ArrayList<>();

    private List<BasicBlock> parents = new ArrayList<>();
    private List<BasicBlock> children = new ArrayList<>();

    /* Liveness Variables */
    private Set<String> gen = new HashSet<>(); // read
    private Set<String> kill = new HashSet<>(); // write

    // in and out from liveness analysis
    private Set<String> in = new HashSet<>();
    private Set<String> out = new HashSet<>();

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

    /**
     * Adds the given basic block as child to the current block, and also adds the
     * current block as parent to the child.
     * 
     * @param child block to assign as child.
     */
    public void addChild(BasicBlock child) {
        children.add(child);
        // remember to add this as parent to new child.
        child.parents.add(this);
    }

    public void addIn(Set<String> s) {
        this.in.addAll(s);
    }

    public void addOut(Set<String> s) {
        this.out.addAll(s);
    }

    public void clearIn() {
        this.in.clear();
    }

    public void clearOut() {
        this.out.clear();
    }

    public void addGen(Set<String> s) {
        this.gen.addAll(s);
    }

    public void addKill(Set<String> s) {
        this.kill.addAll(s);
    }

    public void clearGen() {
        this.gen.clear();
    }

    public void clearKill() {
        this.kill.clear();
    }

    public boolean getIsEntry() {
        return this.isEntry;
    }
}
