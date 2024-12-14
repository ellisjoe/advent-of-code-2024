package com.ellisjoe.aoc.utils;

public record Bounds(Point topLeft, Point bottomRight) {
    public boolean inBounds(Point point) {
        return topLeft.x() <= point.x()
                && topLeft.y() <= point.y()
                && point.x() < bottomRight.x()
                && point.y() < bottomRight.y();
    }

    public Point wrap(Point point) {
        return new Point(wrap(point.x(), topLeft.x(), bottomRight.x()), wrap(point.y(), topLeft.y(), bottomRight.y()));
    }
    private static long wrap(long value, long lower, long upper) {
        long remainder = value % (upper - lower);

        if (remainder >= 0) {
            return remainder + lower;
        } else {
            return remainder + upper;
        }
    }
}
