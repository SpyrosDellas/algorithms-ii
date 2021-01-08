/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 28/06/2020
 *
 * Description:
 * - Create, and sort, a suffix array of a given string S in O(N*logN) time and
 * O(N) space, where N is the length of S
 * - In addition, create an lcp (Longest Common Prefix) array in linear time
 * from the sorted suffix array
 * - Supports lcp queries in constant time and thus can find the longest
 * repeated substring in O(N*logN) time
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class ManberCyclicShifts {

    private final char[] s;   // null terminated representation of the input string
    private final int n;      // the length of s
    private final int radix;  // the radix of the input string
    private int[] order;      // the suffix array
    private int[] classes;    // the equivalence classes of the cyclic shifts of s
    private int numberOfClasses; // number of different classes of cyclic shifts
    private int[] lcp;        // the LCP array


    // Suffix array constructor
    // Assumes that the input string doesn't contain the null character
    public ManberCyclicShifts(String inputString) {

        // create a null terminated char array from the input string
        n = inputString.length() + 1;
        s = new char[n];
        System.arraycopy(inputString.toCharArray(), 0, s, 0, n - 1);

        // calculate the radix (size of the alphabet)
        radix = radix(s);

        order = new int[n];
        classes = new int[n];
        lcp = new int[n - 1];

        int length = 1;
        msd();

        while (length < n) {
            System.out.println("Calling sortDoubled(" + length + ")");
            sortDoubled(length);
            updateClasses(length);
            length *= 2;
        }

        /*
        for (int i = 0; i < order.length; i++) {
            for (int j = order[i]; j < s.length; j++) {
                System.out.print(s[j]);
            }
            System.out.println();
        }

         */

        kasaiLCP();
    }


    // Compute the LCP (Longest Common Prefix) array in linear time from the
    // sorted suffix array order[]
    // The LCP array lcp is the array of length (s.length - 1) such that
    // lcp[i] = LCP(order[i], order[i + 1])
    //
    // Lemma:
    // For any i < j, LCP(order[i], order[j]) <= lcp[i] and
    // LCP(order[i], order[j]) <= lcp[j - 1]
    // Proof: Immediate from the fact that the suffixes are in sorted order
    //
    // Kasai's algorithm:
    // 1. Start at position i = 0 in the string, get order[0] and compute
    //    LCP(order[0], order[0 + 1]) directly
    // 2. Move one position to the right in the string, k = i + 1.
    //    Get order[k] and compute LCP(order[k], order[k + 1]).
    //    It follows from the above lemma that:
    //    LCP(order[k], order[k + 1]) >= LCP(order[i], order[i + 1]) - 1.
    //    Therefore, we can safely ignore the first lcp[i] - 1 characters and
    //    compute lcp[k] by direct comparison of the remaining characters
    // 3. Repeat until the lcp array is fully computed
    private void kasaiLCP() {

        // compute the inverse array
        // inverse[i] gives the index of the suffix starting at position i in
        // the sorted suffix array), i.e. order[inverse[i]] = inverse[order[i]] = i
        int[] inverse = new int[n];
        for (int i = 0; i < n; i++) {
            inverse[order[i]] = i;
        }

        // Apply Kasai's algorithm
        int max = n - 1;

        int previousLCP = 0;
        for (int i = 0; i < n; i++) {
            int curIndex = inverse[i];
            if (curIndex == max)
                continue;

            int nextPos = order[curIndex + 1];
            int nextLCP = lcp(i, nextPos, Math.max(0, previousLCP - 1));
            lcp[curIndex] = nextLCP;
            previousLCP = nextLCP;
        }
    }


    // compute the lcp of two suffixes given an already known common prefix
    private int lcp(int pos1, int pos2, int commonPrefix) {

        int lcp = commonPrefix;

        int start1 = (pos1 + commonPrefix) % n;
        int start2 = (pos2 + commonPrefix) % n;
        for (int i = 0; i < n; i++) {
            if (s[(start1 + i) % n] == s[(start2 + i) % n])
                lcp++;
            else
                break;
        }
        return lcp;
    }


    // Sort the cyclic shifts of length L = 1 (i.e. individual characters) using
    // standard counting sort
    //
    // Also compute the equivalence classes of the cyclic shifts C(i) of length
    // L = 1
    // classes[i] is the number of different cyclic shifts of length L = 1
    // that can be strictly smaller than the cyclic shift C(i) of length L = 1
    // starting at i.
    // Thus classes[i] == classes[j] if and only if C(i) == C(j)
    private void msd() {

        int[] count = new int[radix];

        // Compute frequency counts
        for (int i = 0; i < n; i++) {
            count[s[i]]++;
        }

        // Transform counts to indices
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }

        // Compute classes[]
        for (int i = 0; i < n; i++) {
            classes[i] = s[i];
        }

        // Sort by first character
        for (int i = n - 1; i >= 0; i--) {
            order[--count[s[i]]] = i;
        }

    }


    // Sort the cyclic shifts of length L = 2 * length by counting sort (which
    // is a stable sort) the first half only, starting at index (i - length).
    // The second half starting at index i is already sorted by the previous
    // iterations.
    private void sortDoubled(int length) {

        int[] newOrder = new int[n];

        // we can have up to n different classes of cyclic shifts
        int[] count = new int[n];

        // classes[i] plays the role of the character value in the standard
        // counting sort
        for (int i = 0; i < n; i++) {
            count[classes[i]] += 1;
        }
        for (int i = 1; i < count.length; i++) {
            count[i] += count[i - 1];
        }
        for (int i = n - 1; i >= 0; i--) {
            int start = (order[i] - length + n) % n;
            newOrder[--count[classes[start]]] = start;
        }
        order = newOrder;
    }


    // Update the classes of the doubled cyclic shifts of length L = 2 * length
    private void updateClasses(int length) {

        int[] newClasses = new int[n];

        // set the class of the smallest cyclic shift to zero
        newClasses[order[0]] = 0;

        int prev = order[0];
        for (int i = 1; i < n; i++) {
            // if the two cyclic shifts are equal they belong to the same class
            int cur = order[i];
            if (classes[cur] == classes[prev] &&
                    classes[(cur + length) % n] == classes[(prev + length) % n]) {
                newClasses[cur] = newClasses[prev];
            }
            else {
                numberOfClasses = newClasses[prev] + 1;
                newClasses[cur] = numberOfClasses;
            }
            prev = cur;
        }
        classes = newClasses;
    }


    public int radix() {
        return radix;
    }

    public int lcp(int i) {
        return lcp[i];
    }

    public int order(int i) {
        return order[i];
    }

    private static int radix(char[] s) {
        int radix = 0;
        for (char c : s) {
            if (c > radix)
                radix = c;
        }
        // add 1 for the null character (character 0)
        return radix + 1;
    }


    public static void main(String[] args) {

        String f = "chromosome4.txt";
        System.out.println("Importing file: " + f);
        In in = new In(f);
        String s = in.readAll();

        System.out.println("Number of characters: " + s.length());

        System.out.println("Creating the suffix and lcp arrays...");
        double start = System.currentTimeMillis();
        ManberCyclicShifts sa = new ManberCyclicShifts(s);
        double end = System.currentTimeMillis();
        System.out.println("Total time = " + (end - start) / 1000 + " sec");

        System.out.println(Arrays.toString(sa.order));

        int lrs = 0;
        int lrsStart = 0;
        for (int i = 0; i < s.length(); i++) {
            if (sa.lcp(i) > lrs) {
                lrs = sa.lcp(i);
                lrsStart = i;
            }
        }
        System.out.println("Length of longest repeated substring = " + lrs);
        int pos1 = sa.order(lrsStart);
        int pos2 = sa.order(lrsStart + 1);
        System.out.println("First occurrence position = " + pos1);
        System.out.println("Second occurrence position = " + pos2);
        System.out.println("Longest repeated substring is:\n" +
                                   s.substring(pos1, pos1 + Math.min(15000, lrs)));
    }

}
