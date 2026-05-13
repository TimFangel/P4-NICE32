package backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ir.IrInstruction;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;

public class AssemblyGenerator {
    static int nextLabelNumber = 0;

    public void run(ControlFlowGraph cfg, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".a"))) {
            for (BasicBlock block : cfg.getBlocks()) {
                // Block name
                writer.write("B" + block.getId());
                writer.newLine();

                // Instructions
                for (IrInstruction instr : block.getInstructions()) {
                    InstructionWriter iw = new InstructionWriter(instr);
                    writer.write(iw.write());
                    writer.newLine();
                }

                writer.newLine();
            }
        }
    }

    public static String newLabel() {
        return "L0" + ++nextLabelNumber;
    }
}
