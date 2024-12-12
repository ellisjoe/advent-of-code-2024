package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class Day11 {
    @Test
    void part1() {
        String input = Utils.streamLines(11, false).findFirst().orElseThrow();
        List<Long> values = Splitter.on(" ").splitToStream(input).map(Long::parseLong).toList();

        int times = 25;
        for (int i = 0; i < times; i++) {
            Stopwatch timer = Stopwatch.createStarted();
            values = values.stream().flatMap(Day11::next).toList();
            System.out.println(String.format("Round %s: %s stones [%s]", i + 1, values.size(), timer.stop()));
        }
        System.out.println(values.size());
    }

    @Test
    void part2() {
        String input = Utils.streamLines(11, false).findFirst().orElseThrow();
        List<Long> values = Splitter.on(" ").splitToStream(input).map(Long::parseLong).toList();

        long total = values.stream().mapToLong(value -> cachedStonesAtDepth(value, 0, 75)).sum();
        System.out.println(total);
    }

    private static Stream<Long> next(long value) {
        String stringValue = String.valueOf(value);
        if (value == 0) {
            return Stream.of(1L);
        } else if (stringValue.length() % 2 == 0) {
            int size = stringValue.length() / 2;
            String left = stringValue.substring(0, size);
            String right = stringValue.substring(size);
            return Stream.of(Long.parseLong(left), Long.parseLong(right));
        } else {
            return Stream.of(2024 * value);
        }
    }

    record Key(long value, int currentDepth, int depth) {}

    private Map<Key, Long> cache = new HashMap<>();

    private long cachedStonesAtDepth(long value, int currentDepth, int depth) {
        Key key = new Key(value, currentDepth, depth);
        Long stones = cache.get(key);
        if (stones == null) {
            stones = stonesAtDepth(value, currentDepth, depth);
            cache.put(key, stones);
        }
        return stones;
    }

    private long stonesAtDepth(long value, int currentDepth, int depth) {
        String stringValue = String.valueOf(value);
        if (currentDepth == depth) {
            return 1;
        } else if (value == 0) {
            return cachedStonesAtDepth(1, currentDepth + 1, depth);
        } else if (stringValue.length() % 2 == 0) {
            int size = stringValue.length() / 2;
            Long left = Long.parseLong(stringValue.substring(0, size));
            Long right = Long.parseLong(stringValue.substring(size));

            return cachedStonesAtDepth(left, currentDepth + 1, depth)
                    + cachedStonesAtDepth(right, currentDepth + 1, depth);
        } else {
            return cachedStonesAtDepth(2024 * value, currentDepth + 1, depth);
        }
    }
}
