package com.ellisjoe.aoc;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Day17 {
    @Test
    void part1() {
        boolean test = false;
        Map<String, Long> registers = Utils.streamLines(17, test)
                .filter(l -> l.startsWith("Register"))
                .map(Register::parse)
                .collect(Collectors.toMap(Register::name, Register::value));
        Program program = Utils.streamLines(17, test)
                .filter(l -> l.startsWith("Program"))
                .map(Program::parse)
                .findFirst()
                .orElseThrow();
        ProgramState state = new ProgramState(0, registers, List.of());

        state = run(program, state);

        System.out.println(Joiner.on(",").join(state.output()));
    }

    @Test
    void part2() {
        boolean test = false;
        Map<String, Long> registers = Utils.streamLines(17, test)
                .filter(l -> l.startsWith("Register"))
                .map(Register::parse)
                .collect(Collectors.toMap(Register::name, Register::value));
        Program program = Utils.streamLines(17, test)
                .filter(l -> l.startsWith("Program"))
                .map(Program::parse)
                .findFirst()
                .orElseThrow();


        List<Long> possibleA = new ArrayList<>();
        possibleA.add(0L);

        for (int i = 0; i < program.instructions.size(); i++) {
            List<Long> valuesToTry = Lists.transform(possibleA, x -> x << 3);
            possibleA = new ArrayList<>();

            int programSize = program.instructions().size();
            List<Integer> lastN = program.instructions().subList(programSize - (i + 1), programSize);

            for (Long value : valuesToTry) {
                for (int a = 0; a < 8; a++) {
                    long attempt = value | a;
                    registers.put("A", attempt);
                    ProgramState state = new ProgramState(0, registers, List.of());

                    state = run(program, state);

                    if (state.output().equals(lastN)) {
                        possibleA.add(attempt);
                    }
                }
            }
        }

        long min = possibleA.stream().mapToLong(x -> x).min().getAsLong();
        System.out.println(min);
    }

    private static ProgramState run(Program program, ProgramState state) {
        for (Optional<Instruction> instruction = state.nextInstruction(program);
             instruction.isPresent();
             instruction = state.nextInstruction(program)) {
            state = instruction.get().execute(state);
        }
        return state;
    }

    record Register(String name, long value) {
        static Register parse(String line) {
            List<String> parts = Splitter.on(" ").splitToList(line);
            String name = parts.get(1).replace(":", "");
            int value = Integer.parseInt(parts.get(2));
            return new Register(name, value);
        }
    }

    public record ProgramState(int counter, Map<String, Long> registers, List<Integer> output) {
        Optional<Instruction> nextInstruction(Program program) {
            if (counter + 1 < program.instructions().size()) {
                Instruction instruction = Instruction.parse(
                        program.instructions().get(counter),
                        program.instructions().get(counter + 1));
                return Optional.of(instruction);
            } else {
                return Optional.empty();
            }
        }

        Long get(String name) {
            return registers.get(name);
        }

        ProgramState incCounter() {
            return new ProgramState(counter + 2, registers, output);
        }

        ProgramState setCounter(int counter) {
            return new ProgramState(counter, registers, output);
        }

        ProgramState update(String register, Long value) {
            return new ProgramState(counter, ImmutableMap.<String, Long>builder()
                    .putAll(registers)
                    .put(register, value)
                    .buildKeepingLast(), output);
        }

        ProgramState output(int value) {
            List<Integer> newOutput = ImmutableList.<Integer>builder()
                    .addAll(output)
                    .add(value)
                    .build();
            return new ProgramState(counter, registers, newOutput);
        }
    }

    enum OpCode {
        adv, bxl, bst, jnz, bxc, out, bdv, cdv

    }

    sealed interface Operand permits Operand.Literal, Operand.Pointer {
        long toLiteral(ProgramState state);

        record Literal(long value) implements Operand {
            @Override
            public long toLiteral(ProgramState _state) {
                return value;
            }
        }

        record Pointer(String register) implements Operand {
            @Override
            public long toLiteral(ProgramState state) {
                return state.registers().get(register);
            }
        }
    }

    record Instruction(OpCode op, Operand operand) {
        static Instruction parse(int opCode, int operandLit) {
            OpCode op = OpCode.values()[opCode];
            Operand operand = switch (op) {
                case adv, bst, out, bdv, cdv -> combo(operandLit);
                case bxl, jnz, bxc -> new Operand.Literal(operandLit);
            };
            return new Instruction(op, operand);
        }

        static Operand combo(int operand) {
            return switch (operand) {
                case 0, 1, 2, 3 -> new Operand.Literal(operand);
                case 4 -> new Operand.Pointer("A");
                case 5 -> new Operand.Pointer("B");
                case 6 -> new Operand.Pointer("C");
                default -> throw new IllegalArgumentException();
            };
        }

        ProgramState execute(ProgramState state) {
            state = state.incCounter();
            return switch (op) {
                case adv -> {
                    long num = state.get("A");
                    long denom = (long) Math.pow(2, operand().toLiteral(state));
                    yield state.update("A", num / denom);
                }
                case bxl -> {
                    long res = state.get("B") ^ operand().toLiteral(state);
                    yield state.update("B", res);
                }
                case bst -> {
                    long res = operand().toLiteral(state) % 8;
                    yield state.update("B", res);
                }
                case jnz -> {
                    if (state.get("A") != 0) {
                        yield state.setCounter((int) operand().toLiteral(state));
                    } else {
                        yield state;
                    }
                }
                case bxc -> {
                    long res = state.get("B") ^ state.get("C");
                    yield state.update("B", res);
                }
                case out -> state.output(Math.toIntExact(operand().toLiteral(state) % 8));
                case bdv -> {
                    long num = state.get("A");
                    long denom = (long) Math.pow(2, operand().toLiteral(state));
                    yield state.update("B", num / denom);
                }
                case cdv -> {
                    long num = state.get("A");
                    long denom = (long) Math.pow(2, operand().toLiteral(state));
                    yield state.update("C", num / denom);
                }
            };
        }
    }

    record Program(List<Integer> instructions) {
        static Program parse(String line) {
            String ints = Splitter.on(" ").splitToList(line).get(1);
            List<Integer> values = Splitter.on(",").splitToStream(ints).map(Integer::parseInt).toList();
            return new Program(values);
        }
    }
}
