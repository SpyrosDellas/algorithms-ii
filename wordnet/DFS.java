/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 26/05/2020
 *  Description: Connectivity search implementation using recursive dfs
 *
 * Note:
 * Stack overflows for large graphs
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class DFS {

    private boolean[] marked;
    private int count;          // number of vertices connected to s

    public DFS(UGraph G, int s) {
        marked = new boolean[G.V()];
        dfs(G, s);
    }

    private void dfs(UGraph G, int v) {
        marked[v] = true;
        count++;
        for (int w : G.adj(v)) {
            if (!marked[w]) {
                dfs(G, w);
            }
        }
    }


    // is v connected to s?
    public boolean marked(int v) {
        return marked[v];
    }

    // how many vertices are connected to s?
    public int count() {
        return count;
    }


    public static void main(String[] args) {
        UGraph G = new UGraph(new In(args[0]));
        int s = Integer.parseInt(args[1]);
        DFS search = new DFS(G, s);

        for (int v = 0; v < G.V(); v++)
            if (search.marked(v))
                StdOut.print(v + " ");
        StdOut.println();

        if (search.count() != G.V())
            StdOut.print("NOT ");
        StdOut.println("connected");
    }
}
