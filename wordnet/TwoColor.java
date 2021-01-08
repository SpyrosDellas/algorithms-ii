/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 27/05/2020
 *
 * Description:
 * Can the vertices of a given graph be assigned one of two colors in such a
 * way that no edge connects vertices of the same color?
 * Equivalent to: Is the graph bipartite ?
 *
 * Given a graph, TwoColor.jave finds either (i) a bipartition or (ii) an
 * odd-length cycle.
 *
 * Runs in O(E + V) time.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.Stack;

public class TwoColor {

    private boolean isBipartite;   // is the graph bipartite?
    private boolean[] color;       // color[v] gives vertices on one side of bipartition
    private int[] edgeTo;          // edgeTo[v] = last edge on path to v
    private Stack<Integer> cycle;  // odd-length cycle

    public TwoColor(Graph G) {
        edgeTo = new int[G.V()];
        color = new boolean[G.V()];
        isBipartite = true;
        dfs(G);
    }

    private void dfs(Graph G) {

        boolean[] marked = new boolean[G.V()];

        // Scan through all vertices as the graph might be disconnected
        for (int s = 0; s < G.V(); s++) {
            // If vertex s has been examined already ignore it
            if (marked[s]) continue;

            // Find all vertices connected to s, scanning for odd cycles
            Stack<Integer> vertexStack = new Stack<>();
            vertexStack.push(s);
            marked[s] = true;
            edgeTo[s] = s;

            while (!vertexStack.isEmpty()) {
                int v = vertexStack.pop();
                boolean wColor = !color[v];
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        vertexStack.push(w);
                        marked[w] = true;
                        edgeTo[w] = v;
                        color[w] = wColor;
                    }
                    // Check if the color of the two adjacent vertices is the same
                    else if (color[w] == color[v]) {
                        isBipartite = false;
                        cycle = new Stack<>();
                        cycle.push(w);
                        for (int i = v; i != edgeTo[w]; i = edgeTo[i]) {
                            cycle.push(i);
                        }
                        cycle.push(edgeTo[w]);
                        cycle.push(w);
                        return;
                    }
                }
            }
        }
    }

    public Iterable<Integer> oddCycle() {
        return cycle;
    }

    // Returns the side of the bipartite that vertex v is on.
    public boolean color(int v) {
        if (!isBipartite)
            throw new UnsupportedOperationException("graph is not bipartite");
        return color[v];
    }

    public boolean isBipartite() {
        return isBipartite;
    }

    public static void main(String[] args) {

        Graph G = GraphGenerator.cycle(Integer.parseInt(args[0]));
        System.out.println(G);
        TwoColor c = new TwoColor(G);

        System.out.println("Is the graph bipartite? " + c.isBipartite());
        if (!c.isBipartite())
            System.out.println("First odd cycle detected: " + c.oddCycle());
    }
}
