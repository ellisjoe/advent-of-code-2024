package com.ellisjoe.aoc.utils;

public record Point(long x, long y) {
    public static Point origin() {
        return new Point(0, 0);
    }

    public Point move(Direction direction) {
        return switch (direction) {
            case UP -> new Point(x, y - 1);
            case DOWN -> new Point(x, y + 1);
            case LEFT -> new Point(x - 1, y);
            case RIGHT -> new Point(x + 1, y);
            case UP_RIGHT -> new Point(x + 1, y - 1);
            case UP_LEFT -> new Point(x - 1, y - 1);
            case DOWN_RIGHT -> new Point(x + 1, y + 1);
            case DOWN_LEFT -> new Point(x - 1, y + 1);
        };
    }

    public Point add(Vector vector) {
        return new Point(x + vector.x(), y + vector.y());
    }

    public Point inverse() {
        return new Point(-x, -y);
    }

    public Point subtract(Point point) {
        return new Point(x - point.x(), y - point.y());
    }

    public Point subtract(long value) {
        return new Point(x - value, y - value);
    }

    public Point add(long value) {
        return new Point(x + value, y + value);
    }

    public long manhattanDistance(Point point) {
        return Math.abs(x - point.x()) + Math.abs(y - point.y());
    }
}
