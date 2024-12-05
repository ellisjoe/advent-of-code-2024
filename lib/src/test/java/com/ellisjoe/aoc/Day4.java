package com.ellisjoe.aoc;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

public class Day4 {
    @Test
    void part1() {
        List<String> input = Utils.streamLines(4).toList();
        Map map = new Map(input);

        int total = 0;
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                Point point = new Point(x, y);
                for (Directions dir : Directions.values()) {
                    total += find("XMAS", map, point, dir.direction) ? 1 : 0;
                }
            }
        }
        System.out.println(total);
    }

    @Test
    void part2() {
        List<String> input = Utils.streamLines(4).toList();
        Map map = new Map(input);

        int total = 0;
        for (int y = 0; y < input.size(); y++) {
            String row = input.get(y);
            for (int x = 0; x < row.length(); x++) {
                Point point = new Point(x, y);
                Optional<String> first = firstDiag(map, point);
                Optional<String> second = secondDiag(map, point);
                if (first.map(Day4::containsMas).orElse(false) && second.map(Day4::containsMas).orElse(false)) {
                    total++;
                }
            }
        }
        System.out.println(total);
    }

    static boolean containsMas(String input) {
        return input.equals("MAS") || input.equals("SAM");
    }

    static boolean find(String needle, Map map, Point point, Direction direction) {
        Point currentPoint = point;
        for (char c : needle.toCharArray()) {
            if (!map.inBounds(currentPoint) || map.get(currentPoint) != c) {
                return false;
            }
            currentPoint = currentPoint.move(direction);
        }
        return true;
    }

    static Optional<String> firstDiag(Map map, Point point) {
        Point upLeft = point.move(Directions.UP_LEFT.direction);
        Point downRight = point.move(Directions.DOWN_RIGHT.direction);
        if (map.inBounds(upLeft) && map.inBounds(point) && map.inBounds(downRight)) {
            return Optional.of(String.valueOf(map.get(upLeft)) + map.get(point) + map.get(downRight));
        } else {
            return Optional.empty();
        }
    }

    static Optional<String> secondDiag(Map map, Point point) {
        Point downLeft = point.move(Directions.DOWN_LEFT.direction);
        Point upRight = point.move(Directions.UP_RIGHT.direction);
        if (map.inBounds(downLeft) && map.inBounds(point) && map.inBounds(upRight)) {
            return Optional.of(String.valueOf(map.get(downLeft)) + map.get(point) + map.get(upRight));
        } else {
            return Optional.empty();
        }
    }

    record Map(List<String> map) {
        char get(Point point) {
            return map.get(point.y).charAt(point.x);
        }

        boolean inBounds(Point point) {
            return 0 <= point.x
                    && 0 <= point.y
                    && point.x < map.getFirst().length()
                    && point.y < map.size();
        }
    }

    record Point(int x, int y) {
        Point move(Direction direction) {
            return new Point(x + direction.x, y + direction.y);
        }
    }

    enum Directions {
        UP(new Direction(0, -1)),
        DOWN(new Direction(0, 1)),
        LEFT(new Direction(-1, 0)),
        RIGHT(new Direction(1, 0)),
        UP_RIGHT(new Direction(1, -1)),
        UP_LEFT(new Direction(-1, -1)),
        DOWN_RIGHT(new Direction(1, 1)),
        DOWN_LEFT(new Direction(-1, 1));

        private final Direction direction;

        Directions(Direction direction) {
            this.direction = direction;
        }
    }

    record Direction(int x, int y) {
    }
}
