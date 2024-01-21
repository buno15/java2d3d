package src;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ViewerMain {

    static JFrame frame = new JFrame("Viewer");
    static ViewerPanel viewerPanel;

    static int viewMode = ViewerPanel.MODE_POINT_CLOUD;
    static String filePath = "./test_data/teddy.obj";

    public static void main(String[] args) {
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));

        JPanel leftPanel = new JPanel();
        leftPanel.setBackground(Color.decode("#d2d2d2"));
        leftPanel.setPreferredSize(new Dimension(200, 800));
        frame.add(leftPanel);

        viewerPanel = new ViewerPanel(ViewerPanel.MODE_POINT_CLOUD, filePath);
        viewerPanel.setPreferredSize(new Dimension(1000, 800));
        frame.add(viewerPanel);

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
                    filePath = selectedFile.getAbsolutePath();
                    updateViewerPanel();
                }
            }
        });
        leftPanel.add(selectButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 100)));

        JButton pointCloudButton = new JButton("Point Cloud");
        pointCloudButton.setFont(new Font("Arial", Font.BOLD, 16));
        pointCloudButton.setSize(100, 50);
        pointCloudButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMode = ViewerPanel.MODE_POINT_CLOUD;
                updateViewerPanel();
            }
        });
        leftPanel.add(pointCloudButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton wireframeButton = new JButton("Wireframe");
        wireframeButton.setFont(new Font("Arial", Font.BOLD, 16));
        wireframeButton.setSize(100, 50);
        wireframeButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMode = ViewerPanel.MODE_WIREFRAME;
                updateViewerPanel();
            }
        });
        leftPanel.add(wireframeButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton wireframefillButton = new JButton("Wireframe Fill");
        wireframefillButton.setFont(new Font("Arial", Font.BOLD, 16));
        wireframefillButton.setSize(100, 50);
        wireframefillButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMode = ViewerPanel.MODE_WIREFRAME_FILL;
                updateViewerPanel();
            }
        });
        leftPanel.add(wireframefillButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton smoothButton = new JButton("Flat Shading");
        smoothButton.setFont(new Font("Arial", Font.BOLD, 16));
        smoothButton.setSize(100, 50);
        smoothButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMode = ViewerPanel.MODE_FLAT_SHADING;
                updateViewerPanel();
            }
        });
        leftPanel.add(smoothButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        JButton flatButton = new JButton("Smooth Shading");
        flatButton.setFont(new Font("Arial", Font.BOLD, 16));
        flatButton.setSize(100, 50);
        flatButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                viewMode = ViewerPanel.MODE_SMOOTH_SHADING;
                updateViewerPanel();
            }
        });
        leftPanel.add(flatButton);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 50)));

        frame.pack();
        frame.setVisible(true);
    }

    static void updateViewerPanel() {
        frame.remove(viewerPanel);
        viewerPanel = new ViewerPanel(viewMode, filePath);
        viewerPanel.setPreferredSize(new Dimension(1000, 800));
        frame.add(viewerPanel);
        viewerPanel.revalidate();
        viewerPanel.repaint();
    }
}
