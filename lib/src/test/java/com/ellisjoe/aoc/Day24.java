package com.ellisjoe.aoc;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day24 {
    @Test
    void part1() {
        boolean test = false;
        Map<String, Boolean> wires = Utils.streamLines(24, test)
                .filter(l -> l.contains(":"))
                .map(l -> Splitter.on(": ").splitToList(l))
                .collect(Collectors.toMap(l -> l.get(0), l -> Integer.parseInt(l.get(1)) > 0));
        List<Gate> gates = Utils.streamLines(24, test)
                .filter(l -> l.contains("->"))
                .map(Gate::parse)
                .toList();

        run(gates, wires);

        String binary = getWires("z", wires);

        System.out.println(Long.parseLong(binary, 2));
    }

    @Test
    void part2() {
        boolean test = false;
        List<Gate> gates = Utils.streamLines(24, test)
                .filter(l -> l.contains("->"))
                .map(Gate::parse)
                .toList();

        List<Gate> brokenGates = gates.stream()
                .filter(g -> isBroken(g, gates))
                .toList();

        String result = brokenGates.stream()
                .map(Gate::out)
                .sorted()
                .reduce((a, b) -> a + "," + b)
                .orElseThrow();
        System.out.println(result);
    }

    private boolean isBroken(Gate gate, List<Gate> gates) {
        boolean brokenZ = isBrokenZ(gate);
        boolean brokenIntermediate = isBrokenIntermediate(gate);
        boolean brokenXor = isBrokenXor(gate, gates);
        boolean brokenAnd = isBrokenAnd(gate, gates);
        return brokenZ || brokenIntermediate || brokenXor || brokenAnd;
    }

    private boolean isBrokenZ(Gate gate) {
        return gate.out().startsWith("z") && !gate.out().equals("z45") && !gate.type().equals(Type.XOR);
    }

    private boolean isBrokenIntermediate(Gate gate) {
        boolean notZ = !gate.out().startsWith("z");
        boolean notA = !gate.a().startsWith("x") && !gate.a().startsWith("y");
        boolean notB = !gate.b().startsWith("x") && !gate.b().startsWith("y");

        if (notZ && notA && notB) {
            return gate.type().equals(Type.XOR);
        } else {
            return false;
        }
    }

    private boolean isBrokenXor(Gate gate, List<Gate> gates) {
        if (!gate.type().equals(Type.XOR) || gate.a().contains("00") || gate.b().contains("00") || !gate.hasXorYinput()) {
            return false;
        }

        return gates.stream()
                .filter(g -> g.type().equals(Type.XOR))
                .noneMatch(g -> g.a().equals(gate.out()) || g.b().equals(gate.out()));
    }

    private boolean isBrokenAnd(Gate gate, List<Gate> gates) {
        if (!gate.type().equals(Type.AND) || gate.a().contains("00") || gate.b().contains("y00") || !gate.hasXorYinput()) {
            return false;
        }

        return gates.stream()
                .filter(g -> g.type().equals(Type.OR))
                .noneMatch(g -> g.a().equals(gate.out()) || g.b().equals(gate.out()));
    }

    private static Map<String, Boolean> run(List<Gate> gates, Map<String, Boolean> inputWires) {
        Map<String, Boolean> wires = Maps.newHashMap(inputWires);
        Set<String> zGates = gates.stream()
                .flatMap(g -> Stream.of(g.a(), g.b(), g.out()))
                .filter(x -> x.startsWith("z"))
                .collect(Collectors.toSet());

        while (!wires.keySet().containsAll(zGates)) {
            for (Gate gate : gates) {
                Boolean a = wires.get(gate.a());
                Boolean b = wires.get(gate.b());
                if (a != null && b != null) {
                    Boolean out = gate.gate().apply(a, b);
                    wires.put(gate.out(), out);
                }
            }
        }

        return wires;
    }

    static String getWires(String wireStart, Map<String, Boolean> wires) {
        return wires.entrySet().stream()
                .filter(w -> w.getKey().startsWith(wireStart))
                .sorted(Collections.reverseOrder(Map.Entry.comparingByKey()))
                .map(Map.Entry::getValue)
                .map(x -> x ? "1" : "0")
                .reduce((a, b) -> a + b)
                .orElseThrow();
    }

    enum Type {
        AND, XOR, OR;
    }
    record Gate(String a, String b, String out, Type type, BiFunction<Boolean, Boolean, Boolean> gate) {
        static Gate parse(String line) {
            List<String> parts = Splitter.on(" ").splitToList(line);
            String a = parts.get(0);
            String funcString = parts.get(1);
            String b = parts.get(2);
            String out = parts.get(4);

            BiFunction<Boolean, Boolean, Boolean> gate = switch (funcString) {
                case "AND" -> (x, y) -> x && y;
                case "OR" -> (x, y) -> x || y;
                case "XOR" -> (x, y) -> x ^ y;
                default -> throw new IllegalStateException("Unexpected value: " + funcString);
            };
            Type type = switch (funcString) {
                case "AND" -> Type.AND;
                case "OR" -> Type.OR;
                case "XOR" -> Type.XOR;
                default -> throw new IllegalStateException("Unexpected value: " + funcString);
            };
            return new Gate(a, b, out, type, gate);
        }

        boolean hasXorYinput() {
            return a.startsWith("x") || a.startsWith("y") || b.startsWith("x") || b.startsWith("y");
        }
    }
}
