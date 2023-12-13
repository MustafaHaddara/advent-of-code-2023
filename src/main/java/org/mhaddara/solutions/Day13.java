package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;

import org.mhaddara.Solution;

public class Day13 implements Solution {

    @Override
    public String testInput() {
        return """
                #.##..##.
                ..#.##.#.
                ##......#
                ##......#
                ..#.##.#.
                ..##..##.
                #.#.##.#.

                #...##..#
                #....#..#
                ..##..###
                #####.##.
                #####.##.
                ..##..###
                #....#..#
                  """;
    }

    private List<List<String>> parse(List<String> input) {
        List<List<String>> res = new ArrayList<>();

        List<String> current = new ArrayList<>();
        for (String s: input) {
            if (s.equals("")) {
                res.add(current);
                current = new ArrayList<>();
            } else {
                current.add(s);
            }
        }
        res.add(current);

        return res;
    }

    private boolean hasMirrorColumn(List<String> grid, int col) {
        for (String row: grid) {
            int left = col;
            int right = col+1;
            while ((left) >= 0 && (right) < row.length()) {
                if (row.charAt(left) != row.charAt(right)) {
                    return false;
                }
                left--;
                right++;
            }
        }
        return true;
    }

    private boolean hasMirrorRow(List<String> grid, int row) {
        int top = row;
        int bottom = row+1;

        while (top >= 0 && bottom < grid.size()) {
            String topRow = grid.get(top);
            String bottomRow = grid.get(bottom);
            if (!topRow.equals(bottomRow)) {
                return false;
            }
            top--;
            bottom++;
        }

        return true;
    }

    private int findMirrorCol(List<String> grid) {
        int numCols = grid.get(0).length()-1;
        for (int i = 0; i < numCols; i++) {
            if (hasMirrorColumn(grid, i)) {
                return i;
            }
        }
        return -1;
    }

    private int findMirrorRow(List<String> grid) {
        int numRows = grid.size()-1;
        for (int i = 0; i < numRows; i++) {
            if (hasMirrorRow(grid, i)) {
                return i;
            }
        }
        return -1;
    }

    private void printGrid(List<String> grid) {
        for (String string : grid) {
            System.out.println(string);
        }
    }

    @Override
    public Object solvePartOne(List<String> input) {
        List<List<String>> grids = parse(input);

        int mirrorCols = 0;
        int mirrorRows = 0;

        for (List<String> grid: grids) {
            int maybeCol = findMirrorCol(grid);
            if (maybeCol > -1) {
                mirrorCols += maybeCol+1;
                continue;
            }
            int row = findMirrorRow(grid);
            mirrorRows += row+1;
        }

        return mirrorCols + (100 * mirrorRows);
    }

    private boolean hasAlmostMirrorColumn(List<String> grid, int col) {
        int diff = 0;
        for (String row: grid) {
            int left = col;
            int right = col+1;
            while ((left) >= 0 && (right) < row.length()) {
                if (row.charAt(left) != row.charAt(right)) {
                    if (diff == 1) {
                        return false;
                    }
                    diff++;
                }
                left--;
                right++;
            }
        }
        return diff == 1;
    }

    private boolean hasAlmostMirrorRow(List<String> grid, int row) {
        int top = row;
        int bottom = row+1;

        int diff = 0;
        while (top >= 0 && bottom < grid.size()) {
            String topRow = grid.get(top);
            String bottomRow = grid.get(bottom);
            for (int i = 0; i < topRow.length(); i++) {
                if (topRow.charAt(i) != bottomRow.charAt(i)) {
                    if (diff == 1) {
                        return false;
                    }
                    diff++;
                }
            }
            top--;
            bottom++;
        }

        return diff == 1;
    }

    private int findAlmostMirrorCol(List<String> grid) {
        int numCols = grid.get(0).length()-1;
        for (int i = 0; i < numCols; i++) {
            if (hasAlmostMirrorColumn(grid, i)) {
                return i;
            }
        }
        return -1;
    }

    private int findAlmostMirrorRow(List<String> grid) {
        int numRows = grid.size()-1;
        for (int i = 0; i < numRows; i++) {
            if (hasAlmostMirrorRow(grid, i)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        List<List<String>> grids = parse(input);

        int mirrorCols = 0;
        int mirrorRows = 0;

        for (List<String> grid: grids) {
            int maybeCol = findAlmostMirrorCol(grid);
            if (maybeCol > -1) {
                mirrorCols += maybeCol+1;
                continue;
            }
            int row = findAlmostMirrorRow(grid);
            mirrorRows += row+1;
        }

        return mirrorCols + (100 * mirrorRows);
    }

}
