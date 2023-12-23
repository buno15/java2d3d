package Project1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

class ContourLinePlotPanel extends JPanel {
    List<Point> points;
    List<Triangle> triangles;
    List<Integer> cellTypes;
    List<ColorMap> colorMaps;
    List<Double> isoValues = new ArrayList<>();

    public ContourLinePlotPanel(List<Point> points, List<Triangle> triangles, List<Integer> cellTypes,
            List<ColorMap> colorMaps, List<Double> isoValues) {
        this.points = points;
        this.triangles = triangles;
        this.cellTypes = cellTypes;
        this.colorMaps = colorMaps;
        this.isoValues = isoValues;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawContourLines(g);
    }

    public Color getColorForIsovalue(double isovalue) {
        ColorMap closestColorMap = null;
        double minDifference = Double.MAX_VALUE;

        for (ColorMap colorMap : colorMaps) {
            double difference = Math.abs(colorMap.scalar - isovalue);
            if (difference < minDifference) {
                minDifference = difference;
                closestColorMap = colorMap;
            }
        }

        if (closestColorMap != null) {
            return new Color((float) closestColorMap.r, (float) closestColorMap.g, (float) closestColorMap.b);
        } else {
            return Color.BLACK;
        }
    }

    // 等高線の描画
    private void drawContourLines(Graphics g) {
        for (double isoValue : isoValues) {
            for (Triangle triangle : triangles) {
                List<Point> contourPoints = findContourPoints(triangle, isoValue);
                if (contourPoints.size() == 2) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setColor(getColorForIsovalue(isoValue));
                    g2d.drawLine((int) contourPoints.get(0).x, (int) contourPoints.get(0).y,
                            (int) contourPoints.get(1).x, (int) contourPoints.get(1).y);
                }
            }
        }
    }

    // 三角形と等値線値に基づいて交点を見つける
    private List<Point> findContourPoints(Triangle triangle, double isoValue) {
        List<Point> contourPoints = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Point p1 = triangle.vertices[i];
            Point p2 = triangle.vertices[(i + 1) % 3];

            System.out.println(p1.scalars);

            if ((p1.scalars - isoValue) * (p2.scalars - isoValue) < 0) {
                double t = (isoValue - p1.scalars) / (p2.scalars - p1.scalars);
                double x = p1.x + t * (p2.x - p1.x);
                double y = p1.y + t * (p2.y - p1.y);
                contourPoints.add(new Point(x, y, 0));
            }
        }
        return contourPoints;
    }
}