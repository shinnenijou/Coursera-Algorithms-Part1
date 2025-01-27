import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.RectHV;
import java.util.LinkedList;

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
                    && point.y() <= rect.ymax()) {
                list.add(point);
            }
        }

        return list;
    }

    // a nearest neighbor in the set to point p; null if the set is empty
    public Point2D nearest(Point2D p) {
        if (p == null)
            throw new IllegalArgumentException("p == null");
        Point2D candidate = null;
        double minDistance = Double.POSITIVE_INFINITY;
        for (Point2D point : points) {
            double distance = p.distanceSquaredTo(point);
            candidate = distance < minDistance ? point : candidate;
            minDistance = Math.min(distance, minDistance);
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
            assert pointSet.contains(point);
        }

        StdOut.println("IsEmpty: " + pointSet.isEmpty());
        StdOut.println("Size: " + pointSet.size());

        RectHV rect = new RectHV(0.3, 0.3, 0.7, 0.7);

        Iterable<Point2D> points = pointSet.range(rect);
        int count = 0;

        for (Point2D p : points) {
            count++;
        }

        StdOut.println(String.format("Points in range (%.2f, %.2f, %.2f, %.2f): %d",rect.xmin(), rect.ymin(), rect.xmax(), rect.ymax(), count));

        StdOut.print("Nearest point from (0, 0): ");
        Point2D candidate = pointSet.nearest(new Point2D(0, 0));
        StdOut.println(candidate);
//
//        StdDraw.setScale();
//        StdDraw.setPenRadius(0.01);
//
//        pointSet.draw();
//        rect.draw();
//        candidate.drawTo(new Point2D(0, 0));
//
//        StdDraw.show();
    }
}
