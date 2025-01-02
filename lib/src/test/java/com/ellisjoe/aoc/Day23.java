package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.SetMultimap;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Day23 {
    @Test
    void part1() {
        SetMultimap<String, String> connections = parseInput(false);

        Set<String> computers = connections.keySet();
        Set<Triple> triples = new HashSet<>();

        for (String computerA : computers) {
            Set<String> possibilities = connections.get(computerA);
            for (String computerB : possibilities) {
                Set<String> third = connections.get(computerB);
                for (String computerC : Sets.intersection(possibilities, third)) {
                    triples.add(new Triple(Set.of(computerA, computerB, computerC)));
                }
            }
        }

        long total = triples.stream().filter(Triple::containsT).count();
        System.out.println(total);
    }

    @Test
    void part2() {
        SetMultimap<String, String> connections = parseInput(false);
        for (String key : connections.keySet()) {
            connections.put(key, key);
        }

        List<Set<String>> largestCliques = connections.keySet().stream()
                .map(c -> largestClique(c, connections))
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Set::size)))
                .toList();
        String result = largestCliques.get(0).stream().sorted().reduce((a, b) -> a + "," + b).orElseThrow();
        System.out.println(result);
    }

    static Set<String> largestClique(String computer, SetMultimap<String, String> connections) {
        Set<String> possible = connections.get(computer);
        for (int i = possible.size(); i > 0; i--) {
            for (Set<String> combination : Sets.combinations(possible, i)) {
                if (isClique(combination, connections)) {
                    return combination;
                }
            }
        }
        return Set.of();
    }

    private static boolean isClique(Set<String> combination, SetMultimap<String, String> connections) {
        for (String computer : combination) {
            Sets.SetView<String> others = Sets.difference(combination, Set.of(computer));
            if (!connections.get(computer).containsAll(others)) {
                return false;
            }
        }
        return true;
    }

    private static SetMultimap<String, String> parseInput(boolean test) {
        SetMultimap<String, String> connections = HashMultimap.create();
        Splitter splitter = Splitter.on("-");
        for (String line : Utils.readLines(23, test)) {
            List<String> computers = splitter.splitToList(line);
            connections.put(computers.get(0), computers.get(1));
            connections.put(computers.get(1), computers.get(0));
        }
        return connections;
    }

    record Triple(Set<String> computers) {
        boolean containsT() {
            return computers.stream().anyMatch(x -> x.startsWith("t"));
        }
    }
}
