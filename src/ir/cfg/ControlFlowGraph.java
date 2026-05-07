package ir.cfg;

import java.util.List;

import lombok.Getter;

@Getter
public class ControlFlowGraph {
    private final List<BasicBlock> blocks;
    private final BasicBlock entry;

    public ControlFlowGraph(List<BasicBlock> blocks) {
        this.blocks = blocks;
        // make first block the root of the graph.
        this.entry = blocks.get(0); 
    }
}
