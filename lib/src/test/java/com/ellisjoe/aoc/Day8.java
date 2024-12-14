package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Bounds;
import com.ellisjoe.aoc.utils.Point;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Day8 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(8, false);
        int yMax = lines.size();
        int xMax = lines.getFirst().length();
        Bounds bounds = new Bounds(new Point(0, 0), new Point(xMax, yMax));

        List<Antenna> antennas = parseAntennas(lines);
        Collection<List<Antenna>> antennaGroups = antennas.stream().collect(Collectors.groupingBy(a -> a.frequency)).values();

        Set<Point> antinode = new HashSet<>();
        for (List<Antenna> group : antennaGroups) {
            for (int i = 0; i < group.size() - 1; i++) {
                Antenna left = group.get(i);
                for (int j = i + 1; j < group.size(); j++) {
                    Antenna right = group.get(j);
                    Point distanceVec = left.point().subtract(right.point());
                    Point first = right.point().subtract(distanceVec);
                    Point second = left.point().subtract(distanceVec.inverse());
                    antinode.add(first);
                    antinode.add(second);
                }
            }
        }
        List<Point> list = antinode.stream()
                .filter(bounds::inBounds)
                .toList();
        System.out.println(list.size());
    }

    @Test
    void part2() {
        List<String> lines = Utils.readLines(8, false);
        int yMax = lines.size();
        int xMax = lines.getFirst().length();
        Bounds bounds = new Bounds(new Point(0, 0), new Point(xMax, yMax));

        List<Antenna> antennas = parseAntennas(lines);
        Collection<List<Antenna>> antennaGroups = antennas.stream().collect(Collectors.groupingBy(a -> a.frequency)).values();

        Set<Point> antinode = new HashSet<>();
        for (List<Antenna> group : antennaGroups) {
            for (int i = 0; i < group.size() - 1; i++) {
                for (int j = i + 1; j < group.size(); j++) {
                    Point left = group.get(i).point();
                    Point right = group.get(j).point();
                    Point distanceVec = left.subtract(right);

                    while (bounds.inBounds(right)) {
                        antinode.add(right);
                        right = right.subtract(distanceVec);
                    }

                    while (bounds.inBounds(left)) {
                        antinode.add(left);
                        left = left.subtract(distanceVec.inverse());
                    }
                }
            }
        }
        List<Point> list = antinode.stream()
                .filter(bounds::inBounds)
                .toList();
        printMap(antennas, bounds, list);
        System.out.println(list.size());
    }

    private static List<Antenna> parseAntennas(List<String> lines) {
        List<Antenna> antennas = new ArrayList<>();
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                char c = row.charAt(x);
                if (c != '.') {
                    antennas.add(new Antenna(c, new Point(x, y)));
                }
            }
        }
        return antennas;
    }

    private static void printMap(List<Antenna> antennas, Bounds bounds, List<Point> list) {
        Map<Point, Character> antennaMap = antennas.stream().collect(Collectors.toMap(a -> a.point, a -> a.frequency));
        for (int y = 0; y < bounds.bottomRight().y(); y++) {
            for (int x = 0; x < bounds.bottomRight().x(); x++) {
                Point point = new Point(x, y);
                if (antennaMap.containsKey(point)) {
                    System.out.print(antennaMap.get(point));
                } else if (list.contains(point)) {
                    System.out.print('#');
                } else {
                    System.out.print('.');
                }
            }
            System.out.println();
        }
    }

    record Antenna(char frequency, Point point) {
    }
}
