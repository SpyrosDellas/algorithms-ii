/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 13/06/2020
 *
 * Description:
 * Implementation of Dijkstra's classic algorithm for the single-source shortest
 * paths problem
 *
 *****************************************************************************/

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;


public class Dijkstra {

    double[] distTo;
    DirectedEdge[] edgeTo;


    public Dijkstra(EdgeWeightedDigraph graph, int s) {

        int V = graph.V();

        edgeTo = new DirectedEdge[V];

        distTo = new double[V];
        for (int i = 0; i < V; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        IndexMinPriorityQueue<Double> frontier = new IndexMinPriorityQueue<>(V);
        frontier.insert(s, 0.0);

        while (!frontier.isEmpty()) {
            int v = frontier.delMin();
            relax(graph, frontier, v);
        }

    }


    public Dijkstra(EdgeWeightedDigraph graph, int s, Draw plot, double[] x, double[] y) {

        int V = graph.V();

        edgeTo = new DirectedEdge[V];

        distTo = new double[V];
        for (int i = 0; i < V; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }
        distTo[s] = 0.0;

        IndexMinPriorityQueue<Double> frontier = new IndexMinPriorityQueue<>(V);
        frontier.insert(s, 0.0);

        while (!frontier.isEmpty()) {
            int v = frontier.delMin();
            DirectedEdge e = edgeTo[v];
            if (e != null) {
                plot.line(x[e.from()], y[e.from()], x[v], y[v]);
                plot.pause(20);
            }
            relax(graph, frontier, v);
        }

    }


    private void relax(EdgeWeightedDigraph graph, IndexMinPriorityQueue<Double> frontier, int v) {

        for (DirectedEdge e : graph.adj(v)) {
            int w = e.to();
            double distance = distTo[v] + e.weight();
            if (distance < distTo[w]) {
                distTo[w] = distance;
                edgeTo[w] = e;
                frontier.change(w, distance);
            }
        }
    }

    // distance from s to v; return Double.POSITIVE_INFINITY if no path
    public double distTo(int v) {
        return distTo[v];
    }


    //  path from s to v?
    public boolean hasPathTo(int v) {
        return distTo[v] != Double.POSITIVE_INFINITY;
    }


    //  path from s to v; null if none
    public Iterable<DirectedEdge> pathTo(int v) {

        Stack<DirectedEdge> stack = new Stack<>();
        DirectedEdge e = edgeTo[v];
        while (e != null) {
            stack.push(e);
            e = edgeTo[e.from()];
        }

        return stack;
    }


    // test client
    public static void main(String[] args) {

        /*
        String filename = "euclideanDigraph500.txt";
        In in = new In(filename);
        GridlikeRandomEuclideanDigraph eu = new GridlikeRandomEuclideanDigraph(in);
         */

        int size = Integer.parseInt(args[0]);
        double connectionRatio = Double.parseDouble(args[1]);
        double directedRatio = Double.parseDouble(args[2]);

        GridlikeRandomEuclideanDigraph eu =
                new GridlikeRandomEuclideanDigraph(size, connectionRatio, directedRatio);

        EdgeWeightedDigraph graph = eu.graph();
        System.out.println("Edge-weighted random euclidean digraph imported.");
        System.out.println("The graph has " + graph.V() + " vertices and "
                                   + graph.E() + " edges");

        double[] x = eu.getX();
        double[] y = eu.getY();

        Draw plot = eu.plot();

        int source = 0;

        // mark the source vertex
        plot.setPenColor(Color.BLUE);
        plot.setPenRadius(0.02);
        plot.point(x[source], y[source]);
        plot.show();

        System.out.println("Animating the Shortest Paths Tree as calculated "
                                   + "using Dijkstra's algorithm...");

        plot.setPenRadius(0.004);
        plot.setPenColor(Color.BLACK);
        plot.disableDoubleBuffering();

        // Call overloaded constructor that plots a trace of the algorithm
        Dijkstra sp = new Dijkstra(graph, source, plot, x, y);


    }

}
