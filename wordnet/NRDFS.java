/* *****************************************************************************
 *  Name: Spyridon Theodoros Dellas
 *  Date: 26/05/2020
 *  Description: Connectivity search implementation using non-recursive dfs
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

public class NRDFS {

    private boolean[] marked;
    private int count;          // number of vertices connected to s

    public NRDFS(UGraph G, int s) {

        if (G.V() == 0)
            throw new IllegalArgumentException("Graph is empty");

        marked = new boolean[G.V()];
        nrdfs(G, s);
    }

    private void nrdfs(UGraph G, int s) {
        Stack<Integer> vertexStack = new Stack<>();
        vertexStack.push(s);
        marked[s] = true;
        count++;

        while (!vertexStack.isEmpty()) {
            int v = vertexStack.pop();
            for (int w : G.adj(v)) {
                if (!marked[w]) {
                    vertexStack.push(w);
                    marked[w] = true;
                    count++;
                }
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
        NRDFS search = new NRDFS(G, s);

        for (int v = 0; v < Math.min(50, G.V()); v++) {
            if (search.marked(v))
                StdOut.print(v + " ");
        }
        if (G.V() > 50) System.out.print("...");
        StdOut.println();


        if (search.count() != G.V())
            StdOut.print("NOT ");
        StdOut.println("connected");

        System.out.println("A total of " + search.count() +
                                   " vertices are connected to vertex " + s);
    }
}
