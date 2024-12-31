package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Point;
import com.ellisjoe.aoc.utils.Vector;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day21 {
    private static Map<Character, Point> NUM_PAD = ImmutableMap.<Character, Point>builder()
            .put('7', new Point(0, 0))
            .put('8', new Point(1, 0))
            .put('9', new Point(2, 0))
            .put('4', new Point(0, 1))
            .put('5', new Point(1, 1))
            .put('6', new Point(2, 1))
            .put('1', new Point(0, 2))
            .put('2', new Point(1, 2))
            .put('3', new Point(2, 2))
            .put('0', new Point(1, 3))
            .put('A', new Point(2, 3))
            .build();
    private static Map<Character, Point> DIR_PAD = ImmutableMap.<Character, Point>builder()
            .put('^', new Point(1, 0))
            .put('A', new Point(2, 0))
            .put('<', new Point(0, 1))
            .put('v', new Point(1, 1))
            .put('>', new Point(2, 1))
            .build();
    @Test
    void part1() {
        List<String> lines = Utils.readLines(21, false);

        long total = 0;
        for (String line : lines) {
            String sequence = run(line);
            int num = Integer.parseInt(line.substring(0, line.length() - 1));
            System.out.println(String.format("%s: %s %s", line, sequence.length(), num));
            total += sequence.length() * num;
        }
        System.out.println(total);
    }

    @Test
    void part2() {
        List<String> lines = Utils.readLines(21, false);

        long total = 0;
        for (String line : lines) {
            long lineTotal = run2(line, 25);
            int num = Integer.parseInt(line.substring(0, line.length() - 1));
            total += lineTotal * num;
        }
        System.out.println(total);
    }

    private static String run(String code) {
        String moves = shortestMoves(KeyPad.numPad(), code);
        String moves1 = shortestMoves(KeyPad.dirPad(), moves);
        String moves2 = shortestMoves(KeyPad.dirPad(), moves1);

        return moves2;
    }

    private static Long run2(String code, int dirPads) {
        String moves = shortestMoves(KeyPad.numPad(), code);

        long total = 0;
        KeyPad keyPad = KeyPad.dirPad();
        for (char c : moves.toCharArray()) {
            total += shortestMoves(keyPad, c, 0, dirPads);
            keyPad = keyPad.update(c);
        }
        return total;
    }

    record Key(int depth, char current, char next) {}
    static Map<Key, Long> cache = new HashMap<>();
    private static Long shortestMoves(KeyPad keyPad, Character code, int depth, int maxDepth) {
        if (depth == maxDepth) {
            return 1L;
        }
        String moves = keyPad.moves(code);

        long total = 0;
        KeyPad nextKeyPad = KeyPad.dirPad();
        for (char c : moves.toCharArray()) {
            Key key = new Key(depth + 1, nextKeyPad.key(), c);

            if (!cache.containsKey(key)) {
                Long value = shortestMoves(nextKeyPad, c, depth + 1, maxDepth);
                cache.put(key, value);
            }

            total += cache.get(key);
            nextKeyPad = nextKeyPad.update(c);
        }

        return total;
    }

    private static String shortestMoves(KeyPad keyPad, String code) {
        String moves = "";
        for (char c : code.toCharArray()) {
            moves += keyPad.moves(c);
            keyPad = keyPad.update(c);
        }
        return moves;
    }

    record KeyPad(Character key, Map<Character, Point> keyMap, Point blank) {
        static KeyPad numPad() {
            return new KeyPad('A', NUM_PAD, new Point(0, 3));
        }

        static KeyPad dirPad() {
            return new KeyPad('A', DIR_PAD, new Point(0, 0));
        }

        KeyPad update(Character key) {
            return new KeyPad(key, keyMap, blank);
        }

        String moves(Character nextKey) {
            Point current = keyMap.get(key);
            Point next = keyMap.get(nextKey);
            Point moves = next.subtract(current);

            int xCount = (int) Math.abs(moves.x());
            int yCount = (int) Math.abs(moves.y());
            String horizontal = moves.x() < 0 ? "<".repeat(xCount) : ">".repeat(xCount);
            String vertical = moves.y() < 0 ? "^".repeat(yCount) : "v".repeat(yCount);

            if (current.add(new Vector(moves.x(), 0)).equals(blank)) {
                return vertical + horizontal + "A";
            } else if (current.add(new Vector(0, moves.y())).equals(blank)) {
                return horizontal + vertical + "A";
            } else if (moves.x() < 0) {
                return horizontal + vertical + "A";
            } else {
                return vertical + horizontal + "A";
            }
        }
    }
}
