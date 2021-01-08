/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 27/05/2020
 *
 * Description:
 * This class represents a data type for determining whether an undirected
 * graph has a simple cycle.
 *
 * This implementation uses depth-first search.
 *
 * The depth-first search part takes only O(V) time; however, checking for
 * self-loops and parallel edges takes Theta(V + E) time in the worst case.
 * Each instance method takes Theta(1) time.
 *
 * It uses Theta(V) extra space (not including the graph).
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.Stack;

public class Cycles {

    private int[] edgeTo;
    private Stack<Integer> cycle;

    public Cycles(Graph G) {
        edgeTo = new int[G.V()];
        dfs(G);
    }

    private void dfs(Graph G) {

        boolean[] marked = new boolean[G.V()];

        // Scan through all vertices as the graph might be disconnected
        for (int s = 0; s < G.V(); s++) {
            // If vertex s has been examined already ignore it
            if (marked[s]) continue;

            // Find all vertices connected to s and scan for cycles
            Stack<Integer> vertexStack = new Stack<>();
            vertexStack.push(s);
            marked[s] = true;
            edgeTo[s] = s;
            while (!vertexStack.isEmpty()) {
                int v = vertexStack.pop();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        vertexStack.push(w);
                        marked[w] = true;
                        edgeTo[w] = v;
                    }
                    // Check for self-loops || normal loops || parallel edges
                    else if (w == v || w != edgeTo[v] || edgeTo[w] == v) {
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

    // Returns a cycle if the graph has a cycle and null otherwise
    public Iterable<Integer> cycle() {
        return cycle;
    }

    public boolean hasCycle() {
        return cycle != null;
    }

    public static void main(String[] args) {

        Graph G = GraphGenerator.cycle(Integer.parseInt(args[0]));
        System.out.println(G);
        Cycles c = new Cycles(G);

        System.out.println("Is there a cycle in the graph? " + c.hasCycle());
        if (c.hasCycle())
            System.out.println("First cycle detected: " + c.cycle());

        Graph G1 = GraphGenerator.tree(10);
        System.out.println("\n" + G1);
        Cycles c1 = new Cycles(G1);

        System.out.println("Is there a cycle in the graph? " + c1.hasCycle());
        if (c1.hasCycle())
            System.out.println("First cycle detected: " + c1.cycle());
    }
}
