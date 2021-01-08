/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import java.util.PriorityQueue;
import java.util.Scanner;

public class Test {

    private static void solve() {
        // Comparator<Integer> reverse = Comparator.reverseOrder();
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        Scanner sc = new Scanner(System.in);
        int k = sc.nextInt();
        if (k <= 0)
            throw new IllegalArgumentException("k must be a positive integer");
        while (sc.hasNextInt()) {
            queue.add(sc.nextInt());
        }
        sc.close();

        System.out.println("k = " + k);
        if (queue.size() < k)
            System.out.println("Kth smallest element is: " + null);
        System.out.println("Kth smallest element is: " + kSmallest(queue, k));
    }

    private static int kSmallest(PriorityQueue<Integer> queue, int k) {
        int result = queue.peek();
        for (int i = 0; i < k; i++) {
            result = queue.poll();
        }
        return result;
    }

    public static void main(String[] args) {
        solve();
    }
}
