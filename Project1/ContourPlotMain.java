package Project1;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class ContourPlotMain {

    static JPanel panel1;
    static JPanel panel2;

    static ContourLinePlotPanel clPanel;
    static FilledContourPlotPanel fcPanel;

    public static void main(String[] args) {
        ColorMapReader.readColorMapFile("./CoolWarmFloat257.csv");

        JFrame frame = new JFrame("Contour Plot");
        frame.setSize(1400, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        List<Double> isoValues = new ArrayList<>();
        isoValues.add(0.0);
        isoValues.add(0.001);
        isoValues.add(0.1);
        isoValues.add(0.2);
        isoValues.add(0.3);
        isoValues.add(0.4);
        isoValues.add(0.5);
        isoValues.add(0.6);
        isoValues.add(0.7);
        isoValues.add(0.8);
        isoValues.add(0.9);
        isoValues.add(1.0);

        clPanel = new ContourLinePlotPanel(VTKReader.points, VTKReader.triangles,
                ColorMapReader.colorMaps, isoValues, VTKReader.minPoint, VTKReader.maxPoint, VTKReader.minScalar,
                VTKReader.maxScalar);
        clPanel.setTopMargin(100);
        clPanel.setLeftMargin(60);
        clPanel.setScaleWidth(0.8);
        clPanel.setScaleHeight(0.8);
        clPanel.setBackground(Color.decode("#ffffff"));

        fcPanel = new FilledContourPlotPanel(VTKReader.points, VTKReader.triangles,
                ColorMapReader.colorMaps, clPanel.contourLines, isoValues, VTKReader.minPoint, VTKReader.maxPoint,
                VTKReader.minScalar,
                VTKReader.maxScalar);
        fcPanel.setTopMargin(100);
        fcPanel.setLeftMargin(60);
        fcPanel.setScaleWidth(0.8);
        fcPanel.setScaleHeight(0.8);
        fcPanel.setBackground(Color.decode("#ffffff"));

        JPanel isoPanel = new JPanel();
        isoPanel.setBackground(Color.decode("#d2d2d2"));

        isoPanel.setPreferredSize(new Dimension(200, 800));
        clPanel.setPreferredSize(new Dimension(600, 700));
        fcPanel.setPreferredSize(new Dimension(600, 700));

        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout());
        panel1.setBorder(new EmptyBorder(40, 40, 40, 20));

        panel2 = new JPanel();
        panel2.setLayout(new BorderLayout());
        panel2.setBorder(new EmptyBorder(40, 20, 40, 40));

        JLabel label1 = new JLabel("Contour Line Panel");
        label1.setPreferredSize(new Dimension(600, 100));
        label1.setBackground(Color.decode("#2196F3"));
        label1.setOpaque(true);
        label1.setVerticalAlignment(JLabel.CENTER);
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel label2 = new JLabel("Filled Contour Panel");
        label2.setPreferredSize(new Dimension(600, 100));
        label2.setBackground(Color.decode("#2196F3"));
        label2.setOpaque(true);
        label2.setVerticalAlignment(JLabel.CENTER);
        label2.setHorizontalAlignment(JLabel.CENTER);
        label2.setFont(new Font("Arial", Font.BOLD, 32));

        Border border1 = BorderFactory.createLineBorder(Color.decode("#2196F3"), 10);
        clPanel.setBorder(border1);
        fcPanel.setBorder(border1);

        panel1.add(label1, BorderLayout.NORTH);
        panel1.add(clPanel, BorderLayout.SOUTH);
        panel2.add(label2, BorderLayout.NORTH);
        panel2.add(fcPanel, BorderLayout.SOUTH);

        JButton button = new JButton("Select File");
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected File: " + selectedFile.getAbsolutePath());
                    update(selectedFile.getAbsolutePath());
                }
            }
        });
        isoPanel.add(button);

        frame.add(isoPanel);
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(panel1);
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(panel2);

        frame.pack();
        frame.setVisible(true);
    }

    static void update(String filePath) {
        VTKReader.readVTKFile(filePath);

        clPanel.setData(VTKReader.points, VTKReader.triangles, ColorMapReader.colorMaps,
                clPanel.isoValues, VTKReader.minPoint, VTKReader.maxPoint, VTKReader.minScalar, VTKReader.maxScalar);

        fcPanel.setData(VTKReader.points, VTKReader.triangles,
                ColorMapReader.colorMaps, clPanel.contourLines, clPanel.isoValues, VTKReader.minPoint,
                VTKReader.maxPoint,
                VTKReader.minScalar,
                VTKReader.maxScalar);

        clPanel.revalidate();
        clPanel.repaint();

        fcPanel.revalidate();
        fcPanel.repaint();
    }
}