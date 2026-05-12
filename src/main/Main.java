package main;

import backend.AssemblyGenerator;
import frontend.abstract_syntax.program.Program;
import frontend.coco.Parser;
import frontend.coco.Scanner;
import frontend.semantic_analysis.SemanticAnalyser;
import ir.IrGenerator;
import ir.cfg.ControlFlowGraph;
import ir.cfg.ControlFlowGraphGenerator;
import ir.util.IrPrinter;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java frontend.Main <file>");
            return;
        }

        try {
            // --- Parse ---
            Parser parser = new Parser(new Scanner(args[0]));
            parser.Parse();

            if (parser.hasErrors()) {
                System.out.println("Parse errors.");
                return;
            }

            System.out.println("> Successfully parsed input <");

            // --- Semantic Analysis ---
            Program ast = parser.mainNode;

            SemanticAnalyser semanticAnalyser = new SemanticAnalyser();
            semanticAnalyser.traverse(ast);

            System.out.println(ast);
            System.out.println("> AST passed type checker <");

            // --- IR Generation ---
            IrGenerator irGenerator = new IrGenerator();
            irGenerator.generateProgram(ast);

            IrPrinter irPrinter = new IrPrinter(irGenerator);
            irPrinter.printIR("TestIr");

            System.out.println("> IR has been successfully generated <");

            // --- ControlFlowGraph ---
            ControlFlowGraphGenerator cfgg = new ControlFlowGraphGenerator();
            ControlFlowGraph cfg = cfgg.generateCFG(irGenerator.getInstructions());

            // --- Assembly Generator ---
            AssemblyGenerator ag = new AssemblyGenerator();
            ag.run(cfg, "assembly");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}