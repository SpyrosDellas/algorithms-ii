/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 27/05/2020
 *
 * Description:
 * GraphProperties.java calculates the following properties of a graph
 * - The eccentricity of a vertex v, i.e. the length of the shortest path from
 *   v to the furthest vertex from v
 * - The diameter of the graph, i.e the maximum eccentricity of any vertex
 * - The radius of the graph. i.e. the smallest eccentricity of any vertex
 * - The center of the graph, i.e. the set of all vertices of minimum
 *   eccentricity. Equivalently, it is the set of vertices with eccentricity
 *   equal to the graph's radius. Thus vertices in the center (central points)
 *   minimize the maximal distance from other points in the graph.
 * - The girth of the graph, i.e the length of its shortest cycle. If a graph is
 *   acyclic, then its girth is infinite.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Graph;
import edu.princeton.cs.algs4.GraphGenerator;
import edu.princeton.cs.algs4.In;

import java.util.HashSet;


public class GraphProperties {

    private int origin;
    private boolean disconnected;
    private final boolean acyclic;
    private BfsPaths shortestPathsToCenter;
    private int girth;
    private HashSet<Integer> center;
    private int diameter;
    private int radius;


    // 1st constructor; exception if G not connected, origin = 0
    GraphProperties(Graph G) {

        if (G == null)
            throw new NullPointerException("Graph is null");
        if (G.V() == 0)
            throw new IllegalArgumentException("Graph has no vertices");
        if (!isConnected(G))
            throw new IllegalArgumentException("Graph is not connected");

        origin = 0;
        disconnected = false;
        diameter = 0;
        center = new HashSet<>();
        acyclic = isAcyclic(G);

        if (acyclic) {
            girth = Integer.MAX_VALUE;
            acyclicProperties(G);
        }
        else {
            cyclicProperties(G);
        }

    }

    // 2nd constructor; accepts disconnected graphs, finds the properties
    // of the connected component containing 'origin' only
    GraphProperties(Graph G, int origin) {

        if (G == null)
            throw new NullPointerException("Graph is null");
        if (G.V() == 0)
            throw new IllegalArgumentException("Graph has no vertices");

        this.disconnected = !isConnected(G);
        this.origin = origin;
        diameter = 0;
        center = new HashSet<>();
        acyclic = isAcyclic(G);

        if (acyclic) {
            girth = Integer.MAX_VALUE;
            acyclicProperties(G);
        }
        else {
            cyclicProperties(G);
        }

    }

    private void cyclicProperties(Graph G) {

        int[] eccentricity = new int[G.V()];
        radius = Integer.MAX_VALUE;
        girth = Integer.MAX_VALUE;

        DepthFirstCC cc = new DepthFirstCC(G);

        System.out.print("Current node:        ");
        for (int s = 0; s < G.V(); s++) {

            // ignore disconnected vertices
            if (disconnected && !cc.connected(s, origin)) {
                eccentricity[s] = Integer.MAX_VALUE;
                continue;
            }

            System.out.printf("\b\b\b\b\b\b\b%-7d", s);
            BfsPaths paths = new BfsPaths(G, s);

            for (int i = 0; i < G.V(); i++) {

                // ignore vertices that do not belong to the current connected
                // component
                int distTo = paths.distTo(i);
                if (disconnected && distTo == Integer.MAX_VALUE) {
                    continue;
                }

                if (distTo > diameter) {
                    diameter = distTo;
                }
                if (distTo > eccentricity[s]) {
                    eccentricity[s] = distTo;
                }
            }
            if (eccentricity[s] < radius) {
                radius = eccentricity[s];
            }
            if (paths.hasLocalCycle() && paths.minCycle() < girth) {
                girth = paths.minCycle();
            }
        }
        System.out.println();

        for (int s = 0; s < G.V(); s++) {
            if (eccentricity[s] == radius)
                center.add(s);
        }
        shortestPathsToCenter = new BfsPaths(G, center);

    }

    private void acyclicProperties(Graph G) {

        // Find the vertex u furthest from any random vertex
        BfsPaths paths = new BfsPaths(G, origin);
        int maxDistance = 0;
        int u = 0;
        for (int i = 0; i < G.V(); i++) {
            if (disconnected && paths.distTo(i) == Integer.MAX_VALUE) {
                continue;
            }
            if (paths.distTo(i) > maxDistance) {
                maxDistance = paths.distTo(i);
                u = i;
            }
        }

        // find the vertex v furthest from u; its distance to u is the diameter
        // of the graph!
        paths = new BfsPaths(G, u);
        int v = u;
        for (int i = 0; i < G.V(); i++) {
            if (disconnected && paths.distTo(i) == Integer.MAX_VALUE) {
                continue;
            }
            if (paths.distTo(i) > diameter) {
                diameter = paths.distTo(i);
                v = i;
            }
        }
        radius = (diameter + 1) / 2;

        Iterable<Integer> path = paths.pathTo(v);
        for (int vertex : path) {
            if (paths.distTo(vertex) == diameter / 2 ||
                    paths.distTo(vertex) == (diameter + 1) / 2) {
                center.add(vertex);
            }
        }
        shortestPathsToCenter = new BfsPaths(G, center);
    }


    public boolean isAcyclic() {
        return acyclic;
    }

    public boolean isConnected() {
        return !disconnected;
    }

    // eccentricity of vertex v
    public int eccentricity(int v) {
        return shortestPathsToCenter.distTo(v) + radius;
    }

    // diameter of G
    public int diameter() {
        return diameter;
    }

    // radius of G
    public int radius() {
        return radius;
    }

    // a center of G
    public Iterable<Integer> center() {
        return center;
    }

    // the girth of the graph
    public int girth() {
        return girth;
    }


    // is the graph acyclic?
    private boolean isAcyclic(Graph G) {
        Cycles c = new Cycles(G);
        return !c.hasCycle();
    }

    // is the graph connected?
    private boolean isConnected(Graph G) {
        DepthFirstCC cc = new DepthFirstCC(G);
        return cc.count() == 1;
    }


    // unit testing
    public static void main(String[] args) {

        /*
        int v = Integer.parseInt(args[0]);

        System.out.println("STAR GRAPH");
        Graph G = GraphGenerator.star(v);
        System.out.println(G);
        GraphProperties properties = new GraphProperties(G);
        System.out.println("Is the graph acyclic? " + properties.isAcyclic());
        System.out.println("Diameter = " + properties.diameter());
        System.out.println("Radius = " + properties.radius());
        System.out.println("Girth = " + properties.girth());
        System.out.println("Center: " + properties.center());
        System.out.println("Center: " + properties.center());
        for (int i = 0; i < G.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties.eccentricity(i));
        }

        System.out.println("\nTREE GRAPH");
        G = GraphGenerator.tree(v);
        System.out.println(G);
        properties = new GraphProperties(G);
        System.out.println("Is the graph acyclic? " + properties.isAcyclic());
        System.out.println("Diameter = " + properties.diameter());
        System.out.println("Radius = " + properties.radius());
        System.out.println("Girth = " + properties.girth());
        System.out.println("Center: " + properties.center());
        for (int i = 0; i < G.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties.eccentricity(i));
        }
         */

        System.out.println("\ntestG.txt GRAPH");
        In file = new In("testG.txt");
        Graph G3 = new Graph(file);
        System.out.println(G3);
        GraphProperties properties3 = new GraphProperties(G3);
        System.out.println("Is the graph acyclic? " + properties3.isAcyclic());
        System.out.println("Diameter = " + properties3.diameter());
        System.out.println("Radius = " + properties3.radius());
        System.out.println("Girth = " + properties3.girth());
        System.out.println("Center: " + properties3.center());
        for (int i = 0; i < G3.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties3.eccentricity(i));
        }

        System.out.println("\nWHEEL GRAPH");
        Graph G4 = GraphGenerator.wheel(5);
        System.out.println(G4);
        GraphProperties properties4 = new GraphProperties(G4);
        System.out.println("Is the graph acyclic? " + properties4.isAcyclic());
        System.out.println("Diameter = " + properties4.diameter());
        System.out.println("Radius = " + properties4.radius());
        System.out.println("Girth = " + properties4.girth());
        System.out.println("Center: " + properties4.center());
        for (int i = 0; i < G4.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties4.eccentricity(i));
        }

        System.out.println("\nmediumG.txt GRAPH");
        In file5 = new In("mediumG.txt");
        Graph G5 = new Graph(file5);
        GraphProperties properties5 = new GraphProperties(G5);
        System.out.println("Is the graph acyclic? " + properties5.isAcyclic());
        System.out.println("Diameter = " + properties5.diameter());
        System.out.println("Radius = " + properties5.radius());
        System.out.println("Girth = " + properties5.girth());
        System.out.println("Center: " + properties5.center());


        System.out.println("\ntest2G.txt GRAPH");
        In file6 = new In("test2G.txt");
        Graph G6 = new Graph(file6);
        GraphProperties properties6 = new GraphProperties(G6);
        System.out.println("Is the graph acyclic? " + properties6.isAcyclic());
        System.out.println("Diameter = " + properties6.diameter());
        System.out.println("Radius = " + properties6.radius());
        System.out.println("Girth = " + properties6.girth());
        System.out.println("Center: " + properties6.center());

        System.out.println("\nCYCLE GRAPH");
        Graph G7 = GraphGenerator.cycle(5);
        System.out.println(G7);
        GraphProperties properties7 = new GraphProperties(G7);
        System.out.println("Is the graph acyclic? " + properties7.isAcyclic());
        System.out.println("Diameter = " + properties7.diameter());
        System.out.println("Radius = " + properties7.radius());
        System.out.println("Girth = " + properties7.girth());
        System.out.println("Center: " + properties7.center());
        for (int i = 0; i < G7.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties7.eccentricity(i));
        }

        System.out.println("\ntinyG.txt GRAPH");
        In file9 = new In("tinyG.txt");
        Graph G9 = new Graph(file9);
        System.out.println(G9);
        GraphProperties properties9 = new GraphProperties(G9, 0);
        System.out.println("Is the graph acyclic? " + properties9.isAcyclic());
        System.out.println("Diameter = " + properties9.diameter());
        System.out.println("Radius = " + properties9.radius());
        System.out.println("Girth = " + properties9.girth());
        System.out.println("Center: " + properties9.center());
        for (int i = 0; i < G9.V(); i++) {
            System.out.println("Eccentricity of vertex " + i + ": " + properties9.eccentricity(i));
        }

        System.out.println("\nlargeG.txt GRAPH");
        In file8 = new In("largeG.txt");
        Graph G8 = new Graph(file8);
        GraphProperties properties8 = new GraphProperties(G8);
        System.out.println("Is the graph acyclic? " + properties8.isAcyclic());
        System.out.println("Diameter = " + properties8.diameter());
        System.out.println("Radius = " + properties8.radius());
        System.out.println("Girth = " + properties8.girth());
        System.out.println("Center: " + properties8.center());

    }

}
