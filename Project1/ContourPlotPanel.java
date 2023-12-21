package Project1;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

// draw panel
class ContourPlotPanel extends JPanel {
    List<Triangle> triangles;
    List<Double> isoValues;
    ColorMap colorMap;

    public ContourPlotPanel(List<Triangle> triangles, ColorMap colorMap) {
        this.triangles = triangles;
        this.colorMap = colorMap;
        this.isoValues = new ArrayList<>();
    }

    public void setIsoValues(List<Double> isoValues) {
        this.isoValues = isoValues;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // ここに描画ロジックを実装
    }

    // 等高線プロットの描画メソッドを追加
    // 塗りつぶし等高線プロットの描画メソッドを追加
}