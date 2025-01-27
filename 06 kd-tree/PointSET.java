import edu.princeton.cs.algs4.SET;

import java.util.LinkedList;

import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdIn;

public class PointSET {
    private final SET<Point2D> points;

    // construct an empty set of points
    public PointSET() {
        points = new SET<>();
    }

    // is the set empty?
    public boolean isEmpty() {
        return points.isEmpty();
    }

    // number of points in the set
    public int size() {
        return points.size();
    }

    // add the point to the set (if it is not already in the set)
    public void insert(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        points.add(p);
    }

    // does the set contain point p?
    public boolean contains(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        return points.contains(p);
    }

    // draw all points to standard draw
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null)
            throw new IllegalArgumentException("rect == null");
        LinkedList<Point2D> list = new LinkedList<>();

        for (Point2D point : points) {
            if (point.x() >= rect.xmin()
                    && point.x() <= rect.xmax()
                    && point.y() >= rect.ymin()
                    && point.y() <= rect.xmin()) {
                list.add(point);
            }
        }

        return null;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        Point2D candidate = points.min();
        double minDistance = p.distanceSquaredTo(candidate);
        for (Point2D point : points) {
            double distance = p.distanceSquaredTo(point);
            candidate = distance < minDistance ? point : candidate;
            minDistance = distance < minDistance ? distance : minDistance;
        }
        return candidate;
    }

    // unit testing of the methods (optional)
    public static void main(String[] args) {
        PointSET pointSet = new PointSET();

        while (!StdIn.isEmpty()) {
            double x = StdIn.readDouble();
            double y = StdIn.readDouble();
            Point2D point = new Point2D(x, y);
            pointSet.insert(point);
        }
    }
}
