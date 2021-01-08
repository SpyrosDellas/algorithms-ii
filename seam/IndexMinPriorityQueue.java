/* *****************************************************************************
 *  Name: Spyros Dellas
 *  Date: 14/05/2020
 *
 *  Description:
 *  Index min oriented priority queue implementation
 *
 **************************************************************************** */


import java.math.BigInteger;
import java.util.NoSuchElementException;


public class IndexMinPriorityQueue<K extends Comparable<K>> {

    // binary heap using 1-based indexing, i.e. pq[0] is unused
    private int[] pq;

    // the keys of the queue
    private K[] keys;

    // inverse: qp[pq[i]] = pq[qp[i]] = i
    private int[] qp;

    // The size of the priority queue
    private int size;


    // Create a priority queue of capacity maxN with possible indices
    // between 0 and maxN-1
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public IndexMinPriorityQueue(int maxN) {
        pq = new int[maxN + 1];   // pq[0] is unused to simplify indexing
        qp = new int[maxN];
        keys = (K[]) new Comparable[maxN];
        size = 0;
    }


    // Insert item; associate it with k
    public void insert(int index, K item) {
        pq[++size] = index;
        qp[index] = size;
        keys[index] = item;
        swim(size);
    }


    // change the item associated with index
    public void change(int index, K item) {
        int indexLocation = qp[index];
        if (indexLocation == 0) {
            insert(index, item);
        }
        else {
            keys[index] = item;
            sink(indexLocation);
            swim(indexLocation);
        }
    }


    // is index associated with some item?
    public boolean contains(int index) {
        return qp[index] != 0;
    }


    // remove index and its associated item
    public void delete(int index) {
        int indexLocation = qp[index];
        if (indexLocation == 0) return;

        qp[index] = 0;
        keys[index] = null;
        qp[pq[size]] = indexLocation;
        pq[indexLocation] = pq[size--];
        sink(indexLocation);
    }


    // return the smallest key
    public K min() {
        if (size == 0)
            throw new NoSuchElementException("Priority queue is empty!");
        return keys[pq[1]];
    }


    // return the smallest key's index
    public int minIndex() {
        if (size == 0)
            throw new NoSuchElementException("Priority queue is empty!");
        return pq[1];
    }


    // remove the minimal item and return its index
    public int delMin() {
        if (size == 0)
            throw new NoSuchElementException("Priority queue is empty!");
        int minIndex = pq[1];
        qp[pq[size]] = 1;
        qp[minIndex] = 0;
        keys[minIndex] = null;
        pq[1] = pq[size--];
        sink(1);
        return minIndex;
    }


    // is the priority queue empty?
    public boolean isEmpty() {
        return size == 0;
    }


    // number of keys in the priority queue
    public int size() {
        return size;
    }

    /* ************************************************************************
                         PRIVATE HELPER METHODS
     *************************************************************************/

    // Bottom-up reheapify
    private void swim(int k) {
        while (k > 1 && more(k / 2, k)) {
            exch(k / 2, k);
            k /= 2;
        }
    }


    // Top-down reheapify
    private void sink(int k) {
        while (2 * k <= size) {
            int j = 2 * k;
            if (j < size && more(j, j + 1)) {
                j++;
            }
            if (more(k, j)) {
                exch(k, j);
                k = j;
            }
            else {
                break;
            }
        }
    }

    private boolean more(int i, int j) {
        return keys[pq[i]].compareTo(keys[pq[j]]) > 0;
    }

    private void exch(int i, int j) {
        qp[pq[i]] = j;
        qp[pq[j]] = i;

        int indexAtI = pq[i];
        pq[i] = pq[j];
        pq[j] = indexAtI;
    }

    // Certify that this is a min oriented priority queue
    private boolean isPQ() {
        for (int k = 1; k <= size / 2; k++) {
            int j = 2 * k;
            if (more(k, j) || (j < size && more(k, 2 * k + 1)))
                return false;
        }
        return true;
    }

    private void show() {
        System.out.println("PRIORITY QUEUE:");
        for (int i : pq) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("QP:");
        for (int i : qp) {
            System.out.print(i + " ");
        }
        System.out.println();
        System.out.println("KEYS:");
        for (K i : keys) {
            System.out.print(i + " ");
        }
        System.out.println();
    }


    // test client
    public static void main(String[] args) {

        int n = Integer.parseInt(args[0]);

        IndexMinPriorityQueue<BigInteger> pq = new IndexMinPriorityQueue<>(n);
        BigInteger key = BigInteger.ONE;
        for (int i = 0; i < n; i++) {
            pq.insert(n - 1 - i, key.multiply(key).multiply(key));
            key = key.add(BigInteger.ONE);
        }

        // IndexMinPriorityQueue<Character> pq = new IndexMinPriorityQueue<>(n);

        System.out.println("Index min oriented priority queue created");

        pq.show();
        pq.change(3, BigInteger.valueOf(1000000));
        pq.show();
        int k = pq.delMin();
        pq.show();
        /*
        for (int i = 0; i < 7; i++) {
            System.out.print(pq.min() + " ");
            pq.delMin();
        }
         */

        System.out.println("\nCertified as a min oriented priority queue: " + pq.isPQ());
    }
}
