import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private final Point[] points;
    private LineSegment[] segments;

    private static class Pair implements Comparable<Pair> {
        public Point p1;
        public Point p2;

        public Pair(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }

        @Override
        public int compareTo(Pair that) {
            return Double.compare(this.p1.slopeTo(this.p2), that.p1.slopeTo(that.p2));
        }
    }

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
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
        return segments.length;
    }

    // the line segments
    public LineSegment[] segments() {
        findSegments();
        return segments;
    }

    private void findSegments() {
        if (segments != null) return;

        // point pair consisting of collinear segment
        ArrayList<Pair> pairs = new ArrayList<>();

        for (int i = 0; i < points.length - 3; i++) {
            // first time to ensure natural order(output p->r when p->q->s->r is collinear)
            Arrays.sort(points, i, points.length);

            Point p = points[i];
            Comparator<Point> slopeComp = p.slopeOrder();

            // This sort is guaranteed to be stable
            Arrays.sort(points, i + 1, points.length, slopeComp);

            // j: point to the first index whose slope is equal to adjacent points
            for (int j = i + 1, k = i + 2; k <= points.length; ++k) {
                if (k < points.length && slopeComp.compare(points[j], points[k]) == 0) {
                    continue;
                }

                // if 4 or more points collinear, add segment to result
                if (k - j >= 3) {
                    pairs.add(new Pair(p, points[k - 1]));
                }

                // move to current point
                j = k;
            }
        }

        // subsegment always appears later and with the same end
        for (int i = 0; i < pairs.size(); i++) {
            for (int j = i + 1; j < pairs.size(); j++) {
                if (pairs.get(i).compareTo(pairs.get(j)) == 0 && pairs.get(i).p2.compareTo(pairs.get(j).p2) == 0) {
                    pairs.remove(j);
                    j--;
                }
            }
        }

        segments = new LineSegment[pairs.size()];

        for (int i = 0; i < segments.length; i++) {
            Pair pair = pairs.get(i);
            if (pair == null) continue;
            segments[i] = new LineSegment(pair.p1, pair.p2);
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
