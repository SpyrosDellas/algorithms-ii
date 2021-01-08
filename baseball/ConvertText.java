/* *****************************************************************************
 *  Name:
 *  Date:
 *  Description:
 **************************************************************************** */

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;

public class ConvertText {

    public static void main(String[] args) {

        String file = "mobydick.txt";
        In in = new In(file);
        String s = in.readAll().replaceAll("\\s+", " ").toLowerCase();

        Out out = new Out("mobydick1.txt");
        out.print(s);
    }
}
