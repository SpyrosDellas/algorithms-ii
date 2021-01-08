/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 27/05/2020
 *
 * Description:
 * This class represents a data type for computing connected components using
 * non-recursive depth first search.
 * Runs in O(E + V) time.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.Stack;

import java.util.Iterator;

public class DepthFirstCC {

    private int count;
    private int[] id;

    // preprocessing constructor
    public DepthFirstCC(Graph G) {
        count = 0;
        id = new int[G.V()];
        dfs(G);
    }

    private void dfs(Graph G) {

        boolean[] marked = new boolean[G.V()];

        for (int s = 0; s < G.V(); s++) {
            // If vertex s belongs already to a connected component ignore it
            if (marked[s]) continue;

            // Find all vertices connected to s and update marked[] and id[]
            Stack<Integer> vertexStack = new Stack<>();
            vertexStack.push(s);
            marked[s] = true;
            id[s] = count;
            while (!vertexStack.isEmpty()) {
                int v = vertexStack.pop();
                for (int w : G.adj(v)) {
                    if (!marked[w]) {
                        vertexStack.push(w);
                        marked[w] = true;
                        id[w] = count;
                    }
                }
            }
            count++;
        }
    }

    // are v and w connected?
    public boolean connected(int v, int w) {
        return id[v] == id[w];
    }

    // number of connected components
    public int count() {
        return count;
    }

    //  component identifier for v: between 0 and count()-1
    public int id(int v) {
        return id[v];
    }


    public static void main(String[] args) {

        Graph G = new Graph(new In(args[0]));
        DepthFirstCC cc = new DepthFirstCC(G);

        int nCC = cc.count();
        System.out.println("The graph has a total of: " + nCC + " components");

        Queue<Integer>[] components = (Queue<Integer>[]) new Queue[nCC];
        for (int i = 0; i < nCC; i++)
            components[i] = new Queue<Integer>();
        for (int v = 0; v < G.V(); v++)
            components[cc.id(v)].enqueue(v);

        for (int i = 0; i < nCC; i++) {
            System.out.println("Component id = " + i + ", length = " + components[i].size() + ":");
            Iterator<Integer> iter = components[i].iterator();
            for (int j = 0; j < Math.min(50, components[i].size()); j++) {
                System.out.print(iter.next() + " ");
            }
            if (components[i].size() > 50) System.out.print("...");
            System.out.println();
        }
    }
}
