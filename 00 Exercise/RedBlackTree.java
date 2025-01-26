import java.util.ArrayList;

import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdRandom;

public class RedBlackTree<Key extends Comparable<Key>, Value> {
    private static boolean RED = true;
    private static boolean BLACK = false;
    private static int TRIALS = 100000;

    private Node root;

    private class Node {
        public Key key;
        public Value value;
        public Node left;
        public Node right;
        public int count;
        public boolean color;

        Node(Key initKey, Value initValue) {
            key = initKey;
            value = initValue;
            color = RED;
            count = 1;
        }
    }

    private boolean less(Key lhs, Key rhs) {
        return lhs.compareTo(rhs) < 0;
    }

    private void swap(Node lhs, Node rhs) {
        Key tempKey = lhs.key;
        lhs.key = rhs.key;
        rhs.key = tempKey;

        Value tempValue = lhs.value;
        lhs.value = rhs.value;
        rhs.value = tempValue;
    }

    private boolean isRed(Node node) {
        return node != null && node.color == RED;
    }

    private int count(Node node) {
        return node == null ? 0 : node.count;
    }

    private Node rotateRight(Node root) {
        assert isRed(root.left);
        assert isRed(root.left.left);

        Node newRoot = root.left;
        root.left = newRoot.right;
        newRoot.right = root;

        newRoot.count = root.count;
        root.count = count(root.left) + count(root.right) + 1;

        newRoot.color = root.color;
        root.color = RED;

        return newRoot;
    }

    private Node rotateLeft(Node root) {
        assert isRed(root.right);

        Node newRoot = root.right;
        root.right = newRoot.left;
        newRoot.left = root;

        newRoot.count = root.count;
        root.count = count(root.left) + count(root.right) + 1;

        newRoot.color = root.color;
        root.color = RED;

        return newRoot;
    }

    private Node flipRoot(Node root) {
        assert !isRed(root);
        assert isRed(root.left);
        assert isRed(root.right);

        root.left.color = BLACK;
        root.right.color = BLACK;
        root.color = RED;

        return root;
    }

    private Node reBalance(Node root) {
        // root must be black, then become red after flip
        if (isRed(root.left) && isRed(root.right)) {
            root = flipRoot(root);
        }
        // rotate do not change root's color. continuous red will be processed by parent
        else if (isRed(root.right)) {
            root = rotateLeft(root);
        }
        // root must be black, then become red after flip
        else if (isRed(root.left) && isRed(root.left.left)) {
            root = rotateRight(root);
            root = flipRoot(root);
        }
        // do nothing when both left and right children black

        return root;
    }

    private Node put(Node root, Key key, Value value) {
        if (root == null)
            return new Node(key, value);

        int comp = key.compareTo(root.key);

        if (comp < 0) {
            root.left = put(root.left, key, value);
        } else if (comp > 0) {
            root.right = put(root.right, key, value);
        } else {
            root.value = value;
            return root;
        }

        root = reBalance(root);
        root.count++;

        return root;
    }

    // TODO rebalance buggy
    private Node delete(Node root, Key key) {
        if (root == null)
            return null;

        int comp = key.compareTo(root.key);

        // search in left tree
        if (comp < 0) {
            root.left = delete(root.left, key);
        }
        // search in right tree
        else if (comp > 0) {
            root.right = delete(root.right, key);
        }
        // search hit
        else {
            if (root.right != null && root.left != null) {
                Node successor = root.right;
                while (successor.left != null) {
                    successor = successor.left;
                }
                swap(root, successor);
                root.right = delete(root.right, key);
            } else if (root.left != null) {
                root.left.color = root.color;
                return root.left;
            } else if (root.right != null) {
                root.right.color = root.color;
                return root.right;
            } else {
                return null;
            }
        }

        root = reBalance(root);
        root.count--;

        return root;
    }

    private void inorderKey(Node root, ArrayList<Key> container) {
        if (root == null)
            return;
        inorderKey(root.left, container);
        container.add(root.key);
        inorderKey(root.right, container);
    }

    private void inorderValue(Node root, ArrayList<Value> container) {
        if (root == null)
            return;
        inorderValue(root.left, container);
        container.add(root.value);
        inorderValue(root.right, container);
    }

    private Node find(Node root, Key key) {
        if (root == null)
            return null;

        int comp = key.compareTo(root.key);

        if (comp == 0)
            return root;
        else if (comp < 0)
            return find(root.left, key);
        else
            return find(root.right, key);
    }

    private Node minNode(Node root) {
        if (root == null)
            return null;
        return minNode(root.left);
    }

    private Node maxNode(Node root) {
        if (root == null)
            return null;
        return maxNode(root.right);
    }

    private int height(Node root) {
        if (root == null)
            return 0;
        int leftHeight = height(root.left);
        int rightHeight = height(root.right);

        return (leftHeight > rightHeight ? leftHeight : rightHeight) + 1;
    }

    private int minPath(Node root) {
        if (root == null)
            return 0;
        int leftHeight = minPath(root.left);
        int rightHeight = minPath(root.right);

        return (leftHeight < rightHeight ? leftHeight : rightHeight) + 1;
    }

    private long sumPath(Node root, int depth) {
        if (root == null)
            return 0;

        long sumLeft = sumPath(root.left, depth + 1);
        long sumRight = sumPath(root.right, depth + 1);
        return sumLeft + sumRight + depth;
    }

    RedBlackTree() {
        root = null;
    }

    public int size() {
        return root != null ? root.count : 0;
    }

    public int height() {
        return height(root);
    }

    public int minDepth() {
        return minPath(root);
    }

    public double meanPath() {
        long sum = sumPath(root, 1);
        return (double) sum / size();
    }

    public void put(Key key, Value value) {
        root = put(root, key, value);
    }

    public void delete(Key key) {
        root = delete(root, key);
    }

    public Iterable<Key> keys() {
        ArrayList<Key> container = new ArrayList<>(size());
        inorderKey(root, container);
        return container;
    }

    public Iterable<Value> values() {
        ArrayList<Value> container = new ArrayList<>(size());
        inorderValue(root, container);
        return container;
    }

    public Value find(Key key) {
        Node node = find(root, key);
        return node != null ? node.value : null;
    }

    public Value minValue() {
        Node node = minNode(root);
        return node != null ? node.value : null;
    }

    public Value maxValue() {
        Node node = maxNode(root);
        return node != null ? node.value : null;
    }

    public static void main(String[] args) {
        RedBlackTree<Integer, Integer> rbTree = new RedBlackTree<>();

        for (int i = 0; i < TRIALS; i++) {
            int rand = StdRandom.uniformInt(0, Integer.MAX_VALUE - 1);
            rbTree.put(rand, i);
        }

        {
            Integer last = Integer.MIN_VALUE;

            for (Integer current : rbTree.keys()) {
                assert Integer.compare(last, current) <= 0;
                last = current;
            }
        }

        StdOut.println("----- Before deletion -----");
        StdOut.println("Size: " + rbTree.size());
        StdOut.println("Height: " + rbTree.height());
        StdOut.println("Mean Path: " + rbTree.meanPath());
        StdOut.println("Theoritical height: " + ((int) Math.log(TRIALS) + 1) + "~" + (2 * (int) Math.log(TRIALS) + 1));
        StdOut.println();

        for (int i = 0; i < 1000; i++) {
            for (Integer key : rbTree.keys()) {
                if (StdRandom.bernoulli(0.5))
                    rbTree.delete(key);
            }

            while (rbTree.size() < TRIALS) {
                int rand = StdRandom.uniformInt(0, Integer.MAX_VALUE - 1);
                rbTree.put(rand, i);
            }

            Integer last = Integer.MIN_VALUE;

            for (Integer current : rbTree.keys()) {
                assert Integer.compare(last, current) <= 0;
                last = current;
            }
        }

        StdOut.println("----- After deletion -----");
        StdOut.println("Size: " + rbTree.size());
        StdOut.println("Height: " + rbTree.height());
        StdOut.println("Mean Path: " + rbTree.meanPath());
        StdOut.println("Theoritical height: " + ((int) Math.log(TRIALS) + 1) + "~" + (2 * (int) Math.log(TRIALS) + 1));
    }
}
