package ir.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import ir.IrComponent;
import ir.IrFunction;
import ir.IrGenerator;
import ir.IrInstruction;
import ir.IrInstructionInterface;

/**
 * Prints IR code to a file.
 */
public class IrPrinter {
    private IrGenerator ir;

    public IrPrinter(IrGenerator ir) {
        this.ir = ir;
    }

    public void printIR(String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename + ".NICEIR"))) {
            List<IrInstructionInterface> code = ir.getCode();
            int i = 0;

            writer.write("--- FUNCTIONS ---");
            writer.newLine();

            // write functions and their instructions
            while (i < code.size()) {
                IrInstructionInterface current = code.get(i);
                // stop once SEPARATOR between functions and setup is met.
                if (current instanceof IrInstruction instr && instr.getOperator() == IrOperator.SEPARATOR) {
                    i++;
                    break;
                }

                // print functions and their bodies.
                if (current instanceof IrFunction function) {
                    writer.write(function.toString());
                    writer.newLine();
                    writer.write("param " + function.getParameter().getName());
                    writer.newLine();
                    List<IrInstruction> funcBody = function.getFuncBody();
                    for (IrInstruction instr : funcBody) {
                        writer.write(instr.toString());
                        writer.newLine();
                    }
                    writer.write("end func");
                    writer.newLine(); // space between functions
                    writer.newLine();
                }

                i++;
            }

            writer.newLine();
            writer.write("--- SETUP ---");
            writer.newLine();

            // write setup and main
            while (i < code.size()) {
                IrInstructionInterface current = code.get(i);
                // make differentiation between main and setup
                if (current instanceof IrInstruction instr && instr.getOperator() == IrOperator.SEPARATOR) {
                    writer.newLine();
                    writer.write("--- MAIN ---");
                    writer.newLine();
                    i++;
                    continue;
                }

                // write components
                if (current instanceof IrComponent comp) {
                    for (IrInstruction ii : comp.getVariables()) {
                        writer.write(ii.toString());
                        writer.newLine();
                    }

                    writer.write(comp.getSetup().toString());
                    writer.newLine();
                    writer.newLine();
                }

                // write regular IrInstructions
                if (current instanceof IrInstruction instr) {
                    writer.write(instr.toString());
                    writer.newLine();
                }

                i++;
            }
        }
    }
}
