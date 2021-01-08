/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 02/06/2020
 *
 * Description:
 * Given a list of WordNet nouns x1, x2, ..., xn, which noun is the least
 * related to the others?
 * To identify an outcast, we compute the sum of the distances between each
 * noun and every other one:
 * di = distance(xi, x1) + distance(xi, x2) + ... + distance(xi, xn)
 * and return a noun xt for which dt is maximum.
 * Note that distance(xi, xi) = 0, so it will not contribute to the sum.
 *
 * We assume that the argument to outcast() contains only valid wordnet nouns
 * (and that it contains at least two such nouns).
 **************************************************************************** */

import edu.princeton.cs.algs4.In;

import java.util.Arrays;


public final class Outcast {

    private final WordNet wordnet;

    // constructor takes a WordNet object
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }


    // given an array of WordNet nouns, return an outcast
    public String outcast(String[] nouns) {

        if (nouns == null || nouns.length < 2)
            throw new IllegalArgumentException();

        int n = nouns.length;
        int[] totals = new int[n];
        int[] distanceTable = new int[n * (n + 1) / 2];

        int counter = 0;
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                distanceTable[counter++] = wordnet.distance(nouns[i], nouns[j]);
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                totals[i] += distance(distanceTable, n, i, j);
            }
        }

        int maxTotal = 0;
        String outcast = nouns[0];
        for (int i = 0; i < n; i++) {
            if (totals[i] > maxTotal) {
                maxTotal = totals[i];
                outcast = nouns[i];
            }
        }
        return outcast;
    }


    private int distance(int[] distanceTable, int n, int row, int col) {
        int i;
        int j;
        if (row <= col) {
            i = row;
            j = col;
        }
        else {
            i = col;
            j = row;
        }

        int index = i * n + j;
        for (int k = 1; k <= i; k++) {
            index -= k;
        }

        return distanceTable[index];
    }


    // test client
    // takes from the command line the name of a synset file, the name of a
    // hypernym file, followed by the names of outcast files, and prints out
    // an outcast in each file
    public static void main(String[] args) {

        WordNet wordnet = new WordNet("synsets.txt", "hypernyms.txt");
        Outcast outcast = new Outcast(wordnet);
        String[] words = {
                "outcast2.txt", "outcast3.txt",
                "outcast4.txt", "outcast5.txt", "outcast5a.txt",
                "outcast7.txt", "outcast8.txt", "outcast8a.txt",
                "outcast8b.txt", "outcast8c.txt", "outcast9.txt",
                "outcast9a.txt", "outcast10.txt", "outcast10a.txt",
                "outcast11.txt", "outcast12.txt", "outcast12a.txt",
                "outcast17.txt", "outcast20.txt", "outcast29.txt"
        };

        for (int t = 0; t < words.length; t++) {
            In in = new In(words[t]);
            String[] nouns = in.readAllStrings();
            System.out.println(words[t].toUpperCase());
            System.out.println("Nouns: " + Arrays.toString(nouns));
            System.out.println("Outcast: " + outcast.outcast(nouns));
        }
    }
}
