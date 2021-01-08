/* *****************************************************************************
 * Name: Spyridon Theodoros Dellas
 * Date: 02/06/2020
 *
 * Description:
 * Shortest Ancestral Path (SAP).
 * An ancestral path between two vertices v and
 * w in a digraph is a directed path from v to a common ancestor x, together
 * with a directed path from w to the same ancestor x. A shortest ancestral path
 * is an ancestral path of minimum total length. We refer to the common ancestor
 * in a shortest ancestral path as a shortest common ancestor.
 *
 * We also generalize the notion of shortest common ancestor to subsets of
 * vertices. A shortest ancestral path of two subsets of vertices A and B is a
 * shortest ancestral path over all pairs of vertices v and w, with v in A and
 * w in B.
 *
 * Corner cases.
 * Throws an IllegalArgumentException in the following situations:
 * - Any argument is null
 * - Any vertex argument is outside its prescribed range
 * - Any iterable argument contains a null item
 *
 * Performance requirements.
 * - All methods (and the constructor) take time at most proportional to E + V in
 * the worst case, where E and V are the number of edges and vertices in the
 * digraph, respectively.
 * - The data type uses space proportional to E + V.
 *
 **************************************************************************** */

import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.ResizingArrayQueue;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdIn;

import java.util.HashMap;
import java.util.HashSet;


public final class SAP {

    private final Digraph graph;
    private final int V;

    private int cV = -1;
    private int cW = -1;
    private int cL;
    private int cA;

    private Iterable<Integer> cachedV;
    private Iterable<Integer> cachedW;
    private int cachedLength;
    private int cachedAncestor;

    private boolean[] marked;


    private class AncestralPaths {

        private int length = -1;
        private int ancestor = -1;

        private HashSet<Integer> markedV = new HashSet<>();
        private HashSet<Integer> markedW = new HashSet<>();
        private HashMap<Integer, Integer> distToV = new HashMap<>();
        private HashMap<Integer, Integer> distToW = new HashMap<>();


        public AncestralPaths(Digraph g, Iterable<Integer> v, Iterable<Integer> w) {
            bfspaths(g, v, w);
        }

        private void bfspaths(Digraph g, Iterable<Integer> v, Iterable<Integer> w) {

            ResizingArrayQueue<Integer> frontierV = new ResizingArrayQueue<>();
            int vDistance = 0;
            for (int i : v) {
                markedV.add(i);
                distToV.put(i, 0);
                frontierV.enqueue(i);
            }

            ResizingArrayQueue<Integer> frontierW = new ResizingArrayQueue<>();
            int wDistance = 0;
            for (int i : w) {
                if (markedV.contains(i)) {
                    length = 0;
                    ancestor = i;
                    return;
                }
                markedW.add(i);
                distToW.put(i, 0);
                frontierW.enqueue(i);
            }

            boolean terminateV = false;
            boolean terminateW = false;
            while (!frontierV.isEmpty() || !frontierW.isEmpty()) {

                if (length != -1 && vDistance >= length) {
                    terminateV = true;
                }
                if (length != -1 && wDistance >= length) {
                    terminateW = true;
                }
                if (terminateV && terminateW)
                    break;

                while (!terminateV && !frontierV.isEmpty()
                        && distToV.get(frontierV.peek()) == vDistance) {
                    int a = frontierV.dequeue();
                    for (int b : g.adj(a)) {
                        if (markedV.contains(b)) {
                            continue;
                        }
                        frontierV.enqueue(b);
                        markedV.add(b);
                        distToV.put(b, distToV.get(a) + 1);
                        if (!markedW.contains(b)) {
                            continue;
                        }
                        int thisLength = distToV.get(b) + distToW.get(b);
                        if (length == -1) {
                            length = thisLength;
                            ancestor = b;
                        }
                        else if (thisLength < length) {
                            length = thisLength;
                            ancestor = b;
                        }
                    }
                }
                vDistance++;

                while (!terminateW && !frontierW.isEmpty()
                        && distToW.get(frontierW.peek()) == wDistance) {
                    int c = frontierW.dequeue();
                    for (int d : g.adj(c)) {
                        if (markedW.contains(d)) {
                            continue;
                        }
                        frontierW.enqueue(d);
                        markedW.add(d);
                        distToW.put(d, distToW.get(c) + 1);
                        if (!markedV.contains(d)) {
                            continue;
                        }
                        int thisLength = distToV.get(d) + distToW.get(d);
                        if (length == -1) {
                            length = thisLength;
                            ancestor = d;
                        }
                        else if (thisLength < length) {
                            length = thisLength;
                            ancestor = d;
                        }
                    }
                }
                wDistance++;

            }

        }

    }


    // constructor takes a digraph (not necessarily a DAG)
    public SAP(Digraph G) {

        if (G == null)
            throw new IllegalArgumentException();

        graph = new Digraph(G);
        V = graph.V();
    }


    // length of shortest ancestral path between v and w; -1 if no such path
    public int length(int v, int w) {

        if (!checkVertices(v, w))
            throw new IllegalArgumentException();

        if ((v == cV && w == cW) || (v == cW && w == cV))
            return cL;

        HashSet<Integer> vNode = new HashSet<>();
        vNode.add(v);
        HashSet<Integer> wNode = new HashSet<>();
        wNode.add(w);

        AncestralPaths paths = new AncestralPaths(graph, vNode, wNode);
        cV = v;
        cW = w;
        cL = paths.length;
        cA = paths.ancestor;

        return cL;
    }


    // a common ancestor of v and w that participates in a shortest ancestral
    // path; -1 if no such path
    public int ancestor(int v, int w) {

        if (!checkVertices(v, w))
            throw new IllegalArgumentException();

        if ((v == cV && w == cW) || (v == cW && w == cV))
            return cA;

        HashSet<Integer> vNode = new HashSet<>();
        vNode.add(v);
        HashSet<Integer> wNode = new HashSet<>();
        wNode.add(w);

        AncestralPaths paths = new AncestralPaths(graph, vNode, wNode);
        cV = v;
        cW = w;
        cL = paths.length;
        cA = paths.ancestor;

        return cA;
    }


    // length of shortest ancestral path between any vertex in v and any vertex
    // in w; -1 if no such path
    public int length(Iterable<Integer> v, Iterable<Integer> w) {

        if (!checkVertices(v) || !checkVertices(w)) {
            throw new IllegalArgumentException();
        }

        if ((v.equals(cachedV) && w.equals(cachedW)) || (v.equals(cachedW) && w.equals(cachedV))) {
            return cachedLength;
        }

        AncestralPaths paths = new AncestralPaths(graph, v, w);
        cachedV = v;
        cachedW = w;
        cachedLength = paths.length;
        cachedAncestor = paths.ancestor;

        return cachedLength;
    }


    // a common ancestor that participates in shortest ancestralpath;
    // -1 if no such path
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {

        if (!checkVertices(v) || !checkVertices(w)) {
            throw new IllegalArgumentException();
        }

        if ((v.equals(cachedV) && w.equals(cachedW)) || (v.equals(cachedW) && w.equals(cachedV))) {
            return cachedAncestor;
        }

        AncestralPaths paths = new AncestralPaths(graph, v, w);
        cachedV = v;
        cachedW = w;
        cachedLength = paths.length;
        cachedAncestor = paths.ancestor;

        return cachedAncestor;
    }


    private boolean checkVertices(Iterable<Integer> vertices) {

        if (vertices == null)
            return false;

        for (Integer v : vertices) {
            if (v == null || v < 0 || v >= V)
                return false;
        }
        return true;
    }


    private boolean checkVertices(int x, int y) {
        return x >= 0 && y >= 0 && x < V && y < V;
    }


    /*
    Unit testing of this class. Takes the name of a digraph input file as a
    command-line argument, constructs the digraph, reads in vertex pairs
    from standard input, and prints out the length of the shortest ancestral
    path between the two vertices and a common ancestor that participates in
    that path

    % cat digraph1.txt              % SAP digraph1.txt

    13                              3 11
    11                              length = 4, ancestor = 1
    7  3
    8  3                            9 12
    3  1                            length = 3, ancestor = 5
    4  1
    5  1                            7 2
    9  5                            length = 4, ancestor = 0
    10  5
    11 10                           1 6
    12 10                           length = -1, ancestor = -1
    1  0
    2  0

    % SAP digraph-wordnet.txt

    - v = { 23814, 26923, 75631 }
    - w = { 12986, 13382, 15434, 17440, 35765, 49555, 53352, 60511, 63814, 65338, 81335 }
    - reference length() = 6

    - v = { 18339, 19472, 29858, 40504, 41003, 49353, 52430, 60050, 63885, 78618, 81074 }
    - w = { 38883, 39974, 78281 }
    - reference length() = 6
     */

    public static void main(String[] args) {

        In in = new In("digraph-wordnet.txt");
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);

        Stack<Integer> v = new Stack<>();
        v.push(23814);
        v.push(26923);
        v.push(75631);

        Stack<Integer> w = new Stack<>();
        w.push(12986);
        w.push(13382);
        w.push(15434);
        w.push(17440);
        w.push(35765);
        w.push(49555);
        w.push(53352);
        w.push(60511);
        w.push(63814);
        w.push(65338);
        w.push(81335);
        int length = sap.length(v, w);
        int ancestor = sap.ancestor(v, w);
        System.out.println("For v = " + v + "and  w = " + w + ":");
        System.out.printf("length = %d, ancestor = %d\n", length, ancestor);

        Stack<Integer> x = new Stack<>();
        x.push(18339);
        x.push(19472);
        x.push(29858);
        x.push(40504);
        x.push(41003);
        x.push(49353);
        x.push(52430);
        x.push(60050);
        x.push(63885);
        x.push(78618);
        x.push(81074);

        Stack<Integer> y = new Stack<>();
        y.push(38883);
        y.push(39974);
        y.push(78281);
        int length1 = sap.length(x, y);
        int ancestor1 = sap.ancestor(x, y);
        System.out.println("For v = " + x + "and  w = " + y + ":");
        System.out.printf("length = %d, ancestor = %d\n", length1, ancestor1);


        In in1 = new In(args[0]);
        Digraph G1 = new Digraph(in1);
        SAP sap1 = new SAP(G1);

        while (!StdIn.isEmpty()) {
            int a = StdIn.readInt();
            int b = StdIn.readInt();
            int length2 = sap1.length(a, b);
            int ancestor2 = sap1.ancestor(a, b);
            System.out.printf("length = %d, ancestor = %d\n", length2, ancestor2);
        }

    }
}
