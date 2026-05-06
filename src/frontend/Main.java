package frontend;

import frontend.abstract_syntax.program.Program;
import frontend.coco.Parser;
import frontend.coco.Scanner;
import frontend.semantic_analysis.TypeChecker;
import frontend.symboltable.SymbolTable;
import ir.IrGenerator;
import ir.IrPrinter;

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

            Program ast = parser.mainNode;

            SymbolTable symbolTable = new SymbolTable(parser);

            TypeChecker checker = new TypeChecker(symbolTable);
            checker.check(ast);

            IrGenerator irGenerator = new IrGenerator(null);
            irGenerator.generateProgram(ast);

            IrPrinter irPrinter = new IrPrinter(irGenerator);
            irPrinter.printIR("TestIr");

            System.out.println(ast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}