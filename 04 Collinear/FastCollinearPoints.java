import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class FastCollinearPoints {
    private final Point[] points;
    private ArrayList<LineSegment> segments;

    private static class Pair {
        public Point p1;
        public Point p2;

        public Pair(Point p1, Point p2) {
            this.p1 = p1;
            this.p2 = p2;
        }
    }

    private static class PairComparator implements Comparator<Pair> {
        @Override
        public int compare(Pair lhs, Pair rhs) {
            int i = lhs.p1.compareTo(rhs.p1);
            if (i != 0) return i;
            return lhs.p2.compareTo(rhs.p2);
        }
    }

    // finds all line segments containing 4 or more points
    public FastCollinearPoints(Point[] points) {
        if (points == null) throw new IllegalArgumentException("Constructor parameters may not be null");

        for (Point point : points) {
            if (point == null) throw new IllegalArgumentException("point may not be null");
        }

        this.points = points.clone();
        Arrays.sort(points);

        for(int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException("Duplicate point");
            }
        }
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

    private void findSegments() {
        if (segments != null) return;

        segments = new ArrayList<>();

        // first time to ensure natural order(output p->r when p->q->s->r is collinear)
        // constructor guaranteed unique order
        Arrays.sort(points, 0, points.length);
        Point[] tempPoints = new Point[points.length];

        // point pair container to remove duplicate collinear segment
        ArrayList<Pair> pairs = new ArrayList<>();

        for (int i = 0; i < points.length; i++) {
            System.arraycopy(points, 0, tempPoints, 0, tempPoints.length);

            // swap (0, i)
            Point point = tempPoints[i];
            tempPoints[i] = tempPoints[0];
            tempPoints[0] = point;

            Comparator<Point> slopeComp = point.slopeOrder();

            // This sort is guaranteed to be stable
            Arrays.sort(tempPoints, 1, tempPoints.length, slopeComp);

            // j: point to the first index whose slope is equal to adjacent points
            for (int j = 1, k = 2; k <= tempPoints.length; ++k) {
                if (k < tempPoints.length && slopeComp.compare(tempPoints[j], tempPoints[k]) == 0) {
                    continue;
                }

                // if 4 or more points collinear, add segment to result
                if (k - j >= 3) {
                    Point start = point;
                    Point end = point;

                    for (int l = j; l < k; ++l) {
                        if (tempPoints[l].compareTo(start) < 0) {
                            start = tempPoints[l];
                        } else if (tempPoints[l].compareTo(end) > 0) {
                            end = tempPoints[l];
                        }
                    }

                    pairs.add(new Pair(start, end));
                }

                // move to current point
                j = k;
            }
        }

        // remove duplicate collinear segment
        Pair lastPair = null;
        Comparator<Pair> pairComp = new PairComparator();

        pairs.sort(pairComp);

        for (Pair pair : pairs) {
            if (lastPair == null || pairComp.compare(lastPair, pair) != 0) {
                lastPair = pair;
                segments.add(new LineSegment(lastPair.p1, lastPair.p2));
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }
}
