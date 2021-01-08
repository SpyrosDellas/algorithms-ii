/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 12/06/2020
 *
 * Description:
 * In k-clustering our goal is to divide (classify) a set of objects into k
 * coherent groups, i.e. to divide into k clusters so that objects in
 * different clusters are far apart
 *
 * Single link distance function:
 * The distance between two clusters equals the distance between the two closest
 * objects (one in each cluster).
 *
 * Single-link clustering:
 * Given an integer k, find a k-clustering that maximizes the total distance
 * between all pairs of closest clusters.
 *
 * Solution 1:
 * Use Kruskal's algorithm and stop when k connected components are found
 *
 * Solution 2 (faster, unless k is big):
 * Use Prim's algorithm to find the MST and delete the k - 1 largest edges
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;
import java.util.Arrays;


public class KClusters {


    public static void plotClusters(double[] x, double[] y, Edge[] edges, int k) {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(-0.05, 1.05);
        StdDraw.setYscale(-0.05, 1.05);

        StdDraw.setPenColor(Color.BLUE);
        StdDraw.setPenRadius(0.015);
        for (int i = 0; i < x.length; i++) {
            StdDraw.point(x[i], y[i]);
        }

        StdDraw.setPenRadius(0.005);
        for (int i = 0; i < edges.length - k; i++) {
            int v = edges[i].either();
            int w = edges[i].other(v);
            StdDraw.line(x[v], y[v], x[w], y[w]);
        }
        StdDraw.show();
    }


    // test and visualisation client
    public static void main(String[] args) {

        // Number of clusters
        int k = Integer.parseInt(args[0]);

        String filename = "euclidean500.txt";
        In in = new In(filename);
        RandomEuclideanGraph eu = new RandomEuclideanGraph(in);
        double[] x = eu.getX();
        double[] y = eu.getY();

        EdgeWeightedGraph graph = eu.graph();
        Draw plot = eu.plot();

        Prim mst = new Prim(graph);

        System.out.println(
                "The graph has " + graph.V() + " vertices and " + graph.E() + " edges");
        System.out.println(
                "The total weight (length) of the Minimum Spanning Tree is: " + mst.weight());


        System.out.println(
                "Plotting the Minimum Spanning Tree, with k-1 largest edges in red");

        Edge[] edges = new Edge[graph.V() - 1];
        int pointer = 0;
        for (Edge e : mst.edges()) {
            edges[pointer++] = e;
        }
        Arrays.sort(edges);

        plot.setPenRadius(0.005);
        for (int i = 0; i < graph.V() - k; i++) {
            int v = edges[i].either();
            int w = edges[i].other(v);
            plot.line(x[v], y[v], x[w], y[w]);
        }
        plot.setPenColor(Color.RED);
        for (int i = graph.V() - k; i < graph.V() - 1; i++) {
            int v = edges[i].either();
            int w = edges[i].other(v);
            plot.line(x[v], y[v], x[w], y[w]);
        }
        plot.show();

        plotClusters(x, y, edges, k);
    }
}
