package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class Day15 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(15, false);

        Input input = parseInput(lines, 1);

        List<Direction> directions = input.directions();
        Map map = input.map();

        for (Direction dir : directions) {
            map = stepMap(dir, map);
        }

        long result = map.boxes()
                .stream()
                .map(Rect::min)
                .mapToLong(p -> 100 * p.y() + p.x())
                .sum();
        System.out.println(result);
    }

    @Test
    void part2() {
        List<String> lines = Utils.readLines(15, false);

        Input input = parseInput(lines, 2);

        List<Direction> directions = input.directions();
        Map map = input.map();

        for (Direction dir : directions) {
            map = stepMap(dir, map);
        }

        long result = map.boxes()
                .stream()
                .map(Rect::min)
                .mapToLong(p -> 100 * p.y() + p.x())
                .sum();
        System.out.println(result);
    }

    private static Map stepMap(Direction dir, Map map) {
        Point robot = map.robot();
        Set<Rect> boxesToCheck = new HashSet<>(map.boxes());
        Set<Rect> walls = map.walls();

        List<Point> locationsToPush = new LinkedList<>();
        List<Rect> boxesToMove = new ArrayList<>();
        locationsToPush.add(robot.move(dir));
        while (!locationsToPush.isEmpty()) {
            Point newLoc = locationsToPush.removeFirst();
            Optional<Rect> maybeBox = boxesToCheck.stream().filter(r -> r.intersects(newLoc)).findFirst();
            if (maybeBox.isPresent()) {
                Rect box = maybeBox.get();
                locationsToPush.addAll(box.move(dir).points());
                boxesToCheck.remove(box);
                boxesToMove.add(box);
            }

            Optional<Rect> maybeWall = walls.stream().filter(r -> r.intersects(newLoc)).findFirst();
            if (maybeWall.isPresent()) {
                return map;
            }
        }

        robot = robot.move(dir);
        HashSet<Rect> boxes = new HashSet<>(map.boxes());
        boxes.removeAll(boxesToMove);
        boxes.addAll(boxesToMove.stream().map(b -> b.move(dir)).toList());
        return new Map(walls, boxes, robot);
    }

    private static void print(Map map) {
        Set<Point> wallPoints = map.walls().stream().flatMap(r -> r.points().stream()).collect(Collectors.toSet());
        Set<Point> boxPoints = map.boxes().stream().flatMap(b -> b.points().stream()).collect(Collectors.toSet());

        long xMax = wallPoints.stream().mapToLong(Point::x).max().orElseThrow();
        long yMax = wallPoints.stream().mapToLong(Point::y).max().orElseThrow();

        for (int y = 0; y <= yMax; y++) {
            for (int x = 0; x <= xMax; x++) {
                char c = '.';
                Point p = new Point(x, y);
                if (wallPoints.contains(p)) {
                    c = '#';
                } else if (boxPoints.contains(p)) {
                    c = 'O';
                } else if (map.robot().equals(p)) {
                    c = '@';
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static Input parseInput(List<String> lines, long width) {
        boolean inMap = true;
        List<String> mapLines = new ArrayList<>();
        List<String> directionLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                inMap = false;
            } else if (inMap) {
                mapLines.add(line);
            } else {
                directionLines.add(line);
            }
        }

        Set<Rect> boxes = new HashSet<>();
        Set<Rect> walls = new HashSet<>();
        Point robot = null;
        for (int y = 0; y < mapLines.size(); y++) {
            String row = mapLines.get(y);
            for (int x = 0; x < row.length(); x++) {
                switch (row.charAt(x)) {
                    case '.' -> {
                    }
                    case '#' -> walls.add(Rect.of(width, new Point(x * width, y)));
                    case 'O' -> boxes.add(Rect.of(width, new Point(x * width, y)));
                    case '@' -> robot = new Point(x * width, y);
                    default -> throw new IllegalStateException("Unexpected value: " + row.charAt(x));
                }
            }
        }

        List<Direction> directions = directionLines.stream()
                .flatMap(s -> s.chars().mapToObj(c -> switch (c) {
                    case '^' -> Direction.UP;
                    case 'v' -> Direction.DOWN;
                    case '<' -> Direction.LEFT;
                    case '>' -> Direction.RIGHT;
                    default -> throw new IllegalStateException("Unexpected value: " + c);
                })).toList();

        return new Input(new Map(walls, boxes, robot), directions);
    }


    record Map(Set<Rect> walls, Set<Rect> boxes, Point robot) {
    }

    record Input(Map map, List<Direction> directions) {
    }

    record Rect(Set<Point> points) {
        static Rect of(long width, Point point) {
            Set<Point> points = LongStream.range(0, width)
                    .mapToObj(offset -> new Point(point.x() + offset, point.y()))
                    .collect(Collectors.toSet());
            return new Rect(points);
        }

        static Rect of(Point... points) {
            return new Rect(Set.of(points));
        }

        Rect move(Direction dir) {
            return new Rect(points.stream().map(p -> p.move(dir)).collect(Collectors.toSet()));
        }

        boolean intersects(Point p) {
            return points.contains(p);
        }

        Point min() {
            return points.stream().min(Comparator.comparing(Point::x)).orElseThrow();
        }
    }
}
