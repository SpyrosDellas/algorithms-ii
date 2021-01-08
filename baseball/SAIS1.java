/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 11/07/2020
 *
 * Description:
 * - Implementation of the SA-IS algorithm
 * - Create, and sort, a suffix array of a given string S in O(n) time and
 *   O(n) extra space, where n is the length of S
 * - In addition, create an lcp (Longest Common Prefix) array in linear time
 *   from the sorted suffix array
 * - Supports lcp queries in constant time and thus can find the longest
 *   repeated substring in O(N) time
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class SAIS1 {

    private String s;         // the input string
    private int[] si;         // the input string (integer array representation)
    private int[] sa;         // the suffix array

    private boolean isInt;    // is the input string represented as an int[]
    private int n;            // the length of the suffix array
    private int lms;          // the number of LMS suffixes in the string
    private int reducedRadix; // the radix of the reduced string's alphabet


    // Suffix array constructor
    // str is the input string
    // r is the radix of the alphabet (256 for extended ASCII)
    public SAIS1(String str, int r) {

        if (str == null)
            throw new IllegalArgumentException("Input string cannot be null");
        if (r < 1)
            throw new IllegalArgumentException("Alphabet size must be at least 1");

        // the length of the suffix array is the length of the string plus 1 to
        // account for the sentinel at the end of the string
        n = str.length() + 1;

        isInt = false;
        s = str;

        // the radix is the radix of the string alphabet plus 1 to account for
        // the sentinel value -1 at the end of the string. The sentinel has to
        // be smaller than all the characters of the string, including the null
        // character (0 value)
        int radix = r + 1;       // the number of characters in the alphabet

        // If the string is empty, no further action is required
        if (str.length() == 0)
            return;

        // the boundaries of the buckets for each letter of s
        int[] buckets = new int[radix + 1];

        // -2 if types[i] is L-type,
        // -1 if S-type,
        // index in p1[] if LMS-substring if LMS-type
        int[] types = new int[n];

        classifySuffixes(types, buckets);

        int[] p1 = new int[lms]; // the positions of the lms suffixes in the string
        populateP1(p1, types);

        int[] sa0 = new int[n];
        induceSort(sa0, p1, types, buckets);

        // the reduced string; includes space for the sentinel character at the end
        int[] s1 = new int[lms + 1];

        reduce(s1, p1, sa0, types);
        sa0 = null;   // release the memory occupied by sa0 ahead of recursion

        boolean[] finalTypes = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (types[i] == -2)
                finalTypes[i] = true;
        }
        types = null;  // release the memory occupied by types ahead of recursion

        // System.out.println(Arrays.toString(sa0));
        // System.out.println(Arrays.toString(p1));
        // System.out.println(Arrays.toString(s1));

        if (reducedRadix == lms) {
            s1 = suffixSortFromUniqueChars(s1);
        }
        else {
            System.out.println("RECURSION...");
            SAIS1 reducedSA = new SAIS1(s1, reducedRadix + 1);
            s1 = reducedSA.sa;
        }

        sa = new int[n];
        induceSortReduced(finalTypes, buckets, p1, s1);

        // System.out.println(Arrays.toString(sa));

    }


    // Suffix array constructor
    // str is the input string (the int[] version includes the sentinel a the end)
    // r is the radix of the alphabet; by convention the sentinel character
    // -1 at the end of the string is already accounted for
    public SAIS1(int[] str, int r) {

        if (str == null)
            throw new IllegalArgumentException("Input string cannot be null");
        if (r < 1)
            throw new IllegalArgumentException("Alphabet size must be at least 1");

        // the length of the suffix array is the length of the string, since by
        // convention an int[] string includes the sentinel at the end
        n = str.length;

        isInt = true;
        si = str;

        // If the string is empty, no further action is required
        if (str.length == 0)
            return;

        // the boundaries of the buckets for each letter of s
        int[] buckets = new int[r + 1];

        // -2 if types[i] is L-type,
        // -1 if S-type,
        // index in p1[] if LMS-substring if LMS-type
        int[] types = new int[n];

        classifySuffixesInt(types, buckets);

        int[] p1 = new int[lms]; // the positions of the lms suffixes in the string
        populateP1(p1, types);

        int[] sa0 = new int[n];
        induceSortInt(sa0, p1, types, buckets);

        // the reduced string; includes space for the sentinel character at the end
        int[] s1 = new int[lms + 1];

        reduceInt(s1, p1, sa0, types);
        sa0 = null;   // release the memory occupied by sa0 ahead of recursion

        boolean[] finalTypes = new boolean[n];
        for (int i = 0; i < n; i++) {
            if (types[i] == -2)
                finalTypes[i] = true;
        }
        types = null;  // release the memory occupied by types ahead of recursion

        // System.out.println(Arrays.toString(sa));
        // System.out.println(Arrays.toString(p1));
        // System.out.println(Arrays.toString(s1));

        if (reducedRadix == lms) {
            s1 = suffixSortFromUniqueChars(s1);
        }
        else {
            System.out.println("RECURSION...");
            SAIS1 reducedSA = new SAIS1(s1, reducedRadix + 1);
            s1 = reducedSA.sa;
        }

        sa = new int[n];
        induceSortReducedInt(finalTypes, buckets, p1, s1);

        // System.out.println(Arrays.toString(sa));
    }


    // Classify all suffixes as L-type, S-type or LMS-type and count the
    // frequencies of appearance for each character
    private void classifySuffixes(int[] types, int[] buckets) {

        // initialize buckets[1] for sentinel (end of string character)
        buckets[1]++;

        int prev = -1;       // previous character is the sentinel
        types[n - 1] = -1;   // the sentinel is an S-type suffix by convention
        lms = 0;             // the number of LMS suffixes in the string

        for (int i = n - 2; i >= 0; i--) {
            int curr = s.charAt(i);
            buckets[curr + 2]++;
            if (curr > prev) {
                types[i] = -2;   // L-type
            }
            else if (curr == prev && types[i + 1] == -2) {
                types[i] = -2;   // L-type
            }
            else {
                types[i] = -1;  // S-type
            }
            if (types[i] == -2 && types[i + 1] == -1) {
                types[i + 1] = 0;     // LMS-type
                lms++;
            }
            prev = curr;
        }

        // Calculate the boundaries of the buckets for each character
        for (int i = 1; i < buckets.length; i++) {
            buckets[i] += buckets[i - 1];
        }
    }


    // Classify all suffixes as L-type, S-type or LMS-type and count the
    // frequencies of appearance for each character
    private void classifySuffixesInt(int[] types, int[] buckets) {

        // initialize buckets[1] for sentinel (end of string character)
        buckets[1]++;

        int prev = -1;       // previous character is the sentinel
        types[n - 1] = -1;   // the sentinel is an S-type suffix by convention
        lms = 0;             // the number of LMS suffixes in the string

        for (int i = n - 2; i >= 0; i--) {
            int curr = si[i];
            buckets[curr + 2]++;
            if (curr > prev) {
                types[i] = -2;   // L-type
            }
            else if (curr == prev && types[i + 1] == -2) {
                types[i] = -2;   // L-type
            }
            else {
                types[i] = -1;  // S-type
            }
            if (types[i] == -2 && types[i + 1] == -1) {
                types[i + 1] = 0;     // LMS-type
                lms++;
            }
            prev = curr;
        }

        // Calculate the boundaries of the buckets for each character
        for (int i = 1; i < buckets.length; i++) {
            buckets[i] += buckets[i - 1];
        }
    }


    private void populateP1(int[] p1, int[] types) {

        int counter = lms - 1;
        int lastPos = n - 1;

        for (int i = n - 1; i >= 0; i--) {
            if (types[i] == 0) {
                types[i] = counter;
                p1[counter--] = lastPos - i + 1;  // store the lms substring length
                lastPos = i;
            }
        }

    }


    private void induceSort(int[] sa0, int[] p1, int[] types, int[] buckets) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // update for sentinel
        sa0[0] = n - 1;

        // place the LMS suffixes at the ends of their buckets
        for (int i = n - 2; i >= 1; i--) {
            if (types[i] >= 0)
                sa0[--boundaries[s.charAt(i) + 2]] = i;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n - 1; i++) {
            int curr = sa0[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -2) {
                sa0[boundaries[s.charAt(prev) + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n - 1; i >= 1; i--) {
            int curr = sa0[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] != -2) {
                sa0[--boundaries[s.charAt(prev) + 2]] = prev;
            }
        }
    }


    private void induceSortInt(int[] sa0, int[] p1, int[] types, int[] buckets) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // update for sentinel
        sa0[0] = n - 1;

        // place the LMS suffixes at the ends of their buckets
        for (int i = n - 2; i >= 1; i--) {
            if (types[i] >= 0)
                sa0[--boundaries[si[i] + 2]] = i;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n - 1; i++) {
            int curr = sa0[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -2) {
                sa0[boundaries[si[prev] + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n - 1; i >= 1; i--) {
            int curr = sa0[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] != -2) {
                sa0[--boundaries[si[prev] + 2]] = prev;
            }
        }
    }


    private void induceSortReduced(boolean[] types, int[] buckets, int[] p1, int[] sa1) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        sa[0] = n - 1; // update for sentinel

        // place the sorted LMS suffixes at the ends of their buckets
        for (int i = sa1.length - 1; i >= 2; i--) {
            int index = p1[sa1[i]];
            sa[--boundaries[s.charAt(index) + 2]] = index;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n - 1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev]) {
                sa[boundaries[s.charAt(prev) + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n - 1; i >= 1; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && !types[prev]) {
                sa[--boundaries[s.charAt(prev) + 2]] = prev;
            }
        }
    }


    private void induceSortReducedInt(boolean[] types, int[] buckets, int[] p1, int[] sa1) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // place the sorted LMS suffixes at the ends of their buckets
        for (int i = sa1.length - 1; i >= 1; i--) {
            int index = p1[sa1[i]];
            sa[--boundaries[si[index] + 2]] = index;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n - 1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev]) {
                sa[boundaries[si[prev] + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n - 1; i >= 1; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && !types[prev]) {
                sa[--boundaries[si[prev] + 2]] = prev;
            }
        }
    }


    private void reduce(int[] s1, int[] p1, int[] sa0, int[] types) {

        s1[lms - 1] = 0;     // this is always the sentinel
        s1[lms] = -1;        // put a new sentinel character at the end
        p1[lms - 1] = n - 1; // update for the position of the sentinel

        int prevId = 0;       // identification of the previous lms substring
        int prevLength = 1;   // length of the previous lms substring
        int prevPos = n - 1;  // starting position of the previous lms substring

        for (int i = 1; i < n; i++) {
            int currPos = sa0[i];
            int index = types[currPos];
            if (index < 0)
                continue;  // not an lms substring
            int currLength = p1[index];
            p1[index] = currPos;
            if (currLength == prevLength && isEqual(prevPos, currPos, currLength)) {
                s1[index] = prevId;
            }
            else {
                s1[index] = ++prevId;
            }
            prevLength = currLength;
            prevPos = currPos;
        }

        // the radix of the reduced string (+1 for the sentinel added at the end)
        reducedRadix = prevId + 1;
    }


    private void reduceInt(int[] s1, int[] p1, int[] sa0, int[] types) {

        s1[lms - 1] = 0;     // this is always the sentinel
        s1[lms] = -1;        // put a new sentinel character at the end
        p1[lms - 1] = n - 1; // update for the position of the sentinel

        int prevId = 0;       // identification of the previous lms substring
        int prevLength = 1;   // length of the previous lms substring
        int prevPos = n - 1;  // starting position of the previous lms substring

        for (int i = 1; i < n; i++) {
            int currPos = sa0[i];
            int index = types[currPos];
            if (index < 0)
                continue;  // not an lms substring
            int currLength = p1[index];
            p1[index] = currPos;
            if (currLength == prevLength && isEqualInt(prevPos, currPos, currLength)) {
                s1[index] = prevId;
            }
            else {
                s1[index] = ++prevId;
            }
            prevLength = currLength;
            prevPos = currPos;
        }

        // the radix of the reduced string (+1 for the sentinel added at the end)
        reducedRadix = prevId + 1;
    }


    private boolean isEqual(int p1, int p2, int currLength) {

        if (p1 + currLength - 1 == n - 1 || p2 + currLength - 1 == n - 1)
            return false;

        for (int i = 0; i < currLength; i++) {
            if (s.charAt(p1++) != s.charAt(p2++)) {
                return false;
            }
        }
        return true;
    }


    private boolean isEqualInt(int p1, int p2, int currLength) {

        for (int i = 0; i < currLength; i++) {
            if (si[p1++] != si[p2++]) {
                return false;
            }
        }
        return true;
    }


    private int[] suffixSortFromUniqueChars(int[] s1) {

        int[] sa1 = new int[s1.length];

        for (int i = 0; i < s1.length; i++) {
            sa1[s1[i] + 1] = i;
        }

        return sa1;
    }


    // Compute the LCP (Longest Common Prefix) array in linear time from the
    // sorted suffix array sa[]
    // The LCP array lcp is the array of length (s.length - 1) such that
    // lcp[i] = LCP(sa[i], sa[i + 1])
    //
    // Lemma:
    // For any i < j, LCP(sa[i], sa[j]) <= lcp[i] and
    // LCP(sa[i], sa[j]) <= lcp[j - 1]
    // Proof: Immediate from the fact that the suffixes are in sorted order
    //
    // Kasai's algorithm:
    // 1. Start at position i = 0 in the string, get sa[0] and compute
    //    LCP(sa[0], sa[0 + 1]) directly
    // 2. Move one position to the right in the string, k = i + 1.
    //    Get sa[k] and compute LCP(sa[k], sa[k + 1]).
    //    It follows from the above lemma that:
    //    LCP(sa[k], sa[k + 1]) >= LCP(sa[i], sa[i + 1]) - 1.
    //    Therefore, we can safely ignore the first lcp[i] - 1 characters and
    //    compute lcp[k] by direct comparison of the remaining characters
    // 3. Repeat until the lcp array is fully computed
    public int[] lcp() {

        int[] lcp = new int[n];     // the LCP array

        // compute the inverse array
        // inverse[i] gives the index of the suffix starting at position i in
        // the sorted suffix array), i.e. sa[inverse[i]] = inverse[sa[i]] = i
        int[] inverse = new int[n];
        for (int i = 0; i < n; i++) {
            inverse[sa[i]] = i;
        }

        // Apply Kasai's algorithm
        int max = n - 1;
        int previousLCP = 0;
        for (int i = 0; i < n; i++) {
            int curIndex = inverse[i];
            if (curIndex == max)
                continue;

            int nextPos = sa[curIndex + 1];
            int nextLCP = lcp(i, nextPos, Math.max(0, previousLCP - 1), n);
            lcp[curIndex] = nextLCP;
            previousLCP = nextLCP;
        }

        return lcp;
    }


    // compute the lcp of two suffixes given an already known common prefix
    private int lcp(int pos1, int pos2, int commonPrefix, int n) {

        int lcp = commonPrefix;
        int p1 = (pos1 + commonPrefix);
        int p2 = (pos2 + commonPrefix);

        for (int i = 0; i < n; i++) {
            if (p1 >= n - 1 || p2 >= n - 1)
                break;
            if (s.charAt(p1++) == s.charAt(p2++))
                lcp++;
            else
                break;
        }
        return lcp;
    }


    // Unit testing
    public static void main(String[] args) {

        String f = "chromosome22.txt";
        System.out.println("Importing file: " + f);
        In in = new In(f);
        String s = in.readAll();

        // String s = "ACGTGCCTAGCCTACCGTGCC";
        // or
        // int[] s = new int[] { 1, 3, 7, 20, 7, 3, 3, 20, 1, 7, 3, 3, 20, 1, 3, 3, 7, 20, 7, 3, 3 };
        // Result should be:
        // [21, 13, 0, 8, 20, 19, 14, 10, 5, 15, 1, 11, 6, 18, 9, 4, 16, 2, 12, 7, 17, 3]

        // String s = "ABANANABANDANA";
        // Result should be:
        // [14, 13, 0, 6, 11, 4, 2, 8, 1, 7, 10, 12, 5, 3, 9]

        // String s = "GTCCCGATGTCATGTCAGGA";
        // Result should be:
        // [20, 19, 16, 11, 6, 15, 10, 2, 3, 4, 18, 5, 17, 13, 8, 0, 14, 9, 1, 12, 7]

        // String s = "mmississiippii";
        // Result should be:
        // [14, 13, 12, 8, 9, 5, 2, 1, 0, 11, 10, 7, 4, 6, 3]

        System.out.println("Number of characters: " + s.length());

        System.out.println("Creating the suffix array...");
        double start = System.currentTimeMillis();
        SAIS1 sa = new SAIS1(s, 65535);
        double end = System.currentTimeMillis();
        System.out.println("Total time = " + (end - start) / 1000 + " sec");

        System.out.println("Creating the lcp array...");
        start = System.currentTimeMillis();
        int[] lcp = sa.lcp();
        end = System.currentTimeMillis();
        System.out.println("Total time = " + (end - start) / 1000 + " sec");

        int lrs = 0;
        int lrsStart = 0;
        for (int i = 0; i < s.length(); i++) {
            if (lcp[i] > lrs) {
                lrs = lcp[i];
                lrsStart = i;
            }
        }
        System.out.println("Length of longest repeated substring = " + lrs);
        int pos1 = sa.sa[lrsStart];
        int pos2 = sa.sa[lrsStart + 1];
        System.out.println("First occurrence position = " + pos1);
        System.out.println("Second occurrence position = " + pos2);
        System.out.println("Longest repeated substring is:\n" +
                                   s.substring(pos1, pos1 + Math.min(1000, lrs)));

    }

}
