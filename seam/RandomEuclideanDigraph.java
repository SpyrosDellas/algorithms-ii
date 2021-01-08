/* *****************************************************************************
 * Name: Spyridon Theodors Dellas
 * Date: 13/06/2020
 *
 * Description:
 * Generates a random edge-weighted directed Euclidean graph
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.DirectedEdge;
import edu.princeton.cs.algs4.Draw;
import edu.princeton.cs.algs4.EdgeWeightedDigraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

import javax.swing.WindowConstants;
import java.awt.Color;
import java.util.Arrays;
import java.util.Random;


public class RandomEuclideanDigraph {

    private static final double PI = Math.PI;
    private static final double ARROW_ANGLE = PI / 12;
    private static final double ARROW_SIDE_LENGTH = 0.015;

    private int V;                // Number of vertices in the graph
    private int E;                // Number of directed edges in the graph

    private double threshold;        // threshold value for a fully connected graph

    // If less than 1, the graph is not connected; if more or equal to one the
    // graph is connected, but NOT STRONGLY CONNECTED
    private double connectionRatio;

    private double directedRatio;    // ratio of directed to undirected edges

    private double[] x;              // x coordinates of the points
    private double[] y;              // y coordinates of the points

    private double[][] distances;    // table of pairwise distances
    private boolean[][] edges;       // true if there is a directed edge from i to j


    public RandomEuclideanDigraph(int size, double connectionRatio, double directedRatio) {

        V = size;
        threshold = Math.sqrt((Math.log(V) / Math.log(2)) / (Math.PI * V));
        this.connectionRatio = connectionRatio;
        this.directedRatio = directedRatio;

        if (connectionRatio >= 1.0) {
            generateConnectedDigraph();
        }
        else {
            generateUnconnectedDigraph();
        }
    }


    public RandomEuclideanDigraph(In in) {

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


    private void generateConnectedDigraph() {

        while (!isConnected()) {
            x = new double[V];
            y = new double[V];
            distances = new double[V][V];
            edges = new boolean[V][V];
            E = 0;
            generatePoints();
            calculateDistances();
            generateEdges();
        }
    }


    private void generateUnconnectedDigraph() {

        while (true) {
            x = new double[V];
            y = new double[V];
            distances = new double[V][V];
            edges = new boolean[V][V];
            E = 0;
            generatePoints();
            calculateDistances();
            generateEdges();
            if (!isConnected())
                break;
        }
    }


    private boolean isConnected() {

        if (edges == null)
            return false;

        UnionFind uf = new UnionFind(V);
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j] || edges[j][i])
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
                distances[j][i] = distance;
            }
        }
    }


    private void generateEdges() {

        Random random = new Random();
        double radius = connectionRatio * threshold;
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (distances[i][j] < radius) {
                    double prob = random.nextDouble();
                    if (prob < directedRatio) {
                        edges[i][j] = true;
                        edges[j][i] = true;
                        E += 2;
                    }
                    else {
                        double dir = random.nextDouble();
                        if (dir < 0.5) {
                            edges[i][j] = true;
                        }
                        else {
                            edges[j][i] = true;
                        }
                        E++;
                    }
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
        for (int i = 0; i < V; i++) {
            for (int j = i + 1; j < V; j++) {
                if (edges[i][j] && edges[j][i]) {
                    plotUndirectedLine(plot, i, j);
                }
                else if (edges[i][j]) {
                    plotDirectedLine(plot, i, j);
                }
                else if (edges[j][i]) {
                    plotDirectedLine(plot, j, i);
                }
            }
        }

        // reset pen
        plot.setPenColor();
        plot.setPenRadius();

        plot.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        return plot;
    }


    private void plotUndirectedLine(Draw plot, int i, int j) {

        plot.setPenRadius(0.002);
        plot.setPenColor(Color.BLUE);
        plot.line(x[i], y[i], x[j], y[j]);

        double theta = Math.atan2(y[j] - y[i], x[j] - x[i]);
        plotArrow(plot, x[i], y[i], theta);
        plotArrow(plot, x[j], y[j], PI + theta);
    }


    private void plotDirectedLine(Draw plot, int i, int j) {

        plot.setPenRadius(0.001);
        plot.setPenColor(Color.RED);
        plot.line(x[i], y[i], x[j], y[j]);

        double theta = Math.atan2(y[j] - y[i], x[j] - x[i]);
        plotArrow(plot, x[j], y[j], PI + theta);
    }


    private void plotArrow(Draw plot, double x, double y, double theta) {

        double[] xCoords = new double[3];
        double[] yCoords = new double[3];

        double x1 = x + (ARROW_SIDE_LENGTH) * Math.cos(theta - ARROW_ANGLE);
        double y1 = y + (ARROW_SIDE_LENGTH) * Math.sin(theta - ARROW_ANGLE);

        double x2 = x + (ARROW_SIDE_LENGTH) * Math.cos(theta + ARROW_ANGLE);
        double y2 = y + (ARROW_SIDE_LENGTH) * Math.sin(theta + ARROW_ANGLE);

        xCoords[0] = x;
        xCoords[1] = x1;
        xCoords[2] = x2;
        yCoords[0] = y;
        yCoords[1] = y1;
        yCoords[2] = y2;

        plot.filledPolygon(xCoords, yCoords);
    }


    // Generate the EdgeWeightedDigraph object representation of the graph
    public EdgeWeightedDigraph graph() {

        EdgeWeightedDigraph G = new EdgeWeightedDigraph(V);
        for (int i = 0; i < V; i++) {
            for (int j = 0; j < V; j++) {
                if (edges[i][j]) {
                    DirectedEdge e = new DirectedEdge(i, j, distances[i][j]);
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
            for (int j = 0; j < V; j++) {
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
        double connectionRatio = Double.parseDouble(args[1]);
        double directedRatio = Double.parseDouble(args[2]);

        RandomEuclideanDigraph euclidean = new RandomEuclideanDigraph(size, connectionRatio,
                                                                      directedRatio);

        EdgeWeightedDigraph graph = euclidean.graph();
        System.out.println("Edge-weighted random euclidean digraph created.");
        System.out.println("Number of vertices = " + graph.V());
        System.out.println("Number of directed edges = " + graph.E());

        Draw plot = euclidean.plot();
        plot.show();

        String filename = "euclideanDigraph" + size + ".txt";
        euclidean.saveAs(filename);

        // Import saved graph to verify correctness
        In in = new In(filename);
        RandomEuclideanDigraph euclidean1 = new RandomEuclideanDigraph(in);
        EdgeWeightedDigraph graph1 = euclidean1.graph();
        System.out.println("Edge-weighted random euclidean graph created.");
        System.out.println("Number of vertices = " + graph1.V());
        System.out.println("Number of edges = " + graph1.E());

        Draw plot1 = euclidean1.plot();
        plot1.show();

    }
}
