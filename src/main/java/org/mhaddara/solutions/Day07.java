package org.mhaddara.solutions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mhaddara.Solution;

public class Day07 implements Solution {

    static int FIVE_KIND = 6;
    static int FOUR_KIND = 5;
    static int FULL_HOUSE = 4;
    static int THREE_KIND = 3;
    static int TWO_PAIR = 2;
    static int ONE_PAIR = 1;
    static int HIGH_CARD = 0;

    static String[] ORDERING = new String[] { 
        "A", "K", "Q", "J", "T", "9", "8", "7", "6", "5", "4", "3", "2"
    };
    static Map<String, Integer> VALUES = new HashMap<>();
    static {
        for (int i = 0; i < ORDERING.length; i++) {
            VALUES.put(ORDERING[i], ORDERING.length-i);
        }
    }

    static String[] PART2_ORDERING = new String[] { 
        "A", "K", "Q", "T", "9", "8", "7", "6", "5", "4", "3", "2", "J"
    };
    static Map<String, Integer> PART2_VALUES = new HashMap<>();
    static {
        for (int i = 0; i < PART2_ORDERING.length; i++) {
            PART2_VALUES.put(PART2_ORDERING[i], PART2_ORDERING.length-i);
        }
    }

    private record Hand(String c1, String c2, String c3, String c4, String c5, int bid) implements Comparable<Hand> {
        public int value() {
            Map<String, Integer> countByCard = new HashMap<>();
            countByCard.merge(c1, 1, (x, y) -> x+y);
            countByCard.merge(c2, 1, (x, y) -> x+y);
            countByCard.merge(c3, 1, (x, y) -> x+y);
            countByCard.merge(c4, 1, (x, y) -> x+y);
            countByCard.merge(c5, 1, (x, y) -> x+y);

            // 5 of a kind - 6
            if (countByCard.size() == 1) {
                return FIVE_KIND;
            }

            if (countByCard.size() == 2) {
                // 4 of a kind - 5
                if (countByCard.containsValue(4)) {
                    return FOUR_KIND;
                }

                // full house - 4
                if (countByCard.containsValue(3)) {
                    return FULL_HOUSE;
                }
            }

            // three of a kind - 3
            if (countByCard.containsValue(3)) {
                return THREE_KIND;
            }

            if (countByCard.size() == 3) {
                // two pair - 2
                return TWO_PAIR;
            }

            // one pair - 1
            if (countByCard.containsValue(2)) {
                return ONE_PAIR;
            }

            // high card
            return HIGH_CARD;
        }

        @Override
        public int compareTo(Hand other) {
            int v = value();
            int ov = other.value();
            int cmp = Integer.compare(v, ov);

            if (cmp != 0) {
                return cmp;
            }

            if (!c1.equals(other.c1)) {
                return Integer.compare(VALUES.get(c1), VALUES.get(other.c1));
            }
            if (!c2.equals(other.c2)) {
                return Integer.compare(VALUES.get(c2), VALUES.get(other.c2));
            }
            if (!c3.equals(other.c3)) {
                return Integer.compare(VALUES.get(c3), VALUES.get(other.c3));
            }
            if (!c4.equals(other.c4)) {
                return Integer.compare(VALUES.get(c4), VALUES.get(other.c4));
            }
            if (!c5.equals(other.c5)) {
                return Integer.compare(VALUES.get(c5), VALUES.get(other.c5));
            }

            return 0;
        }
    }

    private record JokerHand(String c1, String c2, String c3, String c4, String c5, int bid) implements Comparable<JokerHand> {
        public int value() {
            Map<String, Integer> countByCard = new HashMap<>();
            countByCard.merge(c1, 1, (x, y) -> x+y);
            countByCard.merge(c2, 1, (x, y) -> x+y);
            countByCard.merge(c3, 1, (x, y) -> x+y);
            countByCard.merge(c4, 1, (x, y) -> x+y);
            countByCard.merge(c5, 1, (x, y) -> x+y);

            int numJokers = countByCard.getOrDefault("J", 0);

            if (numJokers == 0) {
                return (new Hand(c1, c2, c3, c4, c5, numJokers)).value();

            } else if (numJokers == 1) {
                // 5 of a kind - 6
                if (countByCard.size() == 2) {
                    return FIVE_KIND;
                }

                if (countByCard.size() == 3) {
                    // 4 of a kind - 5
                    if (countByCard.containsValue(3)) {
                        return FOUR_KIND;
                    }

                    // full house - 4
                    if (countByCard.containsValue(2)) {
                        return FULL_HOUSE;
                    }
                }

                // three of a kind - 3
                if (countByCard.containsValue(2)) {
                    return THREE_KIND;
                }

                return ONE_PAIR;

            } else if (numJokers == 2) {
                // 5 of a kind - 6
                if (countByCard.size() == 2) {
                    return FIVE_KIND;
                }

                if (countByCard.size() == 3) {
                    // 4 of a kind - 5
                    return FOUR_KIND;
                }

                return THREE_KIND;

            } else if (numJokers == 3) {
                if (countByCard.size() == 2) {
                    return FIVE_KIND;
                }
                return FOUR_KIND;
            }

            return FIVE_KIND;
        }

        @Override
        public int compareTo(JokerHand other) {
            int v = value();
            int ov = other.value();
            int cmp = Integer.compare(v, ov);

            if (cmp != 0) {
                return cmp;
            }

            if (!c1.equals(other.c1)) {
                return Integer.compare(PART2_VALUES.get(c1), PART2_VALUES.get(other.c1));
            }
            if (!c2.equals(other.c2)) {
                return Integer.compare(PART2_VALUES.get(c2), PART2_VALUES.get(other.c2));
            }
            if (!c3.equals(other.c3)) {
                return Integer.compare(PART2_VALUES.get(c3), PART2_VALUES.get(other.c3));
            }
            if (!c4.equals(other.c4)) {
                return Integer.compare(PART2_VALUES.get(c4), PART2_VALUES.get(other.c4));
            }
            if (!c5.equals(other.c5)) {
                return Integer.compare(PART2_VALUES.get(c5), PART2_VALUES.get(other.c5));
            }

            return 0;
        }
    }

    @Override
    public String testInput() {
        return """
            32T3K 765
            T55J5 684
            KK677 28
            KTJJT 220
            QQQJA 483
                """;
    }

    private Hand parse(String line) {
        String[] chunks = line.split(" ");
        String[] cards = chunks[0].split("");
        int bid = Integer.parseInt(chunks[1]);

        return new Hand(cards[0], cards[1], cards[2], cards[3], cards[4], bid);
    }


    @Override
    public Object solvePartOne(List<String> input) {
        List<Hand> hands = input.stream()
            .map(line -> parse(line))
            .sorted()
            .toList();

        long winnings = 0;
        for (int i = 0; i < hands.size(); i++) {
            winnings += ((i+1) * hands.get(i).bid);
        }

        return winnings;
    }

    private JokerHand parse2(String line) {
        String[] chunks = line.split(" ");
        String[] cards = chunks[0].split("");
        int bid = Integer.parseInt(chunks[1]);

        return new JokerHand(cards[0], cards[1], cards[2], cards[3], cards[4], bid);
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        List<JokerHand> hands = input.stream()
            .map(line -> parse2(line))
            .sorted()
            .toList();

        long winnings = 0;
        for (int i = 0; i < hands.size(); i++) {
            winnings += ((i+1) * hands.get(i).bid);
        }

        return winnings;
    }
    
}
