/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 21/06/2020
 *
 * Description:
 * Least Significant Digit (LSD) radix sort implementation
 *
 **************************************************************************** */

import java.util.Arrays;
import java.util.Random;


public class LSDSort {

    private static final int R = 256;

    // Rearranges the array of 32-bit integers in ascending order
    public static void sort(int[] a) {

        int size = a.length;
        int[] aux = new int[size];

        // Sort by key-indexed counting on first 3 bytes
        for (int shift = 0; shift <= 16; shift += 8) {
            int[] count = new int[R + 1];
            // Compute frequency counts
            for (int i = 0; i < size; i++) {
                count[((a[i] >> shift) & 0xFF) + 1]++;
            }
            // Transform counts to indices
            for (int i = 1; i < R + 1; i++) {
                count[i] += count[i - 1];
            }
            // Sort to auxiliary array aux
            for (int i = 0; i < size; i++) {
                aux[count[(a[i] >> shift) & 0xFF]++] = a[i];
            }
            // Switch roles between aux and a
            int[] copy = a;
            a = aux;
            aux = copy;

        }

        // Sort by key-indexed counting on most significant byte
        // taking into account the sign bit (twos complement representation)
        int[] count = new int[R + 1];
        // Compute frequency counts
        for (int i = 0; i < size; i++) {
            count[((a[i] >>> 24) ^ 0x80) + 1]++;
        }
        // Transform counts to indices
        for (int i = 1; i < R + 1; i++) {
            count[i] += count[i - 1];
        }
        // Sort to auxiliary array aux
        for (int i = 0; i < size; i++) {
            aux[count[(a[i] >>> 24) ^ 0x80]++] = a[i];
        }
        // Switch roles between aux and a ?
        // No action required, aux is a pointer to the original array, since
        // this is the 4th exchange of roles between a and aux

    }


    // Rearranges the array of w-character strings in ascending order
    public static void sort(String[] a, int w) {

        int size = a.length;
        String[] aux = new String[size];

        // Sort by key-indexed counting on dth char
        for (int d = w - 1; d >= 0; d--) {
            int[] count = new int[R + 1];
            // Compute frequency counts
            for (int i = 0; i < size; i++) {
                count[a[i].charAt(d) + 1]++;
            }
            // Transform counts to indices
            for (int i = 1; i < R + 1; i++) {
                count[i] += count[i - 1];
            }
            // Sort to auxiliary array aux
            for (int i = 0; i < size; i++) {
                aux[count[a[i].charAt(d)]++] = a[i];
            }
            // Switch roles between aux and a
            String[] copy = a;
            a = aux;
            aux = copy;
        }

        // After an even number of key-indexed sorts, array a is sorted
        if (w % 2 == 0)
            return;

        // After an odd number of key-indexed sorts, array aux is sorted and
        // we need to copy back to a after exchanging pointers
        String[] copy = a;
        a = aux;
        aux = copy;
        for (int i = 0; i < size; i++) {
            a[i] = aux[i];
        }

    }


    private static void check(int[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i] < a[i - 1])
                throw new RuntimeException("Array not sorted");
        }
    }


    private static void check(String[] a) {
        for (int i = 1; i < a.length; i++) {
            if (a[i].compareTo(a[i - 1]) < 0)
                throw new RuntimeException("Array not sorted");
        }
    }


    private static double timeLSD(int size) {

        Random random = new Random();

        System.out.println("Generating array of random 32-bit integers...");
        int[] a = new int[size];

        for (int i = 0; i < size; i++) {
            a[i] = random.nextInt();
        }

        System.out.println("Sorting using LSD sort...");

        double start = System.currentTimeMillis();
        LSDSort.sort(a);
        double end = System.currentTimeMillis();
        check(a);
        return (end - start) / 1000;
    }


    private static double timeLSD(int size, int w) {

        Random random = new Random();

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        System.out.println("Generating array of random " + size +
                                   " " + w + "-character strings...");
        String[] a = new String[size];
        char[] chars = new char[w];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < w; j++) {
                chars[j] = alphabet[random.nextInt(26)];
            }
            a[i] = new String(chars);
        }

        System.out.println("Sorting using LSD sort...");

        double start = System.currentTimeMillis();
        LSDSort.sort(a, w);
        double end = System.currentTimeMillis();
        check(a);
        return (end - start) / 1000;
    }


    private static double timeSystem(int size) {

        Random random = new Random();
        System.out.println("Generating array of random 32-bit integers...");
        int[] a = new int[size];

        for (int i = 0; i < size; i++) {
            a[i] = random.nextInt();
        }

        System.out.println("Sorting using Java Arrays.sort()...");

        double start = System.currentTimeMillis();
        Arrays.sort(a);
        double end = System.currentTimeMillis();
        return (end - start) / 1000;
    }


    private static double timeSystem(int size, int w) {

        Random random = new Random();

        char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toCharArray();
        System.out.println("Generating array of random " + size +
                                   " " + w + "-character strings...");
        String[] a = new String[size];
        char[] chars = new char[w];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < w; j++) {
                chars[j] = alphabet[random.nextInt(26)];
            }
            a[i] = new String(chars);
        }

        System.out.println("Sorting using Java Arrays.sort()...");

        double start = System.currentTimeMillis();
        Arrays.sort(a);
        double end = System.currentTimeMillis();
        return (end - start) / 1000;
    }


    // test client
    public static void main(String[] args) {

        int size = 10000000;
        int w = 4;

        double lsd = timeLSD(size);
        System.out.println("Time required to sort an array of " + size +
                                   " integers: " + lsd + " sec");

        double system = timeSystem(size);
        System.out.println("Time required to sort an array of " + size +
                                   " integers: " + system + " sec");


        lsd = timeLSD(size, w);
        System.out.println("Time required to sort an array of " + size +
                                   " " + w + "-character ASCII strings: " + lsd + " sec");
        system = timeSystem(size, w);
        System.out.println("Time required to sort an array of " + size +
                                   " " + w + "-character ASCII strings: " + system + " sec");
    }
}
