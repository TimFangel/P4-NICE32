package src;

import src.abstract_syntax.*;
import src.coco.Parser;
import src.coco.Scanner;
import src.semantic_analysis.TypeChecker;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java Main <file>");
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

            TypeChecker checker = new TypeChecker();
            checker.check(ast);

            System.out.println(ast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}