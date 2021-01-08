/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 09/07/2020
 *
 * Description:
 * Implementation of the 3-way Quick Sort algorithm adapted for Strings
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Arrays;

public class QuickStringSort {

    private static final int INSERTION_CUTOFF = 10;

    public static void sort(String[] a) {
        sort(a, 0, a.length - 1, 0);
    }

    // return the dth character of s, -1 if d = length of s
    private static int charAt(String s, int d) {
        if (d >= s.length())
            return -1;
        return s.charAt(d);
    }

    private static void exch(String[] a, int index1, int index2) {
        String copy = a[index1];
        a[index1] = a[index2];
        a[index2] = copy;
    }

    private static void sort(String[] a, int lo, int hi, int d) {
        // base case of the recursion
        if (hi - lo <= INSERTION_CUTOFF) {
            insertion(a, lo, hi, d);
            return;
        }

        // initialise pointers
        int lt = lo;
        int gt = hi;

        // initialise pivot character
        int c = charAt(a[lo], d);

        int pointer = lo + 1;
        while (pointer <= gt) {
            int curr = charAt(a[pointer], d);
            if (curr < c) {
                exch(a, pointer++, lt++);
            }
            else if (curr > c) {
                int curr1 = charAt(a[gt], d);
                while (gt > pointer && curr1 > c) {
                    curr1 = charAt(a[--gt], d);
                }
                exch(a, pointer, gt--);
            }
            else {
                pointer++;
            }
        }

        // recursion
        sort(a, lo, lt - 1, d);
        if (c > -1) {
            sort(a, lt, gt, d + 1);
        }
        sort(a, gt + 1, hi, d);
    }

    private static void insertion(String[] a, int lo, int hi, int d) {
        for (int pointer = lo + 1; pointer <= hi; pointer++) {
            for (int k = pointer; k > lo && less(a[k], a[k - 1], d); k--) {
                exch(a, k, k - 1);
            }
        }
    }

    private static boolean less(String a, String b, int d) {
        int first = charAt(a, d);
        int second = charAt(b, d);
        while (first > -1 && second > -1) {
            if (first < second)
                return true;
            else if (first > second)
                return false;
            d++;
            first = charAt(a, d);
            second = charAt(b, d);
        }
        if (first > second)
            return false;
        return true;
    }

    private static boolean isSorted(String[] a) {

        for (int i = 1; i < a.length; i++) {
            if (a[i].compareTo(a[i - 1]) < 0) {
                System.out.println(a[i] + " is less than " + a[i - 1]);
                return false;
            }
        }
        return true;
    }

    private static double timeSystem(String[] a) {

        System.out.println("Sorting using Java Arrays.sort()...");

        double start = System.currentTimeMillis();
        Arrays.sort(a);
        double end = System.currentTimeMillis();
        return (end - start) / 1000;
    }

    private static double timeQuick(String[] a) {

        System.out.println("Sorting using 3-way string quick sort...");

        double start = System.currentTimeMillis();
        QuickStringSort.sort(a);
        double end = System.currentTimeMillis();
        return (end - start) / 1000;
    }

    // unit testing
    public static void main(String[] args) {

        String f = "howto.txt";
        System.out.println("Importing \"" + f + "\" ...");
        In in = new In(f);
        String[] a = in.readAll().trim().replaceAll("\\s+", " ").split(" ");
        System.out.println("Import complete\n");

        double system = timeSystem(a);
        System.out.println("Time required to sort an array of " + a.length +
                                   " Strings: " + system + " sec\n");


        System.out.println("Importing \"" + f + "\" ...");
        in = new In(f);
        a = in.readAll().trim().replaceAll("\\s+", " ").split(" ");
        System.out.println("Import complete\n");

        double quick = timeQuick(a);
        System.out.println("Time required to sort an array of " + a.length +
                                   " Strings: " + quick + " sec\n");

        System.out.println("Assert array sorted: " + isSorted(a));
    }

}
