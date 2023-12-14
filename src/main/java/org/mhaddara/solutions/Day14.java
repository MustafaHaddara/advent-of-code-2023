package org.mhaddara.solutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.mhaddara.Solution;

public class Day14 implements Solution {

    @Override
    public String testInput() {
        return """
            O....#....
            O.OO#....#
            .....##...
            OO.#O....O
            .O.....O#.
            O.#..O.#.#
            ..O..#O..O
            .......O..
            #....###..
            #OO..#....
                """;
    }

    private void slideNorth(List<List<String>> grid) {
        for (int y = 0; y < grid.size(); y++) {
            List<String> row = grid.get(y);

            for (int x = 0; x < row.size(); x++) {
                String c = row.get(x);

                if (c.equals("O")) {
                    int targetRow = y;
                    while (targetRow > 0 && grid.get(targetRow-1).get(x).equals(".")) {
                         targetRow--;
                    }
                    grid.get(y).set(x, ".");
                    grid.get(targetRow).set(x, c);
                }
                
            }
        }
    }

    private void slideSouth(List<List<String>> grid) {
        for (int y = grid.size()-1; y >= 0; y--) {
            List<String> row = grid.get(y);

            for (int x = 0; x < row.size(); x++) {
                String c = row.get(x);

                if (c.equals("O")) {
                    int targetRow = y;
                    while (targetRow < grid.size()-1 && grid.get(targetRow+1).get(x).equals(".")) {
                         targetRow++;
                    }
                    grid.get(y).set(x, ".");
                    grid.get(targetRow).set(x, c);
                }
                
            }
        }
    }

    private void slideWest(List<List<String>> grid) {
        for (int y = 0; y < grid.size(); y++) {
            List<String> row = grid.get(y);

            for (int x = 0; x < row.size(); x++) {
                String c = row.get(x);

                if (c.equals("O")) {
                    int targetCol = x;
                    while (targetCol > 0 && grid.get(y).get(targetCol-1).equals(".")) {
                         targetCol--;
                    }
                    grid.get(y).set(x, ".");
                    grid.get(y).set(targetCol, c);
                }
                
            }
        }
    }

    private void slideEast(List<List<String>> grid) {
        for (int y = 0; y < grid.size(); y++) {
            List<String> row = grid.get(y);

            for (int x = grid.size()-1; x >= 0; x--) {
                String c = row.get(x);

                if (c.equals("O")) {
                    int targetCol = x;
                    while (targetCol < row.size()-1 && grid.get(y).get(targetCol+1).equals(".")) {
                         targetCol++;
                    }
                    grid.get(y).set(x, ".");
                    grid.get(y).set(targetCol, c);
                }
                
            }
        }
    }

    private void cycle(List<List<String>> grid) {
        slideNorth(grid);
        slideWest(grid);
        slideSouth(grid);
        slideEast(grid);
    }

    private List<List<String>> parse(List<String> input) {
        return input.stream()
            .map(s -> s.chars().mapToObj(Character::toString).collect(Collectors.toList()))
            .collect(Collectors.toList());
    }

    private int weight(List<List<String>> grid) {
        int total = 0;
        int base = grid.size();
        for (List<String> row: grid) {
            long amount = row.stream()
                .filter(s -> s.equals("O"))
                .count();
            total += (amount * base);

            base--;
        }
        return total;
    }

    private void printGrid(List<List<String>> grid) {
        for (List<String> row: grid) {
            System.out.println(row);
        }
    }

    private String checksum(List<List<String>> grid) {
        return grid.stream()
            .map(row -> row.stream().collect(Collectors.joining()))
            .collect(Collectors.joining());
    }

    @Override
    public Object solvePartOne(List<String> input) {
        List<List<String>> grid = parse(input);
        slideNorth(grid);
        return weight(grid);
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        int BASE = 1000000000;
        List<List<String>> grid = parse(input);

        Map<String, Integer> seen = new HashMap<>();
        int remainder = 0;
        for (int i = 1; i <= BASE; i++) {
            cycle(grid);

            String key = checksum(grid);

            // System.out.printf("Weight after %s iterations is %d\n%s\n", i, weight(grid), key);

            if (seen.containsKey(key)) {
                System.out.printf("Took %s iterations to find repeat\n", i);
                int firstIter = seen.get(key);
                int cycleLength = i - firstIter;

                remainder = (BASE-firstIter) % cycleLength;

                // System.out.printf("%s %s %s\n", firstIter, cycleLength, remainder);
                break;
            }

            seen.put(key, i);
        }

        // System.out.printf("found remainder: %s\n", remainder);

        for (int i = 0; i < remainder; i++) {
            cycle(grid);
        }

        return weight(grid);
    }
    
}
