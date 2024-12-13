package com.ellisjoe.aoc.utils;

import java.util.List;

public enum Direction {
    UP, DOWN, LEFT, RIGHT, UP_RIGHT, UP_LEFT, DOWN_RIGHT, DOWN_LEFT;

    public Direction rotate() {
        return switch (this) {
            case UP -> RIGHT;
            case DOWN -> LEFT;
            case LEFT -> UP;
            case RIGHT -> DOWN;
            default -> throw new IllegalStateException("Unexpected value: " + this);
        };
    }

    public static List<Direction> cardinalDirections() {
        return List.of(UP, DOWN, LEFT, RIGHT);
    }
}
