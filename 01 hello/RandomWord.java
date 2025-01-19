/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RandomWord {
    public static void main(String[] args) {
        int count = 0;
        String champion = "";
        while (!StdIn.isEmpty()) {
            String input = StdIn.readString();
            count++;

            if (StdRandom.bernoulli(1 / (double) count)) {
                champion = input;
            }
        }

        StdOut.println(champion);
    }
}
