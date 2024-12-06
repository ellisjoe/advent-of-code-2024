package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.collect.ArrayListMultimap;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

public class Day5 {
    @Test
    void part1() {
        List<String> in = Utils.readLines(5, false);
        List<Rule> rules = in.stream().takeWhile(s -> !s.isEmpty()).map(Rule::parse).toList();
        List<Update> updates = in.stream().dropWhile(l -> l.contains("|") || l.isEmpty()).map(Update::parse).toList();

        List<Update> validUpdates = updates.stream()
                .filter(u -> rules.stream().allMatch(u::satisfies))
                .toList();
        int sum = validUpdates.stream()
                .map(u -> u.pages().get(u.pages().size() / 2))
                .mapToInt(x -> x)
                .sum();
        System.out.println(sum);
    }

    @Test
    void part2() {
        List<String> in = Utils.readLines(5, false);
        List<Rule> rules = in.stream().takeWhile(s -> !s.isEmpty()).map(Rule::parse).toList();
        List<Update> updates = in.stream().dropWhile(l -> l.contains("|") || l.isEmpty()).map(Update::parse).toList();

        ArrayListMultimap<Integer, Integer> ruleMap = ArrayListMultimap.create();
        rules.forEach(rule -> ruleMap.put(rule.before, rule.after));

        Comparator<Integer> comparator = (o1, o2) -> {
            List<Integer> o1lessThan = ruleMap.get(o1);
            List<Integer> o2lessThan = ruleMap.get(o2);

            if (o1lessThan.contains(o2)) {
                return -1;
            } else if (o2lessThan.contains(o1)) {
                return 1;
            } else {
                throw new IllegalArgumentException("Unknown sort order");
            }
        };

        int sum = updates.stream()
                .filter(u -> !rules.stream().allMatch(u::satisfies))
                .map(u -> u.pages().stream().sorted(comparator).toList())
                .map(p -> p.get(p.size() / 2))
                .mapToInt(x -> x)
                .sum();
        System.out.println(sum);
    }

    record Rule(int before, int after) {
        static Splitter splitter = Splitter.on("|");

        static Rule parse(String line) {
            List<Integer> values = splitter.splitToStream(line).map(Integer::parseInt).toList();
            return new Rule(values.get(0), values.get(1));
        }
    }

    record Update(List<Integer> pages) {
        static Splitter splitter = Splitter.on(",");

        static Update parse(String line) {
            return new Update(splitter.splitToStream(line).map(Integer::parseInt).toList());
        }

        boolean satisfies(Rule rule) {
            int beforeIndex = pages.indexOf(rule.before);
            int afterIndex = pages.indexOf(rule.after);
            if (beforeIndex != -1 && afterIndex != -1) {
                return beforeIndex < afterIndex;
            } else {
                return true;
            }
        }
    }
}
