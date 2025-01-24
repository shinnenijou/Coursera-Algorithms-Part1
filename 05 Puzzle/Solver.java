import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

import java.util.ArrayList;
import java.util.Comparator;

public class Solver {
    private final Board initial;
    private Node goalNode;

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
    private static class HammingComparator implements Comparator<Node> {
        @Override
        public int compare(Node o1, Node o2) {
            int distance1 = o1.board.hamming() + o1.moves;
            int distance2 = o2.board.hamming() + o2.moves;
            if (distance1 != distance2) return distance1 - distance2;
            return o1.moves - o2.moves;
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

    private void tryToSolve(Board initial) {
        goalNode = null;
        ArrayList<Board> searchedBoards = new ArrayList<>();

        MinPQ<Node> pq = new MinPQ<>(new ManhattanComparator());
        pq.insert(new Node(initial, 0, null));

        while (!pq.isEmpty()) {
            Node node = pq.delMin();

            if (node.board.isGoal()) {
                goalNode = node;
                break;
            }

            for (Board board : node.board.neighbors()) {
                if (!searchedBoards.contains(board)) {
                    pq.insert(new Node(board, node.moves + 1, node));
                }
            }

            searchedBoards.add(node.board);
        }
    }

    // find a solution to the initial board (using the A* algorithm)
    public Solver(Board initial) {
        if (initial == null) throw new IllegalArgumentException();
        this.initial = initial;
        tryToSolve(this.initial);
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

        for (Node node = goalNode; node.prev != null; node = node.prev) {
            solution.add(node.board);
        }

        for (int i = 0, j = solution.size() - 1; i < j; i++, j--) {
            Board temp = solution.get(i);
            solution.set(i, solution.get(j));
            solution.set(j, temp);
        }

        return solution;
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