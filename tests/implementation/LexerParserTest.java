package implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.util.List;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import frontend.abstract_syntax.program.Program;
import frontend.coco.Parser;
import frontend.coco.Scanner;

class LexerParserTest {
    final String basePath = "./tests/resources/implementation/lexer-parser/";
    final String codePath = "code/";
    final String codeFileExtension = ".NICE";
    final String astPath = "ast/";
    final String astFileExtension = ".txt";

    @Test
    void main() {
        // Read all filenames in the directories
        List<String> codeFiles = listFilesForFolder(new File(basePath + codePath), codeFileExtension);
        List<String> astFiles = listFilesForFolder(new File(basePath + astPath), astFileExtension);

        // Run test for each code file
        for (String codeFile : codeFiles) {
            String astFile = codeFile.replace(basePath + codePath, basePath + astPath).replace(codeFileExtension,
                    astFileExtension);
            // Check that ast file matching code file exists
            if (!astFiles.contains(astFile)) {
                fail("Could not find ast file: " + astFile + ".");
                continue;
            }

            String ast = assertDoesNotThrow(() -> parse(codeFile));

            System.out.println(ast);
        }

    }

    public List<String> listFilesForFolder(final File folder) {
        return listFilesForFolder(folder, "");
    }

    public List<String> listFilesForFolder(final File folder, String fileExtension) {
        List<String> list = new ArrayList<>();

        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else if (!fileEntry.getPath().endsWith(fileExtension)) {
                fail("Expected file with extension '" + fileExtension + "' but got '" + fileEntry.getPath() + "'.");
            } else {
                list.add(fileEntry.getPath());
            }
        }

        return list;
    }

    String parse(String fileName) {
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
}
