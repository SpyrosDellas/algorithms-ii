/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 15/06/2020
 *
 * Description:
 * Checks for cycles in an edge-weighted digraph
 *
 *****************************************************************************/

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.ResizingArrayStack;
import edu.princeton.cs.algs4.Stack;

import java.awt.Color;


public class EdgeWeightedCycleFinder {

    private Stack<DirectedEdge> cycle;   // a cycle in the input digraph
    private int V;               // number of vertices in the graph
    private boolean hasCycle;    // is there a cycle in the graph?


    public EdgeWeightedCycleFinder(EdgeWeightedDigraph graph) {

        V = graph.V();
        boolean[] marked = new boolean[V];
        DirectedEdge[] edgeTo = new DirectedEdge[V];
        boolean[] onStack = new boolean[V];
        hasCycle = false;

        // run dfs from each vertex, if not visited already
        for (int i = 0; i < V; i++) {
            if (hasCycle) {
                break;
            }
            if (!marked[i]) {
                dfs(graph, i, marked, edgeTo, onStack);
            }
        }

    }


    // Depth first search from vertex s, using an explicit stack instead of
    // recursion
    private void dfs(EdgeWeightedDigraph graph, int s, boolean[] marked,
                     DirectedEdge[] edgeTo, boolean[] onStack) {

        // explicit stack to keep track of nodes explored during depth-first
        // search
        ResizingArrayStack<Integer> stack = new ResizingArrayStack<>();

        stack.push(s);
        marked[s] = true;

        while (!stack.isEmpty()) {

            // obtain next vertex to be explored
            int v = stack.peek();

            // if already explored, remove the vertex from the stack and
            // continue with next vertex (equivalent to a recursive call return)
            if (onStack[v]) {
                onStack[v] = false;
                stack.pop();
                continue;
            }

            // if not explored, mark it as explored and begin depth-first search
            // from this vertex
            onStack[v] = true;
            for (DirectedEdge e : graph.adj(v)) {
                int w = e.to();
                if (!marked[w]) {
                    marked[w] = true;
                    stack.push(w);
                    edgeTo[w] = e;
                }
                else if (onStack[w]) {
                    hasCycle = true;
                    cycle = new Stack<DirectedEdge>();
                    cycle.push(e);
                    for (int x = v; x != w; x = edgeTo[x].from()) {
                        cycle.push(edgeTo[x]);
                    }
                    return;
                }
            }
        }

    }


    // a cycle; null if no cycles
    public Iterable<DirectedEdge> cycle() {

        if (cycle == null)
            return null;

        Queue<DirectedEdge> cycleCopy = new Queue<>();
        for (DirectedEdge e : cycle) {
            cycleCopy.enqueue(e);
        }
        return cycleCopy;
    }


    // test client
    public static void main(String[] args) {

        int size = Integer.parseInt(args[0]);
        double connectionRatio = Double.parseDouble(args[1]);
        double directedRatio = Double.parseDouble(args[2]);

        GridlikeRandomEuclideanDigraph eu =
                new GridlikeRandomEuclideanDigraph(size, connectionRatio, directedRatio);

        EdgeWeightedDigraph graph = eu.graph();
        System.out.println("Edge-weighted random euclidean digraph created.");
        System.out.println("The graph has " + graph.V() + " vertices and "
                                   + graph.E() + " edges");

        double[] x = eu.getX();
        double[] y = eu.getY();

        Draw plot = eu.plot();

        // Check for directed cycles
        EdgeWeightedCycleFinder cf = new EdgeWeightedCycleFinder(graph);
        Iterable<DirectedEdge> cycle = cf.cycle();

        System.out.println("Is there a cycle in the graph? " + (cycle != null));

        if (cycle != null) {
            plot.setPenRadius(0.004);
            plot.setPenColor(Color.BLACK);
            for (DirectedEdge e : cycle) {
                double x1 = x[e.from()];
                double y1 = y[e.from()];
                double x2 = x[e.to()];
                double y2 = y[e.to()];
                plot.line(x1, y1, x2, y2);
                System.out.println(e);
            }
        }
        plot.show();
    }

}
