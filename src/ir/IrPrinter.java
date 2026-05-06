package ir;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

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
            // write functions first, since it is easier.
            writer.write("--- FUNCTIONS ---");
            writer.newLine();

            List<IrFunction> functions = ir.getFunctions();

            for (IrFunction function : functions) {
                writer.write(function.toString());
                writer.newLine();
                writer.write("param " + function.getParameter().getName());
                writer.newLine();
                List<IrInstruction> funcBody = function.getFuncBody();
                for (IrInstruction instr : funcBody) {
                    writer.write(instr.toString());
                    writer.newLine();
                }
                writer.write("end function");
                writer.newLine(); // space between functions
                writer.newLine();
            }

            // create space between functions and setup/main
            writer.newLine();
            writer.newLine();

            writer.write("--- Setup & Main ---");
            writer.newLine();

            List<IrInstruction> code = ir.getCode();

            for (IrInstruction instr : code) {
                writer.write(instr.toString());
                writer.newLine();
            }
        }
    }
}
