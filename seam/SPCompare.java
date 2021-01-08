/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 14/06/2020
 *
 * Description:
 *
 * Compares the speed of:
 * - Dijkstra's algorithm
 * - Bellman-Ford's algorithm
 * on edge weighted digraphs with non-negative weights
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;


public class SPCompare {

    public static void main(String[] args) {

        String filename = "largeEWD.txt";
        In in = new In(filename);

        System.out.println("Building the edge-weighted digraph...");
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(in);
        System.out.println(
                "The graph has " + graph.V() + " vertices and " + graph.E() + " edges\n");

        System.out.println("Calculating shortest paths from s = 0 using "
                                   + "Dijkstra's algorithm...");
        double start = System.currentTimeMillis();
        Dijkstra dijkstra = new Dijkstra(graph, 0);
        double end = System.currentTimeMillis();
        System.out.println("Time to calculate: " + (end - start) / 1000 + " sec\n");

        for (int i = 0; i < graph.V(); i += 60000) {
            System.out.println("Shortest distance to vertex " + i + ": " +
                                       dijkstra.distTo(i));
        }


        System.out.println("\nCalculating shortest paths from s = 0 using "
                                   + "Bellman-Ford's algorithm...");
        double start1 = System.currentTimeMillis();
        BellmanFord bf = new BellmanFord(graph, 0);
        double end1 = System.currentTimeMillis();
        System.out.println("Time to calculate: " + (end1 - start1) / 1000 + " sec\n");

        for (int i = 0; i < graph.V(); i += 60000) {
            System.out.println("Shortest distance to vertex " + i + ": " +
                                       bf.distTo(i));
        }

    }
}
