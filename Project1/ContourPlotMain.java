package Project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

class ContourPlotMain {
    public static void main(String[] args) {
        VTKReader.readVTKFile("./Project1/test_data/riderr.vtk");
        ColorMapReader.readColorMapFile("./CoolWarmFloat257.csv");

        JFrame frame = new JFrame("Contour Plot");
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        List<Double> isoValues = new ArrayList<>();
        isoValues.add(0.0);
        isoValues.add(0.5);
        isoValues.add(1.0);
        isoValues.add(1.5);
        isoValues.add(2.0);
        isoValues.add(2.5);

        ContourLinePlotPanel clPanel = new ContourLinePlotPanel(VTKReader.points, VTKReader.triangles,
                ColorMapReader.colorMaps, isoValues);

        FilledContourPlotPanel fcPanel = new FilledContourPlotPanel(VTKReader.points, VTKReader.triangles,
                ColorMapReader.colorMaps, clPanel.contourLines, isoValues);

        frame.setLayout(new BorderLayout());
        frame.add(clPanel, BorderLayout.EAST);
        frame.add(fcPanel, BorderLayout.CENTER);

        frame.setVisible(true);
    }
}