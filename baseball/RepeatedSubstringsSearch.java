/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 26/07/2020
 *
 * Description:
 * Finds all non-overlapping substrings in a given string that are repeated at
 * least k-times and have minimum length m.
 *
 * Note:
 * If a k-repeated substring is a substring of another k-repeated substring(s),
 * starting at a different position in the given string, then all occurrences
 * are returned
 *
 * Performance:
 * - Average case O(n), where n is the length of the given string
 * - Worst case O(n^2) for strings with long repeats of the same character
 *
 * Solution:
 * - Create and sort the suffix array of the string using the SA-IS algorithm
 * - Calculate the lcp array again in linear time from the suffix array using
 *   Kasai's algorithm
 * - Scan the lcp array once and return all non-overlapping substrings that are
 *   repeated k or more times
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;

import java.util.HashMap;
import java.util.HashSet;


public class RepeatedSubstringsSearch {

    private String s;    // the input string
    private int n;       // the length of the input string
    private int[] sa;    // the suffix array
    private int[] lcp;   // the lcp array


    public RepeatedSubstringsSearch(String string) {

        s = string;
        n = s.length();

        System.out.println("Input string length is: " + n);

        // Create the suffix and lcp arrays in O(n) time using SA-IS and Kasai's
        // algorithms respectively
        System.out.println("Creating the suffix array...");
        double start = System.currentTimeMillis();
        SAIS sais = new SAIS(s, 65535);
        sa = sais.sa();
        double end = System.currentTimeMillis();
        System.out.println("Total time = " + (end - start) / 1000 + " sec");

        System.out.println("Creating the lcp array...");
        start = System.currentTimeMillis();
        lcp = sais.lcp();
        end = System.currentTimeMillis();
        System.out.println("Total time = " + (end - start) / 1000 + " sec");

    }


    // find all non-overlapping substrings of minimum length m that are repeated
    // at least m times
    public HashMap<Integer, HashSet<Queue<Integer>>> findRepeated(int k, int m) {

        if (k > n)
            throw new IllegalArgumentException(
                    "Parameter k must be less than or equal to the length of the input string.");
        else if (k <= 1)
            throw new IllegalArgumentException("Parameter k must be at least 2");

        if (m < 1)
            throw new IllegalArgumentException("Parameter m must be at least 1");

        HashMap<Integer, HashSet<Queue<Integer>>> result = new HashMap<>();

        int min = 0;      // rolling lcp minimum in current search range
        int start = 0;    // start of current search range
        int end = 0;      // end of current search range
        int repeats = 1;  // number of non-overlapping repeats in current search range
        int nextStart = start;    // the start of the next search range
        int lastLength = 0;
        int lastEnd = 0;

        // scan the lcp[] to find all k-repeated substrings of min length m
        while (start < n - k + 2) {

            // reset search range; no repeated substrings of length m or more
            if (lcp[start] < m) {
                start++;
                end = start;
                nextStart = start;
                min = 0;
                repeats = 1;
                lastLength = 0;
                lastEnd = 0;
                continue;
            }

            // reset the starting indices of the non-overlapping repeated substrings
            Queue<Integer> indices = new Queue<>();

            // first expand the search range up to k repeated non-overlapping
            // substrings
            while (end < n && lcp[end] >= m && repeats < k) {
                int curr = lcp[end];

                // update minimum length in current search range
                // if lcp[end] is longer than the minimum lcp in the range so far
                // update the starting point for the next search range
                if (min == 0) {
                    min = curr;
                }
                else {
                    if (curr <= min) {
                        min = curr;
                    }
                    else if (nextStart == start) {
                        nextStart = end;
                    }
                }

                // degenerate case; has been already included in previous search
                if (end <= lastEnd && min == lastLength) {
                    break;
                }

                // check if the two substrings are overlapping
                if (indices.isEmpty()) {
                    if (Math.abs(sa[end + 1] - sa[start]) >= min) {
                        indices.enqueue(sa[start]);
                        indices.enqueue(sa[end + 1]);
                        repeats++;
                    }
                }
                else {
                    boolean overlapping = false;
                    for (int index : indices) {
                        if (Math.abs(sa[end + 1] - index) < min) {
                            overlapping = true;
                            break;
                        }
                    }
                    if (!overlapping) {
                        indices.enqueue(sa[end + 1]);
                        repeats++;
                    }
                }
                end++;
            }

            // reset search range; no k-repeated non-overlapping substrings
            if (repeats < k) {
                start = end + 1;
                end = start;
                nextStart = start;
                min = 0;
                repeats = 1;
                continue;
            }

            // now try to expand the search range as much as possible, while
            // the length of the repeated substring is more than or equal to min
            while (end < n && lcp[end] >= min) {

                if (nextStart == start && lcp[end] > min) {
                    nextStart = end;
                }

                // check if the two substrings are overlapping
                boolean overlapping = false;
                for (int index : indices) {
                    if (Math.abs(sa[end + 1] - index) < min) {
                        overlapping = true;
                        break;
                    }
                }
                if (!overlapping) {
                    indices.enqueue(sa[end + 1]);
                    repeats++;
                }
                end++;
            }

            lastLength = min;
            lastEnd = end - 1;

            if (result.containsKey(min)) {
                result.get(min).add(indices);
            }
            else {
                result.put(min, new HashSet<>());
                result.get(min).add(indices);
            }

            // System.out.println("repeated substring length = " + min + ", repeats = " + repeats);
            // System.out.println(indices);
            // System.out.println(s.substring(sa[start], sa[start] + min));

            if (nextStart > start) {
                // System.out.println("nextstart = " + nextStart + ", index = " + sa[nextStart]);
                start = nextStart;
            }
            else {
                start = end + 1;
            }
            end = start;
            nextStart = start;
            min = 0;
            repeats = 1;
        }

        return result;
    }


    // find the longest non-overlapping substring that is repeated at least m
    // times
    public HashMap<Integer, Queue<Queue<Integer>>> findLongestRepeated(int k) {

        if (k > n)
            throw new IllegalArgumentException(
                    "Parameter k must be less than or equal to the length of the input string.");
        else if (k <= 1)
            throw new IllegalArgumentException("Parameter k must be at least 2");

        HashMap<Integer, Queue<Queue<Integer>>> result = new HashMap<>();

        // the length of the longest non-overlapping repeated substring
        int longestLength = 0;
        // the end of the range in the lcp array containing the longest
        // non-overlapping repeated substring
        int longestEnd = 0;
        // the ends of all the ranges in the lcp array containing the longest
        // non-overlapping repeated substrings; this handles the case when there
        // exist more than one unique repeated substrings of equal length
        Queue<Integer> longestEnds = new Queue<>();

        int min = 0;      // rolling lcp minimum in current search range
        int start = 0;    // start of current search range
        int end = 0;      // end of current search range
        int repeats = 1;  // number of non-overlapping repeats in current search range
        int nextStart = start;    // the start of the next search range


        // scan the lcp[] to find the longest k-repeated non-overlapping substring
        while (start < n - k + 2) {

            // reset search range if no equal or longer repeated substring in current range
            if (lcp[start] < longestLength) {
                start++;
                end = start;
                nextStart = start;
                min = 0;
                repeats = 1;
                continue;
            }

            // reset the starting indices of the longest non-overlapping repeated
            // substring in current search range
            Queue<Integer> indices = new Queue<>();

            // expand the search range up to k repeated non-overlapping
            // substrings
            while (end < n && lcp[end] >= longestLength && repeats < k) {
                int curr = lcp[end];

                // update minimum length in current search range
                // if lcp[end] is longer than the minimum lcp in the range so far
                // update the starting point for the next search range
                if (min == 0) {
                    min = curr;
                }
                else {
                    if (curr <= min) {
                        min = curr;
                    }
                    else if (nextStart == start) {
                        nextStart = end;
                    }
                }

                // degenerate case; has been already included in previous search
                if (end <= longestEnd && min == longestLength) {
                    break;
                }

                // check if the two substrings are overlapping
                if (indices.isEmpty()) {
                    if (Math.abs(sa[end + 1] - sa[start]) >= min) {
                        indices.enqueue(sa[start]);
                        indices.enqueue(sa[end + 1]);
                        repeats++;
                    }
                }
                else {
                    boolean overlapping = false;
                    for (int index : indices) {
                        if (Math.abs(sa[end + 1] - index) < min) {
                            overlapping = true;
                            break;
                        }
                    }
                    if (!overlapping) {
                        indices.enqueue(sa[end + 1]);
                        repeats++;
                    }
                }
                end++;
            }

            // reset search range; no k-repeated non-overlapping substrings
            if (repeats < k) {
                start = end + 1;
                end = start;
                nextStart = start;
                min = 0;
                repeats = 1;
                continue;
            }

            longestLength = min;
            longestEnd = end;

            // a new k-repeated substring of equal length has been found
            if (result.containsKey(min)) {
                result.get(min).enqueue(indices);
                longestEnds.enqueue(longestEnd);
            }
            else {
                // reset result if a longer repeated substring has been found
                if (!result.isEmpty()) {
                    result = new HashMap<Integer, Queue<Queue<Integer>>>();
                    longestEnds = new Queue<Integer>();
                }
                result.put(min, new Queue<>());
                result.get(min).enqueue(indices);
                longestEnds.enqueue(longestEnd);
            }

            if (nextStart > start) {
                start = nextStart;
            }
            else {
                start = end + 1;
            }
            end = start;
            nextStart = start;
            min = 0;
            repeats = 1;
        }

        // now try to expand the search range(s) of the longest repeated
        // non-overlapping substring(s) found as much as possible
        for (Queue<Integer> indices : result.get(longestLength)) {

            end = longestEnds.dequeue();
            while (end < n && lcp[end] >= longestLength) {

                // check if the two substrings are overlapping
                boolean overlapping = false;
                for (int index : indices) {
                    if (Math.abs(sa[end + 1] - index) < longestLength) {
                        overlapping = true;
                        break;
                    }
                }
                if (!overlapping) {
                    indices.enqueue(sa[end + 1]);
                }
                end++;
            }
        }

        return result;
    }


    public static void main(String[] args) {

        String file = "mobydick1.txt";
        System.out.println("Importing file: " + file);
        In in = new In(file);
        String s = in.readAll();

        // String s = "NNNNNNNNNNNNNNNNNNNNNNNNN";

        /*
        String file = "chromosome22.txt";
        System.out.println("Importing file: " + file);
        In in = new In(file);
        String s = in.readAll();
         */

        int k = Integer.parseInt(args[0]);  // number of repeats
        int m = Integer.parseInt(args[1]);  // minimum length of the repeated substring

        RepeatedSubstringsSearch search = new RepeatedSubstringsSearch(s);

        /*
        HashMap<Integer, HashSet<Queue<Integer>>> repeated = search.findRepeated(k, m);
        System.out.println();
        for (int length : repeated.keySet()) {
            HashSet<Queue<Integer>> indicesSet = repeated.get(length);
            int unique = indicesSet.size();
            System.out.println(
                    "Found " + unique + " unique repeated non-overlapping substring(s) of length "
                            + length);
            for (Queue<Integer> indices : indicesSet) {
                System.out.print("Repeated " + indices.size() + " times at: ");
                System.out.println(indices);
                System.out.println(
                        s.substring(indices.peek(), indices.peek() + length));
            }
            System.out.println();
        }
         */


        HashMap<Integer, Queue<Queue<Integer>>> longestRepeated = search.findLongestRepeated(k);
        System.out.println();
        int longest = 0;
        for (int i : longestRepeated.keySet()) {
            longest = i;
        }
        Queue<Queue<Integer>> indicesSet = longestRepeated.get(longest);
        System.out.println(
                "Length of longest repeated non-overlapping substring(s) is: "
                        + longest);
        for (Queue<Integer> indices : indicesSet) {
            System.out.print("Repeated " + indices.size() + " times at indices: ");
            System.out.println(indices);
            System.out.println(
                    s.substring(indices.peek(), indices.peek() + longest));
        }
        System.out.println();

    }
}
