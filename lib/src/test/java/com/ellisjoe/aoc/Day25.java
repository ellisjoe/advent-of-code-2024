package com.ellisjoe.aoc;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day25 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(25, false);
        List<List<String>> splits = split(lines);

        List<Input> parsed = splits.stream().map(Input::parse).toList();

        List<Lock> locks = parsed.stream().flatMap(Input::lock).toList();
        List<Key> keys = parsed.stream().flatMap(Input::key).toList();

        long count = keys.stream().flatMap(k -> locks.stream().filter(k::fits)).count();
        System.out.println(count);
    }

    private List<List<String>> split(List<String> lines) {
        List<List<String>> splits = new ArrayList<>();
        List<String> buffer = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                splits.add(buffer);
                buffer = new ArrayList<>();
            } else {
                buffer.add(line);
            }
        }
        splits.add(buffer);
        return splits;
    }

    sealed interface Input permits Key, Lock {
        static Input parse(List<String> lines) {
            if (lines.get(0).equals("#####")) {
                return Lock.parse(lines);
            } else {
                return Key.parse(lines);
            }
        }

        default Stream<Key> key() {
            if (this instanceof Key k) {
                return Stream.of(k);
            } else {
                return Stream.empty();
            }
        }

        default Stream<Lock> lock() {
            if (this instanceof Lock l) {
                return Stream.of(l);
            } else {
                return Stream.empty();
            }
        }
    }

    record Key(List<Integer> heights) implements Input {
        boolean fits(Lock lock) {
            for (int i = 0; i < heights.size(); i++) {
                if (heights.get(i) + lock.heights().get(i) > 5) {
                    return false;
                }
            }
            return true;
        }

        static Key parse(List<String> lines) {
            List<String> reverse = Lists.reverse(lines);
            return new Key(parseHeights(reverse));
        }
    }
    record Lock(List<Integer> heights) implements Input {
        static Lock parse(List<String> lines) {
            return new Lock(parseHeights(lines));
        }
    }

    private static List<Integer> parseHeights(List<String> lines) {
        List<Integer> heights = Lists.newArrayList(0, 0, 0, 0, 0);
        for (int height = 0; height < lines.size(); height++) {
            String row = lines.get(height);
            for (int i = 0; i < row.length(); i++) {
                if (row.charAt(i) == '#') {
                    heights.set(i, height);
                }
            }
        }
        return heights;
    }
}
