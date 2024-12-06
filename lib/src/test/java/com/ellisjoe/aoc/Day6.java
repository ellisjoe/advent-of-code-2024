package com.ellisjoe.aoc;

import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day6 {
    @Test
    void part1() {
        List<String> input = Utils.readLines(6, false);

        Set<Point> obstacles = new HashSet<>();
        Guard guard = null;
        Bounds bounds = new Bounds(new Point(0, 0), new Point(input.get(0).length() - 1, input.size() - 1));

        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c == '#') {
                    obstacles.add(new Point(x, y));
                } else if (c == '^') {
                    guard = new Guard(new Point(x, y), Direction.UP);
                }
            }
        }

        Set<Point> seenPoints = new HashSet<>();
        while (bounds.in(guard.location)) {
            Point nextPos = guard.next();
            if (obstacles.contains(nextPos)) {
                guard = guard.rotate();
            } else {
                seenPoints.add(guard.location);
                guard = guard.move();
            }
        }

        System.out.println(seenPoints.size());
        System.out.println(guard);
    }

    @Test
    void part2() {
        List<String> input = Utils.readLines(6, false);

        Set<Point> obstacles = new HashSet<>();
        Guard guard = null;
        Bounds bounds = new Bounds(new Point(0, 0), new Point(input.get(0).length() - 1, input.size() - 1));

        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c == '#') {
                    obstacles.add(new Point(x, y));
                } else if (c == '^') {
                    guard = new Guard(new Point(x, y), Direction.UP);
                }
            }
        }

        Set<Point> loopObstacles = new HashSet<>();
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                Point testObstacle = new Point(x, y);
                if (!obstacles.contains(testObstacle) && !testObstacle.equals(guard.location)) {
                    HashSet<Point> testObstacles = new HashSet<>(obstacles);
                    testObstacles.add(testObstacle);
                    if (hasLoop(bounds, guard, testObstacles)) {
                        loopObstacles.add(testObstacle);
                    }
                }
            }
        }
        System.out.println(loopObstacles.size());
    }

    private static boolean hasLoop(Bounds bounds, Guard guard, Set<Point> obstacles) {
        Set<SeenPoint> seenPoints = new HashSet<>();
        while (bounds.in(guard.location) && !seenPoints.contains(SeenPoint.fromGuard(guard))) {
            Point nextPos = guard.next();
            if (obstacles.contains(nextPos)) {
                guard = guard.rotate();
            } else {
                seenPoints.add(SeenPoint.fromGuard(guard));
                guard = guard.move();
            }
        }
        return bounds.in(guard.location);
    }

    private record SeenPoint(Point location, Direction direction) {
        static SeenPoint fromGuard(Guard guard) {
            return new SeenPoint(guard.location, guard.direction);
        }
    }

    record Point(int x, int y) {
        Point move(Direction direction) {
            return switch (direction) {
                case UP -> new Point(x, y - 1);
                case DOWN -> new Point(x, y + 1);
                case LEFT -> new Point(x - 1, y);
                case RIGHT -> new Point(x + 1, y);
            };
        }
    }

    record Bounds(Point upperLeft, Point bottomRight) {
        boolean in(Point point) {
            return upperLeft.x <= point.x
                    && upperLeft.y <= point.y
                    && point.x <= bottomRight.x
                    && point.y <= bottomRight.y;
        }
    }

    record Guard(Point location, Direction direction) {
        Point next() {
            return location.move(direction);
        }

        Guard rotate() {
            return new Guard(location, direction.rotate());
        }

        Guard move() {
            return new Guard(location.move(direction), direction);
        }
    }

    enum Direction {
        UP, DOWN, LEFT, RIGHT;

        Direction rotate() {
            return switch (this) {
                case UP -> RIGHT;
                case DOWN -> LEFT;
                case LEFT -> UP;
                case RIGHT -> DOWN;
            };
        }
    }
}
