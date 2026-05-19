package backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import ir.IrInstruction;
import ir.cfg.BasicBlock;
import ir.cfg.ControlFlowGraph;

// TODO:
// CMakeLists.txt x2
// . setup
// TIMG0_WDT_WKEY_REG

public class AssemblyGenerator {
    static int nextLabelNumber = 0;

    public void run(ControlFlowGraph cfg, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".S"))) {
            writer.write(writeHeader());
            writer.newLine();
            writer.newLine();

            for (BasicBlock block : cfg.getBlocks()) {
                // Block name
                writer.write("; B" + block.getId());
                writer.newLine();

                // Add entry label
                if (cfg.getEntry().equals(block)) {
                    writer.write("app_main:");
                    writer.newLine();
                }

                // Write instructions
                for (IrInstruction instr : block.getInstructions()) {
                    InstructionGenerator ig = new InstructionGenerator(instr);
                    writer.write(InstructionFormatter.format(ig.write(), instr.toString()));
                }

                writer.newLine();
            }
        }
    }

    public static String newLabel() {
        return ".L0" + ++nextLabelNumber;
    }

    private String writeHeader() {
        StringBuilder string = new StringBuilder();

        string.append("; ESP compiler setup\n");
        string.append(".section .text\n"); // Set following section (everything) to instructions (text)
        string.append(".global app_main\n"); // Set app_main as a global function so a compiler can see it
        string.append(".literal_position\n"); // Set position of large constants to here (move after func if necessary)

        return string.toString();
    }
}
