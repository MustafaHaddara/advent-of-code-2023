package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.function.Function;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day17 implements Solution {

    @Override
    public String testInput() {
        return """
            111111111111
            999999999991
            999999999991
            999999999991
            999999999991
                """;
        // return """
        //     2413432311323
        //     3215453535623
        //     3255245654254
        //     3446585845452
        //     4546657867536
        //     1438598798454
        //     4457876987766
        //     3637877979653
        //     4654967986887
        //     4564679986453
        //     1224686865563
        //     2546548887735
        //     4322674655533
        //         """;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        Point bounds = new Point(input.get(0).length(), input.size());
        Point start = new Point(0, 0);
        Point end = new Point(input.get(0).length()-1, input.size()-1);
        List<List<Integer>> grid = parse(input);

        return lowestCost(
            grid,
            start,
            end,
            bounds,
            this::validDirectionsP1,
            (steps) -> steps.subList(Math.max(0, steps.size()-3), steps.size()).toString(),
            (_steps) -> true
        );
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        Point bounds = new Point(input.get(0).length(), input.size());
        Point start = new Point(0, 0);
        Point end = new Point(input.get(0).length()-1, input.size()-1);
        List<List<Integer>> grid = parse(input);

        return lowestCost(
            grid,
            start,
            end,
            bounds,
            this::validDirectionsP2,
            (steps) -> steps.subList(Math.max(0, steps.size()-10), steps.size()).toString(),
            (steps) -> lastNTheSame(steps, 4)
        );
    }
    
    private enum Direction {
        UP, DOWN, LEFT, RIGHT
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

    private List<List<Integer>> parse(List<String> input) {
        return input.stream()
            .map(s -> s.chars().mapToObj(Character::toString).map(Integer::parseInt).toList())
            .toList();
    }

    private record DfsState(Point current, Long total, List<Direction> steps) {}

    private long lowestCost(List<List<Integer>> grid, Point start, Point end, Point bounds, Function<List<Direction>, Iterable<Direction>> getNextDirections, Function<List<Direction>, String> key, Function<List<Direction>, Boolean> canStop) {
        Map<Point, Map<String, Long>> distances = new HashMap<>();
        PriorityQueue<DfsState> queue = new PriorityQueue<>((s1, s2) -> Long.compare(s1.total, s2.total));

        Long globalMinimum = Long.MAX_VALUE;

        List<Direction> initialSteps = new ArrayList<>();
        queue.add(new DfsState(start, 0L, initialSteps));

        while (!queue.isEmpty()) {
            DfsState state = queue.remove();

            Point current = state.current;
            Long currentTotal = state.total;
            List<Direction> steps = state.steps;

            String trail = key.apply(steps);

            if (currentTotal > globalMinimum) {
                continue;
            }

            if (distances.containsKey(current) && distances.get(current).containsKey(trail) && currentTotal >= distances.get(current).get(trail)) {
                continue;
            }

            distances.putIfAbsent(current, new HashMap<>());
            distances.get(current).put(trail, currentTotal);

            if (current.equals(end) && canStop.apply(steps)) {
                globalMinimum = Math.min(globalMinimum, currentTotal);
                continue;
            }

            Iterable<Direction> dirs = getNextDirections.apply(steps);
            for (Direction d: dirs) {
                Point nextPoint = current.move(d);
                if (!isInBounds(nextPoint, bounds)) {
                    continue;
                }

                List<Direction> nextSteps = new ArrayList<>(steps);
                nextSteps.add(d);

                int cost = grid.get(nextPoint.y).get(nextPoint.x);

                queue.add(new DfsState(nextPoint, currentTotal+cost, nextSteps));
            }
        }

        return globalMinimum;
    }

    private boolean isInBounds(Point p, Point bounds) {
        return p.x >= 0 && p.y >= 0 & p.x < bounds.x & p.y < bounds.y;
    }

    private List<Direction> validDirectionsP1(List<Direction> steps) {
        Stream<Direction> allowed = Stream.of(Direction.RIGHT, Direction.DOWN, Direction.UP, Direction.LEFT);
        if (steps.isEmpty()) {
            return allowed.toList();
        }

        Direction last = steps.get(steps.size()-1);
        Direction opp = opposite(last);
        allowed = allowed.filter(d -> !d.equals(opp));

        if (steps.size() >= 3 && lastNTheSame(steps, 3)) {
            allowed = allowed.filter(d -> !d.equals(last));
        }

        return allowed.toList();
    }

    private List<Direction> validDirectionsP2(List<Direction> steps) {
        Stream<Direction> allowed = Stream.of(Direction.RIGHT, Direction.DOWN, Direction.UP, Direction.LEFT);

        if (steps.isEmpty()) {
            return allowed.toList();
        }

        Direction last = steps.get(steps.size()-1);

        // never allowed to turn backwards
        Direction opp = opposite(last);
        allowed = allowed.filter(d -> !d.equals(opp));

        if (steps.size() < 4) {
            return List.of(last);
        }
        
        // were the last 4 all the same?
        if (!lastNTheSame(steps, 4)) {
            return List.of(last);
        }

        if (steps.size() < 10) {
            // less than 10 steps and the last 4 were the same
            return allowed.toList();
        }

        if (lastNTheSame(steps, 10)) {
            // not allowed to keep going
            allowed = allowed.filter(d -> !d.equals(last));
        }

        return allowed.toList();
    }

    private <T> boolean lastNTheSame(List<T> steps, int n) {
        T last = steps.get(steps.size()-1);

        boolean lastNsame = true;
        for (int i = 2; i <= n; i++) {
            lastNsame = lastNsame & steps.get(steps.size()-i).equals(last);
        }
        return lastNsame;
    }

    private Direction opposite(Direction d) {
        switch (d) {
            case Direction.UP: return Direction.DOWN;
            case Direction.DOWN: return Direction.UP;
            case Direction.LEFT: return Direction.RIGHT;
            case Direction.RIGHT: return Direction.LEFT;
            default: return null;
        }
    }

}
