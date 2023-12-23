package Project1;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

public class FilledContourPlotPanel extends JPanel {
    List<Point> points;
    List<Triangle> triangles;
    List<Integer> cellTypes;
    List<ColorMap> colorMaps;
    List<Double> isoValues = new ArrayList<>();

    public FilledContourPlotPanel(List<Point> points, List<Triangle> triangles, List<Integer> cellTypes,
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

        for (Triangle triangle : triangles) {
            // 各頂点のスカラー値に基づいて色を計算
            Color color1 = getColorForValue(triangle.vertices[0].scalars);
            Color color2 = getColorForValue(triangle.vertices[1].scalars);
            Color color3 = getColorForValue(triangle.vertices[2].scalars);

            // 色の平均を計算
            Color avgColor = new Color(
                    (color1.getRed() + color2.getRed() + color3.getRed()) / 3,
                    (color1.getGreen() + color2.getGreen() + color3.getGreen()) / 3,
                    (color1.getBlue() + color2.getBlue() + color3.getBlue()) / 3);

            // 三角形の塗りつぶし
            Polygon p = new Polygon();
            for (Point vertex : triangle.vertices) {
                p.addPoint((int) vertex.x, (int) vertex.y);
            }

            g.setColor(avgColor);
            g.fillPolygon(p);
        }
    }

    // isovaluesに基づいて最も近い色を取得するメソッド
    private Color getColorForValue(double value) {
        ColorMap closest = colorMaps.stream()
                .min((cm1, cm2) -> Double.compare(Math.abs(cm1.scalar - value),
                        Math.abs(cm2.scalar - value)))
                .orElse(null);

        return closest != null ? new Color((float) closest.r, (float) closest.g, (float) closest.b) : Color.BLACK;
    }
}
