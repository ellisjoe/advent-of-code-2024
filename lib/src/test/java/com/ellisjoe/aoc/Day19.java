package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Day19 {
    @Test
    void part1() {
        boolean test = false;
        List<String> towels = Utils.streamLines(19, test).limit(1).flatMap(l -> Splitter.on(", ").splitToStream(l)).toList();
        List<String> designs = Utils.streamLines(19, test).skip(2).toList();

        List<String> possible = designs.stream().filter(d -> isPossible(towels, d)).toList();
        System.out.println(possible.size());
    }

    @Test
    void part2() {
        boolean test = false;
        List<String> towels = Utils.streamLines(19, test)
                .limit(1)
                .flatMap(l -> Splitter.on(", ").splitToStream(l))
                .sorted(Comparator.comparing(String::length).reversed())
                .toList();
        List<String> designs = Utils.streamLines(19, test).skip(2).toList();

        long total = designs.stream()
                .mapToLong(d -> waysPossible(towels, d))
                .sum();

        System.out.println(total);
    }

    static boolean isPossible(List<String> towels, String design) {
        if (design.isEmpty()) {
            return true;
        }

        for (String towel : towels) {
            if (design.startsWith(towel)) {
                String remaining = design.substring(towel.length());
                if (isPossible(towels, remaining)) {
                    return true;
                }
            }
        }
        return false;
    }

    static Map<String, Long> cache = new HashMap<>();
    static long waysPossible(List<String> towels, String design) {
        if (design.isEmpty()) {
            return 1;
        }

        if (cache.containsKey(design)) {
            return cache.get(design);
        }

        long count = 0;
        for (String towel : towels) {
            if (design.startsWith(towel)) {
                String remaining = design.substring(towel.length());

                count += waysPossible(towels, remaining);
            }
        }

        cache.put(design, count);
        return count;
    }
}
