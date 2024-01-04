import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ColorMapReader {
    static List<ColorMap> colorMaps = new ArrayList<>();

    public static void readColorMapFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = "";
            br.readLine();
            while ((line = br.readLine()) != null) {
                String array[] = line.split(",");
                double scalar = Double.parseDouble(array[0]);
                double r = Double.parseDouble(array[1]);
                double g = Double.parseDouble(array[2]);
                double b = Double.parseDouble(array[3]);
                colorMaps.add(new ColorMap(scalar, r, g, b));
            }
            System.out.println("Finish reading color map file.");
        } catch (IOException e) {
            System.out.println("Error occured while reading color map file.");
            e.printStackTrace();
        }
    }
}
