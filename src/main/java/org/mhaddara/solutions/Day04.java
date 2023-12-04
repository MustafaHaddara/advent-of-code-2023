package org.mhaddara.solutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day04 implements Solution {
    private record Card(int id, Set<Integer> winningNums, List<Integer> cardNums) {
        public long getNumWinning() {
            return cardNums.stream()
                    .filter(s -> winningNums.contains(s))
                    .count();
        }

        public int score() {
            long numWinning = getNumWinning();

            if (numWinning == 0)
                return 0;
            return (int) Math.pow(2, numWinning - 1);
        }
    }

    @Override
    public String testInput() {
        return """
                Card 1: 41 48 83 86 17 | 83 86  6 31 17  9 48 53
                Card 2: 13 32 20 16 61 | 61 30 68 82 17 32 24 19
                Card 3:  1 21 53 59 44 | 69 82 63 72 16 21 14  1
                Card 4: 41 92 73 84 69 | 59 84 76 51 58  5 54 83
                Card 5: 87 83 26 28 32 | 88 30 70 12 93 22 82 36
                Card 6: 31 18 13 56 72 | 74 77 10 23 35 67 36 11
                    """;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        return input.stream()
                .map(this::parseLine)
                .mapToInt(c -> c.score())
                .sum();
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        Map<Integer, Integer> numCards = new HashMap<>();

        for (String line : input) {
            Card card = parseLine(line);
            numCards.merge(card.id, 1, (a, b) -> a + b);

            int multiplier = numCards.get(card.id);
            for (int i = 1; i < card.getNumWinning() + 1; i++) {
                numCards.merge(card.id + i, multiplier, (a, b) -> a + b);
            }
        }

        return numCards.values().stream().mapToInt(i -> i).sum();
    }

    private Card parseLine(String line) {
        Pattern p = Pattern.compile("Card[\\s]+(\\d+):[\s+]([\\d ]*) \\| ([\\d ]*)");
        Matcher m = p.matcher(line);
        if (m.matches()) {
            int cardNum = Integer.parseInt(m.group(1));
            Set<Integer> winningNums = Stream.of(m.group(2).split(" "))
                    .filter(s -> s != "")
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toSet());
            List<Integer> cardNums = Stream.of(m.group(3).split(" "))
                    .filter(s -> s != "")
                    .map(s -> Integer.parseInt(s))
                    .collect(Collectors.toList());

            return new Card(cardNum, winningNums, cardNums);
        }

        return null;
    }
}
