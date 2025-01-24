import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.NoSuchElementException;

public final class Board {
    private final int dimension;
    private int hammingDistance;
    private int manhattanDistance;
    private final int[][] tiles;

    private int correctTile(int row, int col) {
        if (row == dimension - 1 && col == dimension - 1) {
            return 0;
        }
        return row * dimension + col + 1;
    }

    private int correctRow(int tile) {
        if (tile == 0) {
            return dimension - 1;
        }
        return (tile - 1) / dimension;
    }

    private int correctCol(int tile) {
        if (tile == 0) {
            return dimension - 1;
        }
        return (tile - 1) % dimension;
    }

    // Hamming Distance: the number of tiles in the wrong position
    private void resetHamming() {
        hammingDistance = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (tiles[i][j] == 0) continue;
                if (tiles[i][j] != correctTile(i, j)) hammingDistance++;
            }
        }
    }

    // Manhattan Distance: the sum of the Manhattan distances from the tiles to their goal positions.
    private void resetManhattan() {
        manhattanDistance = 0;

        for (int i = 0; i < dimension(); i++) {
            for (int j = 0; j < dimension(); j++) {
                if (tiles[i][j] == 0) continue;
                int manhattanDistanceRow = Math.abs(i - correctRow(tiles[i][j]));
                int manhattanDistanceCol = Math.abs(j - correctCol(tiles[i][j]));
                manhattanDistance += manhattanDistanceRow + manhattanDistanceCol;
            }
        }
    }

    private static class Boards implements Iterable<Board> {
        int[][] tiles;

        Boards(int[][] tiles) {
            this.tiles = tiles;
        }

        @Override
        public Iterator<Board> iterator() {
            return new BoardIterator(tiles);
        }
    }

    private static class BoardIterator implements Iterator<Board> {
        private enum Direction {
            UP, DOWN, LEFT, RIGHT,
        }

        private ArrayList<Direction> directions;
        private final int[][] tiles;
        private int zeroRow;
        private int zeroCol;

        // copy tiles
        BoardIterator(int[][] tiles) {
            this.tiles = new int[tiles.length][tiles[0].length];

            for (int i = 0; i < tiles.length; i++) {
                System.arraycopy(tiles[i], 0, this.tiles[i], 0, tiles[i].length);
            }

            for (int i = 0; i < tiles.length; i++) {
                for (int j = 0; j < tiles[i].length; j++) {
                    if (tiles[i][j] == 0) {
                        zeroRow = i;
                        zeroCol = j;
                        break;
                    }
                }
            }

            directions = new ArrayList<>();
            if (zeroRow > 0) directions.add(Direction.UP);
            if (zeroCol + 1 < tiles[0].length) directions.add(Direction.RIGHT);
            if (zeroRow + 1 < tiles.length) directions.add(Direction.DOWN);
            if (zeroCol > 0) directions.add(Direction.LEFT);
        }

        private void swap(int row1, int col1, int row2, int col2) {
            int temp = tiles[row1][col1];
            tiles[row1][col1] = tiles[row2][col2];
            tiles[row2][col2] = temp;
        }

        private Board makeBoard(int row, int col) {
            swap(zeroRow, zeroCol, row, col);
            Board board = new Board(tiles);
            swap(zeroRow, zeroCol, row, col);
            return board;
        }

        @Override
        public boolean hasNext() {
            return !directions.isEmpty();
        }

        @Override
        public Board next() {
            if (!hasNext()) throw new NoSuchElementException();

            Direction direction = directions.remove(directions.size() - 1);
            Board board;

            switch (direction) {
                case UP:
                    board = makeBoard(zeroRow - 1, zeroCol);
                    break;
                case DOWN:
                    board = makeBoard(zeroRow + 1, zeroCol);
                    break;
                case LEFT:
                    board = makeBoard(zeroRow, zeroCol - 1);
                    break;
                case RIGHT:
                    board = makeBoard(zeroRow, zeroCol + 1);
                    break;
                default:
                    throw new NoSuchElementException();
            }

            return board;
        }
    }

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException("tiles cannot be null");

        this.dimension = tiles.length;
        this.tiles = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            System.arraycopy(tiles[i], 0, this.tiles[i], 0, dimension);
        }

        resetHamming();
        resetManhattan();
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%d\n", dimension));
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                sb.append(String.format("%4d", tiles[i][j]));
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return dimension;
    }

    // number of tiles out of place
    public int hamming() {
        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        return manhattanDistance;
    }

    // is this board the goal board?
    public boolean isGoal() {
        return hamming() == 0;
    }

    // does this board equal y?
    @Override
    public boolean equals(Object y) {
        if (y == null) return false;
        if (!(y instanceof Board)) return false;
        Board that = (Board) y;
        if (dimension() != that.dimension() || hamming() != that.hamming() || manhattan() != that.manhattan())
            return false;

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (tiles[i][j] != that.tiles[i][j]) return false;
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        return new Boards(this.tiles);
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int[][] newTiles = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            System.arraycopy(tiles[i], 0, newTiles[i], 0, dimension);
        }

        for (int i = 0; i < dimension; i++) {
            if (newTiles[i][0] != 0 && newTiles[i][1] != 0) {
                int temp = newTiles[i][0];
                newTiles[i][0] = newTiles[i][1];
                newTiles[i][1] = temp;
                break;
            }
        }

        return new Board(newTiles);
    }

    // unit testing (not graded)
    public static void main(String[] args) {
        int n = StdIn.readInt();
        int[][] tiles = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                tiles[i][j] = StdIn.readInt();
        Board initial = new Board(tiles);
        StdOut.println(initial);
        StdOut.println("Dimension: " + initial.dimension());
        StdOut.println("Hamming: " + initial.hamming());
        StdOut.println("Manhattan: " + initial.manhattan());
        StdOut.println("IsGoal: " + initial.isGoal());
        StdOut.println("Equals to self: " + initial.equals(initial));

        for (Board b : initial.neighbors()) {
            StdOut.println(b);
            StdOut.println("Equals to initial: " + b.equals(initial));
        }
    }
}