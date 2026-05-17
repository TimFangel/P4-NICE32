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
                writer.write("; B" + block.getId());
                writer.newLine();

                // Write instructions
                for (IrInstruction instr : block.getInstructions()) {
                    InstructionGenerator iw = new InstructionGenerator(instr);
                    writer.write(InstructionFormatter.format(iw.write(), instr.toString()));
                }

                writer.newLine();
            }
        }
    }

    public static String newLabel() {
        return "L0" + ++nextLabelNumber;
    }
}
