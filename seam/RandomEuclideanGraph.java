/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 10/06/2020
 *
 * Description:
 * Generates a random fully connected edge-weighted undirected Euclidean graph
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.Edge;
import edu.princeton.cs.algs4.EdgeWeightedGraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import javax.swing.WindowConstants;
import java.awt.Color;
import java.util.Arrays;
import java.util.Random;


public class RandomEuclideanGraph {

    private int V;                // Number of vertices in the graph
    private int E;                // Number of edges in the graph
    private double threshold;        // threshold value for a fully connected graph
    private double[] x;              // x coordinates of the points
    private double[] y;              // y coordinates of the points
    private double[][] distances;    // table of pairwise distances
    private boolean[][] edges;       // true if there is a pairwise edge


    public RandomEuclideanGraph(int size) {

        V = size;
        threshold = Math.sqrt((Math.log(V) / Math.log(2)) / (Math.PI * V));

        while (!isConnected()) {
            x = new double[size];
            y = new double[size];
            distances = new double[size][size];
            edges = new boolean[size][size];
            E = 0;
            generatePoints();
            calculateDistances();
            generateEdges();
        }
    }


    public RandomEuclideanGraph(In in) {

        String[] rows = in.readAllLines();
        V = Integer.parseInt(rows[0]);
        E = Integer.parseInt(rows[1]);

        x = new double[V];
        y = new double[V];
        distances = new double[V][V];
        edges = new boolean[V][V];

        for (int row = 2; row < E + 2; row++) {
            String[] line = rows[row].split(" ");
            int i = Integer.parseInt(line[0]);
            int j = Integer.parseInt(line[1]);
            double distance = Double.parseDouble(line[2]);
            edges[i][j] = true;
            distances[i][j] = distance;
        }

        int counter = 0;
        for (int row = E + 2; row < rows.length; row++) {
            String[] line = rows[row].split(" ");
            double xCoord = Double.parseDouble(line[0]);
            double yCoord = Double.parseDouble(line[1]);
            x[counter] = xCoord;
            y[counter] = yCoord;
            counter++;
        }

    }

    private boolean isConnected() {

        if (edges == null)
            return false;

        UnionFind uf = new UnionFind(V);
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j])
                    uf.union(i, j);
            }
        }
        return uf.count() == 1;
    }


    private void generatePoints() {
        Random random = new Random();
        for (int i = 0; i < V; i++) {
            x[i] = random.nextDouble();
            y[i] = random.nextDouble();
        }
    }


    private void calculateDistances() {
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                double dx2 = x[i] - x[j];
                dx2 *= dx2;
                double dy2 = y[i] - y[j];
                dy2 *= dy2;
                double distance = Math.sqrt(dx2 + dy2);
                distances[i][j] = distance;
            }
        }
    }


    private void generateEdges() {
        double edgeFactor = 1.0 * threshold;
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (distances[i][j] < edgeFactor) {
                    edges[i][j] = true;
                    E++;
                }
            }
        }
    }


    public Draw plot() {

        Draw plot = new Draw();
        plot.enableDoubleBuffering();
        plot.setCanvasSize(900, 900);
        plot.setXscale(-0.05, 1.05);
        plot.setYscale(-0.05, 1.05);

        // Draw the points
        plot.setPenColor(Color.BLUE);
        plot.setPenRadius(0.015);
        for (int i = 0; i < V; i++) {
            plot.point(x[i], y[i]);
        }

        // Draw the edges
        plot.setPenColor(Color.GRAY);
        plot.setPenRadius(0.001);
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j]) {
                    plot.line(x[i], y[i], x[j], y[j]);
                }
            }
        }

        // reset pen
        plot.setPenColor();
        plot.setPenRadius();
        plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        return plot;
    }


    // Generate the EdgeWeightedGraph object representation of the graph
    public EdgeWeightedGraph graph() {

        EdgeWeightedGraph G = new EdgeWeightedGraph(V);
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j]) {
                    Edge e = new Edge(i, j, distances[i][j]);
                    G.addEdge(e);
                }
            }
        }

        return G;
    }


    public double[] getX() {
        return Arrays.copyOf(x, x.length);
    }


    public double[] getY() {
        return Arrays.copyOf(y, y.length);
    }


    // Save the graph
    public void saveAs(String filename) {

        StringBuilder sb = new StringBuilder();

        // Save number of vertices and edges
        sb.append(V + "\n");
        sb.append(E + "\n");

        // Save the edges
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j]) {
                    sb.append(i + " " + j + " " + distances[i][j] + "\n");
                }
            }
        }

        // Save the coordinates of the points
        for (int i = 0; i < V; i++) {
            sb.append(x[i] + " " + y[i] + "\n");
        }

        // Generate file
        Out out = new Out(filename);
        out.print(sb.toString());
        out.close();
    }


    // test client
    public static void main(String[] args) {

        int size = Integer.parseInt(args[0]);
        RandomEuclideanGraph euclidean = new RandomEuclideanGraph(size);

        EdgeWeightedGraph graph = euclidean.graph();
        System.out.println("Edge-weighted random euclidean graph created.");
        System.out.println("Number of vertices = " + graph.V());
        System.out.println("Number of edges = " + graph.E());

        Draw plot = euclidean.plot();
        plot.show();

        String filename = "euclidean" + size + ".txt";
        euclidean.saveAs(filename);


        // Import saved graph to verify correctness
        In in = new In(filename);
        RandomEuclideanGraph euclidean1 = new RandomEuclideanGraph(in);
        EdgeWeightedGraph graph1 = euclidean1.graph();
        System.out.println("Edge-weighted random euclidean graph created.");
        System.out.println("Number of vertices = " + graph1.V());
        System.out.println("Number of edges = " + graph1.E());

        Draw plot1 = euclidean1.plot();
        plot1.show();

    }
}
