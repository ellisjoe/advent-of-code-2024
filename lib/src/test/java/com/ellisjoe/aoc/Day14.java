package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Bounds;
import com.ellisjoe.aoc.utils.Point;
import com.ellisjoe.aoc.utils.Vector;
import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day14 {
    @Test
    void part1_test() {
        List<Robot> robots = Utils.streamLines(14, true)
                .map(Robot::parse)
                .toList();

        Bounds testBounds = new Bounds(Point.origin(), new Point(11, 7));

        runPart1(robots, testBounds);
    }

    @Test
    void part1() {
        List<Robot> robots = Utils.streamLines(14, false)
                .map(Robot::parse)
                .toList();

        Bounds bounds = new Bounds(Point.origin(), new Point(101, 103));
        runPart1(robots, bounds);
    }

    @Test
    void part2() {
        List<Robot> robots = Utils.streamLines(14, false)
                .map(Robot::parse)
                .toList();

        Bounds bounds = new Bounds(Point.origin(), new Point(101, 103));

        for (int i = 0; i < 100_000; i++) {
            robots = robots.stream().map(r -> r.move(bounds)).toList();
            if (treeTest(robots, bounds)) {
                System.out.println("Seconds: " + (i + 1));
                print(robots, bounds);
                break;
            }
        }
    }

    private boolean treeTest(List<Robot> robots, Bounds bounds) {
        Set<Point> points = robots.stream().map(Robot::pos).collect(Collectors.toSet());
        int longestRow = 0;

        for (long y = bounds.topLeft().y(); y < bounds.bottomRight().y(); y++) {
            int currentRow = 0;
            for (long x = bounds.topLeft().x(); x < bounds.bottomRight().x(); x++) {
                if (points.contains(new Point(x, y))) {
                    currentRow++;
                } else {
                    if (currentRow > longestRow) {
                        longestRow = currentRow;
                    }
                    currentRow = 0;
                }
            }
        }
        return longestRow > 8;
    }

    private void runPart1(List<Robot> robots, Bounds bounds) {
        for (int i = 0; i < 100; i++) {
            robots = robots.stream().map(r -> r.move(bounds)).toList();
        }

        long result = 1;
        for (Bounds quadrant : quadrants(bounds)) {
            result *= robots.stream().map(Robot::pos).filter(quadrant::inBounds).count();
        }

        print(robots, bounds);
        System.out.println(result);
    }

    private void print(List<Robot> robots, Bounds bounds) {
        Map<Point, List<Point>> points = robots.stream().map(Robot::pos).collect(Collectors.groupingBy(x -> x));
        for (long y = bounds.topLeft().y(); y < bounds.bottomRight().y(); y++) {
            for (long x = bounds.topLeft().x(); x < bounds.bottomRight().x(); x++) {
                List<Point> value = points.get(new Point(x, y));
                if (value != null) {
                    System.out.print(value.size());
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    private List<Bounds> quadrants(Bounds bounds) {
        long xMin = bounds.topLeft().x();
        long yMin = bounds.topLeft().y();
        long xMax = bounds.bottomRight().x();
        long yMax = bounds.bottomRight().y();

        long quadrantX = (xMax - xMin) / 2;
        long quadrantY = (yMax - yMin) / 2;

        Bounds topLeft = new Bounds(new Point(xMin, yMin), new Point(xMin + quadrantX, yMin + quadrantY));
        Bounds bottomLeft = new Bounds(new Point(xMin, yMin + quadrantY + 1), new Point(xMin + quadrantX, yMin + 2 * quadrantY + 1));
        Bounds topRight = new Bounds(new Point(xMin + quadrantX + 1, yMin), new Point(xMin + 2 * quadrantX + 1, yMin + quadrantY));
        Bounds bottomRight = new Bounds(new Point(xMin + quadrantX + 1, yMin + quadrantY + 1), new Point(xMax, yMax));

        return List.of(topLeft, bottomLeft, topRight, bottomRight);
    }

    record Robot(Point pos, Vector velocity) {
        static Robot parse(String line) {
            List<String> parts = Splitter.on(" ").splitToList(line);

            List<String> pvalue = Splitter.on("=").splitToList(parts.get(0));
            List<Long> pvalues = Splitter.on(",").splitToStream(pvalue.get(1)).map(Long::parseLong).toList();
            Point pos = new Point(pvalues.get(0), pvalues.get(1));

            List<String> vvalue = Splitter.on("=").splitToList(parts.get(1));
            List<Long> vvalues = Splitter.on(",").splitToStream(vvalue.get(1)).map(Long::parseLong).toList();
            Vector velocity = new Vector(vvalues.get(0), vvalues.get(1));

            return new Robot(pos, velocity);
        }

        Robot move(Bounds bounds) {
            return new Robot(bounds.wrap(pos.add(velocity)), velocity);
        }
    }
}
