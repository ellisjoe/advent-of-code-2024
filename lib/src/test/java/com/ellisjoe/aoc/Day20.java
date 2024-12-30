package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Bounds;
import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day20 {
    @Test
    void part1() {
        List<String> in = Utils.readLines(20, false);
        Maze maze = Maze.parse(in);
//        long distanceFilter = 1;
        long distanceFilter = 100;

        Long base = maze.findRoute();
        List<Long> cheatingRoutes = maze.walls().stream()
                .map(w -> maze.withoutWall(w).findRoute())
                .filter(d -> base - d >= distanceFilter)
                .toList();

        System.out.println(cheatingRoutes.size());
    }

    @Test
    void part2() {
        List<String> in = Utils.readLines(20, false);
        Maze maze = Maze.parse(in);
        int maxCheat = 20;
        int minSavings = 100;

        List<Point> path = maze.findPath();

        long total = 0;
        for (int i = 0; i < path.size(); i++) {
            for (int j = i + minSavings; j < path.size(); j++) {
                long curDist = j - i;
                Point start = path.get(i);
                Point end = path.get(j);
                long cheatDist = start.manhattanDistance(end);
                if (cheatDist <= maxCheat && curDist - cheatDist >= minSavings) {
                    total++;
                }
            }
        }
        System.out.println(total);
    }

    record Maze(Point start, Point end, Set<Point> roads, Set<Point> walls, Bounds bounds) {
        Maze withoutWall(Point wall) {
            HashSet<Point> newWalls = new HashSet<>(walls);
            newWalls.remove(wall);
            return new Maze(start, end, roads, newWalls, bounds);
        }

        List<Point> findPath() {
            Set<Point> visited = new HashSet<>();
            List<List<Point>> paths = new ArrayList<>();

            visited.add(start);
            paths.add(List.of(start));

            while (!paths.isEmpty()) {
                Optional<List<Point>> done = paths.stream().filter(p -> p.getLast().equals(end)).findFirst();
                if (done.isPresent()) {
                    return done.get();
                }

                paths = paths.stream()
                        .flatMap(path -> Direction.cardinalDirections().stream()
                                .flatMap(dir -> {
                                    Point current = path.getLast();
                                    Point next = current.move(dir);
                                    if (visited.contains(next)) {
                                        return Stream.of();
                                    } else if (!walls.contains(next)) {
                                        visited.add(next);
                                        List<Point> newPath = Lists.newArrayList(path);
                                        newPath.add(next);
                                        return Stream.of(newPath);
                                    } else {
                                        return Stream.of();
                                    }
                                })).toList();
            }
            throw new IllegalArgumentException();
        }

        Long findRoute() {
            Set<Point> visited = new HashSet<>();
            List<Point> nextSteps = new ArrayList<>();
            nextSteps.add(start);
            long distance = 0;

            while (!nextSteps.isEmpty()) {
                List<Point> round = Lists.newArrayList(nextSteps);
                visited.addAll(round);
                nextSteps.clear();

                distance++;

                for (Point current : round) {
                    for (Direction dir : Direction.cardinalDirections()) {
                        Point next = current.move(dir);
                        if (end.equals(next)) {
                            return distance;
                        } else if (bounds.inBounds(next) && !walls.contains(next) && !visited.contains(next)) {
                            nextSteps.add(next);
                        }
                    }
                }
            }
            throw new IllegalStateException("No route");
        }

        static Maze parse(List<String> input) {
            Point start = null;
            Point end = null;
            Set<Point> roads = new HashSet<>();
            Set<Point> walls = new HashSet<>();
            Bounds bounds = new Bounds(Point.origin(), new Point(input.getFirst().length(), input.size()));

            for (int y = 0; y < input.size(); y++) {
                String row = input.get(y);
                for (int x = 0; x < row.length(); x++) {
                    Point p = new Point(x, y);
                    switch (row.charAt(x)) {
                        case '.' -> roads.add(p);
                        case '#' -> walls.add(p);
                        case 'S' -> {
                            start = p;
                            roads.add(p);
                        }
                        case 'E' -> {
                            end = p;
                            roads.add(p);
                        }
                    }
                }
            }

            return new Maze(start, end, roads, walls, bounds);
        }
    }
}
