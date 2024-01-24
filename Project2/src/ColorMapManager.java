package src;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jogamp.vecmath.Color3f;

public class ColorMapManager {
    List<ColorMap> colorMaps;

    static final Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
    static final Color3f WHITE = new Color3f(1.0f, 1.0f, 1.0f);
    static final Color3f GRAY = new Color3f(0.6f, 0.6f, 0.6f);
    static final Color3f RED = new Color3f(1f, 0.0f, 0.0f);
    static final Color3f GREEN = new Color3f(0.0f, 1.0f, 0.0f);
    static final Color3f BLUE = new Color3f(0.0f, 0.0f, 1.0f);
    static final Color3f YELLOW = new Color3f(1.0f, 1.0f, 0.0f);
    static final Color3f MAX = new Color3f(0.2298057f, 0.298717966f, 0.753683153f);

    public ColorMapManager() {
        colorMaps = new ArrayList<>();
    }

    public void clearColorMaps() {
        colorMaps.clear();
    }

    public void readColorMapFile(String fileName) {
        clearColorMaps();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = "";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String array[] = line.split(",");
                double scalar = Double.parseDouble(array[0]);
                float r = Float.parseFloat(array[1]);
                float g = Float.parseFloat(array[2]);
                float b = Float.parseFloat(array[3]);
                colorMaps.add(new ColorMap(scalar, r, g, b));
            }
            System.out.println("Finish reading color map file.");
        } catch (IOException e) {
            System.out.println("Error occured while reading color map file.");
            e.printStackTrace();
        }
    }

    // This method is used to get the color from the scalar value.
    public Color getColorFromScalar(double scalar, double minScalar, double maxScalar) {
        double normalizedScalar = 1 - (scalar - minScalar) / (maxScalar - minScalar);

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

        return Color.BLUE;
    }
}
