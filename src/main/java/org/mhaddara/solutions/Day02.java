package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.mhaddara.Solution;

public class Day02 implements Solution {

    private record ColorSet(int red, int green, int blue) {
        @Override
        public String toString() {
            return String.format("%s %s %s", red, green, blue);
        }

        public int power() {
            return red * blue * green;
        }
    }

    private record Game(int id, List<ColorSet> sets) {
        @Override
        public String toString() {
            return String.format("Game %s - " + sets.toString(), id);
        }
    }

    @Override
    public String testInput() {
        return """
            Game 1: 3 blue, 4 red; 1 red, 2 green, 6 blue; 2 green
            Game 2: 1 blue, 2 green; 3 green, 4 blue, 1 red; 1 green, 1 blue
            Game 3: 8 green, 6 blue, 20 red; 5 blue, 4 red, 13 green; 5 green, 1 red
            Game 4: 1 green, 3 red, 6 blue; 3 green, 6 red; 3 green, 15 blue, 14 red
            Game 5: 6 red, 1 blue, 3 green; 2 blue, 1 red, 2 green
                """;
    }

    private Game parseLine(String line) {
        String[] chunks = line.split(": ");
        int gameId = Integer.parseInt(chunks[0].split(" ")[1]);

        String[] sets = chunks[1].split("; ");

        List<ColorSet> colorSets = new ArrayList<>();
        for (String set: sets) {
            String[] colors = set.split(", ");

            int red = 0, green = 0, blue = 0;
            for (String color: colors) {
                String[] parts = color.split(" ");
                switch(parts[1]) {
                    case "red": red = Integer.parseInt(parts[0]); break;
                    case "blue": blue = Integer.parseInt(parts[0]); break;
                    case "green": green = Integer.parseInt(parts[0]); break;
                }
            }
            colorSets.add(new ColorSet(red, green, blue));
        }
        return new Game(gameId, colorSets);
    }

    private boolean gameIsInvalid(Game game, ColorSet max) {
        return game.sets.stream()
            .allMatch(set -> max.red >= set.red && max.blue >= set.blue && max.green >= set.green);
    }

    @Override
    public String solvePartOne(List<String> input) {
        return input.stream()
            .map(this::parseLine)
            .filter(game -> gameIsInvalid(game, new ColorSet(12, 13, 14)))
            .collect(Collectors.summingInt(game -> game.id))
            .toString();

    }

    private ColorSet minimumSet(Game g) {
        return g.sets.stream()
            .reduce(new ColorSet(0, 0, 0), (set, mins) -> new ColorSet(
                Integer.max(set.red, mins.red),
                Integer.max(set.green, mins.green),
                Integer.max(set.blue, mins.blue)
            ));
    }

    @Override
    public String solvePartTwo(List<String> input) {
        return input.stream()
            .map(this::parseLine)
            .map(this::minimumSet)
            .collect(Collectors.summingInt(set -> set.power()))
            .toString();
    }
    
}
