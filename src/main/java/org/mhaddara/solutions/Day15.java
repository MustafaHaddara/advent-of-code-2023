package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day15 implements Solution {

  @Override
  public String testInput() {
    return """
      rn=1,cm-,qp=3,cm=2,qp-,pc=4,ot=9,ab=5,pc-,pc=6,ot=7
        """;
  }

  private int hash(String s) {
    int current = 0;
    for (Integer i: s.chars().toArray()) {
      current += i;
      current *= 17;
      current = current % 256;
    }
    return current;
  }

  @Override
  public Object solvePartOne(List<String> input) {
    return Stream.of(input.get(0).split(",")).mapToInt(s -> hash(s)).sum();
  }

  private enum Operation {
    ADD,
    REMOVE,
  }

  private class Instruction {
    final String label;
    final Operation op;
    final Integer focalLength;

    public Instruction(String spec) {
      if (spec.contains("=")) {
        this.op = Operation.ADD;
        String[] chunks = spec.split("=");
        this.label = chunks[0];
        this.focalLength = Integer.parseInt(chunks[1]);
      } else {
        this.op = Operation.REMOVE;
        String[] chunks = spec.split("-");
        this.label = chunks[0];
        this.focalLength = null;
      }
    }

    public String toString() {
      return String.format("[%s %s %s]", this.label, this.op, this.focalLength);
    }
  }

  private record Lens (String label, Integer focalLength) {}

  public void add(Lens lens, List<Lens> lenses) {
    int idx;
    for (idx = 0; idx < lenses.size(); idx++) {
      if (lenses.get(idx).label.equals(lens.label)) {
        break;
      }
    }
    if (idx < lenses.size()) {
      lenses.set(idx, lens);
    } else {
      lenses.add(lens);
    }
  }

  public int focusPower(int boxNum, List<Lens> box) {
    int power = 0;
    for (int i = 0; i < box.size(); i++) {
      power += (boxNum+1) * (i+1) * box.get(i).focalLength;
    }
    return power;
  }

  @Override
  public Object solvePartTwo(List<String> input) {
    List<Instruction> instructions = Stream.of(input.get(0).split(",")).map(s -> new Instruction(s)).toList();

    Map<Integer, List<Lens>> boxes = new HashMap<>();

    for (Instruction i: instructions) {
      int addr = hash(i.label);
      boxes.putIfAbsent(addr, new ArrayList<>());

      switch (i.op) {
        case Operation.ADD:
          add(new Lens(i.label, i.focalLength), boxes.get(addr));
          break;
        case Operation.REMOVE:
          boxes.get(addr).removeIf(lens -> lens.label.equals(i.label));
          break;
      }
    }

    return boxes.entrySet().stream().mapToInt((e) -> focusPower(e.getKey(), e.getValue())).sum();
  }

}
