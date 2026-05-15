package backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ir.IrInstruction;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;

public class AssemblyGenerator {

    public void run(ControlFlowGraph cfg, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".a"))) {
            for (BasicBlock bb : cfg.getBlocks()) {
                for (IrInstruction ii : bb.getInstructions()) {
                    writer.write(ii.toString());
                    writer.newLine();
                }
            }
        }
    }
}
