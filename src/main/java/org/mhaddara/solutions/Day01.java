package org.mhaddara.solutions;

import java.util.List;
import java.util.stream.Collectors;

import org.mhaddara.Solution;

public class Day01 implements Solution {

    @Override
    public String testInput() {
        return """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
            """;
    }

    @Override
    public String solvePartOne(List<String> input) {
        return input.stream()
            .map(s -> removeAlphas(s))
            .map(s -> s.substring(0, 1) + s.substring(s.length()-1, s.length()))
            .collect(Collectors.summingInt(Integer::parseInt))
            .toString();
    }

    private String removeAlphas(String s) {
        return s.chars()
            .mapToObj(c -> (char) c)
            .filter(Character::isDigit)
            .map(c -> c.toString())
            .collect(Collectors.joining());
    }

    @Override
    public String solvePartTwo(List<String> input) {
        return input.stream()
            .map(s -> (findFirstDigit(s) *10) + findLastDigit(s))
            .collect(Collectors.summingInt(i -> i))
            .toString();
    }

    private int findFirstDigit(String s) {
        String[] digits = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        };
        String[] words = {
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        };

        for (int i=0; i<digits.length; i++) {
            if (s.startsWith(digits[i])) {
                return i;
            }
        }
        for (int i=0; i<words.length; i++) {
            if (s.startsWith(words[i])) {
                return i;
            }
        }
        return findFirstDigit(s.substring(1));
    }

    private int findLastDigit(String s) {
        String[] digits = {
            "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"
        };
        String[] words = {
            "zero", "one", "two", "three", "four", "five", "six", "seven", "eight", "nine"
        };

        for (int i=digits.length-1; i>=0; i--) {
            if (s.endsWith(digits[i])) {
                return i;
            }
        }
        for (int i=words.length-1; i>=0; i--) {
            if (s.endsWith(words[i])) {
                return i;
            }
        }
        return findLastDigit(s.substring(0, s.length()-1));
    }
    
}
