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
        String[] commentArr = comment.split("\n");
        return formatInstruction(str, commentArr);
    }

    private String formatInstruction(String str, String[] comments) {
        StringBuilder result = new StringBuilder();

        String[] lines = str.split("\n");

        // Write each line based on arg count
        int i = 0;
        for (; i < lines.length; i++) {
            if (comments[i].isBlank()) {
                result.append(formatString(lines[i], ""));
            } else {
                result.append(formatString(lines[i], comments[i]));
            }
        }

        // Add extra comments
        for (; i < comments.length; i++) {
            result.deleteCharAt(result.length()-1);
            result.append("; ").append(comments[i].strip()).append("\n");
        }

        return result.toString();
    }

    String formatString(String line, String comment) {
        final int insLen = 9;
        final int argLen = 4;

        // format: ins arg, arg, arg ; com
        String format = "%-" + insLen + "s %" + argLen + "s %" + argLen + "s %" + argLen + "s %s";

        String com = "";
        StringBuilder newLine = new StringBuilder();

        String[] tokens = line.split(" ");

        // Get comment if exists
        if (!comment.isBlank()) {
            com = "; " + comment;
        }

        // Format based on amount of arguments
        switch (tokens.length) {
            case 2:
                newLine.append(String.format(format, tokens[0], tokens[1] + " ", "", "", com));
                break;
            case 3:
                newLine.append(String.format(format, tokens[0], tokens[1], tokens[2] + " ", "", com));
                break;
            case 4:
                newLine.append(String.format(format, tokens[0], tokens[1], tokens[2], tokens[3], com));
                break;

            default:
                newLine.append(String.format(format, tokens[0] + " ", "", "", "", com));
                break;
        }

        // Remove excess whitespaces
        int commentStartIndex = insLen + argLen * 3 + 4;
        while (newLine.length() > commentStartIndex && newLine.charAt(commentStartIndex) == ' ') {
            newLine.deleteCharAt(commentStartIndex);
        }

        return newLine.toString() + "\n";
    }
}
