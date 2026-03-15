package org.example;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        String inputDir = "src/main/resources";
        String inputFile = "input.txt";
        String outputFile = "output.txt";
        Path inputPath = Paths.get(inputDir, inputFile);
        Path outputPath = Paths.get(inputDir, outputFile);

        // non parallel
        System.out.println("Starting non parallel task!");
        long start = System.currentTimeMillis();

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
        System.out.println("Time taken: " + (end - start) / 1000.0 + "\n\n");

        // parallel
        for (int consumer_num = 1; consumer_num <= 3; consumer_num += 2) {
            long start2 = System.currentTimeMillis();
            MonitorQueue<lineRecord> lr = new MonitorQueue<>(5);
            ConcurrentSkipListMap<Integer, String> results = new ConcurrentSkipListMap<>();
            Consumer[] consumers = new Consumer[consumer_num];
            for (int i = 0; i < consumer_num; i++) {
                consumers[i] = new Consumer(lr, results);
                consumers[i].start();
            }

            //producer act
            List<String> ls = Files.readAllLines(inputPath);
            for (int i = 0; i < ls.size(); i++) {
                lr.put(new lineRecord(i, ls.get(i)));
            }
            for (int i = 0; i < consumer_num; i++) {
                lr.put(null);
            }

            for (int i = 0; i < consumer_num; i++) {
                consumers[i].join();
            }

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < ls.size(); i++) {
                sb.append(results.get(i));
                sb.append("\n");
            }
            Files.writeString(outputPath, sb.toString());
            long end2 = System.currentTimeMillis();
            System.out.printf("cores taken: %d\n", consumer_num + 1);
            System.out.println("Time taken: " + (end2 - start2) / 1000.0 + "\n\n");
        }
    }
}
