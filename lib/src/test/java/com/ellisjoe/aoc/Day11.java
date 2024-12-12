package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import org.junit.jupiter.api.Test;

import java.util.List;
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
}
