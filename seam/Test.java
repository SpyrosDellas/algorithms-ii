/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

public class Test {

    public static void main(String[] args) {

        int V = Integer.parseInt(args[0]);

        double root = Math.floor(Math.sqrt(V));
        int rows = (int) root;
        int cols = (int) Math.ceil(V / root);

        System.out.println("V = " + V + ", rows = " + rows + ", columns = " + cols);
    }
}
