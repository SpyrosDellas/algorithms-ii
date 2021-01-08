/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 18/06/2020
 *
 * Description:
 * Implements the classic Ford-Fulkerson algorithm to find the MaxFlow and
 * MinCut in a capacitated flow network, using the shortest augmenting paths
 * heuristic
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;


public class FordFulkersonMaxFlow {

    private static final double FLOATING_POINT_EPSILON = 1.0E-11;

    private int V;                // Number of vertices in the flow network
    private boolean[] marked;     // marked[v] = true iff s->v path in residual graph
    private FlowEdge[] edgeTo;    // edgeTo[v] = last edge on shortest residual s->v path
    private double value;         // maxFlow value


    public FordFulkersonMaxFlow(FlowNetwork fn, int s, int t) {

        V = fn.V();
        validate(s);
        validate(t);

        if (s == t)
            throw new IllegalArgumentException("Source equals sink");

        if (!isFeasible(fn, s, t))
            throw new IllegalArgumentException("Initial flow is infeasible");

        // While there exists an augmenting path, use it
        while (hasAugmentingPath(fn, s, t)) {

            // Compute bottleneck capacity
            double bottleneck = Double.POSITIVE_INFINITY;
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                double residual = edgeTo[v].residualCapacityTo(v);
                if (residual < bottleneck) {
                    bottleneck = residual;
                }
            }
            for (int v = t; v != s; v = edgeTo[v].other(v)) {
                edgeTo[v].addResidualFlowTo(v, bottleneck);
            }
        }

        value = netFlow(fn, t);
    }


    // is there an augmenting path s -> t? if so, upon termination edgeTo[] will
    // contain a parent-link representation of such a path
    // this implementation finds a shortest augmenting path (fewest number of
    // edges), which performs well both in theory and in practice
    private boolean hasAugmentingPath(FlowNetwork fn, int s, int t) {

        marked = new boolean[V];
        edgeTo = new FlowEdge[V];
        Queue<Integer> q = new Queue<>();

        q.enqueue(s);
        marked[s] = true;

        while (!q.isEmpty()) {
            int v = q.dequeue();
            for (FlowEdge e : fn.adj(v)) {
                int w = e.other(v);
                if (!marked[w] && e.residualCapacityTo(w) > FLOATING_POINT_EPSILON) {
                    q.enqueue(w);
                    marked[w] = true;
                    edgeTo[w] = e;
                }
            }
        }

        return marked[t];
    }


    // Returns the value of the maximum flow
    public double value() {
        return value;
    }


    // Returns true if the specified vertex is on the s side of the minCut
    public boolean inCut(int v) {
        validate(v);
        return marked[v];
    }


    // throw an IllegalArgumentException if v is outside prescribed range
    private void validate(int v) {
        if (v < 0 || v >= V)
            throw new IllegalArgumentException("vertex " + v + " is not between 0 and " + (V - 1));
    }


    // Calculate net flow at vertex v; net flow should be:
    // - positive if net inflow (e.g. sink)
    // - negative if net outflow (e.g. source)
    // - zero for any other vertex
    private double netFlow(FlowNetwork fn, int v) {

        double netflow = 0.0;
        for (FlowEdge e : fn.adj(v)) {
            if (e.to() == v)
                netflow += e.flow();
            else
                netflow -= e.flow();
        }
        return netflow;
    }


    // Check that flow on each edge is non-negative and not greater than the
    // edge's capacity
    private boolean isFeasible(FlowNetwork fn, int s, int t) {

        for (int v = 0; v < fn.V(); v++) {

            // check that capacity constraints are satisfied
            for (FlowEdge e : fn.adj(v)) {
                if (e.flow() < -FLOATING_POINT_EPSILON
                        || e.flow() > e.capacity() + FLOATING_POINT_EPSILON) {
                    System.err.println("Edge does not satisfy capacity constraints: " + e);
                    return false;
                }
            }

            double net = netFlow(fn, v);
            // check that net flow into a vertex equals zero, except at source and sink
            //
            // net outflow at source should be equal to the value of maxFlow
            if (v == s && Math.abs(value + net) > FLOATING_POINT_EPSILON) {
                System.err.println("Outflow at source = " + net);
                System.err.println("Max flow          = " + value);
                return false;
            }
            // net inflow at sink should be equal to the value of maxFlow
            else if (v == t && Math.abs(value - net) > FLOATING_POINT_EPSILON) {
                System.err.println("Inflow at sink = " + net);
                System.err.println("Max flow       = " + value);
                return false;
            }
            else if (Math.abs(net) > FLOATING_POINT_EPSILON) {
                System.err.println("Net flow out of " + v + " doesn't equal zero");
                return false;
            }
        }

        return true;
    }


    public static void main(String[] args) {

        String filename = "tinyFN.txt";
        In in = new In(filename);
        FlowNetwork fn = new FlowNetwork(in);

        int s = 0;
        int t = fn.V() - 1;
        FordFulkersonMaxFlow ff = new FordFulkersonMaxFlow(fn, s, t);

        System.out.println("Max flow value = " + ff.value());

        System.out.println("Max flow from " + s + " to " + t + ":");
        for (int v = 0; v < fn.V(); v++) {
            for (FlowEdge e : fn.adj(v)) {
                if (e.from() == v)
                    System.out.println(e);
            }
        }
    }
}
