/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 24/07/2020
 *
 * Description:
 * - Implementation of the SA-IS algorithm
 * - Create, and sort, a suffix array of a given string S in O(n) time and
 *   O(n) extra space, where n is the length of S
 * - In addition, create an lcp (Longest Common Prefix) array in linear time
 *   from the sorted suffix array, using Kasai's algorithm
 * - Supports lcp queries in constant time and thus can find the longest
 *   repeated substring in O(N) time
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

public class SAIS {

    private String s;         // the input string
    private int[] sa;         // the suffix array


    // Suffix array constructor
    public SAIS(String inputString, int radix) {

        if (inputString == null)
            throw new IllegalArgumentException("Input string cannot be null");
        if (radix < 1)
            throw new IllegalArgumentException("Alphabet size must be at least 1");

        // the input string
        s = inputString;

        // the length of the suffix array;
        // includes space for an additional sentinel character at the end
        int n0 = s.length() + 1;

        // the suffix array
        sa = new int[n0];

        // If the string is empty, no further action is required
        if (s.length() == 0) {
            return;
        }

        sais0(radix, n0);
    }


    private void sais0(int radix, int n0) {

        // the boundaries of the buckets for each letter in the alphabet of s,
        // including one extra bucket for the sentinel character at the end
        int[] buckets = new int[radix + 2];

        // -1 if types[i] is L-type,
        // 0 if S-type,
        // 1 if LMS-type
        byte[] types = new byte[n0];

        classifySuffixes(types, buckets, n0);

        induceSort(types, buckets, n0);

        reduce(types, buckets, n0);
    }


    private void sais1(int radix, int n1) {

        // the boundaries of the buckets for each letter in the alphabet of s
        int[] buckets = new int[radix + 2];

        // -1 if types[i] is L-type,
        // 0 if S-type,
        // 1 if LMS-type
        byte[] types = new byte[n1];

        classifySuffixesInt(types, buckets, n1);

        induceSortInt(types, buckets, n1);

        reduceInt(types, buckets, n1);
    }


    // Classify all suffixes as L-type, S-type or LMS-type
    private void classifySuffixes(byte[] types, int[] buckets, int n0) {

        // initialize buckets[1] for sentinel (end of string character)
        buckets[1]++;

        int prev = -1;       // previous character is the sentinel

        for (int i = n0 - 2; i >= 0; i--) {
            int curr = s.charAt(i);
            buckets[curr + 2]++;
            if (curr > prev) {
                types[i] = -1;   // L-type
            }
            else if (curr == prev && types[i + 1] == -1) {
                types[i] = -1;   // L-type
            }
            if (types[i] == -1 && types[i + 1] == 0) {
                types[i + 1] = 1;     // LMS-type
            }
            prev = curr;
        }

        // Calculate the boundaries of the buckets for each character
        for (int i = 1; i < buckets.length; i++) {
            buckets[i] += buckets[i - 1];
        }
    }


    // Classify all suffixes as L-type, S-type or LMS-type
    private void classifySuffixesInt(byte[] types, int[] buckets, int n1) {

        int prev = 0;       // previous character is the sentinel
        buckets[1]++;       // initialize buckets[1] for sentinel

        int pointer = n1 - 2;
        for (int i = 2 * n1 - 2; i >= n1; i--) {
            int curr = sa[i];
            buckets[curr + 1]++;
            if (curr > prev) {
                types[pointer] = -1;   // L-type
            }
            else if (curr == prev && types[pointer + 1] == -1) {
                types[pointer] = -1;   // L-type
            }
            if (types[pointer] == -1 && types[pointer + 1] == 0) {
                types[pointer + 1] = 1;     // LMS-type

            }
            pointer--;
            prev = curr;
        }

        // Calculate the boundaries of the buckets for each character
        for (int i = 1; i < buckets.length; i++) {
            buckets[i] += buckets[i - 1];
        }
    }


    private void induceSort(byte[] types, int[] buckets, int n0) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // update for sentinel
        sa[0] = n0 - 1;

        // place the LMS suffixes at the ends of their buckets
        for (int i = n0 - 2; i >= 1; i--) {
            if (types[i] == 1)
                sa[--boundaries[s.charAt(i) + 2]] = i;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n0 - 1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -1) {
                sa[boundaries[s.charAt(prev) + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n0 - 1; i >= 1; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] != -1) {
                sa[--boundaries[s.charAt(prev) + 2]] = prev;
            }
        }
    }


    private void induceSortInt(byte[] types, int[] buckets, int n1) {

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // initialize the part of sa[] that will be storing the reduced suffix array
        for (int i = 0; i < n1; i++)
            sa[i] = -1;

        // place the LMS suffixes at the ends of their buckets
        int pointer = n1 - 1;
        for (int i = 2 * n1 - 1; i >= n1; i--) {
            if (types[pointer] == 1)
                sa[--boundaries[sa[i] + 1]] = pointer;
            pointer--;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -1) {
                sa[boundaries[sa[n1 + prev]]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n1 - 1; i >= 0; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] != -1) {
                sa[--boundaries[sa[n1 + prev] + 1]] = prev;
            }
        }
    }


    private void induceSortReduced(byte[] types, int[] buckets, int n0, int lms0) {

        // store the positions of the lms suffixes into sa[lms0]...sa[lms0 + lms0 - 1]
        int pointer = lms0;
        for (int i = 0; i < n0; i++) {
            if (types[i] == 1)
                sa[pointer++] = i;
        }

        // recover the indices of the sorted lms suffixes
        for (int i = 0; i < lms0; i++) {
            sa[i] = sa[lms0 + sa[i]];
        }

        // clean the remaining part of sa[]
        for (int i = lms0; i < n0; i++)
            sa[i] = -1;

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        sa[0] = n0 - 1; // update for sentinel

        // place the sorted LMS suffixes at the ends of their buckets
        for (int i = lms0 - 1; i >= 1; i--) {
            int index = sa[i];
            sa[i] = -1;
            sa[--boundaries[s.charAt(index) + 2]] = index;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n0 - 1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -1) {
                sa[boundaries[s.charAt(prev) + 1]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n0 - 1; i >= 1; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] >= 0) {
                sa[--boundaries[s.charAt(prev) + 2]] = prev;
            }
        }
    }


    private void induceSortReducedInt(byte[] types, int[] buckets, int n1, int lms1) {

        // store the positions of the lms suffixes into sa[lms1]...sa[lms1 + lms1 - 1]
        int pointer = lms1;
        for (int i = 0; i < n1; i++) {
            if (types[i] == 1)
                sa[pointer++] = i;
        }

        // recover the indices of the sorted lms suffixes
        for (int i = 0; i < lms1; i++) {
            sa[i] = sa[lms1 + sa[i]];
        }

        // clean the remaining part of sa[]
        for (int i = lms1; i < n1; i++)
            sa[i] = -1;

        // create a copy of buckets[]
        int[] boundaries = buckets.clone();

        // place the sorted LMS suffixes at the ends of their buckets
        for (int i = lms1 - 1; i >= 0; i--) {
            int index = sa[i];
            sa[i] = -1;
            sa[--boundaries[sa[n1 + index] + 1]] = index;
        }

        // induce sort the L-prefixes
        boundaries = buckets.clone();
        for (int i = 0; i < n1 - 1; i++) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] == -1) {
                sa[boundaries[sa[n1 + prev]]++] = prev;
            }
        }

        // induce sort the S-prefixes
        boundaries = buckets.clone();
        for (int i = n1 - 1; i >= 1; i--) {
            int curr = sa[i];
            int prev = curr - 1;
            if (curr > 0 && types[prev] >= 0) {
                sa[--boundaries[sa[n1 + prev] + 1]] = prev;
            }
        }
    }


    private void reduce(byte[] types, int[] buckets, int n0) {

        // compact the sorted lms substrings
        int lms0 = 0;
        for (int i = 0; i < n0; i++) {
            if (types[sa[i]] == 1)
                sa[lms0++] = sa[i];
        }

        // init the name array buffer
        for (int i = lms0; i < n0; i++)
            sa[i] = -1;

        // find the lexicographic names of all substrings
        sa[lms0 + (n0 - 1) / 2] = 0;    // update for sentinel
        int prevId = 0;        // identification of the previous lms substring
        int prevPos = n0 - 1;  // starting position of the previous lms substring

        for (int i = 1; i < lms0; i++) {
            int currPos = sa[i];
            if (isEqual(prevPos, currPos, types, n0)) {
                sa[lms0 + currPos / 2] = prevId;
            }
            else {
                sa[lms0 + currPos / 2] = ++prevId;
            }
            prevPos = currPos;
        }

        // compact the reduced string from sa[lms0]...sa[lms0 + lms0 - 1]
        int pointer = lms0;
        for (int i = lms0; i < n0; i++) {
            if (sa[i] >= 0)
                sa[pointer++] = sa[i];
        }

        if (prevId + 1 == lms0) {
            suffixSortFromUniqueChars(lms0);
        }
        else {
            // System.out.println("RECURSION: radix = " + prevId + ", lms0 = " + lms0);
            sais1(prevId, lms0);
        }

        induceSortReduced(types, buckets, n0, lms0);
    }


    private void reduceInt(byte[] types, int[] buckets, int n1) {

        // compact the sorted lms substrings
        int lms1 = 0;
        for (int i = 0; i < n1; i++) {
            if (types[sa[i]] == 1)
                sa[lms1++] = sa[i];
        }

        // init the name array buffer
        for (int i = lms1; i < n1; i++)
            sa[i] = -1;

        // find the lexicographic names of all substrings
        sa[lms1 + (n1 - 1) / 2] = 0;    // update for sentinel
        int prevId = 0;        // identification of the previous lms substring
        int prevPos = n1 - 1;  // starting position of the previous lms substring

        for (int i = 1; i < lms1; i++) {
            int currPos = sa[i];
            if (isEqualInt(prevPos, currPos, types, n1)) {
                sa[lms1 + currPos / 2] = prevId;
            }
            else {
                sa[lms1 + currPos / 2] = ++prevId;
            }
            prevPos = currPos;
        }

        // compact the reduced string from sa[lms1]...sa[lms1 + lms1 - 1]
        int pointer = lms1;
        for (int i = lms1; i < n1; i++) {
            if (sa[i] >= 0)
                sa[pointer++] = sa[i];
        }

        if (prevId + 1 == lms1) {
            suffixSortFromUniqueChars(lms1);
        }
        else {
            // System.out.println("RECURSION: radix = " + prevId + ", lms1 = " + lms1);
            sais1(prevId, lms1);
        }

        induceSortReducedInt(types, buckets, n1, lms1);
    }


    private boolean isEqual(int p1, int p2, byte[] types, int n0) {

        int max = n0 - 1;

        if (p1 == max || p2 == max)
            return false;

        if (s.charAt(p1++) != s.charAt(p2++))
            return false;

        for (int i = 0; i < n0; i++) {
            if (p1 == max || p2 == max || s.charAt(p1) != s.charAt(p2))
                return false;
            if (types[p1] == 1 || types[p2] == 1)
                break;
            p1++;
            p2++;
        }

        return (types[p1] == 1 && types[p2] == 1);
    }


    private boolean isEqualInt(int p1, int p2, byte[] types, int n1) {

        int max = n1 - 1;

        if (p1 == max || p2 == max)
            return false;

        int pointer1 = n1 + p1;
        int pointer2 = n1 + p2;

        // compare the first characters
        if (sa[pointer1++] != sa[pointer2++])
            return false;

        p1++;
        p2++;
        for (int i = 0; i < n1; i++) {
            if (p1 == max || p2 == max || sa[pointer1++] != sa[pointer2++])
                return false;
            if (types[p1] == 1 || types[p2] == 1)
                break;
            p1++;
            p2++;
        }

        return (types[p1] == 1 && types[p2] == 1);
    }


    private void suffixSortFromUniqueChars(int lms) {

        // store the suffix array in the first lms positions of sa0[]
        for (int i = 0; i < lms; i++) {
            sa[sa[lms + i]] = i;
        }
    }


    // Computes the LCP (Longest Common Prefix) array in linear time from the
    // sorted suffix array sa[] using Kasai's algorithm
    // The LCP array lcp is the array of length (sa.length - 1) such that
    // lcp[i] = LCP(sa[i], sa[i + 1])
    //
    // Lemma:
    // For any i < j, LCP(sa[i], sa[j]) <= lcp[i] and
    // LCP(sa[i], sa[j]) <= lcp[j - 1]
    // Proof: Immediate from the fact that the suffixes are in sorted order
    //
    // Kasai's algorithm:
    // 1. Start at position i = 0 in the string, get sa[inverse[i]] and compute:
    //    lcp[inverse[i] = LCP(sa[inverse[i]], sa[inverse[i] + 1])
    //    directly by character comparison
    //
    // 2. Move one position to the right in the string, k = i + 1.
    //    Get sa[inverse[k]] and compute:
    //    lcp[inverse[k] = LCP(sa[inverse[k]], sa[inverse[k] + 1])
    //    It follows from the above lemma that:
    //    lcp[inverse[k] = lcp[inverse[i + 1]] >= lcp[inverse[i] - 1
    //    Therefore, we can safely ignore the first lcp[i] - 1 characters and
    //    compute lcp[k] by direct comparison of the remaining characters
    //
    // 3. Repeat until the lcp array is fully computed
    //
    public int[] lcp() {

        int n = sa.length;

        int[] lcp = new int[n - 1];     // the LCP array

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


    public int[] sa() {
        return sa;
    }


    // Unit testing
    public static void main(String[] args) {

        String f = "etext99.txt";
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
        SAIS sa = new SAIS(s, 65535);
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
