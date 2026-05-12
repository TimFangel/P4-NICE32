package frontend;

import frontend.abstract_syntax.program.Program;
import frontend.coco.Parser;
import frontend.coco.Scanner;
import frontend.semantic_analysis.SemanticAnalyser;
import ir.IrGenerator;
import ir.util.IrPrinter;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java frontend.Main <file>");
            return;
        }

        try {
            Parser parser = new Parser(new Scanner(args[0]));
            parser.Parse();

            if (parser.hasErrors()) {
                System.out.println("Parse errors.");
                return;
            }

            System.out.println("> Successfully parsed input <");

            Program ast = parser.mainNode;

            SemanticAnalyser semanticAnalyser = new SemanticAnalyser();
            semanticAnalyser.traverse(ast);

            System.out.println(ast);
            System.out.println("> AST passed type checker <");

            IrGenerator irGenerator = new IrGenerator();
            irGenerator.generateProgram(ast);

            IrPrinter irPrinter = new IrPrinter(irGenerator);
            irPrinter.printIR("TestIr");

            System.out.println("> IR has been successfully generated <");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}