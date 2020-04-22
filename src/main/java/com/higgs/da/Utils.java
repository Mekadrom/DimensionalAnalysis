package com.higgs.da;

import java.util.*;
import java.util.function.BiConsumer;

public class Utils {
    private static final int[] _factorials = new int[] {
            1,
            1,
            2,
            2 * 3,
            2 * 3 * 4,
            2 * 3 * 4 * 5,
            2 * 3 * 4 * 5 * 6,
            2 * 3 * 4 * 5 * 6 * 7,
            2 * 3 * 4 * 5 * 6 * 7 * 8,
            2 * 3 * 4 * 5 * 6 * 7 * 8 * 9,
            2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
            2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
            2 * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12
    };

    public static int charToInt(final char c) {
        return Integer.parseInt(String.valueOf(c));
    }

    public static void twoDimensionIter(final BiConsumer<Integer, Integer> consumer, final int firstD, final int secondD) {
        for (int i = 0; i < firstD; i++) {
            for (int j = 0; j < secondD; j++) {
                consumer.accept(i, j);
            }
        }
    }

    public int factorial(int num) {
        if (num < 0) return -factorial(Math.abs(num));
        return _factorials[num];
    }

    /**
     * Create list of all possible combinations of the input set possibles, limiting each entry to a length of r
     * and dropping entries according to orderMatters and repetitionAllowed
     * @param possibles a set of data to permute over
     * @param r the number of places to permute into
     * @param orderMatters if false, removes permutations that have the same elements but are in a different order
     * @param repetitionAllowed if false, removes entries that consist of only one element
     * @return a list containing the permutations with proper elements removed
     */
    public static List<String> permute(final char[] possibles, int r, boolean orderMatters, boolean repetitionAllowed) {
        if (r <= 0) r = 1;
        final List<String> permutations = permute(possibles, r);
        if (!repetitionAllowed) {
            removeRepetitive(permutations);
            removeMultipled(permutations);
        }
        if (!orderMatters) removeUnorderedDuplicates(permutations);
        return permutations;
    }

    private static void removeMultipled(final List<String> permutations) {
        final List<String> toRemove = new ArrayList<>();
        for (final String string1 : permutations) {
            if (hasDuplicates(string1.toCharArray())) {
                toRemove.add(string1);
            }
        }
        permutations.removeAll(toRemove);
    }

    public static boolean hasDuplicates(char[] input) {
        Set<Character> tempSet = new HashSet<>();
        for (char c : input) {
            if (!tempSet.add(c)) {
                return true;
            }
        }
        return false;
    }

    private static void removeUnorderedDuplicates(final List<String> permutations) {
        final List<String> toRemove = new ArrayList<>();
        for (final String string1 : permutations) {
            if (toRemove.contains(string1)) continue;
            for (final String string2 : permutations) {
                if (Objects.equals(string1, string2)) continue;
                char[] chars1 = string1.toCharArray();
                char[] chars2 = string2.toCharArray();
                Arrays.sort(chars1);
                Arrays.sort(chars2);
                if (Objects.equals(new String(chars1), new String(chars2))) {
                    toRemove.add(string2);
                }
            }
        }
        permutations.removeAll(toRemove);
    }

    private static void removeRepetitive(final List<String> permutations) {
        final List<String> toRemove = new ArrayList<>();
        for (final String string1 : permutations) {
            char lastChar = '\0';
            for (char c : string1.toCharArray()) {
                if (c == lastChar) {
                    toRemove.add(string1);
                    break;
                }
                lastChar = c;
            }
        }
        permutations.removeAll(toRemove);
    }

    private static List<String> permute(final char[] possibles, int r) {
        return new Permutator(possibles, r).permutations;
    }

    public static boolean numArrayContains(final int num, final int[] array) {
        for (int i : array)
            if (i == num) return true;
        return false;
    }

    private static class Permutator {
        private final List<String> permutations = new ArrayList<>();

        Permutator(final char[] set, int r) {
            permute(set, "", set.length, r);
        }

        private void permute(final char[] set, String prefix, int n, int r) {
            if (r == 0) {
                permutations.add(prefix);
                return;
            }
            for (int i = 0; i < n; i++) {
                permute(set, prefix + set[i], n, r - 1);
            }
        }
    }
}
