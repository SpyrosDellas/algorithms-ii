/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 11/06/2020
 *
 * Description:
 *
 * Compares the speed of:
 * - Kruskal's MST algorithm
 * - Prim's MST algorithm (lazy implementation)
 * - Prim's MST algorithm (eager implementation)
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;


public class MSTCompare {

    public static void main(String[] args) {

        String filename = "largeEWG.txt";
        In in = new In(filename);

        System.out.println("Building the graph...");
        EdgeWeightedGraph graph = new EdgeWeightedGraph(in);
        System.out.println(
                "The graph has " + graph.V() + " vertices and " + graph.E() + " edges\n");

        System.out.println("Calculating MST using the eager implementation of "
                                   + "Prim's algorithm...");
        double start = System.currentTimeMillis();
        Prim prim = new Prim(graph);
        double end = System.currentTimeMillis();
        System.out.println("Total weight of the Minimum Spanning Tree: "
                                   + prim.weight());
        System.out.println("Time to calculate: " + (end - start) / 1000 + " sec\n");

        System.out.println("Calculating MST using the lazy implementation of "
                                   + "Prim's algorithm...");
        double start1 = System.currentTimeMillis();
        LazyPrim lazyPrim = new LazyPrim(graph);
        double end1 = System.currentTimeMillis();
        System.out.println("Total weight of the Minimum Spanning Tree: "
                                   + lazyPrim.weight());
        System.out.println("Time to calculate: " + (end1 - start1) / 1000 + " sec\n");

        System.out.println("Calculating MST using Kruskal's algorithm...");
        double start2 = System.currentTimeMillis();
        Kruskal kruskal = new Kruskal(graph);
        double end2 = System.currentTimeMillis();
        System.out.println("Total weight of the Minimum Spanning Tree: "
                                   + kruskal.weight());
        System.out.println("Time to calculate: " + (end2 - start2) / 1000 + " sec\n");
    }
}
