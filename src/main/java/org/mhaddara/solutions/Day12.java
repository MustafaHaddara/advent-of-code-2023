package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Map<String, Long> cache = new HashMap<>();
        long totalCombos = 0;
        for (String line : input) {
            String[] chunks = line.split(" ");
            String spec = repeat(chunks[0], "?");
            List<Integer> checksum = Stream.of(repeat(chunks[1], ",").split(",")).map(Integer::parseInt).toList();

            long res = numWaysToFit(spec, checksum, cache);
            totalCombos += res;

            if (res == 0) {
                System.out.printf("Counted %s for %s, current total is %s\n", res, spec, totalCombos);
            }
        }

        return totalCombos;
    }

    private long numWaysToFit(String spec, List<Integer> groups, Map<String, Long> cache) {
        String key = makeKey(spec, groups);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        long res = numWaysToFitInner(spec, groups, cache);
        cache.put(key, res);
        return res;
    }

    private long numWaysToFitInner(String spec, List<Integer> groups, Map<String, Long> cache) {
        if (groups.size() == 0) {
            if (!spec.contains("#")) {
                return 1;
            } else {
                return 0;
            }
        }

        if (spec.chars().allMatch(c -> c == '.')) {
            return 0;
        }

        int group = groups.get(0);

        if (group > spec.length()) {
            // no way for group to fit in here
            return 0;
        }

        long res = 0;
        int startIdx = 0;
        while (startIdx < spec.length()) {
            String chunk = spec.substring(startIdx);

            if (canFit(chunk, group)) {
                String nextChunk = spec.substring(Math.min(startIdx + group + 1, spec.length()));
                res += numWaysToFit(nextChunk, groups.subList(1, groups.size()), cache);
            }

            if (spec.charAt(startIdx) == '#') {
                // not allowed to iterate forward
                break;
            }

            startIdx++;
        }

        return res;
    }

    private String makeKey(String spec, List<Integer> groups) {
        return groups.toString() + spec;
    }

    private boolean canFit(String spec, Integer size) {
        if (size > spec.length()) {
            return false;
        }

        int i = 0;
        while (i < size) {
            if (spec.charAt(i) == '.') {
                return false;
            }
            i++;
        }
        
        // ends at the end of the string or with a . or ?
        return (i == spec.length() || spec.charAt(i) != '#');
    }
}
