import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Comparator;

public class Solver {
    private static class Node {
        public Board board;
        public int moves;
        public Node prev;

        public Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
        }
    }

    // Use MinPQ -> element with the lowest priority firstly out -> lower === shorter
    private static class ManhattanComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            int distance1 = o1.board.manhattan() + o1.moves;
            int distance2 = o2.board.manhattan() + o2.moves;
            if (distance1 != distance2) return distance1 - distance2;
            return o1.moves - o2.moves;
        }
    }

    private final Board initial;
    private Node goalNode;

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board board) {
        if (board == null) throw new IllegalArgumentException();
        initial = board;
        tryToSolve();
    }

    // is the initial board solvable? (see below)
    public boolean isSolvable() {
        return goalNode != null;
    }

    // min number of moves to solve initial board; -1 if unsolvable
    public int moves() {
        if (!isSolvable()) return -1;
        return goalNode.moves;
    }

    // sequence of boards in a shortest solution; null if unsolvable
    public Iterable<Board> solution() {
        if (!isSolvable()) return null;

        ArrayList<Board> solution = new ArrayList<>();

        for (Node node = goalNode; node != null; node = node.prev) {
            solution.add(node.board);
        }

        for (int i = 0, j = solution.size() - 1; i < j; i++, j--) {
            Board temp = solution.get(i);
            solution.set(i, solution.get(j));
            solution.set(j, temp);
        }

        return solution;
    }

    private void tryToSolve() {
        goalNode = null;

        // init board
        MinPQ<Node> queueInit = new MinPQ<>(new ManhattanComparator());
        queueInit.insert(new Node(initial, 0, null));

        // twin board
        MinPQ<Node> queueTwin = new MinPQ<>(new ManhattanComparator());
        queueTwin.insert(new Node(initial.twin(), 0, null));

        while (!queueInit.isEmpty() && !queueTwin.isEmpty()) {
            // Process Initial Board
            Node node = queueInit.delMin();

            if (node.board.isGoal()) {
                goalNode = node;
                break;
            }

            for (Board board : node.board.neighbors()) {
                queueInit.insert(new Node(board, node.moves + 1, node));
            }

            // Process twin board
            node = queueTwin.delMin();

            if (node.board.isGoal()) {
                break;
            }

            for (Board board : node.board.neighbors()) {
                queueTwin.insert(new Node(board, node.moves + 1, node));
            }

        }
    }

    // test client (see below)
    public static void main(String[] args) {
        // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = in.readInt();
        Board initial = new Board(tiles);

        // solve the puzzle
        Solver solver = new Solver(initial);

        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }
}