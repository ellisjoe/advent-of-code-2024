package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class Day2 {
    @Test
    void part1() {
        long numSafe = Utils.streamLines(2)
                .map(x -> Splitter.on(" ").splitToStream(x).map(Integer::parseInt).toList())
                .map(Day2::isSafe)
                .filter(x -> x)
                .count();
        System.out.println(numSafe);
    }

    @Test
    void part2() {
        List<List<Integer>> reports = Utils.streamLines(2)
                .map(x -> Splitter.on(" ").splitToStream(x).map(Integer::parseInt).toList())
                .toList();

        int numSafe = 0;
        for (List<Integer> report : reports) {
            if (isSafe(report)) {
                numSafe++;
            } else {
                for (int i = 0; i < report.size(); i++) {
                    ArrayList<Integer> sublist = new ArrayList<>(report);
                    sublist.remove(i);
                    if (isSafe(sublist)) {
                        numSafe++;
                        break;
                    }
                }
            }
        }
        System.out.println(numSafe);
    }

    private static boolean isSafe(List<Integer> report) {
        boolean increasing = report.get(0) < report.get(1);

        for (int i = 0; i < report.size() - 1; i++) {
            int left = report.get(i);
            int right = report.get(i + 1);
            int diff = Math.abs(left - right);
            if (left < right == increasing && 1 <= diff && diff <= 3) {
                continue;
            } else {
                return false;
            }
        }

        return true;
    }
}
