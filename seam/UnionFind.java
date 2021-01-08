/* *****************************************************************************
 *  Name:              Spyros Dellas
 *  Coursera User ID:  spyrosdellas@yahoo.com
 *  Last modified:     24/04/2020
 *
 * Implementation of the clasic Union-Find data structure with weighted quick
 * union by rank and with path compression by halving
 *
 * The class represents a union–find data type (also known as the
 * disjoint-sets data type.
 * It supports the classic union and find operations, along with a count
 * operation that returns the total number of sets.
 *
 * The union-find data type models a collection of sets containing
 * n elements, with each element in exactly one set.
 * The elements are named 0 through n–1.
 * Initially, there are n sets, with each element in its own set. The canonical
 * element of a set (also known as the root, identifier, leader, or set
 * representative) is one distinguished element in the set.
 *
 * Here is a summary of the operations:
 *
 * find(p) returns the canonical element of the set containing p. The find
 * operation returns the same value for two elements if and only if they are
 * in the same set.
 *
 * union(p, q) merges the set containing element p with the set containing
 * element q. That is, if p and q are in different sets, replace these two sets
 * with a new set that is the union of the two.
 *
 * count() returns the number of sets.
 *
 * The canonical element of a set can change only when the set itself changes
 * during a call to union; it cannot change during a call to either find or
 * count.
 *
 **************************************************************************** */

public class UnionFind {

    // stores the component's parent node for each element
    private int[] parent;
    // stores the depth of its tree at its corresponding parent node
    private byte[] rank;
    // number of components (trees)
    private int count;


    public UnionFind(int N) { // Initialize component id array.
        if (N == 0)
            throw new IllegalArgumentException("Union-Find minimum size is 1, but 0 provided");
        count = N;
        parent = new int[N];
        rank = new byte[N];
        for (int i = 0; i < N; i++) {
            // Each element's root becomes the element itself
            parent[i] = i;
            // Each root has one connected element (itself)
            rank[i] = 0;
        }
    }


    public int count() {
        return count;
    }


    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }


    private void validate(int p) {
        if (p < 0 || p >= parent.length)
            throw new IllegalArgumentException("Index " + p + " out of bounds");
    }


    public int find(int p) {
        // Follow the connections in the element's component to find the root
        // node
        // Note: Since p is a primitive data type, it's passed by value.
        // Therefore we can modify it as we please without any side effects
        // This optimisation saves additional array accesses to copy p to a
        // new variable and simplifies the code
        validate(p);
        while (parent[p] != p) {
            // First step: connect p to its grandparent to reduce depth of the tree
            parent[p] = parent[parent[p]];
            // Second step: update p
            p = parent[p];
        }
        return p;
    }


    public void union(int p, int q) {
        // Check if the two elements are already connected
        int rootP = find(p);
        int rootQ = find(q);
        if (rootP == rootQ) return;
        // Connect the root of the smaller tree to the root of the larger tree
        // to keep the depth of the combined tree bounded by log(N)
        if (rank[rootP] > rank[rootQ]) {
            parent[rootQ] = rootP;
        }
        else if (rank[rootP] < rank[rootQ]) {
            parent[rootP] = rootQ;
        }
        else {
            parent[rootP] = rootQ;
            // When two trees of equal depth are merged the resulting tree's
            // depth is larger by one
            rank[rootQ]++;
        }
        // Total number of individual components (trees) is reduced by one
        count--;
    }


    public static void main(String[] args) {

    }
}
