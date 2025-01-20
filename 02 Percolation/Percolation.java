/* *****************************************************************************
 *  Name:              Ada Lovelace
 *  Coursera User ID:  123456
 *  Last modified:     October 16, 1842
 **************************************************************************** */

import edu.princeton.cs.algs4.QuickFindUF;
import edu.princeton.cs.algs4.QuickUnionUF;
import edu.princeton.cs.algs4.WeightedQuickUnionUF;

public class Percolation {
    public static void main(String[] args) {

        Percolation percolation = new Percolation(100);
        percolation.open(1, 1);
    }

    private final int realSize;
    private final UnionFind unionFindImpl;
    private final int virtualTop;
    private final int virtualBottom;
    private final boolean[] openSet;
    private int openSites;

    // creates n-by-n grid, with all sites initially blocked
    public Percolation(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException("Percolation requires n > 0");
        }

        realSize = n;
        virtualTop = realSize * realSize;
        virtualBottom = realSize * realSize + 1;
        openSet = new boolean[realSize * realSize];
        openSites = 0;

        // Here to change uf implementation
        // options: MyQuickFindUF, MyQuickUnionUF, AlgsQuickFindUF, AlgsQuickUnionUF, AlgsWeightedQuickUnionUF
        unionFindImpl = new UnionFind(n * n + 2, "MyWeightedQuickUnionUF");

        // init open set
        for (int i = 0; i < openSet.length; i++) {
            openSet[i] = false;
        }

        // union virtual top root obj to all top objs
        for (int i = 1; i <= realSize; i++) {
            unionFindImpl.union(virtualTop, convertIndex(1, i));
        }

        // union virtual bottom root obj to all top objs
        for (int i = 1; i <= realSize; i++) {
            unionFindImpl.union(virtualBottom, convertIndex(realSize, i));
        }
    }

    // opens the site (row, col) if it is not open already
    public void open(int row, int col) {
        validate(row, col);

        if (isOpen(row, col)) {
            return;
        }

        int currentIndex = convertIndex(row, col);
        openSet[currentIndex] = true;
        openSites++;

        if (row > 1 && isOpen(row - 1, col)) {
            unionFindImpl.union(currentIndex, convertIndex(row - 1, col));
        }
        if (row < realSize && isOpen(row + 1, col)) {
            unionFindImpl.union(currentIndex, convertIndex(row + 1, col));
        }
        if (col > 1 && isOpen(row, col - 1)) {
            unionFindImpl.union(currentIndex, convertIndex(row, col - 1));
        }
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
        return unionFindImpl.connected(convertIndex(row, col), virtualTop);
    }

    // returns the number of open sites
    public int numberOfOpenSites() {
        return openSites;
    }

    // does the system percolate?
    public boolean percolates() {
        return unionFindImpl.connected(virtualTop, virtualBottom);
    }

    private void validate(int row, int col) {
        if (1 > row || row > realSize || 1 > col || col > realSize)
            throw new IllegalArgumentException("row and col must be in the range of [1, n]");
    }

    private int convertIndex(int row, int col) {
        return (row - 1) * realSize + col - 1;
    }


    private interface IUnionFind {
        void union(int p, int q);

        boolean connected(int p, int q);
    }

    private static class MyQuickFindUF implements IUnionFind {
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

    private static class MyQuickUnionUF implements IUnionFind {
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

    // TODO
    private static class MyWeightedQuickUnionUF implements IUnionFind {
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

    private static class AlgsQuickFindUF implements IUnionFind {
        private final QuickFindUF impl;

        AlgsQuickFindUF(int n) {
            impl = new QuickFindUF(n);
        }

        public void union(int p, int q) {
            impl.union(p, q);
        }

        public boolean connected(int p, int q) {
            return impl.find(p) == impl.find(q);
        }
    }

    private static class AlgsQuickUnionUF implements IUnionFind {
        private final QuickUnionUF impl;

        AlgsQuickUnionUF(int n) {
            impl = new QuickUnionUF(n);
        }

        public void union(int p, int q) {
            impl.union(p, q);
        }

        public boolean connected(int p, int q) {
            return impl.find(p) == impl.find(q);
        }
    }

    private static class AlgsWeightedQuickUnionUF implements IUnionFind {
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

    private static class UnionFind {
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
                case "AlgsQuickFindUF":
                    impl = new AlgsQuickFindUF(n);
                    break;
                case "AlgsQuickUnionUF":
                    impl = new AlgsQuickUnionUF(n);
                    break;
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

}
