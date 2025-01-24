import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;

public final class Board {
    // helper function
    private static void swapTiles(int[][] tiles, int row1, int col1, int row2, int col2) {
        int temp = tiles[row1][col1];
        tiles[row1][col1] = tiles[row2][col2];
        tiles[row2][col2] = temp;
    }

    private final short[][] tiles;

    // create a board from an n-by-n array of tiles,
    // where tiles[row][col] = tile at (row, col)
    public Board(int[][] tiles) {
        if (tiles == null) throw new IllegalArgumentException("tiles cannot be null");

        this.tiles = new short[tiles.length][tiles[0].length];

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                this.tiles[i][j] = (short) tiles[i][j];
            }
        }
    }

    // string representation of this board
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int digit = (int) Math.log10(dimension() * dimension() - 1) + 1;
        String format = "%" + digit + "d";

        sb.append(String.format("%d\n", dimension()));
        for (short[] tile : tiles) {
            for (short i : tile) {
                sb.append(String.format(format, i));
                sb.append(' ');
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("\n");
        }

        return sb.toString();
    }

    // board dimension n
    public int dimension() {
        return tiles.length;
    }

    // number of tiles out of place
    public int hamming() {
        int hammingDistance = 0;
        int dimension = dimension();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == 0) continue;
                if (tiles[i][j] != i * dimension + j + 1) hammingDistance++;
            }
        }

        return hammingDistance;
    }

    // sum of Manhattan distances between tiles and goal
    public int manhattan() {
        int manhattanDistance = 0;
        int dimension = dimension();

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] == 0) continue;
                int manhattanDistanceRow = Math.abs(i - (tiles[i][j] - 1) / dimension);
                int manhattanDistanceCol = Math.abs(j - (tiles[i][j] - 1) % dimension);
                manhattanDistance += manhattanDistanceRow + manhattanDistanceCol;
            }
        }

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
        if (y.getClass() != Board.class) return false;
        Board that = (Board) y;
        if (dimension() != that.dimension()) return false;

        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                if (tiles[i][j] != that.tiles[i][j]) return false;
            }
        }

        return true;
    }

    // all neighboring boards
    public Iterable<Board> neighbors() {
        ArrayList<Board> neighbors = new ArrayList<>(4);
        int dimension = dimension();

        int zeroRow = 0;
        int zeroCol = 0;

        // find zero
        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                if (tiles[i][j] != 0) continue;
                zeroRow = i;
                zeroCol = j;
                break;
            }
        }

        int[][] newTiles = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                newTiles[i][j] = tiles[i][j];
            }
        }

        // up
        if (zeroRow > 0) {
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow - 1, zeroCol);
            neighbors.add(new Board(newTiles));
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow - 1, zeroCol);
        }

        // right
        if (zeroCol + 1 < dimension) {
            swapTiles(newTiles,zeroRow, zeroCol, zeroRow, zeroCol + 1);
            neighbors.add(new Board(newTiles));
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow, zeroCol + 1);
        }

        // down
        if (zeroRow + 1 < dimension) {
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow + 1, zeroCol);
            neighbors.add(new Board(newTiles));
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow + 1, zeroCol);
        }

        // left
        if (zeroCol > 0) {
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow, zeroCol - 1);
            neighbors.add(new Board(newTiles));
            swapTiles(newTiles, zeroRow, zeroCol, zeroRow, zeroCol - 1);
        }

        return neighbors;
    }

    // a board that is obtained by exchanging any pair of tiles
    public Board twin() {
        int dimension = dimension();
        int[][] newTiles = new int[dimension][dimension];

        for (int i = 0; i < dimension; i++) {
            for (int j = 0; j < dimension; j++) {
                newTiles[i][j] = tiles[i][j];
            }
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