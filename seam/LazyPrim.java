/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 11/06/2020
 *
 * Description:
 * Lazy implementation of Prim's MST algorithm
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;

public class LazyPrim {

    private Queue<Edge> mst;
    private double weight;


    // calculate a Minimum Spanning Tree
    public LazyPrim(EdgeWeightedGraph graph) {

        boolean[] marked = new boolean[graph.V()];

        mst = new Queue<>();

        MinPQ<Edge> crossingEdges = new MinPQ<>();

        visit(0, graph, marked, crossingEdges);

        while (!crossingEdges.isEmpty() && mst.size() < graph.V() - 1) {
            Edge e = crossingEdges.delMin();
            int v = e.either();
            int w = e.other(v);
            if (!marked[v]) {
                visit(v, graph, marked, crossingEdges);
            }
            else if (!marked[w]) {
                visit(w, graph, marked, crossingEdges);
            }
            else {
                continue;
            }
            mst.enqueue(e);
            weight += e.weight();
        }

    }


    private void visit(int v, EdgeWeightedGraph graph, boolean[] marked,
                       MinPQ<Edge> crossingEdges) {

        marked[v] = true;

        for (Edge e : graph.adj(v)) {
            int w = e.other(v);
            if (!marked[w])
                crossingEdges.insert(e);
        }
    }


    public Iterable<Edge> edges() {
        return mst;
    }


    public double weight() {
        return weight;
    }


    // test and visualisation client
    public static void main(String[] args) {

        String filename = "euclidean1000.txt";
        In in = new In(filename);
        RandomEuclideanGraph eu = new RandomEuclideanGraph(in);

        EdgeWeightedGraph graph = eu.graph();
        Draw plot = eu.plot();

        LazyPrim mst = new LazyPrim(graph);

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
            plot.pause(10);
        }

    }
}
