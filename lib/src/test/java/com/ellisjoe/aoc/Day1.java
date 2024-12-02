package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Day1 {
    @Test
    void part1() {
        List<Integer> left = new ArrayList<>();
        List<Integer> right = new ArrayList<>();
        Utils.streamLines(1)
                .map(s -> Splitter.on("   ").splitToList(s))
                .map(n -> Lists.transform(n, Integer::parseInt))
                .forEach(n -> {
                    left.add(n.get(0));
                    right.add(n.get(1));
                });

        Collections.sort(left);
        Collections.sort(right);

        int totalDistance = 0;
        for (int i = 0; i < left.size(); i++) {
            totalDistance += Math.abs(left.get(i) - right.get(i));
        }

        System.out.println(totalDistance);
    }

    @Test
    void part2() {
        List<Integer> left = new ArrayList<>();
        Map<Integer, Integer> right = new HashMap<>();
        Utils.streamLines(1)
                .map(s -> Splitter.on("   ").splitToList(s))
                .map(n -> Lists.transform(n, Integer::parseInt))
                .forEach(n -> {
                    left.add(n.get(0));
                    right.compute(n.get(1), (k, v) -> v == null ? 1 : v + 1);
                });

        int total = left.stream()
                .mapToInt(x -> x * right.getOrDefault(x, 0))
                .sum();

        System.out.println(total);
    }
}
