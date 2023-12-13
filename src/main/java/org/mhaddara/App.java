package org.mhaddara;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.mhaddara.solutions.*;

public class App {
    // todo: populate this via reflection?
    static List<Solution> solutions = List.of(
        new Day01(),
        new Day02(),
        new Day03(),
        new Day04(),
        new Day05(),
        new Day06(),
        new Day07(),
        new Day08(),
        new Day09(),
        new Day10(),
        new Day11(),
        new Day12(),
        new Day13()
    );

    public static void main( String[] args ) throws IOException {
        // args: day, part, test or real
        int dayNum = Integer.parseInt(args[0]);
        String part = args[1];
        boolean testMode = Boolean.parseBoolean(args[2]);

        Solution solution = solutions.get(dayNum - 1);

        List<String> lines = testMode ? solution.testInput().lines().toList() : getInput(dayNum);

        Object result = switch (part) {
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
