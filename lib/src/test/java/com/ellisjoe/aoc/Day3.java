package com.ellisjoe.aoc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day3 {
    @Test
    void part1() {
        long result = Utils.streamLines(3)
                .map(this::parse)
                .mapToLong(x -> x.stream().mapToLong(Mul::result).sum())
                .sum();
        System.out.println(result);
    }

    record Mul(long x, long y) {
        static Mul of(String x, String y) {
            return new Mul(Long.parseLong(x), Long.parseLong(y));
        }

        long result() {
            return x * y;
        }
    }

    private List<Mul> parse(String input) {
        Pattern pattern = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)");
        Matcher matcher = pattern.matcher(input);
        List<Mul> result = new ArrayList<>();
        while (matcher.find()) {
            String x = matcher.group(1);
            String y = matcher.group(2);
            result.add(Mul.of(x, y));
        }
        return result;
    }
}
