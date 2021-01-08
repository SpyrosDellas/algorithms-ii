/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 11/06/2020
 *
 * Description:
 * Eager implementation of Prim's MST algorithm
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdDraw;

import java.awt.Color;


public class Prim {

    private Queue<Edge> mst;
    private double weight;


    // calculate a Minimum Spanning Tree
    public Prim(EdgeWeightedGraph graph) {

        mst = new Queue<>();

        double[] distTo = new double[graph.V()];
        for (int v = 0; v < distTo.length; v++) {
            distTo[v] = Double.POSITIVE_INFINITY;
        }

        Edge[] edgeTo = new Edge[graph.V()];

        IndexMinPriorityQueue<Double> closestVertices = new IndexMinPriorityQueue<>(graph.V());

        visit(0, graph, edgeTo, distTo, closestVertices);

        while (!closestVertices.isEmpty()) {
            int v = closestVertices.delMin();
            mst.enqueue(edgeTo[v]);
            weight += distTo[v];

            visit(v, graph, edgeTo, distTo, closestVertices);
        }

    }


    private void visit(int v, EdgeWeightedGraph graph, Edge[] edgeTo, double[] distTo,
                       IndexMinPriorityQueue<Double> closestVertices) {

        distTo[v] = 0;        // if distTo[v] is 0, then v is part of the MST

        for (Edge e : graph.adj(v)) {
            int w = e.other(v);
            if (distTo[w] == 0) {
                continue;
            }
            double distance = e.weight();
            if (distance < distTo[w]) {
                edgeTo[w] = e;
                distTo[w] = distance;
                closestVertices.change(w, distance);
            }
        }
    }


    public Iterable<Edge> edges() {
        return mst;
    }


    public double weight() {
        return weight;
    }


    public void plotAsMaze(double[] x, double[] y) {

        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize(900, 900);
        StdDraw.setXscale(-0.05, 1.05);
        StdDraw.setYscale(-0.05, 1.05);
        StdDraw.setPenRadius(0.005);

        for (Edge e : mst) {
            int v = e.either();
            int w = e.other(v);
            StdDraw.line(x[v], y[v], x[w], y[w]);
        }
        StdDraw.setPenColor(Color.BLUE);
        StdDraw.filledCircle(0.5, 0.55, 0.01);
        StdDraw.show();
    }


    // test and visualisation client
    public static void main(String[] args) {

        String filename = "euclidean2000.txt";
        In in = new In(filename);
        RandomEuclideanGraph eu = new RandomEuclideanGraph(in);

        EdgeWeightedGraph graph = eu.graph();
        Draw plot = eu.plot();

        Prim mst = new Prim(graph);

        double[] x = eu.getX();
        double[] y = eu.getY();

        plot.setPenRadius(0.005);
        plot.disableDoubleBuffering();
        plot.show();

        System.out.println(
                "The graph has " + graph.V() + " vertices and " + graph.E() + " edges");
        System.out.println(
                "The total weight (length) of the Minimum Spanning Tree is: " + mst.weight());


        System.out.println(
                "Animating the Minimum Spanning Tree as calculated using Prim's algorithm...");
        for (Edge e : mst.edges()) {
            int v = e.either();
            int w = e.other(v);
            plot.line(x[v], y[v], x[w], y[w]);
            // plot.pause(10);
        }

        mst.plotAsMaze(x, y);
    }
}
