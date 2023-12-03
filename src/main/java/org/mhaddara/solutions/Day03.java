package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day03 implements Solution {

    @Override
    public String testInput() {
        return """
            467..114..
            ...*......
            ..35..633.
            ......#...
            617*......
            .....+.58.
            ..592.....
            ......755.
            ...$.*....
            .664.598..
                """;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        return findParts(input).stream()
            .mapToInt(i -> i)
            .sum();
    }

    private List<Integer> findParts(List<String> input) {
        List<Integer> parts = new ArrayList<>();

        int val = 0;
        boolean isPart = false;
        for (int row = 0; row < input.size(); row++) {
            String line = input.get(row);
            for (int col = 0; col < line.length(); col++) {
                Character c = line.charAt(col);
                if (!Character.isDigit(c)) {
                    // count the part
                    if (isPart) {
                        parts.add(val);
                    }

                    // reset state
                    val = 0;
                    isPart = false;
                    continue;
                }
                val *= 10;
                val += Integer.parseInt(Character.toString(c));
                if (!isPart) {
                    // check if it's attached to a part
                    if (isAttached(input, row, col)) {
                        isPart = true;
                    }
                }
            }
        }
        // might need to count the last part
        if (isPart) {
            parts.add(val);
        }

        return parts;
    }

    private boolean isAttached(List<String> input, int row, int col) {
        return Stream.of(-1, 0, 1).anyMatch(rx -> {
            return Stream.of(-1, 0, 1).anyMatch(cx -> {
                return isSymbol(input, row + rx, col + cx);
            });
        });
    }

    private boolean isSymbol(List<String> input, int row, int col) {
        if (row < 0 || col < 0 || row >= input.size() || col >= input.get(0).length()) {
            return false;
        }
        char c = input.get(row).charAt(col);
        if (c == '.' || Character.isDigit(c)) {
            return false;
        }

        return true;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        int total = 0;

        for (int row = 0; row < input.size(); row++) {
            String line = input.get(row);
            for (int col = 0; col < line.length(); col++) {
                char c = line.charAt(col);
                if (c != '*') {
                    continue;
                }
                List<String> adjacents = new ArrayList<>();

                // above
                if (row > 0) {
                    String rowAbove = input.get(row-1);
                    adjacents.addAll(numbersOnRow(rowAbove, col));
                }

                // below
                if (row < input.size() - 1) {
                    String rowBelow = input.get(row+1);
                    adjacents.addAll(numbersOnRow(rowBelow, col));
                }

                // left
                String left = findNumberLeft(line, col - 1);
                adjacents.add(left);

                // right
                String right = findNumberRight(line, col + 1);
                adjacents.add(right);


                // is it a gear
                List<Integer> adjacentInts = adjacents
                    .stream()
                    .filter(s -> s != "")
                    .map(Integer::parseInt)
                    .collect(Collectors.toList());

                if (adjacentInts.size() == 2) {
                    total += (adjacentInts.get(0) * adjacentInts.get(1));
                }
            }
        }

        return total;
    }

    private String findNumberLeft(String row, int start) {
        String result = "";
        int idx = start;
        while (idx >= 0 && Character.isDigit(row.charAt(idx))) {
            result = row.charAt(idx) + result;
            idx -= 1;
        }
        return result;
    }

    private String findNumberRight(String row, int start) {
        String result = "";
        int idx = start;
        while (idx < row.length() && Character.isDigit(row.charAt(idx))) {
            result = result + row.charAt(idx);
            idx += 1;
        }
        return result;
    }

    private List<String> numbersOnRow(String row, int center) {
        List<String> adjacents = new ArrayList<>();

        char centerChar = row.charAt(center);
        
        String left = findNumberLeft(row, center - 1);
        String right = findNumberRight(row, center + 1);


        if (Character.isDigit(centerChar)) {
            // left and right are one
            adjacents.add(left + centerChar + right);
        } else {
            // left and right are separate
            adjacents.add(left);
            adjacents.add(right);
        }

        return adjacents;
    }
}
