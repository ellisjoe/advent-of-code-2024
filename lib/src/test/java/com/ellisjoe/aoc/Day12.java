package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Day12 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(12, false);
        List<Plot> garden = parse(lines);

        Map<Character, List<Point>> parsed = garden.stream()
                .collect(Collectors.groupingBy(Plot::crop, Collectors.mapping(Plot::point, Collectors.toList())));

        long totalPrice = 0;
        for (var entry : parsed.entrySet()) {
            List<Region> regions = regions(entry.getKey(), entry.getValue());
            for (Region region : regions) {
                int perimeter = perimeter(region.points());
                totalPrice += perimeter * region.points().size();
            }
        }
        System.out.println(totalPrice);
    }

    @Test
    void part2() {
        List<String> lines = Utils.readLines(12, false);
        List<Plot> garden = parse(lines);

        Map<Character, List<Point>> parsed = garden.stream()
                .collect(Collectors.groupingBy(Plot::crop, Collectors.mapping(Plot::point, Collectors.toList())));

        long totalPrice = 0;
        for (var entry : parsed.entrySet()) {
            List<Region> regions = regions(entry.getKey(), entry.getValue());
            for (Region region : regions) {
                int numSides = numSides(region.points());
                int price = numSides * region.points().size();
                totalPrice += price;
            }
        }
        System.out.println(totalPrice);
    }
    private static int numSides(Set<Point> points) {
        int corners = 0;
        for (Point point : points) {
            corners += corners(point, points, Direction.UP, Direction.RIGHT, Direction.UP_RIGHT);
            corners += corners(point, points, Direction.UP, Direction.LEFT, Direction.UP_LEFT);
            corners += corners(point, points, Direction.DOWN, Direction.RIGHT, Direction.DOWN_RIGHT);
            corners += corners(point, points, Direction.DOWN, Direction.LEFT, Direction.DOWN_LEFT);
        }
        return corners;
    }

    private static int corners(Point point, Set<Point> points, Direction sideA, Direction sideB, Direction diag) {
        return corners(
                points.contains(point.move(sideA)),
                points.contains(point.move(sideB)),
                points.contains(point.move(diag)));
    }

    private static int corners(boolean sideA, boolean sideB, boolean diag) {
        if (sideA && sideB && !diag) {
            return 1;
        } else if (!sideA && !sideB) {
            return 1;
        } else {
            return 0;
        }
    }
    private static List<Region> regions(char crop, List<Point> points) {
        Set<Point> toVisit = new HashSet<>(points);
        List<Set<Point>> regions = new ArrayList<>();
        while (!toVisit.isEmpty()) {
            Point point = toVisit.stream().findFirst().orElseThrow();
            toVisit.remove(point);

            Set<Point> region = region(point, toVisit);
            regions.add(region);
            toVisit.removeAll(region);
        }
        return regions.stream().map(r -> new Region(crop, r)).toList();
    }

    private static Set<Point> region(Point point, Set<Point> points) {
        Set<Point> toVisit = new HashSet<>(Collections.singleton(point));
        Set<Point> remaining = new HashSet<>(points);
        Set<Point> region = new HashSet<>();

        while (!toVisit.isEmpty()) {
            Point curr = toVisit.stream().findFirst().orElseThrow();
            region.add(curr);
            toVisit.remove(curr);

            for (Direction dir : Direction.cardinalDirections()) {
                Point next = curr.move(dir);
                if (remaining.contains(next)) {
                    remaining.remove(next);
                    toVisit.add(next);
                }
            }
        }
        return region;
    }

    private static int perimeter(Set<Point> points) {
        Set<Point> seen = new HashSet<>();
        int perimeter = 0;
        for (Point point : points) {
            for (Direction dir : Direction.cardinalDirections()) {
                if (!seen.contains(point.move(dir))) {
                    perimeter++;
                } else {
                    perimeter--;
                }
            }
            seen.add(point);
        }
        return perimeter;
    }

    private static List<Plot> parse(List<String> lines) {
        List<Plot> garden = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                garden.add(new Plot(row.charAt(x), new Point(x, y)));
            }
        }
        return garden;
    }

    record Region(char crop, Set<Point> points) {
    }

    record Plot(char crop, Point point) {
    }
}
