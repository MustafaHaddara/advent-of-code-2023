package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day12 implements Solution {

    @Override
    public String testInput() {
        return """
                ???.### 1,1,3
                .??..??...?##. 1,1,3
                ?#?#?#?#?#?#?#? 1,3,1,6
                ????.#...#... 4,1,1
                ????.######..#####. 1,6,5
                ?###???????? 3,2,1
                  """;
    }

    private boolean arraysEqual(List<Integer> expected, List<Integer> recieved) {
        if (expected.size() != recieved.size()) {
            return false;
        }
        for (int i = 0; i < expected.size(); i++) {
            if (!expected.get(i).equals(recieved.get(i))) {
                return false;
            }
        }
        return true;
    }

    private boolean isValid(String line, List<Integer> expected) {
        List<Integer> recieved = new ArrayList<>();
        int chunkLength = 0;
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '.') {
                if (chunkLength > 0) {
                    recieved.add(chunkLength);
                    chunkLength = 0;
                }
            } else {
                chunkLength++;
            }
        }
        if (chunkLength > 0) {
            recieved.add(chunkLength);
            chunkLength = 0;
        }

        return arraysEqual(expected, recieved);
    }

    private List<String> findCombos(String spec) {
        List<String> result = new ArrayList<>();

        if (spec.length() == 0) {
            result.add("");
            return result;
        }

        String firstChar = spec.substring(0, 1);

        String next = spec.substring(1);
        List<String> nextCombos = findCombos(next);

        for (String sub : nextCombos) {
            if (firstChar.equals("?")) {
                result.add('.' + sub);
                result.add('#' + sub);
            } else {
                result.add(firstChar + sub);
            }
        }

        return result;
    }

    private long findNumCombos(String spec, String checksum) {
        List<Integer> checks = Stream.of(checksum.split(",")).map(Integer::parseInt).toList();
        List<String> combos = findCombos(spec);

        return combos.stream()
                .filter(s -> isValid(s, checks))
                .count();
    }

    @Override
    public Object solvePartOne(List<String> input) {
        long totalCombos = 0;
        for (String line : input) {
            String[] chunks = line.split(" ");
            String spec = chunks[0];
            String checksum = chunks[1];

            totalCombos += findNumCombos(spec, checksum);
        }

        return totalCombos;
    }

    private String repeat(String b, String sep) {
        return b + sep + b + sep + b + sep + b + sep + b;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        long totalCombos = 0;
        for (String line : input) {
            String[] chunks = line.split(" ");
            String spec = repeat(chunks[0], "#");
            String checksum = repeat(chunks[1], ",");

            totalCombos += findNumCombos(spec, checksum);
        }

        return totalCombos;
    }
}
