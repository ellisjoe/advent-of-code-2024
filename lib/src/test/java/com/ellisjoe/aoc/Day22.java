package com.ellisjoe.aoc;

import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Day22 {
    @Test
    void part1() {
        long sum = Utils.streamLines(22, false)
                .map(Long::parseLong)
                .mapToLong(s -> secretAt(s, 2_000))
                .sum();

        System.out.println(sum);
    }

    @Test
    void part2() {
        List<List<Long>> prices = Utils.streamLines(22, false)
                .map(Long::parseLong)
                .map(s -> secretList(s, 2_000))
                .map(x -> x.stream().map(v -> v % 10).toList())
                .toList();

        List<List<Long>> changes = prices.stream().map(Day22::diff).toList();

        List<Long> bestPattern = List.of();
        long maxBananas = 0;

        for (int a = -9; a <= 9; a++) {
            System.out.println("a: " + a);
            for (int b = -9; b <= 9; b++) {
                for (int c = -9; c <= 9; c++) {
                    for (int d = -9; d <= 9; d++) {
                        List<Long> pattern = List.of((long) a, (long) b, (long) c, (long) d);
                        long total = 0;
                        for (int i = 0; i < prices.size(); i++) {
                            List<Long> monkeyPrices = prices.get(i);
                            List<Long> monkeyChanges = changes.get(i);
                            total += bannanasForPattern(pattern, monkeyChanges, monkeyPrices);
                        }
                        if (total > maxBananas) {
                            bestPattern = pattern;
                            maxBananas = total;
                        }
                    }
                }
            }
        }

        System.out.println(bestPattern);
        System.out.println(maxBananas);
    }

    record Key(int index, long pattern) {
        static Key create(int index, List<Long> pattern) {
            return new Key(index, pack(pattern));
        }
    }

    static long pack(List<Long> longs) {
        long value = 0;
        for (Long num : longs) {
            value = value << 5;
            value |= (num + 9);
        }
        return value;
    }

    static List<Long> unpack(long value) {
        List<Long> values = new ArrayList<>();
        long mask = 31;
        for (int i = 0; i < 4; i++) {
            values.add((value & mask) - 9);
            value >>= 5;
        }
        return Lists.reverse(values);
    }

    record Result(long bananas, long pattern) {
    }

    @Test
    void part2_index() {
        List<List<Long>> prices = Utils.streamLines(22, false)
                .map(Long::parseLong)
                .map(s -> secretList(s, 2_000))
                .map(x -> x.stream().map(v -> v % 10).toList())
                .toList();

        List<List<Long>> changes = prices.stream().map(Day22::diff).toList();

        Map<Key, Long> monkeyPatternToPrice = new HashMap<>();
        for (int i = 0; i < changes.size(); i++) {
            List<Long> monkeyPrices = prices.get(i);
            List<Long> monkeyChanges = changes.get(i);
            for (int j = 0; j < monkeyChanges.size() - 3; j++) {
                List<Long> pattern = monkeyChanges.subList(j, j + 4);
                Long price = monkeyPrices.get(j + 4);
                monkeyPatternToPrice.putIfAbsent(Key.create(i, pattern), price);
            }
        }

        Set<Long> patterns = monkeyPatternToPrice.keySet().stream().map(Key::pattern).collect(Collectors.toSet());

        System.out.println("Searching");
        Stopwatch stopwatch = Stopwatch.createStarted();

        Result result = patterns.parallelStream()
                .map(pattern -> {
                    long total = 0;
                    for (int i = 0; i < prices.size(); i++) {
                        total += monkeyPatternToPrice.getOrDefault(new Key(i, pattern), 0L);
                    }
                    return new Result(total, pattern);
                })
                .max(Comparator.comparing(Result::bananas))
                .orElseThrow();

        System.out.println("Time: " + stopwatch.stop());
        System.out.println(unpack(result.pattern()));
        System.out.println(result.bananas());
    }

    static Long bannanasForPattern(List<Long> pattern, List<Long> changes, List<Long> prices) {
        int index = Collections.indexOfSubList(changes, pattern);
        if (index < 0) {
            return 0L;
        } else {
            return prices.get(index + 4);
        }
    }

    static List<Long> diff(List<Long> values) {
        List<Long> diff = new ArrayList<>();
        for (int i = 0; i < values.size() - 1; i++) {
            Long left = values.get(i);
            Long right = values.get(i + 1);
            diff.add(right - left);
        }
        return diff;
    }

    static List<Long> secretList(long secret, long iterations) {
        List<Long> result = new ArrayList<>();
        result.add(secret);
        for (int i = 0; i < iterations; i++) {
            secret = next(secret);
            result.add(secret);
        }
        return result;
    }

    static long secretAt(long secret, long iterations) {
        for (int i = 0; i < iterations; i++) {
            secret = next(secret);
        }
        return secret;
    }

    static long next(long secret) {
        long first = mixAndPrune(secret * 64, secret);
        long second = mixAndPrune(first / 32, first);
        return mixAndPrune(second * 2048, second);
    }

    static long mixAndPrune(long left, long right) {
        return prune(mix(left, right));
    }

    static long mix(long left, long right) {
        return left ^ right;
    }

    static long prune(long value) {
        return value % 16777216;
    }
}
