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

    public FilledContourPlotPanel(List<Point> points, List<Triangle> triangles,
            List<ColorMap> colorMaps, List<ContourLine> contourLines, List<Double> isoValues) {
        this.points = points;
        this.triangles = triangles;
        this.colorMaps = colorMaps;
        this.contourLines = contourLines;
        this.isoValues = isoValues;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawFilledContour(g);
        System.out.println("FilledContourPlotPanel");
    }

    void drawFilledContour(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        for (ContourLine line : contourLines) {
            Color color = ColorMap.getColorForIsoValue(line.isoValue, colorMaps);
            g2d.setColor(color);

            for (Segment segment : line.segments) {
                g2d.drawLine((int) segment.start.x, (int) segment.start.y, (int) segment.end.x, (int) segment.end.y);
            }
        }
    }
}
