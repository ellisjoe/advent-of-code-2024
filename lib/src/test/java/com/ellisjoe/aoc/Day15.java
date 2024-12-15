package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import org.junit.jupiter.api.Test;

import java.util.*;

public class Day15 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(15, false);

        Input input = parseInput(lines);

        List<Direction> directions = input.directions();
        Map map = input.map();

        for (Direction dir : directions) {
            map = stepMap(dir, map);
        }

        long result = map.boxes()
                .stream()
                .mapToLong(p -> 100 * p.y() + p.x())
                .sum();
        System.out.println(result);
    }

    private static Map stepMap(Direction dir, Map map) {
        Point robot = map.robot();
        Set<Point> boxes = map.boxes();
        Set<Point> walls = map.walls();

        Point newLoc = robot.move(dir);
        List<Point> boxesToMove = new ArrayList<>();
        while (boxes.contains(newLoc)) {
            boxesToMove.add(newLoc);
            newLoc = newLoc.move(dir);
        }
        if (!walls.contains(newLoc)) {
            robot = robot.move(dir);
            boxes.removeAll(boxesToMove);
            boxes.addAll(boxesToMove.stream().map(b -> b.move(dir)).toList());
        }
        return new Map(walls, boxes, robot);
    }

    private static void print(Map map) {
        long xMax = map.walls().stream().mapToLong(Point::x).max().orElseThrow();
        long yMax = map.walls().stream().mapToLong(Point::y).max().orElseThrow();

        for (int y = 0; y <= yMax; y++) {
            for (int x = 0; x <= xMax; x++) {
                char c = '.';
                Point p = new Point(x, y);
                if (map.walls().contains(p)) {
                    c = '#';
                } else if (map.boxes().contains(p)) {
                    c = 'O';
                } else if (map.robot().equals(p)) {
                    c = '@';
                }
                System.out.print(c);
            }
            System.out.println();
        }
    }

    private static Input parseInput(List<String> lines) {
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

        Set<Point> boxes = new HashSet<>();
        Set<Point> walls = new HashSet<>();
        Point robot = null;
        for (int y = 0; y < mapLines.size(); y++) {
            String row = mapLines.get(y);
            for (int x = 0; x < row.length(); x++) {
                switch (row.charAt(x)) {
                    case '.' -> {}
                    case '#' -> walls.add(new Point(x, y));
                    case 'O' -> boxes.add(new Point(x, y));
                    case '@' -> robot = new Point(x, y);
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


    record Map(Set<Point> walls, Set<Point> boxes, Point robot) {}

    record Input(Map map, List<Direction> directions) {}
}
