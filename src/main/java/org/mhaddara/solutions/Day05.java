package org.mhaddara.solutions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.StructuredTaskScope;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.mhaddara.Solution;

public class Day05 implements Solution {

    @Override
    public String testInput() {
        return """
            seeds: 79 14 55 13

            seed-to-soil map:
            50 98 2
            52 50 48
            
            soil-to-fertilizer map:
            0 15 37
            37 52 2
            39 0 15
            
            fertilizer-to-water map:
            49 53 8
            0 11 42
            42 0 7
            57 7 4
            
            water-to-light map:
            88 18 7
            18 25 70
            
            light-to-temperature map:
            45 77 23
            81 45 19
            68 64 13
            
            temperature-to-humidity map:
            0 69 1
            1 0 69
            
            humidity-to-location map:
            60 56 37
            56 93 4
                """;
    }

    private List<List<String>> splitInput(List<String> input) {
        List<List<String>> result = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line: input) {
            if (line.equals("")) {
                result.add(current);
                current = new ArrayList<>();
            } else {
                current.add(line);
            }
        }
        result.add(current);
        return result;
    }

    

    private record Mapping(String source, String dest, List<Range> lut) {
        public Long get(Long i) {
            return lut.stream().filter(r -> r.contains(i)).findFirst().orElse(IDENTITY_RANGE).transform(i);
        }
    }

    private record Range(Long destStart, Long srcStart, Long size) {
        public boolean contains(Long i) {
            return srcStart <= i && i < (srcStart + size);
        }
        public Long transform(Long i) {
            return i - srcStart + destStart;
        }
    }

    private static Range IDENTITY_RANGE = new Range(0L,0L,0L);

    private Mapping parseMap(List<String> mapSpec) {
        String[] def = mapSpec.get(0).split(" ")[0].split("-to-");
        String source = def[0];
        String dest = def[1];

        List<Range> lut = new ArrayList<>();
        for (int i = 1; i < mapSpec.size(); i++) {
            String[] mapChunks = mapSpec.get(i).split(" ");
            Long destStart = Long.parseLong(mapChunks[0]); 
            Long srcStart = Long.parseLong(mapChunks[1]); 
            Long length = Long.parseLong(mapChunks[2]); 

            Range r = new Range(destStart, srcStart, length);
            lut.add(r);
        }

        return new Mapping(source, dest, lut);
    }

    public Long getLocation(Long seedNum, Map<String, Mapping> mappers) {
        String type = "seed";
        Long curr = seedNum;
        while (!type.equals("location")) {
            Mapping m = mappers.get(type);
            // System.out.printf("currently on %s with type %s. Going to type %s.\n", curr, type, m.dest);
            curr = m.get(curr);
            type = m.dest;
        }
        return curr;
    }

    @Override
    public Object solvePartOne(List<String> input) {
        List<List<String>> chunks = splitInput(input);
        String seedsSpec = input.get(0);

        Map<String, Mapping> mappers = new HashMap<>();

        for (int i = 1; i < chunks.size(); i++) {
            Mapping m = parseMap(chunks.get(i));
            mappers.put(m.source, m);
            System.out.printf("Found mapper from %s to %s.\n", m.source, m.dest);
        }

        return Stream.of(seedsSpec.split(": ")[1].split(" "))
            .map(Long::parseLong)
            .mapToLong(i -> getLocation(i, mappers))
            .min()
            .getAsLong();
    }



    @Override
    public Object solvePartTwo(List<String> input) {
        List<List<String>> chunks = splitInput(input);
        String seedsSpec = input.get(0);

        Map<String, Mapping> mappers = new HashMap<>();
        for (int i = 1; i < chunks.size(); i++) {
            Mapping m = parseMap(chunks.get(i));
            mappers.put(m.source, m);
            System.out.printf("Found mapper from %s to %s.\n", m.source, m.dest);
        }

        String[] seedChunks = seedsSpec.split(": ")[1].split(" ");
        // Long min = Long.MAX_VALUE;
        // for (int i = 0; i < seedChunks.length; i+=2) {
        //     Long start = Long.parseLong(seedChunks[i]);
        //     Long range = Long.parseLong(seedChunks[i+1]);

        //     for (long j=start; j<start+range; j++) {
        //         Long loc = getLocation(j, mappers);
        //         // System.out.printf("tried seed %s, got location %s.\n", j, loc);
        //         min = Long.min(loc, min);
        //     }

        //     System.out.printf("Finished seed chunk %s of %s.\n", i, seedChunks.length/2);
        // }

        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            List<Supplier<Long>> tasks = new ArrayList<>();
            for (int i = 0; i < seedChunks.length; i+=2) {
                final Long start = Long.parseLong(seedChunks[i]);
                final Long range = Long.parseLong(seedChunks[i+1]);

                tasks.add(scope.fork(() -> {
                    Long min = Long.MAX_VALUE;
                    for (long j=start; j<start+range; j++) {
                        Long loc = getLocation(j, mappers);
                        // System.out.printf("tried seed %s, got location %s.\n", j, loc);
                        min = Long.min(loc, min);
                    }
                    return min;
                }));
            }

            // wait for everyone to finish
            scope.join();

            return tasks.stream()
                .mapToLong(t -> t.get())
                .min()
                .getAsLong();

        } catch (InterruptedException e) {
            // do nothing
            e.printStackTrace();
        }
        return null;

        // return min;
    }
}
