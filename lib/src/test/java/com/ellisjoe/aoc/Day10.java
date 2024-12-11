package com.ellisjoe.aoc;

import com.google.common.collect.ImmutableSet;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

public class Day10 {
    @Test
    void part1() {
        List<List<Integer>> values = Utils.streamLines(10, false)
                .map(s -> s.chars().mapToObj(c -> c - '0').toList())
                .toList();
        Map map = new Map(values);

        long total = 0;
        for (Point point : map.allPoints()) {
            total += findTrails(0, point, map).size();
        }
        System.out.println(total);
    }

    @Test
    void part2() {
        List<List<Integer>> values = Utils.streamLines(10, false)
                .map(s -> s.chars().mapToObj(c -> c - '0').toList())
                .toList();
        Map map = new Map(values);

        long total = 0;
        for (Point point : map.allPoints()) {
            total += findTrails2(0, point, map);
        }
        System.out.println(total);
    }

    static Set<Point> findTrails(int nextValue, Point point, Map map) {
        if (!map.inBounds(point)) {
            return Set.of();
        }

        if (map.get(point) == nextValue) {
            if (nextValue == 9) {
                return Set.of(point);
            } else {
                Set<Point> up = findTrails(nextValue + 1, point.move(Direction.UP), map);
                Set<Point> down = findTrails(nextValue + 1, point.move(Direction.DOWN), map);
                Set<Point> left = findTrails(nextValue + 1, point.move(Direction.LEFT), map);
                Set<Point> right = findTrails(nextValue + 1, point.move(Direction.RIGHT), map);
                return ImmutableSet.<Point>builder()
                        .addAll(up)
                        .addAll(down)
                        .addAll(left)
                        .addAll(right)
                        .build();
            }
        } else {
            return Set.of();
        }
    }

    static long findTrails2(int nextValue, Point point, Map map) {
        if (!map.inBounds(point)) {
            return 0;
        }

        if (map.get(point) == nextValue) {
            if (nextValue == 9) {
                return 1;
            } else {
                return findTrails2(nextValue + 1, point.move(Direction.UP), map)
                        + findTrails2(nextValue + 1, point.move(Direction.DOWN), map)
                        + findTrails2(nextValue + 1, point.move(Direction.LEFT), map)
                        + findTrails2(nextValue + 1, point.move(Direction.RIGHT), map);
            }
        } else {
            return 0;
        }
    }

    record Map(List<List<Integer>> map) {
        Integer get(Point point) {
            return map.get(point.y).get(point.x);
        }

        List<Point> allPoints() {
            List<Point> points = new ArrayList<>();
            for (int y = 0; y < map.size(); y++) {
                List<Integer> row = map.get(y);
                for (int x = 0; x < row.size(); x++) {
                    points.add(new Point(x, y));
                }
            }
            return points;
        }

        boolean inBounds(Point point) {
            return 0 <= point.x
                    && 0 <= point.y
                    && point.x < map.getFirst().size()
                    && point.y < map.size();
        }
    }

    record Point(int x, int y) {
        Point move(Direction direction) {
            return new Point(x + direction.x, y + direction.y);
        }
    }

    record Direction(int x, int y) {
        static final Direction UP = new Direction(0, -1);
        static final Direction DOWN = new Direction(0, 1);
        static final Direction LEFT = new Direction(-1, 0);
        static final Direction RIGHT = new Direction(1, 0);
    }
}
