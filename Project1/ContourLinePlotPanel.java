package Project1;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

class ContourLinePlotPanel extends JPanel {
    List<Point> points;
    List<Triangle> triangles;
    List<ColorMap> colorMaps;
    List<Double> isoValues = new ArrayList<>();
    List<ContourLine> contourLines = new ArrayList<>();

    public ContourLinePlotPanel(List<Point> points, List<Triangle> triangles,
            List<ColorMap> colorMaps, List<Double> isoValues) {
        this.points = points;
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.isoValues = isoValues;

    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawContourLines(g);
        System.out.println("ContourLinePlotPanel");
    }

    private void drawContourLines(Graphics g) {
        contourLines.clear();

        for (int i = 0; i < isoValues.size(); i++) {
            double isoValue = isoValues.get(i);
            contourLines.add(new ContourLine(isoValue));

            for (Triangle triangle : triangles) {
                List<Point> intersectioniPoints = findIntersectionPoints(triangle, isoValue);
                if (intersectioniPoints.size() == 2) {
                    contourLines.get(i).addSegment(intersectioniPoints.get(0), intersectioniPoints.get(1), triangle);
                    g.setColor(ColorMap.getColorForIsoValue(isoValue, colorMaps));
                    g.drawLine((int) intersectioniPoints.get(0).x, (int) intersectioniPoints.get(0).y,
                            (int) intersectioniPoints.get(1).x, (int) intersectioniPoints.get(1).y);
                }
            }
        }
        System.out.println(contourLines.size());
    }

    // Find intersection points based on triangle and isoValue
    private List<Point> findIntersectionPoints(Triangle triangle, double isoValue) {
        List<Point> intersectioniPoints = new ArrayList<>(); // intersection points
        for (int i = 0; i < 3; i++) {
            Point p1 = triangle.vertices[i];
            Point p2 = triangle.vertices[(i + 1) % 3];

            if ((p1.scalars - isoValue) * (p2.scalars - isoValue) < 0) {
                double t = (isoValue - p1.scalars) / (p2.scalars - p1.scalars); // Ratio of the distance from p1 to p2
                                                                                // to the point where the isolines
                                                                                // intersect
                double x = p1.x + t * (p2.x - p1.x);
                double y = p1.y + t * (p2.y - p1.y);
                intersectioniPoints.add(new Point(x, y, 0));
            }
        }
        return intersectioniPoints;
    }
}