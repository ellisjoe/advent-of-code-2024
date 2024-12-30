package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Bounds;
import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Day18 {
    @Test
    void part1() {
        List<Point> points = Utils.streamLines(18, false)
                .map(Day18::parse)
                .toList();

        Point start = Point.origin();
//        Bounds bounds = new Bounds(Point.origin(), new Point(7, 7));
//        Set<Point> corrupt = points.stream().limit(12).collect(Collectors.toSet());
        Bounds bounds = new Bounds(Point.origin(), new Point(71, 71));
        Set<Point> corrupt = points.stream().limit(1024).collect(Collectors.toSet());

        Map<Point, Integer> map = shortestPaths(start, bounds, corrupt);

//        System.out.println(map.get(new Point(6, 6)));
        System.out.println(map.get(new Point(70, 70)));
    }

    @Test
    void part2() {
        List<Point> points = Utils.streamLines(18, false)
                .map(Day18::parse)
                .toList();

        Point start = Point.origin();

//        Point end = new Point(6, 6);
//        Bounds bounds = new Bounds(Point.origin(), new Point(7, 7));

        Point end = new Point(70, 70);
        Bounds bounds = new Bounds(Point.origin(), new Point(71, 71));

        for (int i = 0; i < points.size(); i++) {
            Set<Point> corrupt = points.stream().limit(i).collect(Collectors.toSet());
            Map<Point, Integer> map = shortestPaths(start, bounds, corrupt);

            if (!map.containsKey(end)) {
                System.out.println(points.get(i - 1));
                break;
            }
        }
    }

    static void print(Bounds bounds, Map<Point, Integer> map, Set<Point> corrupt) {
        for (int y = 0; y <= bounds.bottomRight().y(); y++) {
            for (int x = 0; x <= bounds.bottomRight().x(); x++) {
                Point point = new Point(x, y);
                if (map.containsKey(point)) {
                    System.out.print("0");
                } else if (corrupt.contains(point)) {
                    System.out.print("#");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    static Map<Point, Integer> shortestPaths(Point start, Bounds bounds, Set<Point> corrupt) {
        Map<Point, Integer> map = new HashMap<>();

        int steps = 0;
        map.put(start, steps);

        Set<Point> nextSteps = new HashSet<>();
        nextSteps.add(start);

        while (!nextSteps.isEmpty()) {
            steps++;
            Set<Point> stepsToCheck = new HashSet<>(nextSteps);
            nextSteps.clear();

            for (Point step : stepsToCheck) {
                for (Direction dir : Direction.cardinalDirections()) {
                    Point point = step.move(dir);
                    if (bounds.inBounds(point) && !corrupt.contains(point) && !map.containsKey(point)) {
                        map.put(point, steps);
                        nextSteps.add(point);
                    }
                }
            }
        }

        return map;
    }

    static Point parse(String in) {
        List<Integer> values = Splitter.on(",").splitToStream(in).map(Integer::parseInt).toList();
        return new Point(values.get(0), values.get(1));
    }
}
