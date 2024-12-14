package com.ellisjoe.aoc.utils;

public class Maths {

    public static long gcd(long a, long b) {
        if (b == 0) {
            return a;
        } else {
            return gcd(b, a % b);
        }
    }

    public static long lcm(long a, long b) {
        if (a == 0 || b == 0) {
            return 0;
        } else {
            return Math.abs(a * b) / gcd(a, b);
        }
    }
}
