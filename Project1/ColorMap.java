package Project1;

import java.awt.Color;
import java.util.List;

public class ColorMap {
    double scalar;
    double r, g, b;

    public ColorMap(double scalar, double r, double g, double b) {
        this.scalar = scalar;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public static Color getColorForIsoValue(double isoValue, List<ColorMap> colorMaps) {
        ColorMap closestColorMap = null;
        double minDifference = Double.MAX_VALUE;

        for (ColorMap colorMap : colorMaps) {
            double difference = Math.abs(colorMap.scalar - isoValue);
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
}
