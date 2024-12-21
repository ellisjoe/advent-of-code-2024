package com.ellisjoe.aoc;

import com.ellisjoe.aoc.utils.Direction;
import com.ellisjoe.aoc.utils.Point;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.stream.Collectors;

public class Day16 {
    @Test
    void part1() {
        List<String> lines = Utils.readLines(16, false);

        Maze maze = Maze.parse(lines);

        Map<Step, Result> cost = evaluateCost(maze);
        OptionalLong result = cost.entrySet()
                .stream()
                .filter(e -> e.getKey().point().equals(maze.end()))
                .map(Map.Entry::getValue)
                .mapToLong(Result::cost)
                .min();

        System.out.println(result.getAsLong());
    }

    @Test
    void part2() {
        List<String> lines = Utils.readLines(16, false);

        Maze maze = Maze.parse(lines);

        Map<Step, Result> cost = evaluateCost(maze);
        Step min = cost.entrySet()
                .stream()
                .filter(e -> e.getKey().point().equals(maze.end()))
                .min(Comparator.comparingLong(e -> e.getValue().cost()))
                .map(Map.Entry::getKey)
                .orElseThrow();

        Set<Step> bestSteps = new HashSet<>();
        List<Step> nextSteps = new ArrayList<>();
        nextSteps.add(min);
        while (!nextSteps.isEmpty()) {
            Step step = nextSteps.removeFirst();
            bestSteps.add(step);

            Result result = cost.get(step);
            nextSteps.addAll(result.previousSteps());
        }
        Set<Point> bestPoints = bestSteps.stream().map(Step::point).collect(Collectors.toSet());
        System.out.println(bestPoints.size());
        print(lines, bestPoints);
    }

    static void print(List<String> lines, Set<Point> steps) {
        for (int y = 0; y < lines.size(); y++) {
            String row = lines.get(y);
            for (int x = 0; x < row.length(); x++) {
                Point point = new Point(x, y);
                if (steps.contains(point)) {
                    System.out.print('O');
                } else {
                    System.out.print(row.charAt(x));
                }
            }
            System.out.println();
        }
    }

    static Map<Step, Result> evaluateCost(Maze maze) {

        Map<Step, Result> stepCost = new HashMap<>();
        List<Step> stepsToCheck = new LinkedList<>();

        Step firstStep = new Step(maze.start, Direction.RIGHT);
        stepCost.put(firstStep, new Result(0L, List.of()));
        stepsToCheck.add(firstStep);

        while (!stepsToCheck.isEmpty()) {
            Step step = stepsToCheck.removeFirst();
            Result result = stepCost.get(step);
            long cost = result.cost();

            for (Direction dir : Direction.cardinalDirections()) {
                Step nextStep = new Step(step.point().move(dir), dir);

                if (!maze.valid().contains(nextStep.point())) {
                    continue;
                }

                long nextStepCost;
                if (dir.equals(step.direction())) {
                    nextStepCost = cost + 1;
                } else {
                    nextStepCost = cost + 1001;
                }
                Result currentResult = stepCost.getOrDefault(nextStep, new Result(Long.MAX_VALUE, List.of()));
                if (nextStepCost < currentResult.cost()) {
                    stepCost.put(nextStep, new Result(nextStepCost, List.of(step)));
                    stepsToCheck.add(nextStep);
                } else if (nextStepCost == currentResult.cost()) {
                    ArrayList<Step> steps = new ArrayList<>(currentResult.previousSteps());
                    steps.add(step);
                    stepCost.put(nextStep, new Result(nextStepCost, steps));
                }
            }
        }

        return stepCost;
    }

    record Step(Point point, Direction direction) {
    }

    record Result(Long cost, List<Step> previousSteps) {

        public static Result min(Result left, Result right) {
            return left.cost() < right.cost() ? left : right;
        }
    }

    record Maze(Point start, Point end, Set<Point> valid) {
        static Maze parse(List<String> lines) {
            Point start = null;
            Point end = null;
            Set<Point> valid = new HashSet<>();

            for (int y = 0; y < lines.size(); y++) {
                String row = lines.get(y);
                for (int x = 0; x < row.length(); x++) {
                    switch (row.charAt(x)) {
                        case '.' -> valid.add(new Point(x, y));
                        case 'S' -> start = new Point(x, y);
                        case 'E' -> end = new Point(x, y);
                    }
                }
            }

            valid.add(start);
            valid.add(end);

            return new Maze(start, end, valid);
        }
    }
}
