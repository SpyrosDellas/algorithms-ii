/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 27/05/2020
 *
 * Description:
 * Analysis of the bipartite graph from movies.txt
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Stack;

public class MoviesAnalysis {

    private SymbolGraph sg;             // the symbol graph
    private boolean hasCycle;           // is there a cycle in the graph?
    private int count;                  // number of connected components
    private boolean isConnected;        // is the graph connected?
    private int V;                      // number of vertices
    private int E;                      // number of edges
    private int[] id;


    public MoviesAnalysis(String file, String delimiter) {
        sg = new SymbolGraph(file, delimiter);
        V = sg.graph().V();
        E = sg.graph().E();
    }

    public void analyse() {
        if (sg.graph().V() == 0)
            throw new IllegalArgumentException("Graph is empty");

        cc();
        cycles();
    }


    // Compute the size of the largest component, and the number of components
    // of size less than 10.
    // Find the eccentricity, diameter, radius, a center, and the girth of the
    // largest component in the graph.
    // Does it contain Kevin Bacon?
    public void analyseBacon() {

        int[] sizes = new int[count];
        int largestID = 0;
        int largestSize = 0;
        int lessThan10 = 0;

        for (int v = 0; v < V; v++) {
            sizes[id[v]]++;
        }
        for (int i = 0; i < count; i++) {
            if (sizes[i] > largestSize) {
                largestID = i;
                largestSize = sizes[i];
            }
            if (sizes[i] < 10) {
                lessThan10++;
            }
        }

        System.out.println("Largest component id: " + largestID);
        System.out.println("Largest component size: " + largestSize);
        System.out.println("Components of size less than 10: " + lessThan10);
        int baconV = sg.indexOf("Bacon, Kevin");

        if (id[baconV] == largestID) {
            System.out.println("The largest component contains Kevin Bacon");
        }
        else {
            System.out.println("The largest component DOES NOT contain Kevin Bacon");
        }

        System.out.println("Analysing the largest component...");

        GraphProperties properties = new GraphProperties(sg.graph(), baconV);
        System.out.println("Diameter of largest component = " + properties.diameter());
        System.out.println("Radius of largest component = " + properties.radius());
        System.out.println("Girth of largest component = " + properties.girth());
        System.out.println("Center: " + properties.center());
    }


    // is the graph acyclic?
    private void cycles() {
        Cycles c = new Cycles(sg.graph());
        this.hasCycle = c.hasCycle();
    }


    // is the graph connected?
    private void cc() {

        count = 0;
        id = new int[V];
        boolean[] marked = new boolean[V];

        for (int s = 0; s < V; s++) {
            // If vertex s belongs already to a connected component ignore it
            if (marked[s]) continue;

            // Find all vertices connected to s and update marked[] and id[]
            Stack<Integer> vertexStack = new Stack<>();
            vertexStack.push(s);
            marked[s] = true;
            id[s] = count;
            while (!vertexStack.isEmpty()) {
                int v = vertexStack.pop();
                for (int w : sg.graph().adj(v)) {
                    if (!marked[w]) {
                        vertexStack.push(w);
                        marked[w] = true;
                        id[w] = count;
                    }
                }
            }
            count++;
        }

        isConnected = (count == 1);
    }


    public int getE() {
        return E;
    }


    public int getV() {
        return V;
    }


    public int getcComponents() {
        return count;
    }


    public boolean isConnected() {
        return isConnected;
    }


    public boolean hasCycle() {
        return hasCycle;
    }


    public static void main(String[] args) {

        String file = "movies.txt";
        String delimiter = "/";
        System.out.println("Generating bipartite graph from 'movies.txt'...");
        MoviesAnalysis movies = new MoviesAnalysis(file, delimiter);

        System.out.println("\nAnalysing graph...");
        movies.analyse();
        System.out.println("Number of vertices = " + movies.getV());
        System.out.println("Number of edges = " + movies.getE());
        System.out.println("Is the graph connected? " + movies.isConnected());
        System.out.println("Number of connected components = " + movies.getcComponents());
        System.out.println("Is the graph acyclic? " + !movies.hasCycle());

        System.out.println("\nRunning Kevin Bacon analysis...");
        movies.analyseBacon();
    }
}
