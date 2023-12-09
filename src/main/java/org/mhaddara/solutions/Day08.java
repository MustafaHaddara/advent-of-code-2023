package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mhaddara.Solution;

public class Day08 implements Solution {

    @Override
    public String testInput() {
        return """
            RL

            AAA = (BBB, CCC)
            BBB = (DDD, EEE)
            CCC = (ZZZ, GGG)
            DDD = (DDD, DDD)
            EEE = (EEE, EEE)
            GGG = (GGG, GGG)
            ZZZ = (ZZZ, ZZZ)
                """;
    }

    private record Node(String loc, String left, String right) {}

    @Override
    public Object solvePartOne(List<String> input) {
        String instructions = input.get(0);

        Map<String, Node> nodesById = new HashMap<>();

        Pattern p = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
        for (int i = 2; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String loc = m.group(1);
                String left = m.group(2);
                String right = m.group(3);

                nodesById.put(loc, new Node(loc, left, right));
            }
        }

        String current = "AAA";
        int steps = 0;
        int idx = 0;
        while (!current.equals("ZZZ")) {
            Character i = instructions.charAt(idx);

            Node c = nodesById.get(current);
            switch (i) {
                case 'L':
                    current = c.left;
                    break;
                case 'R':
                    current = c.right;
                    break;
                default:
                    break;
            }

            steps++;
            idx++;
            if (idx >= instructions.length()) {
                idx = 0;
            }
        }

        return steps;
    }

    private Map<String, Node> findBigSteps(String instructions, String start, Map<String, Node> nodesById) {
        Map<String, Node> bigSteps = new HashMap<>();

        String current = start;
        while (true) {
            String stepStart = current;

            for (Character i: instructions.toCharArray()) {
                Node c = nodesById.get(current);
                switch (i) {
                    case 'L':
                        current = c.left;
                        break;
                    case 'R':
                        current = c.right;
                        break;
                    default:
                        break;
                }
            }

            bigSteps.put(stepStart, nodesById.get(current));
            if (bigSteps.containsKey(current)) {
                break;
            }
        }

        return bigSteps;
    }

    @Override
    public Object solvePartTwo(List<String> input) {
        String instructions = input.get(0);

        Map<String, Node> nodesById = new HashMap<>();

        List<String> cursors = new ArrayList<>();
        Pattern p = Pattern.compile("(\\w{3}) = \\((\\w{3}), (\\w{3})\\)");
        for (int i = 2; i < input.size(); i++) {
            String line = input.get(i);
            Matcher m = p.matcher(line);
            if (m.matches()) {
                String loc = m.group(1);
                String left = m.group(2);
                String right = m.group(3);

                nodesById.put(loc, new Node(loc, left, right));

                if (loc.endsWith("A")) {
                    cursors.add(loc);
                }
            }
        }

        long steps = 1;
        for (String pos: cursors) {
            Map<String, Node> bigSteps = findBigSteps(instructions, pos, nodesById);

            int initialSteps = 0;
            String c = pos;
            while (!c.endsWith("Z")) {
                Node next = bigSteps.get(c);
                initialSteps++;
                c = next.loc;
            }

            steps *= initialSteps;
        }

        return steps * instructions.length();
    }
    
}
