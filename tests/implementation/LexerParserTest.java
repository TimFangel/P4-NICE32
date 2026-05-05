package implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Stream;
import java.util.ArrayList;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import frontend.abstract_syntax.program.Program;
import frontend.coco.Parser;
import frontend.coco.Scanner;

class LexerParserTest {
    static final Path basePath = Paths.get("tests", "resources", "implementation", "lexer-parser");
    static final String CODE_PATH = "code";
    static final String CODE_FILE_EXTENSION = ".NICE";
    static final String AST_PATH = "ast";
    static final String AST_FILE_EXTENSION = ".txt";

    // Read all filenames in the directories
    List<String> codeFiles = listFilesForFolder(basePath.resolve(CODE_PATH).toFile(), CODE_FILE_EXTENSION);
    List<String> astFiles = listFilesForFolder(basePath.resolve(AST_PATH).toFile(), AST_FILE_EXTENSION);

    @TestFactory
    Stream<DynamicTest> main() {

        // Run test for each code file
        return codeFiles.stream()
                .map(cf -> DynamicTest.dynamicTest("Test", () -> testBody(cf, codeToAstWithTest(cf, astFiles))));

    }

    static void testBody(String codeFile, String astFile) {

        // Read expected output
        String ast = "";
        try (java.util.Scanner astScanner = new java.util.Scanner(new File(astFile))) {
            ast = removeWhiteSpaces(astScanner.useDelimiter("\\Z").next());
        } catch (FileNotFoundException e) {
            fail("Could not read ast: " + astFile);
        } catch (NoSuchElementException e) {
            fail("ast file was empty");
        }

        // Parse input
        String parsedAst = removeWhiteSpaces(assertDoesNotThrow(() -> parse(codeFile)));

        if (ast.compareTo(parsedAst) != 0) {
            fail(String.format("AST' does not match. Expected:%n'''%n%s%n'''%nAnd got:%n'''%n%s%n'''",
                    prettifyAst(ast), prettifyAst(parsedAst)));
        }
    }

    static String codeToAstWithTest(String codeFile, List<String> astFiles) {
        String astFile = codeFile.replace(
                basePath.resolve(CODE_PATH).toString(),
                basePath.resolve(AST_PATH).toString())
                .replace(CODE_FILE_EXTENSION, AST_FILE_EXTENSION);

        // Check that ast file matching code file exists
        if (!astFiles.contains(astFile)) {
            fail("Could not find ast file: " + astFile + ".");
        }

        return astFile;
    }

    public List<String> listFilesForFolder(final File folder) {
        return listFilesForFolder(folder, "");
    }

    public List<String> listFilesForFolder(final File folder, String fileExtension) {
        List<String> list = new ArrayList<>();

        // Read every file in directory
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                list.addAll(listFilesForFolder(fileEntry, fileExtension));
            } else if (!fileEntry.getPath().endsWith(fileExtension)) {
                fail("Expected file with extension '" + fileExtension + "' but got '" + fileEntry.getPath() + "'.");
            } else {
                list.add(fileEntry.toPath().normalize().toString());
            }
        }

        return list;
    }

    // Main program copy
    static String parse(String fileName) {
        Program ast = null;
        try {
            Parser parser = new Parser(new Scanner(fileName));
            parser.Parse();

            if (parser.hasErrors()) {
                System.out.println("Parse errors.");
                fail("Parser errors");
            }

            ast = parser.mainNode;
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (ast == null) {
            fail("AST was not generated for '" + fileName + "'.");
        }

        return ast.toString();
    }

    static String prettifyAst(String in) {
        if (in == null)
            return "";

        int indentSize = 2;
        int indentLevel = 0;
        StringBuilder out = new StringBuilder();

        // add new lines around grouping tokens
        in = in.replaceAll("([\\(\\[\\{,])", "$1\n");
        in = in.replaceAll("([\\)\\]\\}])", "\n$1");

        // add indents
        for (String line : in.split("\\n")) {
            line = line.strip();

            if (line.isEmpty())
                continue;

            if (line.startsWith(")") || line.startsWith("]") || line.startsWith("}")) {
                indentLevel = Math.max(0, indentLevel - 1);
            }

            int spaces = Math.max(0, indentLevel * indentSize);
            if (spaces > 0)
                out.append(" ".repeat(spaces));
            out.append(line).append('\n');

            if (line.endsWith("(") || line.endsWith("[") || line.endsWith("{")) {
                indentLevel++;
            }
        }

        return out.toString().trim();
    }

    static String removeWhiteSpaces(String in) {
        return in.replaceAll("\\s+", "");
    }
}
