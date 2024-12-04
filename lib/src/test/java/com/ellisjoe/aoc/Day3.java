package com.ellisjoe.aoc;

import com.google.common.base.Joiner;
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

    @Test
    void part2() {
        String fullProgram = Joiner.on("").join(Utils.streamLines(3).toList());
        long result = parse2(fullProgram).stream().mapToLong(Mul::result).sum();
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

    private List<Mul> parse2(String input) {
        Pattern pattern = Pattern.compile("mul\\(([0-9]{1,3}),([0-9]{1,3})\\)|do\\(\\)|don't\\(\\)");
        Matcher matcher = pattern.matcher(input);

        boolean enabled = true;
        List<Mul> result = new ArrayList<>();

        while (matcher.find()) {
            String command = matcher.group();
            if (command.equals("do()")) {
                enabled = true;
            } else if (command.equals("don't()")) {
                enabled = false;
            } else if (enabled) {
                String x = matcher.group(1);
                String y = matcher.group(2);
                result.add(Mul.of(x, y));
            }
        }
        return result;
    }
}
