package Project1;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

class ContourPlotMain {
    public static void main(String[] args) {
        VTKReader.readVTKFile("./Project1/test_data/riderr.vtk");
        ColorMapReader.readColorMapFile("./CoolWarmFloat257.csv");

        JFrame frame = new JFrame("My Application");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        List<Double> isoValues = new ArrayList<>();
        isoValues.add(0.0);
        isoValues.add(0.5);
        isoValues.add(1.0);
        isoValues.add(2.0);
        isoValues.add(3.0);
        isoValues.add(4.0);

        ContourLinePlotPanel clPanel = new ContourLinePlotPanel(VTKReader.points, VTKReader.triangles,
                VTKReader.cellTypes,
                ColorMapReader.colorMaps, isoValues);

        FilledContourPlotPanel fcPanel = new FilledContourPlotPanel(VTKReader.points, VTKReader.triangles,
                VTKReader.cellTypes, ColorMapReader.colorMaps, isoValues);

        frame.add(clPanel);
        frame.setVisible(true);
    }
}