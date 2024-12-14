package com.ellisjoe.aoc.utils;

public record Vector(long x, long y) {
    public Vector multiply(long mul) {
        return new Vector(x * mul, y * mul);
    }

    public Vector add(Vector vector) {
        return new Vector(x + vector.x, y + vector.y);
    }
}
