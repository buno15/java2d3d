package Project1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class FilledContourPlotPanel extends JPanel {
    List<Point> points;
    List<Triangle> triangles;
    List<ColorMap> colorMaps;
    List<Double> isoValues = new ArrayList<>();
    List<ContourLine> contourLines;

    Point minPoint;
    Point maxPoint;

    double minScalar;
    double maxScalar;

    double topMargin = 0;
    double leftMargin = 0;

    double scaleWidth = 1;
    double scaleHeight = 1;

    public FilledContourPlotPanel(List<Point> points, List<Triangle> triangles,
            List<ColorMap> colorMaps, List<ContourLine> contourLines, List<Double> isoValues, Point minPoint,
            Point maxPoint, double minScalar,
            double maxScalar) {
        this.points = points;
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.contourLines = contourLines;
        this.isoValues = isoValues;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.minScalar = minScalar;
        this.maxScalar = maxScalar;
    }

    public void setData(List<Point> points, List<Triangle> triangles, List<ColorMap> colorMaps,
            List<ContourLine> contourLines, List<Double> isoValues, Point minPoint, Point maxPoint, double minScalar,
            double maxScalar) {
        this.points = points;
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.contourLines = contourLines;
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
        drawFilledContour(g);
        System.out.println("FilledContourPlotPanel");
    }

    void drawFilledContour(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        final double scaleX = getWidth() / (maxPoint.x - minPoint.x) * scaleWidth;
        final double scaleY = 600 / (maxPoint.y - minPoint.y) * scaleHeight;

        for (Triangle triangle : triangles) {
            Point p1 = scaleAndTransformPoint(triangle.vertices[0], scaleX, scaleY);
            Point p2 = scaleAndTransformPoint(triangle.vertices[1], scaleX, scaleY);
            Point p3 = scaleAndTransformPoint(triangle.vertices[2], scaleX, scaleY);

            for (int x = (int) Math.min(Math.min(p1.x, p2.x), p3.x); x <= Math.max(Math.max(p1.x, p2.x), p3.x); x++) {
                for (int y = (int) Math.min(Math.min(p1.y, p2.y), p3.y); y <= Math.max(Math.max(p1.y, p2.y),
                        p3.y); y++) {
                    if (isInsideTriangle(p1, p2, p3, x, y)) {
                        Color color = interpolateColor(triangle, x, y, scaleX, scaleY);
                        g2d.setColor(color);
                        g2d.drawLine((int) (x + leftMargin), (int) (y + topMargin), (int) (x + leftMargin),
                                (int) (y + topMargin));
                    }
                }
            }
        }
    }

    private Point scaleAndTransformPoint(Point p, double scaleX, double scaleY) {
        double transformedX = (p.x - minPoint.x) * scaleX;
        double transformedY = (maxPoint.y - p.y) * scaleY;

        Point np = new Point(transformedX, transformedY, 0);
        np.setScalars(p.scalars);
        return np;
    }

    private boolean isInsideTriangle(Point p1, Point p2, Point p3, double x, double y) {
        double denominator = ((p2.y - p3.y) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.y - p3.y));
        double a = ((p2.y - p3.y) * (x - p3.x) + (p3.x - p2.x) * (y - p3.y)) / denominator;
        double b = ((p3.y - p1.y) * (x - p3.x) + (p1.x - p3.x) * (y - p3.y)) / denominator;
        double c = 1 - a - b;

        return 0 <= a && a <= 1 && 0 <= b && b <= 1 && 0 <= c && c <= 1;
    }

    private Color interpolateColor(Triangle triangle, double x, double y, double scaleX, double scaleY) {
        Point p1 = scaleAndTransformPoint(triangle.vertices[0], scaleX, scaleY);
        Point p2 = scaleAndTransformPoint(triangle.vertices[1], scaleX, scaleY);
        Point p3 = scaleAndTransformPoint(triangle.vertices[2], scaleX, scaleY);

        Color c1 = ColorMap.getColorFromScalar(p1.scalars, minScalar, maxScalar, colorMaps);
        Color c2 = ColorMap.getColorFromScalar(p2.scalars, minScalar, maxScalar, colorMaps);
        Color c3 = ColorMap.getColorFromScalar(p3.scalars, minScalar, maxScalar, colorMaps);

        double totalArea = calculateTriangleArea(p1, p2, p3);

        double area1 = calculateTriangleArea(new Point(x, y, 0), p2, p3);
        double area2 = calculateTriangleArea(p1, new Point(x, y, 0), p3);
        double area3 = calculateTriangleArea(p1, p2, new Point(x, y, 0));

        double ratio1 = area1 / totalArea;
        double ratio2 = area2 / totalArea;
        double ratio3 = area3 / totalArea;

        double r = c1.getRed() * ratio1 + c2.getRed() * ratio2 + c3.getRed() * ratio3;
        double g = c1.getGreen() * ratio1 + c2.getGreen() * ratio2 + c3.getGreen() * ratio3;
        double b = c1.getBlue() * ratio1 + c2.getBlue() * ratio2 + c3.getBlue() * ratio3;

        return new Color((int) r, (int) g, (int) b);
    }

    private double calculateTriangleArea(Point p1, Point p2, Point p3) {
        double a = calculateDistance(p1, p2);
        double b = calculateDistance(p2, p3);
        double c = calculateDistance(p3, p1);
        double s = (a + b + c) / 2.0;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }
}
