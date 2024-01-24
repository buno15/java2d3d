package src;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;

import javax.swing.JPanel;

import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.View;
import org.jogamp.java3d.utils.picking.behaviors.PickRotateBehavior;
import org.jogamp.java3d.utils.picking.behaviors.PickTranslateBehavior;
import org.jogamp.java3d.utils.picking.behaviors.PickZoomBehavior;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

public class ViewerPanel extends JPanel {
    static final int MODE_POINT_CLOUD = 1;
    static final int MODE_WIREFRAME = 2;
    static final int MODE_WIREFRAME_FILL = 3;
    static final int MODE_SMOOTH_SHADING = 4;
    static final int MODE_FLAT_SHADING = 5;

    private Canvas3D canvas;
    private ColorMapManager cmm = new ColorMapManager();

    private MeshManager meshManager;
    float maxDistance = Float.MIN_VALUE;
    float minDistance = Float.MAX_VALUE;
    int chooseVertexIndex = 0;

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
    }

    public ViewerPanel(int viewMode, String fileName) {
        cmm.readColorMapFile("./test_data/CoolWarmFloat257.csv");

        ObjReader objReader = new ObjReader();
        meshManager = objReader.readobj(fileName);
        meshManager.setNormals();

        for (int i = 0; i < meshManager.getNumVertices(); i++) {
            float distance = meshManager.getGeodesicDistance(chooseVertexIndex, i);
            maxDistance = Math.max(maxDistance, distance);
            minDistance = Math.min(minDistance, distance);
            meshManager.setVertexWeight(i, distance);
        }

        GraphicsConfiguration cf = SimpleUniverse.getPreferredConfiguration();
        canvas = new Canvas3D(cf);
        setLayout(new BorderLayout());
        add("Center", canvas);
        add(canvas, BorderLayout.CENTER);
        setOpaque(true);

        SimpleUniverse universe = new SimpleUniverse(canvas);
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();
        BranchGroup rootScene = new BranchGroup();
        rootScene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        rootScene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        rootScene.setCapability(BranchGroup.ALLOW_DETACH);
        rootScene.setName("Root Scene");

        BranchGroup contentScene = new BranchGroup();
        contentScene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        contentScene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        contentScene.setCapability(BranchGroup.ALLOW_DETACH);
        contentScene.setName("Content Scene");
        contentScene.addChild(createSphere(viewMode, 0.2));
        contentScene.compile();

        // camera position
        TransformGroup viewTransformGroup = viewingPlatform.getViewPlatformTransform();
        Transform3D viewTransform3D = new Transform3D();
        Point3d cameraPosition = new Point3d(0, 0, 0);
        viewTransform3D.setTranslation(new Vector3d(cameraPosition));
        viewTransformGroup.setTransform(viewTransform3D);

        // view direction
        View view = universe.getViewer().getView();
        double frontClipDistance = 0.001;
        double backClipDistance = 10000.0;
        view.setFrontClipDistance(frontClipDistance);
        view.setBackClipDistance(backClipDistance);
        universe.getViewingPlatform().setNominalViewingTransform();

        // background color
        Background background = new Background(ColorMapManager.WHITE);
        background.setName("Background");
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        rootScene.addChild(background);
        rootScene.addChild(getBaseLight());
        rootScene.addChild(contentScene);
        setBehavior(rootScene, viewTransformGroup);
        rootScene.compile();

        universe.addBranchGraph(rootScene);
    }

    private Group createSphere(int viewMode, double scale) {
        BranchGroup contentShape = new BranchGroup();
        contentShape.setName("Shape Scene");
        switch (viewMode) {
            case MODE_POINT_CLOUD:
                PointCloud pointCloud = new PointCloud(meshManager, cmm);
                contentShape
                        .addChild(pointCloud.createPointCloud(canvas, chooseVertexIndex, minDistance, maxDistance));
                break;
            case MODE_WIREFRAME:
                WireFrame wireFrame = new WireFrame(meshManager);
                contentShape.addChild(wireFrame.setWireFrame());
                break;
            case MODE_WIREFRAME_FILL:
                FilledWireFrame filledWireFrame = new FilledWireFrame(meshManager);
                contentShape.addChild(filledWireFrame.setFilledWireFrame());
                break;
            case MODE_FLAT_SHADING:
                FlatShading flatShading = new FlatShading(meshManager);
                contentShape.addChild(flatShading.setFlatShading());
                break;
            case MODE_SMOOTH_SHADING:
                SmoothShading smoothShading = new SmoothShading(meshManager, cmm);
                contentShape.addChild(
                        smoothShading.setSmoothShading(canvas, chooseVertexIndex, minDistance, maxDistance));
                break;
        }

        Transform3D t3d = new Transform3D();
        t3d.set(scale, new Vector3d(0, 0, 0));
        TransformGroup tg = new TransformGroup(t3d);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);
        tg.addChild(contentShape);

        return tg;
    }

    private void setBehavior(BranchGroup shape, TransformGroup tg) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        PickRotateBehavior behavior1 = new PickRotateBehavior(shape, canvas, bounds);
        shape.addChild(behavior1);
        PickZoomBehavior behavior2 = new PickZoomBehavior(shape, canvas, bounds);
        shape.addChild(behavior2);
        PickTranslateBehavior behavior3 = new PickTranslateBehavior(shape, canvas, bounds);
        shape.addChild(behavior3);

        // MouseBehavior behavior4 = new MouseRotate();
        // behavior4.setTransformGroup(tg);
        // behavior4.setSchedulingBounds(bounds);
        // shape.addChild(behavior4);
        // MouseBehavior behavior5 = new MouseTranslate();
        // behavior5.setTransformGroup(tg);
        // behavior5.setSchedulingBounds(bounds);
        // shape.addChild(behavior5);
        // MouseBehavior behavior6 = new MouseZoom();
        // behavior6.setTransformGroup(tg);
        // behavior6.setSchedulingBounds(bounds);
        // shape.addChild(behavior6);
    }

    private DirectionalLight getBaseLight() {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = ColorMapManager.GRAY;
        Vector3f lightDirection = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
        light.setInfluencingBounds(bounds);
        light.setName("Base Light");
        return light;
    }

    // private void exploreNodes(Node node, int depth) {
    // // Indentation for hierarchy visualization
    // for (int i = 0; i < depth; i++) {
    // System.out.print(" ");
    // }

    // // Print node class and name
    // System.out.println(node.getClass().getSimpleName() + ": " + node.getName());

    // // If this node is a group, explore its children
    // if (node instanceof Group) {
    // Group group = (Group) node;
    // Iterator<Node> iterator = group.getAllChildren();
    // while (iterator.hasNext()) {
    // exploreNodes(iterator.next(), depth + 1);
    // }
    // }
    // }

    // fixme: 未完成
    // private void saveObjFile(BranchGroup group, String filename) {
    // try (PrintWriter out = new PrintWriter(new BufferedWriter(new
    // FileWriter(filename)))) {
    // AtomicBoolean shape3DNodeFound = new AtomicBoolean(false);
    // group.getAllChildren().forEachRemaining(node -> {
    // if (node instanceof Shape3D) {
    // shape3DNodeFound.set(true);
    // Shape3D shape = (Shape3D) node;
    // GeometryArray geomArray = (GeometryArray) shape.getGeometry();
    // float[] vertexData = new float[geomArray.getVertexCount() * 3];
    // geomArray.getCoordinates(0, vertexData);
    // for (int i = 0; i < vertexData.length; i += 3) {
    // out.printf("v %f %f %f%n", vertexData[i], vertexData[i + 1], vertexData[i +
    // 2]);
    // }
    // }
    // });
    // if (!shape3DNodeFound.get()) {
    // System.out.println("Shape3D型のノードが見つかりませんでした。");
    // } else {
    // System.out.println("OBJファイルを保存しました: " + filename);
    // }
    // } catch (IOException e) {
    // System.out.println("OBJファイルの保存中にエラーが発生しました: " + e.getMessage());
    // }
    // }
}