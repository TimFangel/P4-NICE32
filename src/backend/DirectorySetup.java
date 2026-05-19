package backend;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;

public class DirectorySetup {
    static final String C_MAKE_FILE = "CMakeLists.txt";

    private DirectorySetup() {
    }

    public static void create(String outputFolder, String mainFolder, String name) throws IOException {
        createProjectSetup(outputFolder, name);
        createMainSetup(outputFolder, mainFolder, name);
    }

    /**
     * Creates setup of project for ESP-IDF
     * 
     * @param outputFolder path of output folder
     * @param name         of file
     * @throws IOException
     */
    private static void createProjectSetup(String outputFolder, String name) throws IOException {
        // Create path for cmake file
        Path path = Paths.get(outputFolder, C_MAKE_FILE);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            writer.write("cmake_minimum_required(VERSION 3.16)");
            writer.newLine();
            writer.newLine();

            writer.write("include($ENV{IDF_PATH}/tools/cmake/project.cmake)");
            writer.newLine();

            // Set project name
            writer.write("project(" + name + ")");
        }
    }

    /**
     * Creates setup of main file (the code) for ESP-IDF
     * 
     * @param outputFolder path of output folder
     * @param mainFolder   path to subfolder for code
     * @param name         of file
     * @throws IOException
     */
    private static void createMainSetup(String outputFolder, String mainFolder, String name) throws IOException {
        // Create path for cmake file
        Path path = Paths.get(outputFolder, mainFolder, C_MAKE_FILE);

        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile()))) {
            // Create component to assembly file (compiled code)
            writer.write("idf_component_register(SRCS \"" + name + ".S\" INCLUDE_DIRS \".\")");
        }
    }
}
