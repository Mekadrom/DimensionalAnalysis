package com.higgs.da;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;

public class Utils {
    private static final long[] factorials = new long[] {
            1,
            1,
            2,
            2L * 3,
            2L * 3 * 4,
            2L * 3 * 4 * 5,
            2L * 3 * 4 * 5 * 6,
            2L * 3 * 4 * 5 * 6 * 7,
            2L * 3 * 4 * 5 * 6 * 7 * 8,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18 * 19,
            2L * 3 * 4 * 5 * 6 * 7 * 8 * 9 * 10 * 11 * 12 * 13 * 14 * 15 * 16 * 17 * 18 * 19 * 20
    };

    public static int charToInt(final char c) {
        return Integer.parseInt(String.valueOf(c));
    }

    public static void twoDimensionIterate(final BiConsumer<Integer, Integer> consumer, final int firstD, final int secondD) {
        for (int i = 0; i < firstD; i++) {
            for (int j = 0; j < secondD; j++) {
                consumer.accept(i, j);
            }
        }
    }

    public long factorial(final int num) {
        if (num < 0) {
            return -this.factorial(Math.abs(num)); // not necessarily always correct, but maintains the negativity of the input
        }
        return Utils.factorials[num];
    }

    /**
     * Create list of all possible combinations of the input set possibles, limiting each entry to a length of r
     * and dropping entries according to orderMatters and repetitionAllowed
     *
     * @param possibles         a set of data to permute over
     * @param r                 the number of places to permute into
     * @param orderMatters      if false, removes permutations that have the same elements but are in a different order
     * @param repetitionAllowed if false, removes entries that consist of only one element
     * @return a list containing the permutations with proper elements removed
     */
    public static List<String> permute(final char[] possibles, int r, final boolean orderMatters, final boolean repetitionAllowed) {
        if (r <= 0) r = 1;
        final List<String> permutations = Utils.permute(possibles, r);
        if (!repetitionAllowed) {
            Utils.removeRepetitive(permutations);
            Utils.removeMultipled(permutations);
        }
        if (!orderMatters) Utils.removeUnorderedDuplicates(permutations);
        return permutations;
    }

    private static void removeMultipled(final List<String> permutations) {
        final List<String> toRemove = new ArrayList<>();
        for (final String string1 : permutations) {
            if (Utils.hasDuplicates(string1.toCharArray())) {
                toRemove.add(string1);
            }
        }
        permutations.removeAll(toRemove);
    }

    public static boolean hasDuplicates(final char[] input) {
        final Set<Character> tempSet = new HashSet<>();
        for (final char c : input) {
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
                final char[] chars1 = string1.toCharArray();
                final char[] chars2 = string2.toCharArray();
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
            for (final char c : string1.toCharArray()) {
                if (c == lastChar) {
                    toRemove.add(string1);
                    break;
                }
                lastChar = c;
            }
        }
        permutations.removeAll(toRemove);
    }

    private static List<String> permute(final char[] possibles, final int r) {
        return new Permutator(possibles, r).permutations;
    }

    public static boolean numArrayContains(final int num, final int[] array) {
        for (final int i : array)
            if (i == num) return true;
        return false;
    }

    private static class Permutator {
        private final List<String> permutations = new ArrayList<>();

        Permutator(final char[] set, final int r) {
            this.permute(set, "", set.length, r);
        }

        private void permute(final char[] set, final String prefix, final int n, final int r) {
            if (r == 0) {
                this.permutations.add(prefix);
                return;
            }
            for (int i = 0; i < n; i++) {
                this.permute(set, prefix + set[i], n, r - 1);
            }
        }
    }
}
