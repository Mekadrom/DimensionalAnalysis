package com.higgs.da;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertArrayEquals;

public class UtilsTest {
    @Test
    public void combinations() {
        final char[] c = "xyzwu".toCharArray();

        final List<String> permutations = Utils.permute(c, 3, false, false);
        final String[] expected = new String[] { "xyz", "xyw", "xyu", "xzw", "xzu", "xwu", "yzw", "yzu", "ywu", "zwu" };

        assertArrayEquals(expected, permutations.toArray());
    }
}