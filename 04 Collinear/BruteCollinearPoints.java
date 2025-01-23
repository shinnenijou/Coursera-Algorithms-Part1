import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Arrays;

public class BruteCollinearPoints {
    private final Point[] points;
    private ArrayList<LineSegment> segments;

    // finds all line segments containing 4 points
    public BruteCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Constructor parameters may not be null");

        for (int i = 0; i < points.length; i++) {
            if (points[i] == null) throw new IllegalArgumentException("point may not be null");

            for (int j = i + 1; j < points.length; j++) {
                if (points[i].compareTo(points[j]) == 0) throw new IllegalArgumentException("duplicate point");
            }
        }

        this.points = points.clone();
    }

    // the number of line segments
    public int numberOfSegments() {
        findSegments();
        return segments.size();
    }

    // the line segments
    public LineSegment[] segments() {
        findSegments();
        return segments.toArray(new LineSegment[0]);
    }

    private Point maxPoint(Point... points) {
        assert points.length > 0;
        Point maxPoint = points[0];

        for (int i = 1; i < points.length; i++) {
            if (points[i].compareTo(maxPoint) > 0){
                maxPoint = points[i];
            }
        }

        return maxPoint;
    }

    private Point minPoint(Point... points) {
        assert points.length > 0;
        Point minPoint = points[0];

        for (int i = 1; i < points.length; i++) {
            if (points[i].compareTo(minPoint) < 0){
                minPoint = points[i];
            }
        }

        return minPoint;
    }

    private void findSegments() {
        if (segments != null) {
            return;
        }

        segments = new ArrayList<>();
        int count = 0;

        for (int i = 0; i < points.length; i++) {
            for (int j = i + 1; j < points.length; j++) {
                double slope_i_j = points[i].slopeTo(points[j]);

                for (int k = j + 1; k < points.length; k++) {
                    double slope_i_k = points[i].slopeTo(points[k]);

                    for (int l = k + 1; l < points.length; l++) {
                        double slope_i_l = points[i].slopeTo(points[l]);

                        if (slope_i_j == slope_i_k && slope_i_j == slope_i_l) {
                            Point max = maxPoint(points[i], points[j], points[k], points[l]);
                            Point min = minPoint(points[i], points[j], points[k], points[l]);
                            segments.add(new LineSegment(min, max));
                        }
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}