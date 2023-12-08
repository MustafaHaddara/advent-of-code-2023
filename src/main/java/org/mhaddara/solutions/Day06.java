package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;

import org.mhaddara.Solution;

public class Day06 implements Solution {

    @Override
    public String testInput() {
        return """
            Time:      7  15   30
            Distance:  9  40  200
                """;
    }

    private record Race(long duration, long record) {}

    @Override
    public Object solvePartOne(List<String> input) {
        String[] times = input.get(0).substring(11).strip().split("\\s+");
        String[] distances = input.get(1).substring(11).strip().split("\\s+");

        List<Race> races = new ArrayList<>();
        for (int i = 0; i < times.length; i++) {
            races.add(new Race(Long.parseLong(times[i].strip()), Long.parseLong(distances[i].strip())));
        }

        int totalWins = 1;
        for (Race r: races) {
            totalWins *= countWinningStrategies(r);
        }

        return totalWins;
    }

    private int countWinningStrategies(Race r) {
        int wins = 0;
        for (int i = 0; i < r.duration; i++) {
            int chargeTime = i;
            long runTime = r.duration - i;
            long distance = runTime * chargeTime;

            if (distance > r.record) {
                wins++;
            }
        }
        return wins;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        String time = String.join("", input.get(0).substring(11).strip().split("\\s+"));
        String distance = String.join("", input.get(1).substring(11).strip().split("\\s+"));

        return countWinningStrategies(new Race(Long.parseLong(time), Long.parseLong(distance)));
    }
    
}
