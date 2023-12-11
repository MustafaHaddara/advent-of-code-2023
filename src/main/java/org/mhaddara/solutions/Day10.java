package org.mhaddara.solutions;

import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.mhaddara.Solution;

public class Day10 implements Solution{

    @Override
    public String testInput() {
        // return """
        //     ..F7.
        //     .FJ|.
        //     SJ.L7
        //     |F--J
        //     LJ...
        //         """;
        // return """
        //     -L|F7
        //     7S-7|
        //     L|7||
        //     -L-J|
        //     L|-JF
        //         """;
        return """
            ..........
            .S------7.
            .|F----7|.
            .||....||.
            .||....||.
            .|L-7F-J|.
            .|..||..|.
            .L--JL--J.
            ..........
                """;
    }

    private record Point(int x, int y) {}
    private record Grid(Point start, Map<Point, Set<Point>> connections) {}

    private enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    Set<Character> topEntrance = Set.of('|', 'J', 'L', 'S');
    Set<Character> rightEntrance = Set.of('-', 'L', 'F', 'S');
    Set<Character> downEntrance = Set.of('|', '7', 'F', 'S');
    Set<Character> leftEntrance = Set.of('-', '7', 'J', 'S');

    private boolean isValidConnection(Character srcChar, Character destChar, Direction d) {
        switch (d) {
            case UP:
                return topEntrance.contains(srcChar) && downEntrance.contains(destChar);
            case DOWN:
                return downEntrance.contains(srcChar) && topEntrance.contains(destChar);
            case RIGHT:
                return rightEntrance.contains(srcChar) && leftEntrance.contains(destChar);
            case LEFT:
                return leftEntrance.contains(srcChar) && rightEntrance.contains(destChar);
        }
        return false;
    }

    private boolean isInBounds(Point p, Point bounds) {
        if (p.x < 0 || p.y < 0 || p.y > bounds.y || p.x > bounds.x) {
            return false;
        }

        return true;
    }

    private void addIfValid(Set<Point> points, Point nextPoint, Point bounds, Character srcChar, List<String> input, Direction dir) {
        if (!isInBounds(nextPoint, bounds)) {
            return;
        }
        Character destChar = input.get(nextPoint.y).charAt(nextPoint.x);
        if (!isValidConnection(srcChar, destChar, dir)) {
            return;
        }
        points.add(nextPoint);
    }

    private Set<Point> nextPoints(int x, int y, List<String> input) {
        String row = input.get(y);
        Character c = row.charAt(x);

        int maxY = input.size()-1;
        int maxX = row.length()-1;
        Point bounds = new Point(maxX, maxY);

        Set<Point> points = new HashSet<>();
        switch (c) {
            case '|':
                addIfValid(points, new Point(x, y-1), bounds, c, input, Direction.UP);
                addIfValid(points, new Point(x, y+1), bounds, c, input, Direction.DOWN);
                break;
            case '-':
                addIfValid(points, new Point(x-1, y), bounds, c, input, Direction.LEFT);
                addIfValid(points, new Point(x+1, y), bounds, c, input, Direction.RIGHT);
                break;
            case 'L':
                addIfValid(points, new Point(x, y-1), bounds, c, input, Direction.UP);
                addIfValid(points, new Point(x+1, y), bounds, c, input, Direction.RIGHT);
                break;
            case 'J':
                addIfValid(points, new Point(x, y-1), bounds, c, input, Direction.UP);
                addIfValid(points, new Point(x-1, y), bounds, c, input, Direction.LEFT);
                break;
            case '7':
                addIfValid(points, new Point(x, y+1), bounds, c, input, Direction.DOWN);
                addIfValid(points, new Point(x-1, y), bounds, c, input, Direction.LEFT);
                break;
            case 'F':
                addIfValid(points, new Point(x, y+1), bounds, c, input, Direction.DOWN);
                addIfValid(points, new Point(x+1, y), bounds, c, input, Direction.RIGHT);
                break;
            case '.':
            default:
                break;
        }
        return points;
    }

    private Grid parse(List<String> input) {
        Point start = null;
        Map<Point, Set<Point>> connections = new HashMap<>();

        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                Character c = row.charAt(x);

                Point current = new Point(x, y);
                Set<Point> newAdj = new HashSet<>();

                switch (c) {
                    case 'S':
                        start = new Point(x, y);
                        break;
                    case '|':
                    case '-':
                    case 'L':
                    case 'J':
                    case '7':
                    case 'F':
                        newAdj.addAll(nextPoints(x, y, input));
                        break;
                    case '.':
                    default:
                        break;
                }
                

                connections.putIfAbsent(current, new HashSet<>());
                Set<Point> adj = connections.get(current);
                adj.addAll(newAdj);

                for (Point a: newAdj) {
                    connections.putIfAbsent(a, new HashSet<>());
                    connections.get(a).add(current);
                }
            }
        }

        return new Grid(start, connections);
    }

    @Override
    public Object solvePartOne(List<String> input) {
        Grid grid = parse(input);
        Set<Point> seen = new HashSet<>();

        Point c = grid.start;
        while (true) {
            seen.add(c);
            Point next = null;
            for (Point p: grid.connections.get(c)) {
                if (!seen.contains(p)) {
                    next = p;
                    break;
                }
            }
            if (next == null) {
                break;
            }
            c = next;
        }

        return (int) seen.size() / 2;
    }

    private Set<Point> findPointsOutside(List<String> input, Set<Point> loop) {
        // DFS to find the cells outside the grid
        Point bounds = new Point(input.get(0).length(), input.size());

        Point start = new Point(-1, -1);
        Set<Point> seen = new HashSet<>();
        List<Point> queue = new ArrayList<>();
        queue.add(start);

        Set<Point> outside = new HashSet<>();
        while (!queue.isEmpty()) {
            Point current = queue.removeFirst();
            if (!isInsideP2(current, bounds) || seen.contains(current) || loop.contains(current)) {
                continue;
            }

            seen.add(current);

            if (current.x >= 0 && current.y >= 0 && current.x < bounds.x && current.y < bounds.y) {
                outside.add(current);
            }

            queue.addLast(new Point(current.x-1, current.y));
            queue.addLast(new Point(current.x+1, current.y));
            queue.addLast(new Point(current.x, current.y-1));
            queue.addLast(new Point(current.x, current.y+1));
        }
        return outside;
    }

    private boolean isInsideP2(Point candidate, Point bounds) {
        return candidate.x >= -1 && candidate.y >= -1 && candidate.x <= bounds.x && candidate.y <= bounds.y;
    }

    private List<String> insertColumns(List<String> input) {
        List<String> newInput = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String row = input.get(i);
            StringBuilder sb = new StringBuilder();

            for (int j = 0; j < row.length(); j++) {
                Character left = row.charAt(j);
                sb.append(left);

                if (j < row.length()-1) {
                    // maybe pad
                    Character right = row.charAt(j+1);
                    if (rightEntrance.contains(left) && leftEntrance.contains(right)) {
                        // there's a connection!
                        sb.append('-');
                    } else {
                        sb.append('.');
                    }
                }
            }

            newInput.add(sb.toString());
        }
        return newInput;
    }

    private List<String> insertRows(List<String> input) {
        List<String> newInput = new ArrayList<>();
        for (int i = 0; i < input.size(); i++) {
            String row = input.get(i);
            newInput.add(row);

            if (i < input.size() - 1) {
                StringBuilder sb = new StringBuilder();

                String topRow = input.get(i);
                String bottomRow = input.get(i+1);

                for (int j = 0; j < topRow.length(); j++) {
                    Character topChar = topRow.charAt(j);
                    Character bottomChar = bottomRow.charAt(j);

                    if (downEntrance.contains(topChar) && topEntrance.contains(bottomChar)) {
                        // there's a connection!
                        sb.append('|');
                    } else {
                        sb.append('.');
                    }
                }
                newInput.add(sb.toString());
            }
        }

        return newInput;
    }

    private List<String> enlarge(List<String> input) {
        List<String> withColumns = insertColumns(input);
        List<String> withRows = insertRows(withColumns);

        return withRows;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        List<String> bigInput = enlarge(input);
        Grid grid = parse(bigInput);
        Set<Point> seen = new HashSet<>();

        Point c = grid.start;
        while (true) {
            seen.add(c);
            Point next = null;
            for (Point p: grid.connections.get(c)) {
                if (!seen.contains(p)) {
                    next = p;
                    break;
                }
            }
            if (next == null) {
                break;
            }
            c = next;
        }

        Point bounds = new Point(bigInput.get(0).length(), bigInput.size());

        Set<Point> outside = findPointsOutside(bigInput, seen);

        int actuallyInside = 0;
        for (int y = 0; y < bounds.y; y++) {
            for (int x = 0; x < bounds.x; x++) {
                Point p = new Point(x, y);
                if (seen.contains(p) || outside.contains(p)) {
                    continue;
                }
                // don't count the extra rows/columns we added
                if (x % 2 == 1 || y % 2 == 1) {
                    continue;
                }
                actuallyInside++;
            }
        }

        return actuallyInside;
    }
}
