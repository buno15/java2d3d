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
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

class ContourPlotMain {

    static ContourLinePlotPanel clPanel;
    static FilledContourPlotPanel fcPanel;

    static List<Double> isoValues = new ArrayList<>();

    static String filePath = "";

    public static void main(String[] args) {
        ColorMapReader.readColorMapFile("./CoolWarmFloat257.csv");

        isoValues.add(0.0);
        isoValues.add(0.01);
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

        JFrame frame = new JFrame("Contour Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
        frame.setResizable(false);

        clPanel = new ContourLinePlotPanel(VTKReader.triangles,
                ColorMapReader.colorMaps, isoValues, VTKReader.minPoint, VTKReader.maxPoint, VTKReader.minScalar,
                VTKReader.maxScalar);
        clPanel.setTopMargin(100);
        clPanel.setLeftMargin(60);
        clPanel.setScaleWidth(0.8);
        clPanel.setScaleHeight(0.8);
        clPanel.setBackground(Color.decode("#ffffff"));

        fcPanel = new FilledContourPlotPanel(VTKReader.triangles,
                ColorMapReader.colorMaps, VTKReader.minPoint, VTKReader.maxPoint,
                VTKReader.minScalar,
                VTKReader.maxScalar);
        fcPanel.setTopMargin(100);
        fcPanel.setLeftMargin(60);
        fcPanel.setScaleWidth(0.8);
        fcPanel.setScaleHeight(0.8);
        fcPanel.setBackground(Color.decode("#ffffff"));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#d2d2d2"));

        leftPanel.setPreferredSize(new Dimension(200, 800));
        clPanel.setPreferredSize(new Dimension(600, 700));
        fcPanel.setPreferredSize(new Dimension(600, 700));

        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BorderLayout());
        middlePanel.setBorder(new EmptyBorder(40, 40, 40, 20));

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        rightPanel.setBorder(new EmptyBorder(40, 20, 40, 40));

        JLabel clLabel = new JLabel("Contour Line Panel");
        clLabel.setPreferredSize(new Dimension(600, 100));
        clLabel.setBackground(Color.decode("#c2c2c2"));
        clLabel.setOpaque(true);
        clLabel.setVerticalAlignment(JLabel.CENTER);
        clLabel.setHorizontalAlignment(JLabel.CENTER);
        clLabel.setFont(new Font("Arial", Font.BOLD, 32));

        JLabel fcLabel = new JLabel("Filled Contour Panel");
        fcLabel.setPreferredSize(new Dimension(600, 100));
        fcLabel.setBackground(Color.decode("#c2c2c2"));
        fcLabel.setOpaque(true);
        fcLabel.setVerticalAlignment(JLabel.CENTER);
        fcLabel.setHorizontalAlignment(JLabel.CENTER);
        fcLabel.setFont(new Font("Arial", Font.BOLD, 32));

        Border border = BorderFactory.createLineBorder(Color.decode("#c2c2c2"), 1);
        clPanel.setBorder(border);
        fcPanel.setBorder(border);

        middlePanel.add(clLabel, BorderLayout.NORTH);
        middlePanel.add(clPanel, BorderLayout.SOUTH);
        rightPanel.add(fcLabel, BorderLayout.NORTH);
        rightPanel.add(fcPanel, BorderLayout.SOUTH);

        JButton selectButton = new JButton("Select File");
        selectButton.setFont(new Font("Arial", Font.BOLD, 16));
        selectButton.setSize(100, 50);
        selectButton.addActionListener(new ActionListener() {
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
        leftPanel.add(selectButton);

        JPanel isoPanel = new JPanel();
        isoPanel.setLayout(new BoxLayout(isoPanel, BoxLayout.Y_AXIS));
        isoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        leftPanel.add(isoPanel);

        refreshList(isoPanel);

        frame.add(leftPanel);
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(middlePanel);
        frame.add(Box.createRigidArea(new Dimension(5, 0)));
        frame.add(rightPanel);

        frame.pack();
        frame.setVisible(true);
    }

    static void update(String filePath) {
        if (filePath.equals(""))
            return;

        VTKReader.readVTKFile(filePath);
        ContourPlotMain.filePath = filePath;

        clPanel.reloadData(VTKReader.triangles, ColorMapReader.colorMaps,
                isoValues, VTKReader.minPoint, VTKReader.maxPoint, VTKReader.minScalar, VTKReader.maxScalar);

        fcPanel.reloadData(VTKReader.triangles,
                ColorMapReader.colorMaps, VTKReader.minPoint,
                VTKReader.maxPoint,
                VTKReader.minScalar,
                VTKReader.maxScalar);

        clPanel.revalidate();
        clPanel.repaint();

        fcPanel.revalidate();
        fcPanel.repaint();
    }

    private static void refreshList(JPanel panel) {
        panel.removeAll();

        DefaultListModel<Double> listModel = new DefaultListModel<>();
        isoValues.sort((a, b) -> a.compareTo(b));

        for (Double d : isoValues) {
            listModel.addElement(d);
        }

        JPanel addPanel = new JPanel();
        JTextField textField = new JTextField(6);
        JButton addButton = new JButton("Add");

        addPanel.add(textField);
        addPanel.add(addButton);
        addButton.addActionListener(e -> {
            double value = Double.parseDouble(textField.getText());
            listModel.addElement(value);
            isoValues.add(value);
            textField.setText("");
            refreshList(panel);
            update(ContourPlotMain.filePath);
        });
        panel.add(addPanel);

        for (int i = 0; i < listModel.size(); i++) {
            Double item = listModel.get(i);
            JPanel itemPanel = new JPanel();
            JButton deleteButton = new JButton("Delete");

            int index = i;
            deleteButton.addActionListener(e -> {
                listModel.remove(index);
                isoValues.remove(index);
                refreshList(panel);
                update(ContourPlotMain.filePath);
            });
            itemPanel.add(new JLabel(item.toString()));
            itemPanel.add(deleteButton);
            panel.add(itemPanel);
        }
        panel.revalidate();
        panel.repaint();
    }
}