package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day09 implements Solution {

    @Override
    public String testInput() {
        return """
            0 3 6 9 12 15
            1 3 6 10 15 21
            10 13 16 21 30 45
                """;
    }

    private List<List<Integer>> computeLayers(List<Integer> ints) {
        List<List<Integer>> layers = new ArrayList<>();
        layers.add(ints);

        while (true) {
            List<Integer> nextLayer = new ArrayList<>();
            List<Integer> currentLayer = layers.getLast();
            boolean allZeroes = true;
            for (int i = 1; i < currentLayer.size(); i++) {
                int diff = currentLayer.get(i) - currentLayer.get(i-1);
                nextLayer.add(diff);
                allZeroes = allZeroes && (diff == 0);
            }
            layers.add(nextLayer);

            if (allZeroes) {
                break;
            }
        }

        return layers;
    }

    private int computeNextValue(List<Integer> ints) {
        List<List<Integer>> layers = computeLayers(ints);

        int next = 0;
        for (int i = layers.size()-1; i >= 0; i--) {
            next = next + layers.get(i).getLast();
        }

        return next;
    }

    private int computePrevValue(List<Integer> ints) {
        List<List<Integer>> layers = computeLayers(ints);

        int next = 0;
        for (int i = layers.size()-1; i >= 0; i--) {
            next = layers.get(i).getFirst() - next;
        }

        return next;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        int total = 0;
        for (String line: input) {
            int nextVal = computeNextValue(Stream.of(line.split("\\s+")).map(Integer::parseInt).toList());
            total += nextVal;
        }
        return total;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        int total = 0;
        for (String line: input) {
            int nextVal = computePrevValue(Stream.of(line.split("\\s+")).map(Integer::parseInt).toList());
            total += nextVal;
        }
        return total;
    }
    
}
