/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 26/05/2020
 *  Description: Single-source paths implementation using non-recursive dfs
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class NRdfsPaths {

    private boolean[] marked;
    private int[] edgeTo;       // last vertex on known path to this vertex
    private int s;              // store s to support pathTo() queries

    public NRdfsPaths(UGraph G, int s) {
        marked = new boolean[G.V()];
        edgeTo = new int[G.V()];
        this.s = s;
        dfs(G, s);
    }

    private void dfs(UGraph G, int s) {
        Stack<Integer> vertexStack = new Stack<>();
        vertexStack.push(s);
        marked[s] = true;

        while (!vertexStack.isEmpty()) {
            int v = vertexStack.pop();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    vertexStack.push(w);
                    edgeTo[w] = v;
                    marked[w] = true;
                }
            }

        }
    }

    // path from s to v; null if no such path
    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v))
            return null;
        Stack<Integer> path = new Stack<>();
        for (int vertex = v; vertex != s; vertex = edgeTo[vertex]) {
            path.push(vertex);
        }
        path.push(s);
        return path;
    }

    // is there a path from s to v?
    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public static void main(String[] args) {
        UGraph G = new UGraph(new In(args[0]));
        int s = Integer.parseInt(args[1]);
        int v = Integer.parseInt(args[2]);
        NRdfsPaths search = new NRdfsPaths(G, s);

        StdOut.print(s + " to " + v + ": ");
        if (search.hasPathTo(v))
            for (int x : search.pathTo(v))
                if (x == s) StdOut.print(x);
                else StdOut.print("-" + x);
        StdOut.println();
    }
}
