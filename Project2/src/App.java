package src;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JFrame;

import org.jogamp.java3d.Alpha;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.RotationInterpolator;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.geometry.ColorCube;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.vecmath.Point3d;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setSize(640, 480);
        frame.setLayout(new BorderLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration cf = SimpleUniverse.getPreferredConfiguration();

        System.out.println("Graphics configuration: " + cf);

        Canvas3D canvas = new Canvas3D(cf);
        frame.add("Center", canvas);

        SimpleUniverse univ = new SimpleUniverse(canvas);
        univ.getViewingPlatform().setNominalViewingTransform();

        BranchGroup scene = createSceneGraph();
        scene.compile();
        univ.addBranchGraph(scene);

        frame.setVisible(true);
    }

    private static BranchGroup createSceneGraph() {
        // Make a scene graph branch
        BranchGroup branch = new BranchGroup();

        // Make a changeable 3D transform
        TransformGroup trans = new TransformGroup();
        trans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        branch.addChild(trans);

        // Make a shape
        ColorCube demo = new ColorCube(0.4);
        trans.addChild(demo);

        // Make a behavor to spin the shape
        Alpha spinAlpha = new Alpha(-1, 4000);
        RotationInterpolator spinner = new RotationInterpolator(spinAlpha, trans);
        spinner.setSchedulingBounds(
                new BoundingSphere(new Point3d(), 1000.0));
        trans.addChild(spinner);

        return branch;
    }
}