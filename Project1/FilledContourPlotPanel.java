package Project1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.List;

import javax.swing.JPanel;

public class FilledContourPlotPanel extends JPanel {
    private List<Triangle> triangles;
    private List<ColorMap> colorMaps;

    private Point minPoint;
    private Point maxPoint;

    private double minScalar;
    private double maxScalar;

    private double topMargin = 0;
    private double leftMargin = 0;

    private double scaleWidth = 1;
    private double scaleHeight = 1;

    public FilledContourPlotPanel(List<Triangle> triangles,
            List<ColorMap> colorMaps, Point minPoint,
            Point maxPoint, double minScalar,
            double maxScalar) {
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.minPoint = minPoint;
        this.maxPoint = maxPoint;
        this.minScalar = minScalar;
        this.maxScalar = maxScalar;
    }

    public void reloadData(List<Triangle> triangles, List<ColorMap> colorMaps,
            Point minPoint, Point maxPoint, double minScalar,
            double maxScalar) {
        this.triangles = triangles;
        this.colorMaps = colorMaps;
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
        System.out.println("Plot FilledContourPlotPanel");
    }

    private void drawFilledContour(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        final double scaleX = getWidth() / (maxPoint.x - minPoint.x) * scaleWidth;
        final double scaleY = 600 / (maxPoint.y - minPoint.y) * scaleHeight;

        for (Triangle triangle : triangles) {
            Point p1 = triangle.vertices[0].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);
            Point p2 = triangle.vertices[1].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);
            Point p3 = triangle.vertices[2].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);

            int minX = (int) Math.min(Math.min(p1.x, p2.x), p3.x);
            int maxX = (int) Math.max(Math.max(p1.x, p2.x), p3.x);
            int minY = (int) Math.min(Math.min(p1.y, p2.y), p3.y);
            int maxY = (int) Math.max(Math.max(p1.y, p2.y), p3.y);

            for (int x = minX; x <= maxX; x++) {
                for (int y = minY; y <= maxY; y++) {
                    if (isPointInTriangle(new Point(x, y, 0), p1, p2, p3)) {
                        Color color = setPointColor(triangle, x, y, scaleX, scaleY);
                        g2d.setColor(color);

                        int px = (int) (x + leftMargin);
                        int py = (int) (y + topMargin);

                        g2d.drawLine(px, py, px, py);
                    }
                }
            }
        }
    }

    // calculate cross product
    private double crossProduct(Point a, Point b, Point c) {
        return (b.x - a.x) * (c.y - a.y) - (b.y - a.y) * (c.x - a.x);
    }

    // check if point is in triangle
    private boolean isPointInTriangle(Point p, Point v1, Point v2, Point v3) {
        double c1 = crossProduct(v1, v2, p);
        double c2 = crossProduct(v2, v3, p);
        double c3 = crossProduct(v3, v1, p);

        boolean hasNegative = (c1 < 0) || (c2 < 0) || (c3 < 0);
        boolean hasPositive = (c1 > 0) || (c2 > 0) || (c3 > 0);

        return !(hasNegative && hasPositive);
    }

    // set point color
    private Color setPointColor(Triangle triangle, double x, double y, double scaleX, double scaleY) {
        Point p1 = triangle.vertices[0].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);
        Point p2 = triangle.vertices[1].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);
        Point p3 = triangle.vertices[2].scaleAndTransformPoint(scaleX, scaleY, minPoint, maxPoint);

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

        // calculate color by area ratio
        double r = c1.getRed() * ratio1 + c2.getRed() * ratio2 + c3.getRed() * ratio3;
        double g = c1.getGreen() * ratio1 + c2.getGreen() * ratio2 + c3.getGreen() * ratio3;
        double b = c1.getBlue() * ratio1 + c2.getBlue() * ratio2 + c3.getBlue() * ratio3;

        return new Color((int) r, (int) g, (int) b);
    }

    // calculate triangle area
    private double calculateTriangleArea(Point p1, Point p2, Point p3) {
        double a = calculateDistance(p1, p2);
        double b = calculateDistance(p2, p3);
        double c = calculateDistance(p3, p1);
        double s = (a + b + c) / 2.0;

        return Math.sqrt(s * (s - a) * (s - b) * (s - c));
    }

    // calculate distance between two points
    private double calculateDistance(Point p1, Point p2) {
        return Math.sqrt(Math.pow(p2.x - p1.x, 2) + Math.pow(p2.y - p1.y, 2));
    }
}
