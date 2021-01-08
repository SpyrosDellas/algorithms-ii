/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 02/06/2020
 *
 * Description:
 * Builds the WordNet digraph: each vertex v is an integer that represents a
 * synset, and each directed edge v→w represents that w is a hypernym of v.
 *
 * The WordNet digraph is a rooted DAG: it is acyclic and has one vertex (the
 * root) that is an ancestor of every other vertex. However, it is not
 * necessarily a tree because a synset can have more than one hypernym.
 *
 * Corner cases.
 * Throws an IllegalArgumentException in the following situations:
 * - Any argument to the constructor or an instance method is null
 * - The input to the constructor does not correspond to a rooted DAG
 * - Any of the noun arguments in distance() or sap() is not a WordNet noun
 *
 * Performance requirements.
 * - The data type should use space linear in the input size (size of synsets
 * and hypernyms files).
 * - The constructor should take time linearithmic (or better) in the input
 * size.
 * - The method isNoun() should run in time logarithmic (or better) in the
 * number of nouns.
 * - The methods distance() and sap() should run in time linear in the size
 * of the WordNet digraph.
 * For the analysis, we assume that the number of nouns per synset is bounded
 * by a constant.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.DirectedCycle;
import edu.princeton.cs.algs4.In;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;


public final class WordNet {

    // WordNet nouns -> set of synsets in which they appear
    private final HashMap<String, HashSet<Integer>> nouns;

    // synset id -> synset string (all the nouns in the synset)
    private final String[] ids;

    // SAP object of the WordNet graph
    private final SAP sap;


    // constructor takes the name of the two input files
    public WordNet(String synsets, String hypernyms) {

        if (synsets == null || hypernyms == null)
            throw new IllegalArgumentException("Null arguments provided");

        In synsetsFile = new In(synsets);
        String[] lines = synsetsFile.readAllLines();
        ids = new String[lines.length];
        nouns = new HashMap<>(lines.length);

        for (String line : lines) {
            String[] fields = line.split(",");
            int id = Integer.parseInt(fields[0]);
            ids[id] = fields[1];
            String[] synset = fields[1].split(" ");
            for (String word : synset) {
                if (nouns.containsKey(word)) {
                    nouns.get(word).add(id);
                }
                else {
                    nouns.put(word, new HashSet<>());
                    nouns.get(word).add(id);
                }
            }
        }

        Digraph graph = new Digraph(ids.length);
        In hypernymsFile = new In(hypernyms);
        while (hypernymsFile.hasNextLine()) {
            String[] line = hypernymsFile.readLine().split(",");
            int hyponym = Integer.parseInt(line[0]);
            for (int i = 1; i < line.length; i++) {
                graph.addEdge(hyponym, Integer.parseInt(line[i]));
            }
        }

        if (!checkRootedDAG(graph))
            throw new IllegalArgumentException("Graph is not a rooted DAG");

        sap = new SAP(graph);
    }


    private boolean checkRootedDAG(Digraph graph) {

        // verify that the graph has no cycles
        DirectedCycle finder = new DirectedCycle(graph);
        if (finder.hasCycle()) {
            return false;
        }

        // if the graph is connected and has a single root, there can be only
        // one vertex with zero outdegree
        int counter = 0;
        for (int i = 0; i < graph.V(); i++) {
            if (graph.outdegree(i) == 0) {
                counter++;
            }
        }
        if (counter != 1) return false;
        return true;
    }


    // returns all WordNet nouns
    public Iterable<String> nouns() {
        return nouns.keySet();
    }


    // is the word a WordNet noun?
    public boolean isNoun(String word) {

        if (word == null)
            throw new IllegalArgumentException();

        return nouns.containsKey(word);
    }


    // distance between nounA and nounB = length of shortest ancestral path of
    // subsets A and B, where:
    // A = set of synsets in which x appears
    // B = set of synsets in which y appears
    public int distance(String nounA, String nounB) {

        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        return sap.length(nouns.get(nounA), nouns.get(nounB));
    }


    // a synset (second field of synsets.txt) that is the common ancestor of
    // nounA and nounB in a shortest ancestral path
    public String sap(String nounA, String nounB) {

        if (!isNoun(nounA) || !isNoun(nounB))
            throw new IllegalArgumentException();

        int ancestor = sap.ancestor(nouns.get(nounA), nouns.get(nounB));
        return ids[ancestor];
    }

    private int nounsNumber() {
        return nouns.size();
    }

    private int synsetsNumber() {
        return ids.length;
    }


    // unit testing of this class
    public static void main(String[] args) {

        String s1 = "synsets3.txt";
        String[] hyper1 = {
                "hypernyms3InvalidCycle.txt", "hypernyms3InvalidTwoRoots.txt"
        };
        for (String h : hyper1) {
            try {
                WordNet wordNet = new WordNet(s1, h);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Invalid input file: " + h);
            }
        }

        String s2 = "synsets6.txt";
        String[] hyper2 = {
                "hypernyms6InvalidCycle+Path.txt", "hypernyms6InvalidCycle.txt",
                "hypernyms6InvalidTwoRoots.txt"
        };
        for (String h : hyper2) {
            try {
                WordNet wordNet = new WordNet(s2, h);
            }
            catch (IllegalArgumentException e) {
                System.out.println("Invalid input file: " + h);
            }
        }


        System.out.println("\nParsing WordNet...");
        WordNet wordNet = new WordNet("synsets.txt", "hypernyms.txt");
        for (String word : wordNet.nouns()) {
            if (!wordNet.isNoun(word))
                throw new RuntimeException("WordNet has not been parsed correctly");
        }
        System.out.println("Number of nouns: " + wordNet.nounsNumber());
        System.out.println("Number of synsets: " + wordNet.synsetsNumber());
        System.out.println();

        String noun1 = "worm";
        String noun2 = "bird";
        System.out.println("Distance of '" + noun1 + "' to '" + noun2 + "' is: "
                                   + wordNet.distance(noun1, noun2));
        System.out.println("Shortest common ancestor of '" + noun1 + "' to '" + noun2 + "' is: "
                                   + wordNet.sap(noun1, noun2));

        String noun3 = "municipality";
        String noun4 = "region";
        System.out.println("Distance of '" + noun3 + "' to '" + noun4 + "' is: "
                                   + wordNet.distance(noun3, noun4));
        System.out.println("Shortest common ancestor of '" + noun3 + "' to '" + noun4 + "' is: "
                                   + wordNet.sap(noun3, noun4));

        String noun5 = "individual";
        String noun6 = "edible_fruit";
        System.out.println("Distance of '" + noun5 + "' to '" + noun6 + "' is: "
                                   + wordNet.distance(noun5, noun6));
        System.out.println("Shortest common ancestor of '" + noun5 + "' to '" + noun6 + "' is: "
                                   + wordNet.sap(noun5, noun6));

        String noun7 = "Black_Plague";
        String noun8 = "black_marlin";
        System.out.println("Distance of '" + noun7 + "' to '" + noun8 + "' is: "
                                   + wordNet.distance(noun7, noun8));
        System.out.println("Shortest common ancestor of '" + noun7 + "' to '" + noun8 + "' is: "
                                   + wordNet.sap(noun7, noun8));

        String noun9 = "Black_Plague";
        String noun10 = "Black_Death";
        System.out.println("Distance of '" + noun9 + "' to '" + noun10 + "' is: "
                                   + wordNet.distance(noun9, noun10));
        System.out.println("Shortest common ancestor of '" + noun9 + "' to '" + noun10 + "' is: "
                                   + wordNet.sap(noun9, noun10));

        System.out.println("\nChecking performance...");
        System.out.println("Benchmark time for 50000 pairs of queries to "
                                   + "distance() and sap() WITH ARRAYS AND NO CACHING: 5.6 sec");
        System.out.println("Benchmark time for 50000 pairs of queries to "
                                   + "distance() and sap() WITH ARRAYS AND CACHING : 3.65 sec");

        int trials = 50000;
        Iterator<String> nouns = wordNet.nouns().iterator();
        double start = System.currentTimeMillis();
        for (int i = 0; i < trials; i++) {
            String word1 = nouns.next();
            String word2 = nouns.next();
            int dist = wordNet.distance(word1, word2);
            String ancest = wordNet.sap(word1, word2);
        }
        double end = System.currentTimeMillis();
        System.out
                .println("Actual optimised time with hashtables and caching for " + trials
                                 + " pairs of queries to distance() and sap(): "
                                 + (end - start) / 1000 + " sec");
    }
}
