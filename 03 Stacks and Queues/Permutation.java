import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class Permutation {
    public static void main(String[] args) {
        if (args.length < 1) {
            throw new IllegalArgumentException();
        }

        int k = Integer.parseInt(args[0]);

        // Reservoir sampling: algorithm L
        // see: https://en.wikipedia.org/wiki/Reservoir_sampling
        RandomizedQueue<String> queue = new RandomizedQueue<>();
        double w = Math.exp(Math.log(StdRandom.uniformDouble()) / k);

        for (int i = 0; i < k && !StdIn.isEmpty(); i++) {
            queue.enqueue(StdIn.readString());
        }

        while (!StdIn.isEmpty()) {
            String str = StdIn.readString();

            if (StdRandom.uniformDouble() < w) {
                queue.dequeue();
                queue.enqueue(str);
                w *= Math.exp(Math.log(StdRandom.uniformDouble()) / k);
            }
        }

        // print result
        for (String s : queue) {
            StdOut.println(s);
        }
    }
}
