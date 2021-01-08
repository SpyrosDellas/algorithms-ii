/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 26/05/2020
 *  Description:
 *  This class represents a data type for finding shortest paths (number of
 *  edges) from a source vertex s (or a set of source vertices) to every other
 *  vertex in an undirected graph.
 *
 *  This implementation uses breadth-first search.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

public class BfsPaths {

    private static final int INFINITY = Integer.MAX_VALUE;

    private boolean[] marked;
    private int[] edgeTo;      // edgeTo[v] = previous edge on shortest s-v path
    private int[] distTo;      // distTo[v] = number of edges shortest s-v path
    private boolean haslocalCycle;  // is there a cycle in the connected component containing s
    private int minCycle;      // the shortest cycle found while searching from s
    private boolean multipleSources;


    // Computes the shortest path between the source vertex s and every other
    // vertex in the graph
    public BfsPaths(Graph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        multipleSources = false;
        haslocalCycle = false;
        bfs(G, s);
    }

    // Computes the shortest path between any one of the source vertices in
    // sources and every other vertex in the graph
    public BfsPaths(Graph G, Iterable<Integer> sources) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        distTo = new int[G.V()];
        multipleSources = true;
        for (int i = 0; i < distTo.length; i++) {
            distTo[i] = INFINITY;
        }
        bfs(G, sources);
    }

    private void bfs(Graph G, int s) {
        Queue<Integer> frontier = new Queue<>();

        // Mark the source and put it on the queue.
        frontier.enqueue(s);
        marked[s] = true;
        edgeTo[s] = s;

        while (!frontier.isEmpty()) {
            int v = frontier.dequeue();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    frontier.enqueue(w);
                }
                else if (!haslocalCycle) {
                    // check if self-loop
                    if (w == v) {
                        haslocalCycle = true;
                        minCycle = 0;
                    }
                    // check if parallel edge
                    if (edgeTo[w] == v) {
                        haslocalCycle = true;
                        minCycle = 2;
                    }
                    // check for other loops
                    if (w != edgeTo[v]) {
                        haslocalCycle = true;
                        minCycle = distTo[v] + distTo[w] + 1;
                    }
                }
            }
        }
    }

    private void bfs(Graph G, Iterable<Integer> sources) {

        Queue<Integer> frontier = new Queue<>();
        for (int s : sources) {
            frontier.enqueue(s);
            marked[s] = true;
            distTo[s] = 0;
        }

        while (!frontier.isEmpty()) {
            int v = frontier.dequeue();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    marked[w] = true;
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    frontier.enqueue(w);
                }
            }
        }

    }

    public boolean hasLocalCycle() {
        if (multipleSources)
            throw new UnsupportedOperationException("Multiple sources provided");
        return haslocalCycle;
    }

    public int minCycle() {
        if (multipleSources)
            throw new UnsupportedOperationException("Multiple sources provided");
        return minCycle;
    }

    // path from s to v; null if no such path
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v))
            return null;

        Stack<Integer> path = new Stack<>();
        int distance = distTo(v);
        for (int i = 0; i <= distance; i++) {
            path.push(v);
            v = edgeTo[v];
        }

        return path;
    }

    // is there a path from s to v?
    public boolean hasPathTo(int v) {
        validateVertex(v);
        return marked[v];
    }

    // Number of edges in a shortest path between the source vertex s (or
    // sources) and vertex v
    public int distTo(int v) {
        validateVertex(v);
        if (hasPathTo(v))
            return distTo[v];
        else
            return INFINITY;
    }

    // throw an IllegalArgumentException unless 0 <= v < V
    private void validateVertex(int v) {
        int V = marked.length;
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }

    // unit tests
    public static void main(String[] args) {

        /*
        Graph G = new Graph(new In(args[0]));
        int s = Integer.parseInt(args[1]);
        int v = Integer.parseInt(args[2]);

        System.out.println("TEST 1");
        BfsPaths search = new BfsPaths(G, s);
        System.out.println(
                "Length of shortest path from " + s + " to " + v + ": " + search.distTo(v));
        StdOut.print("A shortest path from " + s + " to " + v + " is: ");
        if (search.hasPathTo(v))
            for (int x : search.pathTo(v))
                if (x == s) StdOut.print(x);
                else StdOut.print("-" + x);
        StdOut.println();

        System.out.println("\nTEST 2");
        TreeSet<Integer> sources = new TreeSet<>();
        for (int i = 0; i < 10; i++) {
            sources.add(i);
        }
        BfsPaths search1 = new BfsPaths(G, sources);
        System.out.println(
                "Length of shortest path from " + sources + " to " + v + ": " + search1.distTo(v));
        StdOut.print("A shortest path from " + sources + " to " + v + " is: ");
        if (search1.hasPathTo(v))
            System.out.println(search1.pathTo(v));
        StdOut.println();

         */

        System.out.println("TREE GRAPH");
        Graph G1 = GraphGenerator.tree(10);
        System.out.println("\n" + G1);
        BfsPaths paths1 = new BfsPaths(G1, 0);

        System.out.println("Is there a cycle in the graph? " + paths1.hasLocalCycle());
        if (paths1.hasLocalCycle())
            System.out.println("Cycle length: " + paths1.minCycle());


        System.out.println("\ntestG.txt GRAPH");
        In file = new In("testG.txt");
        Graph G3 = new Graph(file);
        System.out.println(G3);
        for (int v = 0; v < G3.V(); v++) {
            BfsPaths paths = new BfsPaths(G3, v);
            System.out.println("Is there a cycle in the graph, starting from " + v + "? " + paths
                    .hasLocalCycle());
            if (paths.hasLocalCycle())
                System.out.println("Cycle length: " + paths.minCycle());
        }
    }
}
