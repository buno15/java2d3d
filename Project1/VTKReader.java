package Project1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class VTKReader {
    static List<Point> points = new ArrayList<>();
    static List<Triangle> triangles = new ArrayList<>();
    static List<Integer> cellTypes = new ArrayList<>();

    public static void readVTKFile(String fileName) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = "";
            while ((line = br.readLine()) != null) {
                if (line.startsWith("DATASET")) {
                    int size = Integer.parseInt(br.readLine().split(" ")[1]);
                    for (int i = 0; i < size; i++) {
                        String[] array = br.readLine().split(" ");
                        double p1 = Double.parseDouble(array[0]) * 30 + 500;
                        double p2 = Double.parseDouble(array[1]) * 30 + 200;
                        double p3 = Double.parseDouble(array[2]);
                        points.add(new Point(p1, p2, p3));
                    }
                } else if (line.startsWith("CELLS")) {
                    int size = Integer.parseInt(line.split(" ")[1]);
                    for (int i = 0; i < size; i++) {
                        String[] array = br.readLine().split(" ");
                        int p1 = Integer.parseInt(array[2]);
                        int p2 = Integer.parseInt(array[3]);
                        int p3 = Integer.parseInt(array[4]);
                        Point pArray[] = new Point[3];
                        pArray[0] = points.get(p1);
                        pArray[1] = points.get(p2);
                        pArray[2] = points.get(p3);
                        triangles.add(new Triangle(pArray));
                    }
                } else if (line.startsWith("CELL_TYPES")) {
                    int size = Integer.parseInt(line.split(" ")[1]);
                    for (int i = 0; i < size; i++) {
                        int type = Integer.parseInt(br.readLine());
                        cellTypes.add(type);
                    }
                } else if (line.startsWith("POINT_DATA")) {
                    int size = Integer.parseInt(line.split(" ")[1]);
                    br.readLine();
                    br.readLine();
                    for (int i = 0; i < size; i++) {
                        double value = Double.parseDouble(br.readLine());
                        points.get(i).setScalars(value);
                    }
                }
            }
            System.out.println("Finish reading VTK file.");
        } catch (IOException e) {
            System.out.println("Error occured while reading VTK file.");
            e.printStackTrace();
        }
    }
}
