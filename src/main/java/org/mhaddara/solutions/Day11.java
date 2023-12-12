package org.mhaddara.solutions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.mhaddara.Solution;

public class Day11 implements Solution {

    @Override
    public String testInput() {
        return """
            ...#......
            .......#..
            #.........
            ..........
            ......#...
            .#........
            .........#
            ..........
            .......#..
            #...#.....
                """;
    }

    private record Point(long x, long y) {
        public long dist(Point other) {
            return Math.abs(other.x - x) + Math.abs(other.y - y);
        }
    }

    private List<List<String>> parse(List<String> input) {
        return input.stream()
            .map(s -> List.of(s.split("")))
            .collect(Collectors.toList());
    }

    private Set<Integer> findEmptyColumns(List<List<String>> grid) {
        Set<Integer> emptyColumns = new HashSet<>();

        for (int x = 0; x < grid.get(0).size(); x++) {
            int col = x;
            boolean emptyColumn = grid.stream()
                .allMatch(row -> row.get(col).equals("."));

            if (emptyColumn) {
                emptyColumns.add(col);
            }
        }

        return emptyColumns;
    }

    private Set<Integer> findEmptyRows(List<List<String>> grid) {
        Set<Integer> emptyRows = new HashSet<>();

        for (int y = 0; y < grid.size(); y++) {
            List<String> row = grid.get(y);
            boolean emptyRow = row.stream()
                .allMatch(c -> c.equals("."));

            if (emptyRow) {
                emptyRows.add(y);
            }
        }

        return emptyRows;
    }

    private Set<Point> findPoints(List<List<String>> grid, Set<Integer> emptyColumns, Set<Integer> emptyRows, long emptyMultiplier) {
        Set<Point> points = new HashSet<>();
        for (int y = 0; y < grid.size(); y++) {
            int y2 = y;
            List<String> row = grid.get(y);
            for (int x = 0; x < row.size(); x++) {
                if (row.get(x).equals("#")) {
                    int col = x;

                    // 
                    long numExtraCols = emptyColumns.stream().filter(i -> i < col).count() * (emptyMultiplier-1);
                    long numExtraRows = emptyRows.stream().filter(i -> i < y2).count() * (emptyMultiplier-1);


                    points.add(new Point(x+numExtraCols, y+numExtraRows));
                }
            }
        }
        return points;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        List<List<String>> grid = parse(input);
        Set<Integer> emptyColumns = findEmptyColumns(grid);
        Set<Integer> emptyRows = findEmptyRows(grid);

        Set<Point> points = findPoints(grid, emptyColumns, emptyRows, 2);
        List<Point> ordered = points.stream().toList();

        long totalDist = 0;
        for (int i = 0; i < ordered.size(); i++) {
            Point p1 = ordered.get(i);
            for (int j = i+1; j < ordered.size(); j++) {
                Point p2 = ordered.get(j);
                totalDist += p1.dist(p2);
            }
        }

        return totalDist;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        List<List<String>> grid = parse(input);
        Set<Integer> emptyColumns = findEmptyColumns(grid);
        Set<Integer> emptyRows = findEmptyRows(grid);

        Set<Point> points = findPoints(grid, emptyColumns, emptyRows, 1_000_000);
        List<Point> ordered = points.stream().toList();

        long totalDist = 0;
        for (int i = 0; i < ordered.size(); i++) {
            Point p1 = ordered.get(i);
            for (int j = i+1; j < ordered.size(); j++) {
                Point p2 = ordered.get(j);
                totalDist += p1.dist(p2);
            }
        }

        return totalDist;
    }
    
}
