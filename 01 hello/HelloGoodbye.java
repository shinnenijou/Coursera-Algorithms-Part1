/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdOut;

public class HelloGoodbye {
    public static void main(String[] args) {
        if (args.length < 2) {
            return;
        }

        StdOut.print("Hello ");
        StdOut.print(args[0]);

        StdOut.print(" and ");
        StdOut.print(args[1]);
        StdOut.println(".");

        StdOut.print("Goodbye ");
        StdOut.print(args[1]);

        StdOut.print(" and ");
        StdOut.print(args[0]);

        StdOut.println(".");
    }
}
