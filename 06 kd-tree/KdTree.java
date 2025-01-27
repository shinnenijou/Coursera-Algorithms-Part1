import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.RectHV;

import java.util.LinkedList;

public class KdTree {
    private final static boolean VERTICAL = true;
    private final static boolean HORIZONTAL = false;

    // helper function to determine which coordinate to be used
    private static double coordinate(Point2D point, boolean direction) {
        return direction == VERTICAL ? point.x() : point.y();
    }

    // helper function to determine which coordinate to be used
    private static double coordinateMin(RectHV rect, boolean direction) {
        return direction == VERTICAL ? rect.xmin() : rect.ymin();
    }

    // helper function to determine which coordinate to be used
    private static double coordinateMax(RectHV rect, boolean direction) {
        return direction == VERTICAL ? rect.xmax() : rect.ymax();
    }

    private static boolean direction(int depth) {
        // VERTICAL if odd, otherwise HORIZONTAL
        return (depth & 1) == 1 ? VERTICAL : HORIZONTAL;
    }

    private static boolean isInRect(Point2D point, RectHV rect) {
        return point.x() >= rect.xmin()
                && point.x() <= rect.xmax()
                && point.y() >= rect.ymin()
                && point.y() <= rect.ymax();
    }

    private static int count(Node node){
        return node == null ? 0 : node.count;
    }

    private static class Node {
        public final Point2D point;
        public boolean direction;
        public Node left;
        public Node right;
        public int count;

        public Node(Point2D point, boolean direction) {
            this.point = point;
            this.direction = direction;
            this.count = 1;
        }
    }

    private static class PointDistance {
        double distance = Double.POSITIVE_INFINITY;
        Point2D point;
    }

    private Node root;

    private Node find(Node root, Point2D point) {
        if (root == null) return null;
        if (root.point.equals(point)) return root;

        int comp = Double.compare(coordinate(root.point, root.direction), coordinate(point, root.direction));

        // point is on left/down then go left
        if (comp > 0) return find(root.left, point);
            // go right (same coordinate is specified to right)
        else return find(root.right, point);
    }

    private void draw(Node root) {
        if (root == null) return;
        root.point.draw();
        draw(root.left);
        draw(root.right);
    }

    private Node insert(Node root, Point2D point, int depth) {
        if (root == null) return new Node(point, direction(depth));

        // search hit then do noting
        if (root.point.equals(point)) return root;

        int comp = Double.compare(coordinate(root.point, root.direction), coordinate(point, root.direction));

        if (comp > 0) root.left = insert(root.left, point, depth + 1);
        else root.right = insert(root.right, point, depth + 1);

        root.count = count(root.left) + count(root.right) + 1;

        return root;
    }

    private void range(Node root, RectHV rect, LinkedList<Point2D> container) {
        if (root == null) return;

        if (isInRect(root.point, rect)) container.add(root.point);

        if (coordinateMin(rect, root.direction) < coordinate(root.point, root.direction)) {
            range(root.left, rect, container);
        }

        if (coordinateMax(rect, root.direction) >= coordinate(root.point, root.direction)) {
            range(root.right, rect, container);
        }
    }

    private void nearest(Node root, Point2D toPoint, PointDistance nearest) {
        if (root == null) return;

        double distance = root.point.distanceSquaredTo(toPoint);

        if (distance < nearest.distance) {
            nearest.distance = distance;
            nearest.point = root.point;
        }

        if (coordinate(toPoint, root.direction) < coordinate(root.point, root.direction)) {
            nearest(root.left, toPoint, nearest);

            // now all distance use square of Euclidean distances.
            // should be sqrt here to add with coordinate
            if (coordinate(toPoint, root.direction) + Math.sqrt(nearest.distance) >= coordinate(root.point, root.direction)) {
                nearest(root.right, toPoint, nearest);
            }
        } else {
            nearest(root.right, toPoint, nearest);

            // now all distance use square of Euclidean distances.
            // should be sqrt here to add with coordinate
            if (coordinate(toPoint, root.direction) - Math.sqrt(nearest.distance) < coordinate(root.point, root.direction)) {
                nearest(root.left, toPoint, nearest);
            }
        }
    }

    // construct an empty set of points
    public KdTree() {

    }

    // is the set empty?
    public boolean isEmpty() {
        return root == null;
    }

    // number of points in the set
    public int size() {
        return root != null ? root.count : 0;
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        root = insert(root, p, 1);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        Node node = find(root, p);
        return node != null;
    }

    // draw all points to standard draw
    public void draw() {
        draw(root);
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("rect == null");
        LinkedList<Point2D> points = new LinkedList<>();
        range(root, rect, points);
        return points;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        // a global variable to store current min distance and nearest point
        PointDistance pointDistance = new PointDistance();
        nearest(root, p, pointDistance);
        return pointDistance.point;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        PointSET pointSet = new PointSET();
        KdTree tree = new KdTree();

        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();

            Point2D p1 = new Point2D(x, y);
            pointSet.insert(p1);
            assert pointSet.contains(p1);

            Point2D p2 = new Point2D(x, y);
            tree.insert(p2);
            assert tree.contains(p1);
        }

        assert tree.isEmpty() == pointSet.isEmpty();
        assert tree.size() == pointSet.size() : "Tree size: " + tree.size() + ", Expected: " + pointSet.size();

        RectHV rect = new RectHV(0.3, 0.3, 0.7, 0.7);
        LinkedList<Point2D> treeRange = ((LinkedList<Point2D>) tree.range(rect));
        LinkedList<Point2D> pointSetRange = ((LinkedList<Point2D>) pointSet.range(rect));
        assert treeRange.size() == pointSetRange.size() : "Tree range size: " + treeRange.size() + ", Expected: " + pointSetRange.size();

        Point2D origin = new Point2D(0, 0);
        assert tree.nearest(origin).equals(pointSet.nearest(origin));
    }
}
