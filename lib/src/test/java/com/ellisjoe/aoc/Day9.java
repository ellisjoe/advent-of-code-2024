package com.ellisjoe.aoc;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;

public class Day9 {
    @Test
    void part1() {
        String line = Utils.readLines(9, false).getFirst();
        List<Character> blocks = processInput(line);
        List<Character> defragged = defrag(blocks);
        long checksum = checksum(defragged);
        System.out.println(checksum);
    }

    @Test
    void part2() {
        String line = Utils.readLines(9, false).getFirst();
        List<Block> blocks = processInput2(line);
        List<Block> defragged = defrag2(blocks);
        List<Integer> flattened = flatten(defragged);
        long checksum = checksum2(flattened);
        System.out.println(checksum);
    }

    private static long checksum(List<Character> blocks) {
        long checksum = 0;
        for (int i = 0; i < blocks.size(); i++) {
            Character c = blocks.get(i);
            if (c != '.') {
                checksum += (i * (c - '0'));
            }
        }
        return checksum;
    }

    private static List<Character> defrag(List<Character> blocks) {
        List<Character> defragged = new ArrayList<>(blocks);
        for (int i = blocks.size() - 1; i >= 0; i--) {
            Character c = blocks.get(i);
            if (c != '.') {
                int nextFree = defragged.indexOf('.');
                if (nextFree > i) {
                    break;
                } else {
                    defragged.set(nextFree, c);
                    defragged.set(i, '.');
                }
            }
        }
        return defragged;
    }

    private static List<Character> processInput(String input) {
        List<Character> fs = new ArrayList<>();
        int id = 0;
        for (int i = 0; i < input.length(); i++) {
            int blocks = input.charAt(i) - '0';
            char value = '.';
            if (i % 2 == 0) {
                value = (char) ('0' + id);
                id++;
            }
            for (int j = 0; j < blocks; j++) {
                fs.add(value);
            }
        }
        return fs;
    }

    private static long checksum2(List<Integer> blocks) {
        long checksum = 0;
        for (int i = 0; i < blocks.size(); i++) {
            checksum += i * blocks.get(i);
        }
        return checksum;
    }

    private static List<Integer> flatten(List<Block> blocks) {
        List<Integer> fs = new ArrayList<>();
        for (int i = 0; i < blocks.size(); i++) {
            switch (blocks.get(i)) {
                case FileBlock fileBlock -> {
                    for (int i1 = 0; i1 < fileBlock.size(); i1++) {
                        fs.add(fileBlock.id());
                    }
                }
                case FreeBlock freeBlock -> {
                    for (int i1 = 0; i1 < freeBlock.size(); i1++) {
                        fs.add(0);
                    }
                }
            }
        }
        return fs;
    }

    private static List<Block> defrag2(List<Block> blocks) {
        List<Block> defragged = new ArrayList<>(blocks);
        List<FileBlock> fileBlocks = blocks.stream()
                .filter(b -> b instanceof FileBlock)
                .map(b -> (FileBlock) b)
                .toList()
                .reversed();
        for (FileBlock fileBlock : fileBlocks) {
            int fileIndex = defragged.indexOf(fileBlock);
            int freeSpace = findFreeSpace(fileBlock, defragged);
            if (freeSpace != -1 && freeSpace < fileIndex) {
                FreeBlock freeBlock = (FreeBlock) defragged.get(freeSpace);
                // Replace entire file block with free space
                defragged.set(fileIndex, new FreeBlock(fileBlock.size()));

                // Replace the free space with the file block and possible some remaining free space
                defragged.remove(freeSpace);
                defragged.add(freeSpace, fileBlock);
                freeBlock.fill(fileBlock.size()).ifPresent(f -> defragged.add(freeSpace + 1, f));
            }
        }
        return defragged;
    }

    private static int findFreeSpace(FileBlock fileBlock, List<Block> blocks) {
        for (int i = 0; i < blocks.size(); i++) {
            Block block = blocks.get(i);
            if (block instanceof FreeBlock freeBlock) {
                if (freeBlock.size() >= fileBlock.size()) {
                    return i;
                }
            }
        }
        return -1;
    }

    private static List<Block> processInput2(String input) {
        List<Block> fs = new LinkedList<>();
        int id = 0;
        for (int i = 0; i < input.length(); i++) {
            int size = input.charAt(i) - '0';
            if (i % 2 == 0) {
                fs.add(new FileBlock(id, size));
                id++;
            } else {
                fs.add(new FreeBlock(size));
            }
        }
        return fs;
    }

    private static void print(List<Block> blocks) {
        for (Block block : blocks) {
            for (int i = 0; i < block.size(); i++) {
                switch (block) {
                    case FileBlock fileBlock -> System.out.print(fileBlock.id);
                    case FreeBlock ignored -> System.out.print('.');
                }
            }
        }
        System.out.println();
    }

    sealed interface Block permits FileBlock, FreeBlock {
        int size();
    }

    record FileBlock(int id, int size) implements Block {
    }

    record FreeBlock(int size) implements Block {
        Optional<FreeBlock> fill(int fileSize) {
            checkArgument(fileSize <= size);
            int newSize = size - fileSize;
            return newSize > 0 ? Optional.of(new FreeBlock(newSize)) : Optional.empty();
        }
    }
}
