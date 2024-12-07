package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.function.BiFunction;

public class Day7 {
    @Test
    void part1() {
        long result = Utils.streamLines(7)
                .map(Equation::parse)
                .filter(Equation::solvable)
                .mapToLong(Equation::result)
                .sum();
        System.out.println(result);
    }

    @Test
    void part2() {
        long result = Utils.streamLines(7)
                .map(Equation::parse)
                .filter(Equation::solvable2)
                .mapToLong(Equation::result)
                .sum();
        System.out.println(result);
    }

    record Equation(long result, List<Long> values) {
        static Equation parse(String input) {
            List<String> parts = Splitter.on(": ").splitToList(input);
            long result = Long.parseLong(parts.get(0));
            List<Long> values = Splitter.on(" ").splitToStream(parts.get(1)).map(Long::parseLong).toList();
            return new Equation(result, values);
        }

        boolean solvable() {
            long options = (long) Math.pow(2, values.size() - 1);
            for (int round = 0; round < options; round++) {
                long total = values.get(0);
                for (int j = 1; j < values.size(); j++) {
                    BiFunction<Long, Long, Long> operator = (round >> (j - 1)) % 2 == 0
                            ? (x, y) -> x * y
                            : (x, y) -> x + y;
                    total = operator.apply(total, values.get(j));
                }
                if (total == result) {
                    return true;
                }
            }
            return false;
        }

        boolean solvable2() {
            return recurse(result, values.getFirst(), values.subList(1, values.size()));
        }

        boolean recurse(long total, long current, List<Long> values) {
            if (values.isEmpty()) {
                return current == total;
            }

            Long next = values.getFirst();
            List<Long> rest = values.subList(1, values.size());

            return recurse(total, current * next, rest)
                    || recurse(total, current + next, rest)
                    || recurse(total, concat(current, next), rest);
        }
    }

    static long concat(long left, long right) {
        return Long.parseLong(Long.toString(left) + Long.toString(right));
    }
}
