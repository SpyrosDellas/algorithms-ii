/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 15/06/2020
 *
 * Description:
 * Implementation of Bellman-Ford's classic algorithm for the single-source
 * shortest paths problem in digraphs with negative weights, but no negative
 * cycles.
 * If one or more negative cycles exist, the algorithm will detect and return
 * one after each vertex is relaxed V times at most
 *
 *****************************************************************************/

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;


public class BellmanFord {

    private int V;                       // number of vertices in the query digraph
    private boolean negativeCyclesOnly;  // true if instantiated with second constructor
    private double[] distTo;             // length of path to v
    private DirectedEdge[] edgeTo;       // last edge on path to v
    private int cost;        // number of calls to relax()
    private Iterable<DirectedEdge> cycle;   // negative cycle in edgeTo[]?


    // Main constructor
    // Finds shortest paths from source s, or a negative cycle reachable from s
    public BellmanFord(EdgeWeightedDigraph graph, int s) {

        negativeCyclesOnly = false;
        V = graph.V();

        edgeTo = new DirectedEdge[V];

        distTo = new double[V];
        for (int i = 0; i < V; i++) {
            distTo[i] = Double.POSITIVE_INFINITY;
        }

        Queue<Integer> q = new Queue<>();   // vertices being relaxed

        boolean[] onQ = new boolean[V];     // Is this vertex on the queue?

        distTo[s] = 0.0;
        q.enqueue(s);
        onQ[s] = true;

        while (!q.isEmpty() && cycle == null) {
            int v = q.dequeue();
            onQ[v] = false;
            relax(graph, q, onQ, v);
        }
    }


    // Second constructor
    // Checks for negative cycles in the whole graph and reports one if exists
    // i.e. if finds and reports a negative cycle which is reachable from some
    // vertex v of the graph;
    public BellmanFord(EdgeWeightedDigraph graph) {

        negativeCyclesOnly = true;
        V = graph.V();

        edgeTo = new DirectedEdge[V];
        distTo = new double[V];    // initial distance to reach any vertex is 0

        Queue<Integer> q = new Queue<>();   // vertices being relaxed
        boolean[] onQ = new boolean[V];     // Is this vertex on the queue?

        for (int i = 0; i < V; i++) {
            q.enqueue(i);
            onQ[i] = true;
        }

        while (!q.isEmpty() && cycle == null) {
            int v = q.dequeue();
            onQ[v] = false;
            relax(graph, q, onQ, v);
        }
    }


    private void relax(EdgeWeightedDigraph graph, Queue<Integer> q, boolean[] onQ, int v) {

        for (DirectedEdge e : graph.adj(v)) {
            int w = e.to();
            double distance = distTo[v] + e.weight();
            if (distance < distTo[w]) {
                distTo[w] = distance;
                edgeTo[w] = e;
                if (!onQ[w]) {
                    q.enqueue(w);
                    onQ[w] = true;
                }
            }
        }
        cost++;
        if (cost % V == 0) {
            findNegativeCycle();
        }
    }


    // finds a negative cycle;
    // To find a negative cycle we create a subgraph containing the edges
    // in the current shortest paths tree and check if a cycle exists
    // If a cycle is found then it is a negative cycle (if it was positive, it
    // wouldn't be in the shortest paths tree)
    private void findNegativeCycle() {

        int V = edgeTo.length;
        EdgeWeightedDigraph graph = new EdgeWeightedDigraph(V);

        for (int i = 0; i < V; i++) {
            if (edgeTo[i] != null) {
                graph.addEdge(edgeTo[i]);
            }
        }

        EdgeWeightedCycleFinder cf = new EdgeWeightedCycleFinder(graph);
        cycle = cf.cycle();
    }


    // is there a negative cycle in the graph?
    public boolean hasNegativeCycle() {
        return cycle != null;
    }


    // a negative cycle (null if no negative cycles)
    public Iterable<DirectedEdge> negativeCycle() {
        return cycle;
    }


    // distance from s to v; return Double.POSITIVE_INFINITY if no path
    public double distTo(int v) {

        if (negativeCyclesOnly)
            throw new UnsupportedOperationException(
                    "Use 'BellmanFord(EdgeWeightedDigraph graph, int s)' to support shortest paths queries");

        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");

        return distTo[v];
    }


    //  path from s to v; null if none
    public Iterable<DirectedEdge> pathTo(int v) {

        if (negativeCyclesOnly)
            throw new UnsupportedOperationException(
                    "Use 'BellmanFord(EdgeWeightedDigraph graph, int s)' to support shortest paths queries");

        if (hasNegativeCycle())
            throw new UnsupportedOperationException("Negative cost cycle exists");

        if (!hasPathTo(v))
            return null;

        Stack<DirectedEdge> stack = new Stack<>();
        DirectedEdge e = edgeTo[v];
        while (e != null) {
            stack.push(e);
            e = edgeTo[e.from()];
        }

        return stack;
    }


    //  path from s to v?
    public boolean hasPathTo(int v) {

        if (negativeCyclesOnly)
            throw new UnsupportedOperationException(
                    "Use 'BellmanFord(EdgeWeightedDigraph graph, int s)' to support shortest paths queries");

        return distTo[v] != Double.POSITIVE_INFINITY;
    }


    /*
    % java BellmanFordSP tinyEWDn.txt 0
      0 to 0 ( 0.00)
      0 to 1 ( 0.93)  0->2  0.26   2->7  0.34   7->3  0.39   3->6  0.52   6->4 -1.25   4->5  0.35   5->1  0.32
      0 to 2 ( 0.26)  0->2  0.26
      0 to 3 ( 0.99)  0->2  0.26   2->7  0.34   7->3  0.39
      0 to 4 ( 0.26)  0->2  0.26   2->7  0.34   7->3  0.39   3->6  0.52   6->4 -1.25
      0 to 5 ( 0.61)  0->2  0.26   2->7  0.34   7->3  0.39   3->6  0.52   6->4 -1.25   4->5  0.35
      0 to 6 ( 1.51)  0->2  0.26   2->7  0.34   7->3  0.39   3->6  0.52
      0 to 7 ( 0.60)  0->2  0.26   2->7  0.34

    % java BellmanFordSP tinyEWDnc.txt 0
      4->5  0.35
      5->4 -0.66
     */
    public static void main(String[] args) {

        In in = new In(args[0]);
        int s = Integer.parseInt(args[1]);
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);

        BellmanFord sp = new BellmanFord(G, s);

        // print negative cycle
        if (sp.hasNegativeCycle()) {
            for (DirectedEdge e : sp.negativeCycle())
                StdOut.println(e);
        }

        // print shortest paths
        else {
            for (int v = 0; v < G.V(); v++) {
                if (sp.hasPathTo(v)) {
                    StdOut.printf("%d to %d (%5.2f)  ", s, v, sp.distTo(v));
                    for (DirectedEdge e : sp.pathTo(v)) {
                        StdOut.print(e + "   ");
                    }
                    StdOut.println();
                }
                else {
                    StdOut.printf("%d to %d           no path\n", s, v);
                }
            }
        }

    }
}
