/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 10/06/2020
 *
 * Description:
 *
 * Implementation of Kruskal's MST algorithm
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Queue;


public class Kruskal {

    private Queue<Edge> mst;
    private double weight;


    // calculate a Minimum Spanning Tree
    public Kruskal(EdgeWeightedGraph graph) {

        UnionFind uf = new UnionFind(graph.V());

        mst = new Queue<>();

        Edge[] edges = graphEdges(graph);
        MinPQ<Edge> pq = new MinPQ<>(edges);

        while (!pq.isEmpty() && mst.size() < graph.V() - 1) {
            Edge e = pq.delMin();
            int v = e.either();
            int w = e.other(v);
            if (uf.connected(v, w)) {
                continue;
            }
            uf.union(v, w);
            mst.enqueue(e);
            weight += e.weight();
        }

    }


    private Edge[] graphEdges(EdgeWeightedGraph graph) {

        Edge[] edges = new Edge[graph.E()];

        int counter = 0;
        for (int v = 0; v < graph.V(); v++) {
            int selfLoops = 0;
            for (Edge e : graph.adj(v)) {
                if (e.other(v) > v) {
                    edges[counter++] = e;
                }
                // add only one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) {
                        edges[counter++] = e;
                    }
                    selfLoops++;
                }

            }
        }
        return edges;
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

        Kruskal mst = new Kruskal(graph);

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
                "Animating the Minimum Spanning Tree as calculated using Kruskal's algorithm...");
        for (Edge e : mst.edges()) {
            int v = e.either();
            int w = e.other(v);
            plot.line(x[v], y[v], x[w], y[w]);
            plot.pause(10);
        }

    }
}