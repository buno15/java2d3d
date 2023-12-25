package Project1;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

class ContourLinePlotPanel extends JPanel {
    private List<Triangle> triangles;
    private List<ColorMap> colorMaps;
    private List<Double> isoValues = new ArrayList<>();
    private List<ContourLine> contourLines = new ArrayList<>();

    private Point minPoint;
    private Point maxPoint;

    private double minScalar;
    private double maxScalar;

    private double topMargin = 0;
    private double leftMargin = 0;

    private double scaleWidth = 1;
    private double scaleHeight = 1;

    public ContourLinePlotPanel(List<Triangle> triangles,
            List<ColorMap> colorMaps, List<Double> isoValues, Point minPoint, Point maxPoint, double minScalar,
            double maxScalar) {
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.isoValues = isoValues;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.minScalar = minScalar;
        this.maxScalar = maxScalar;
    }

    public void reloadData(List<Triangle> triangles, List<ColorMap> colorMaps,
            List<Double> isoValues, Point minPoint, Point maxPoint, double minScalar, double maxScalar) {
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.isoValues = isoValues;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.minScalar = minScalar;
        this.maxScalar = maxScalar;
    }

    public void setTopMargin(double topMargin) {
        this.topMargin = topMargin;
    }

    public void setLeftMargin(double leftMargin) {
        this.leftMargin = leftMargin;
    }

    public void setScaleWidth(double scaleWidth) {
        this.scaleWidth = scaleWidth;
    }

    public void setScaleHeight(double scaleHeight) {
        this.scaleHeight = scaleHeight;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawContourLines(g);
        System.out.println("Plot ContourLinePlotPanel");
    }

    private void drawContourLines(Graphics g) {
        contourLines.clear();

        final double scaleX = getWidth() / (maxPoint.x - minPoint.x) * scaleWidth;
        final double scaleY = 600 / (maxPoint.y - minPoint.y) * scaleHeight;

        for (int i = 0; i < isoValues.size(); i++) {
            double isoValue = isoValues.get(i);
            contourLines.add(new ContourLine(isoValue));

            for (Triangle triangle : triangles) {
                List<Point> intersectioniPoints = findIntersectionPoints(triangle, isoValue, scaleX, scaleY);
                if (intersectioniPoints.size() == 2) {
                    contourLines.get(i).addSegment(intersectioniPoints.get(0), intersectioniPoints.get(1), triangle);
                    g.setColor(ColorMap.getColorFromScalar(isoValue, 0, 1, colorMaps));

                    int x1 = (int) (intersectioniPoints.get(0).x + leftMargin);
                    int y1 = (int) (intersectioniPoints.get(0).y + topMargin);

                    int x2 = (int) (intersectioniPoints.get(1).x + leftMargin);
                    int y2 = (int) (intersectioniPoints.get(1).y + topMargin);

                    g.drawLine(x1, y1, x2, y2);
                }
            }
        }
    }

    // Find intersection points based on triangle and isoValue
    private List<Point> findIntersectionPoints(Triangle triangle, double isoValue, double scaleX, double scaleY) {
        List<Point> intersectioniPoints = new ArrayList<>(); // intersection points
        for (int i = 0; i < 3; i++) {
            Point p1 = triangle.vertices[i].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);
            Point p2 = triangle.vertices[(i + 1) % 3].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);

            p1.normalizedScalars(minScalar, maxScalar);
            p2.normalizedScalars(minScalar, maxScalar);

            if ((p1.scalars - isoValue) * (p2.scalars - isoValue) < 0) {
                double t = (isoValue - p1.scalars) / (p2.scalars - p1.scalars); // Ratio of the distance from p1 to p2
                                                                                // to the point where the isolines
                                                                                // intersect
                double x = p1.x + t * (p2.x - p1.x);
                double y = p1.y + t * (p2.y - p1.y);
                Point np = new Point(x, y, 0);
                np.setScalars(isoValue);
                intersectioniPoints.add(np);
            }
        }
        return intersectioniPoints;
    }
}