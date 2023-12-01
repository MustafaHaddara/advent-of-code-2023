package org.mhaddara;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.mhaddara.solutions.*;

public class App {
    static List<Solution> solutions = List.of();

    public static void main( String[] args ) throws IOException {
        // args: day, part, test or real
        int dayNum = Integer.parseInt(args[0]);
        String part = args[1];
        boolean testMode = Boolean.parseBoolean(args[2]);

        Solution solution = solutions.get(dayNum - 1);

        List<String> lines = testMode ? solution.testInput().lines().toList() : getInput(dayNum);

        System.out.println(lines);
        
        String result = switch (part) {
            case "A" -> solution.solvePartOne(lines);
            case "B" -> solution.solvePartTwo(lines);
            default -> "Invalid part";
        };

        System.out.println(result);
    }

    private static List<String> getInput(int day) throws IOException {
        String fileName = String.format("src/resources/inputs/day%02d.txt", day);
        List<String> lines = Files.readAllLines(Path.of(fileName));
        return lines;
    }
}
