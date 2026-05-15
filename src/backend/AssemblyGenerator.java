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

                // Instructions
                for (IrInstruction instr : block.getInstructions()) {
                    InstructionWriter iw = new InstructionWriter(instr);
                    writer.write(formatInstruction(iw.write(), instr.toString()));
                }

                writer.newLine();
            }
        }
    }

    public static String newLabel() {
        return "L0" + ++nextLabelNumber;
    }

    private String formatInstruction(String str, String comment) {
        String[] commentArr = new String[] { comment };
        return formatInstruction(str, commentArr);
    }

    private String formatInstruction(String str, String[] comments) {
        final int insLen = 9;
        final int argLen = 4;

        // format: ins arg, arg, arg ; com
        String format = "%-" + insLen + "s %" + argLen + "s %" + argLen + "s %" + argLen + "s %s\n";
        StringBuilder result = new StringBuilder();

        String[] lines = str.split("\n");

        // Write each line based on arg count
        for (int i = 0; i < lines.length; i++) {
            String[] tokens = lines[i].split(" ");
            String com = "";

            // Get comment if exists
            if (comments.length > i) {
                com = "; " + comments[i];
            }

            switch (tokens.length) {
                case 2:
                    result.append(String.format(format, tokens[0], tokens[1] + " ", "", "", com));
                    break;
                case 3:
                    result.append(String.format(format, tokens[0], tokens[1], tokens[2] + " ", "", com));
                    break;
                case 4:
                    result.append(String.format(format, tokens[0], tokens[1], tokens[2], tokens[3], com));
                    break;

                default:
                    result.append(String.format(format, tokens[0] + " ", "", "", "", com));
                    break;
            }

            int commentStartIndex = insLen + argLen * 3 + 4;
            while (result.charAt(commentStartIndex) == ' ') {
                result.deleteCharAt(commentStartIndex);
            }
        }

        return result.toString();
    }
}
