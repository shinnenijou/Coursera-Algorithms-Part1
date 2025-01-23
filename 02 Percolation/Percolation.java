/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Percolation {
    // Here to change uf implementation
    private static final String UF_IMPL = "AlgsWeightedQuickUnionUF";

    private final int realSize;
    private final UnionFind unionFindImpl;
    private final boolean[] openSet;
    private int openSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Percolation requires n > 0");
        }

        realSize = n;
        openSites = 0;

        unionFindImpl = new UnionFind(realSize * realSize + 2, UF_IMPL);

        // init open set
        openSet = new boolean[realSize * realSize];

        for (int i = 0; i < openSet.length; i++) {
            openSet[i] = false;
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        if (isOpen(row, col)) {
            return;
        }

        int currentIndex = convertIndex(row, col);
        openSet[currentIndex] = true;
        openSites++;

        // up
        if (row == 1 || isOpen(row - 1, col)) {
            unionFindImpl.union(currentIndex, convertIndex(row - 1, col));
        }

        // down
        if (row == realSize || isOpen(row + 1, col)) {
            unionFindImpl.union(currentIndex, convertIndex(row + 1, col));
        }

        // left
        if (col > 1 && isOpen(row, col - 1)) {
            unionFindImpl.union(currentIndex, convertIndex(row, col - 1));
        }

        // right
        if (col < realSize && isOpen(row, col + 1)) {
            unionFindImpl.union(currentIndex, convertIndex(row, col + 1));
        }
    }

    // is the site (row, col) open?
    public boolean isOpen(int row, int col) {
        validate(row, col);
        return openSet[convertIndex(row, col)];
    }

    // is the site (row, col) full?
    public boolean isFull(int row, int col) {
        validate(row, col);
        return unionFindImpl.connected(convertIndex(row, col), convertIndex(0, 0));
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return unionFindImpl.connected(convertIndex(0, 0), convertIndex(realSize + 1, 0));
    }

    private void validate(int row, int col) {
        if (row < 1 || row > realSize || col < 1 || col > realSize)
            throw new IllegalArgumentException("row and col must be in the range of [1, n]");
    }

    private int convertIndex(int row, int col) {
        if (1 <= row && row <= realSize) {
            return (row - 1) * realSize + (col - 1);
        }

        // virtual top
        if (row < 1) {
            return realSize * realSize;
        }

        // virtual bottom
        return realSize * realSize + 1;
    }

    public static void main(String[] args) {
        if (StdIn.isEmpty()) {
            return;
        }
        Percolation percolation = new Percolation(StdIn.readInt());

        while (!StdIn.isEmpty()) {
            int row = StdIn.readInt();
            int col = StdIn.readInt();
            percolation.open(row, col);
            StdOut.println("(" + row + ", " + col + ") isFull = " + percolation.isFull(row, col));
        }
    }
}

interface IUnionFind {
    void union(int p, int q);

    boolean connected(int p, int q);
}

class MyQuickFindUF implements IUnionFind {
    private final int[] entries;

    MyQuickFindUF(int n) {
        entries = new int[n];

        for (int i = 0; i < entries.length; i++) {
            entries[i] = i;
        }
    }

    public void union(int p, int q) {
        if (connected(p, q)) {
            return;
        }

        int firstEntry = entries[p];
        int secondEntry = entries[q];

        for (int i = 0; i < entries.length; i++) {
            if (entries[i] == firstEntry) {
                entries[i] = secondEntry;
            }
        }
    }

    public boolean connected(int p, int q) {
        validate(p);
        validate(q);
        return entries[p] == entries[q];
    }

    private void validate(int p) {
        if (p < 0 || p >= entries.length) throw new IllegalArgumentException("p is out of bounds");
    }
}

class MyQuickUnionUF implements IUnionFind {
    private final int[] parents;

    MyQuickUnionUF(int n) {
        parents = new int[n];

        for (int i = 0; i < parents.length; i++) {
            parents[i] = i;
        }
    }

    public void union(int p, int q) {
        int firstRoot = find(p);
        int secondRoot = find(q);

        if (firstRoot == secondRoot) {
            return;
        }
        parents[firstRoot] = secondRoot;
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    private int find(int p) {
        validate(p);

        while (p != parents[p]) {
            p = parents[p];
        }

        return p;
    }

    private void validate(int p) {
        if (p < 0 || p >= parents.length) throw new IllegalArgumentException("p is out of bounds");
    }
}

class MyWeightedQuickUnionUF implements IUnionFind {
    private final int[] parents;
    private final int[] heights;

    MyWeightedQuickUnionUF(int n) {
        parents = new int[n];

        for (int i = 0; i < parents.length; i++) {
            parents[i] = i;
        }

        heights = new int[n];

        for (int i = 0; i < heights.length; i++) {
            heights[i] = 1;
        }
    }

    public void union(int p, int q) {
        int firstRoot = find(p);
        int secondRoot = find(q);

        if (firstRoot == secondRoot) {
            return;
        }

        if (heights[firstRoot] > heights[secondRoot]) {
            parents[secondRoot] = firstRoot;
        } else if (heights[firstRoot] < heights[secondRoot]) {
            parents[firstRoot] = secondRoot;
        } else {
            parents[firstRoot] = secondRoot;
            heights[secondRoot] = heights[secondRoot] + 1;
        }
    }

    public boolean connected(int p, int q) {
        return find(p) == find(q);
    }

    private int find(int p) {
        validate(p);

        while (p != parents[p]) {
            p = parents[p];
        }

        return p;
    }

    private void validate(int p) {
        if (p < 0 || p >= parents.length) throw new IllegalArgumentException("p is out of bounds");
    }
}

//class AlgsQuickFindUF implements IUnionFind {
//    private final QuickFindUF impl;
//
//    AlgsQuickFindUF(int n) {
//        impl = new QuickFindUF(n);
//    }
//
//    public void union(int p, int q) {
//        impl.union(p, q);
//    }
//
//    public boolean connected(int p, int q) {
//        return impl.find(p) == impl.find(q);
//    }
//}
//
//class AlgsQuickUnionUF implements IUnionFind {
//    private final QuickUnionUF impl;
//
//    AlgsQuickUnionUF(int n) {
//        impl = new QuickUnionUF(n);
//    }
//
//    public void union(int p, int q) {
//        impl.union(p, q);
//    }
//
//    public boolean connected(int p, int q) {
//        return impl.find(p) == impl.find(q);
//    }
//}

class AlgsWeightedQuickUnionUF implements IUnionFind {
    private final WeightedQuickUnionUF impl;

    AlgsWeightedQuickUnionUF(int n) {
        impl = new WeightedQuickUnionUF(n);
    }

    public void union(int p, int q) {
        impl.union(p, q);
    }

    public boolean connected(int p, int q) {
        return impl.find(p) == impl.find(q);
    }
}

class UnionFind {
    private final IUnionFind impl;

    UnionFind(int n, String type) {
        switch (type) {
            case "MyQuickFindUF":
                impl = new MyQuickFindUF(n);
                break;
            case "MyQuickUnionUF":
                impl = new MyQuickUnionUF(n);
                break;
            case "MyWeightedQuickUnionUF":
                impl = new MyWeightedQuickUnionUF(n);
                break;
//            case "AlgsQuickFindUF":
//                impl = new AlgsQuickFindUF(n);
//                break;
//            case "AlgsQuickUnionUF":
//                impl = new AlgsQuickUnionUF(n);
//                break;
            case "AlgsWeightedQuickUnionUF":
                impl = new AlgsWeightedQuickUnionUF(n);
                break;
            default:
                throw new IllegalArgumentException("UnionFind: Unrecognized type");
        }
    }

    public void union(int p, int q) {
        impl.union(p, q);
    }

    public boolean connected(int p, int q) {
        return impl.connected(p, q);
    }
}
