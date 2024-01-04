import java.util.ArrayList;
import java.util.List;

public class ContourLine {
    double isoValue;
    List<Segment> segments;

    ContourLine(double isoValue) {
        this.isoValue = isoValue;
        this.segments = new ArrayList<>();
    }

    void addSegment(Point start, Point end, Triangle triangle) {
        segments.add(new Segment(start, end, triangle));
    }
}

class Segment {
    Point start;
    Point end;
    Triangle triangle;

    Segment(Point start, Point end, Triangle triangle) {
        this.start = start;
        this.end = end;
        this.triangle = triangle;
    }
}