package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.mhaddara.Solution;

public class Day16 implements Solution {

    @Override
    public String testInput() {
        return """
            .|...\\....
            |.-.\\.....
            .....|-...
            ........|.
            ..........
            .........\\
            ..../.\\\\..
            .-.-/..|..
            .|....-|.\\
            ..//.|....
                """;
    }

    private record Point(int x, int y) {
        public Point move(Direction d) {
            switch (d) {
                case UP: return new Point(x, y-1);
                case DOWN: return new Point(x, y+1);
                case LEFT: return new Point(x-1, y);
                case RIGHT: return new Point(x+1, y);
                default: return this;
            }
        }
    }
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
    }
    private class Beam {
        Direction dir;
        Point start;
        public Beam(Direction dir, Point start) {
            this.dir = dir;
            this.start = start;
        }
    }

    private boolean isInBounds(Point loc, List<String> input) {
        return loc.x >= 0 && loc.y >= 0 && loc.y < input.size() && loc.x < input.get(0).length();
    }

    @Override
    public Object solvePartOne(List<String> input) {
        return findEnergizedPoints(input, new Beam(Direction.RIGHT, new Point(-1, 0)));
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        int maxY = input.size();
        int maxX = input.get(0).length();

        int maxPoints = -1;
        for (int i = 0; i < maxY; i++) {
            int left = findEnergizedPoints(input, new Beam(Direction.RIGHT, new Point(-1, i)));
            int right = findEnergizedPoints(input, new Beam(Direction.LEFT, new Point(maxX, i)));

            maxPoints = Math.max(maxPoints, left);
            maxPoints = Math.max(maxPoints, right);
        }

        for (int i = 0; i < maxX; i++) {
            int up = findEnergizedPoints(input, new Beam(Direction.DOWN, new Point(i, -1)));
            int down = findEnergizedPoints(input, new Beam(Direction.UP, new Point(i, maxY)));

            maxPoints = Math.max(maxPoints, up);
            maxPoints = Math.max(maxPoints, down);
        }

        return maxPoints;
    }

    private int findEnergizedPoints(List<String> input, Beam start) {
        Map<Point, Set<Direction>> seen = new HashMap<>();

        List<Beam> queue = new ArrayList<>();
        queue.add(start);

        while (!queue.isEmpty()) {
            Beam b = queue.removeFirst();

            Direction dir = b.dir;
            Point loc = b.start;
            while (dir != null) {
                if (seen.containsKey(loc) && seen.get(loc).contains(dir)) {
                    break;
                }
                seen.putIfAbsent(loc, new HashSet<>());
                seen.get(loc).add(dir);

                Point nextLoc = loc.move(dir);
                Direction nextDir = null;

                if (!isInBounds(nextLoc, input)) {
                    break;
                }

                switch (input.get(nextLoc.y).charAt(nextLoc.x)) {
                    case '.':
                        nextDir = dir;
                        break;
                    case '/':
                        switch (dir) {
                            case UP: nextDir = Direction.RIGHT; break;
                            case DOWN: nextDir = Direction.LEFT; break;
                            case LEFT: nextDir = Direction.DOWN; break;
                            case RIGHT: nextDir = Direction.UP; break;
                        }
                        break;
                    case '\\':
                        switch (dir) {
                            case UP: nextDir = Direction.LEFT; break;
                            case DOWN: nextDir = Direction.RIGHT; break;
                            case LEFT: nextDir = Direction.UP; break;
                            case RIGHT: nextDir = Direction.DOWN; break;
                        }
                        break;
                    case '|':
                        switch (dir) {
                            case UP: nextDir = dir; break;
                            case DOWN: nextDir = dir; break;
                            case LEFT:
                            case RIGHT:
                                nextDir = Direction.UP;
                                queue.add(new Beam(Direction.DOWN, nextLoc));
                                break;
                        }
                        break;
                    case '-':
                        switch (dir) {
                            case UP: 
                            case DOWN:
                                nextDir = Direction.RIGHT;
                                queue.add(new Beam(Direction.LEFT, nextLoc));
                                break;
                            case LEFT: nextDir = dir; break;
                            case RIGHT: nextDir = dir; break;
                        }
                        break;
                }

                loc = nextLoc;
                dir = nextDir;
            }
        }

        // debug(input, seen.keySet());


        return seen.size() - 1;
    }
    
}
