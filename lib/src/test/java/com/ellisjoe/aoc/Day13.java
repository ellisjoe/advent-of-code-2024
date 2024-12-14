package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Point;
import com.ellisjoe.aoc.utils.Vector;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkState;

public class Day13 {

    private static final long TEN_T = 10000000000000L;

    @Test
    void part1() {
        Iterator<String> iter = Utils.readLines(13, false).iterator();
        List<Machine> machines = parseMachines(iter);

        long total = machines.stream()
                .map(Day13::cheapestRoute)
                .flatMap(Optional::stream)
                .mapToLong(Moves::cost)
                .sum();
        System.out.println(total);
    }

    @Test
    void part2() {
        Iterator<String> iter = Utils.readLines(13, false).iterator();
        List<Machine> machines = parseMachines(iter);

        long total = machines.stream()
                .map(Day13::cheapestRoute2)
                .flatMap(Optional::stream)
                .mapToLong(Moves::cost)
                .sum();
        System.out.println(total);
    }

    private static Optional<Moves> cheapestRoute2(Machine machine) {
        Vector buttonA = machine.buttonA();
        Vector buttonB = machine.buttonB();
        Point prize = machine.prize();
        Point actualPrize = prize.add(TEN_T);

        long ax = buttonA.x();
        long ay = buttonA.y();
        long bx = buttonB.x();
        long by = buttonB.y();
        long px = prize.x();
        long py = prize.y();

        long num = ay * px + ay * TEN_T - ax * py - ax * TEN_T;
        long den = ay * bx - ax * by;
        long b = num / den;
        long a = (py + TEN_T - by * b) / ay;

        if (Point.origin().move(buttonA.multiply(a).add(buttonB.multiply(b))).equals(actualPrize)) {
            return Optional.of(new Moves(a, b));
        } else {
            return Optional.empty();
        }
    }

    private static Optional<Moves> cheapestRoute(Machine machine) {
        Vector buttonA = machine.buttonA();
        Vector buttonB = machine.buttonB();
        Point prize = machine.prize();
        for (int b = 100; b >= 0; b--) {
            for (int a = 0; a <= 100; a++) {
                Vector vec = buttonA.multiply(a).add(buttonB.multiply(b));
                Point point = Point.origin().move(vec);
                if (point.equals(prize)) {
                    return Optional.of(new Moves(a, b));
                }
            }
        }
        return Optional.empty();
    }

    private static List<Machine> parseMachines(Iterator<String> iter) {
        List<Machine> machines = new ArrayList<>();
        while (iter.hasNext()) {
            Vector buttonA = parseButton(iter.next());
            Vector buttonB = parseButton(iter.next());
            Point prize = parsePrize(iter.next());

            if (iter.hasNext()) {
                // throw away the newline
                iter.next();
            }

            machines.add(new Machine(buttonA, buttonB, prize));
        }
        return machines;
    }

    record Moves(long a, long b) {
        public long cost() {
            return a * 3L + b;
        }
    }

    record Machine(Vector buttonA, Vector buttonB, Point prize) {
    }

    private static Vector parseButton(String line) {
        Matcher matcher = Pattern.compile("Button ([A-Z]): X\\+(\\d+), Y\\+(\\d+)").matcher(line);
        checkState(matcher.matches());
        int x = Integer.parseInt(matcher.group(2));
        int y = Integer.parseInt(matcher.group(3));
        return new Vector(x, y);
    }

    private static Point parsePrize(String line) {
        Matcher matcher = Pattern.compile("Prize: X=(\\d+), Y=(\\d+)").matcher(line);
        checkState(matcher.matches());
        long x = Long.parseLong(matcher.group(1));
        long y = Long.parseLong(matcher.group(2));
        return new Point(x, y);
    }
}
