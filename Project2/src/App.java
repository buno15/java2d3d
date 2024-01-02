package src;

import java.io.FileReader;

import javax.swing.JFrame;

import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.universe.SimpleUniverse;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Java 3D OBJ Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Canvas3D canvas3D = new Canvas3D(SimpleUniverse.getPreferredConfiguration());
        frame.add(canvas3D);
        frame.setSize(800, 600);
        frame.setVisible(true);

        SimpleUniverse universe = new SimpleUniverse(canvas3D);
        universe.getViewingPlatform().setNominalViewingTransform();

        BranchGroup sceneGroup = new BranchGroup();
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene scene = null;

        try {
            scene = loader.load(new FileReader("/Project2/test_data/armadillo.obj"));
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        sceneGroup.addChild(scene.getSceneGroup());
        universe.addBranchGraph(sceneGroup);
    }
}