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

    public static Color getColorFromScalar(double scalar, double minScalar, double maxScalar,
            List<ColorMap> colorMaps) {
        double normalizedScalar = (scalar - minScalar) / (maxScalar - minScalar);

        for (int i = 0; i < colorMaps.size() - 1; i++) {
            ColorMap lower = colorMaps.get(i);
            ColorMap upper = colorMaps.get(i + 1);
            if (normalizedScalar >= lower.scalar && normalizedScalar <= upper.scalar) {
                double ratio = (normalizedScalar - lower.scalar) / (upper.scalar - lower.scalar);
                double r = lower.r + ratio * (upper.r - lower.r);
                double g = lower.g + ratio * (upper.g - lower.g);
                double b = lower.b + ratio * (upper.b - lower.b);
                return new Color((float) r, (float) g, (float) b);
            }
        }

        return Color.BLACK;
    }
}
