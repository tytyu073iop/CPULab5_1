package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        // non parallel
        System.out.println("Starting non parallel task!");
        long start = System.currentTimeMillis();
        String inputDir = "src/main/resources";
        String inputFile = "input.txt";
        String outputFile = "output.txt";

        Path inputPath = Paths.get(inputDir, inputFile);
        Path outputPath = Paths.get(inputDir, outputFile);

        try {
            String content = Files.readString(inputPath);
            StringBuilder result = new StringBuilder(content.length());
            StringBuilder word = new StringBuilder();

            for (int i = 0; i < content.length(); i++) {
                char c = content.charAt(i);
                if (Character.isWhitespace(c)) {
                    if (!word.isEmpty()) {
                        result.append(word.reverse());
                        word.setLength(0);
                    }
                    result.append(c);
                } else {
                    word.append(c);
                }
            }

            if (!word.isEmpty()) {
                result.append(word.reverse());
            }

            Files.writeString(outputPath, result.toString());
        } catch (IOException e) {
            throw new RuntimeException("Failed to process files", e);
        }
        long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) / 1000.0);
    }
}
