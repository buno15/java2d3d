package src;

import java.awt.GraphicsConfiguration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JFrame;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.Background;
import org.jogamp.java3d.BoundingSphere;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.DirectionalLight;
import org.jogamp.java3d.Geometry;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.IndexedGeometryArray;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.Node;
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.Transform3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.TransparencyAttributes;
import org.jogamp.java3d.View;
import org.jogamp.java3d.loaders.IncorrectFormatException;
import org.jogamp.java3d.loaders.ParsingErrorException;
import org.jogamp.java3d.loaders.Scene;
import org.jogamp.java3d.loaders.objectfile.ObjectFile;
import org.jogamp.java3d.utils.picking.behaviors.PickRotateBehavior;
import org.jogamp.java3d.utils.picking.behaviors.PickTranslateBehavior;
import org.jogamp.java3d.utils.picking.behaviors.PickZoomBehavior;
import org.jogamp.java3d.utils.universe.SimpleUniverse;
import org.jogamp.java3d.utils.universe.ViewingPlatform;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3d;
import org.jogamp.vecmath.Vector3f;

public class App extends JFrame {

    public App() {
        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        GraphicsConfiguration cf = SimpleUniverse.getPreferredConfiguration();

        Canvas3D canvas = new Canvas3D(cf);
        add("Center", canvas);

        SimpleUniverse univ = new SimpleUniverse(canvas);
        setupViewingEnvironment(univ);
        univ.getViewingPlatform().setNominalViewingTransform();

        BranchGroup scene = new BranchGroup();
        Background background = new Background(new Color3f(1.0f, 1.0f, 1.0f));
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0,
                0.0), 100.0));
        scene.addChild(background);

        // addPointCloudToScene(scene, "./test_data/turtle.obj", canvas);

        scene.addChild(createSphere(0.8, 0.0, 0.0, "./test_data/teddy.obj",
                canvas));
        addLight(scene);

        scene.compile();

        univ.addBranchGraph(scene);

        saveObjFile(scene, "./test_data/teapot.xyz");

        setVisible(true);

        exploreNode(scene, 0);
    }

    private void setupViewingEnvironment(SimpleUniverse universe) {
        ViewingPlatform viewingPlatform = universe.getViewingPlatform();

        // camera position
        TransformGroup viewTransformGroup = viewingPlatform.getViewPlatformTransform();
        Transform3D viewTransform3D = new Transform3D();
        Point3d cameraPosition = new Point3d(0, 0, 0);
        viewTransform3D.setTranslation(new Vector3d(cameraPosition));
        viewTransformGroup.setTransform(viewTransform3D);

        // view direction
        View view = universe.getViewer().getView();
        double frontClipDistance = 0.1;
        double backClipDistance = 100.0;
        view.setFrontClipDistance(frontClipDistance);
        view.setBackClipDistance(backClipDistance);
    }

    private Group createSphere(double scale, double xpos, double ypos, String fileName, Canvas3D canvas) {
        Transform3D t = new Transform3D();
        t.set(scale, new Vector3d(xpos, ypos, 0.0));
        TransformGroup objTrans = new TransformGroup(t);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        TransformGroup spinTg = new TransformGroup();
        spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        spinTg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        Appearance app = new Appearance();

        Color3f black = new Color3f(0.0f, 0.0f, 0.0f);
        Color3f white = new Color3f(1.0f, 1.0f, 1.0f);
        Color3f gray = new Color3f(0.4f, 0.4f, 0.4f);

        app.setMaterial(new Material(black, black, gray,
                white, 80.0f));

        BranchGroup shape = loadObjFile(fileName);
        if (shape != null) {
            shape.setName("Loaded OBJ");

            setWireFrame(shape);

            TransparencyAttributes ta = new TransparencyAttributes();
            ta.setTransparencyMode(TransparencyAttributes.BLENDED);
            ta.setTransparency(0.2f);
            app.setTransparencyAttributes(ta);

        } else {
            System.out.println("OBJオブジェクトのロードに失敗しました");
        }

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        PickRotateBehavior behavior = new PickRotateBehavior(shape, canvas, bounds);
        shape.addChild(behavior);

        PickZoomBehavior behavior2 = new PickZoomBehavior(shape, canvas, bounds);
        shape.addChild(behavior2);

        PickTranslateBehavior behavior3 = new PickTranslateBehavior(shape, canvas, bounds);
        shape.addChild(behavior3);

        spinTg.addChild(shape);
        objTrans.addChild(spinTg);

        return objTrans;
    }

    public void setWireFrame(BranchGroup shape) {
        Shape3D shape3D = (Shape3D) shape.getChild(0);

        Appearance appearance = shape3D.getAppearance();
        PolygonAttributes polyAttr = new PolygonAttributes();
        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
        polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polyAttr);
        displayMeshInfo(shape3D);
    }

    public void setSmoothShading(BranchGroup shape) {
        Shape3D shape3D = (Shape3D) shape.getChild(0);

        Appearance appearance = shape3D.getAppearance();
        PolygonAttributes polyAttr = new PolygonAttributes();
        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        appearance.setPolygonAttributes(polyAttr);

        Material material = new Material();
        material.setLightingEnable(true);
        material.setShininess(128.0f);
        appearance.setMaterial(material);
    }

    public void setFlatShading(BranchGroup shape) {
        Shape3D shape3D = (Shape3D) shape.getChild(0);

        Appearance appearance = shape3D.getAppearance();
        PolygonAttributes polyAttr = new PolygonAttributes();
        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        appearance.setPolygonAttributes(polyAttr);

        Material material = new Material();
        material.setLightingEnable(true);
        material.setShininess(128.0f);
        appearance.setMaterial(material);
    }

    private void applyAppearanceToAllChildren(Group group, Appearance appearance) {
        Iterator<Node> iterator = group.getAllChildren();
        while (iterator.hasNext()) {
            Node node = iterator.next();
            if (node instanceof Shape3D) {
                ((Shape3D) node).setAppearance(appearance);
            } else if (node instanceof Group) {
                applyAppearanceToAllChildren((Group) node, appearance);
            }
        }
    }

    public void combineWireframeAndFill(BranchGroup shape) {
        Node node = shape.getChild(0);
        if (node instanceof Shape3D) {
            Shape3D originalShape = (Shape3D) node;
            Geometry geometry = originalShape.getGeometry();

            // fill
            Appearance fillAppearance = new Appearance();
            ColoringAttributes ca = new ColoringAttributes();
            ca.setColor(new Color3f(0.4f, 0.4f, 0.4f));
            fillAppearance.setColoringAttributes(ca);

            // wireframe
            Appearance wireframeAppearance = new Appearance();
            PolygonAttributes polyAttr = new PolygonAttributes();
            polyAttr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
            polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
            wireframeAppearance.setPolygonAttributes(polyAttr);
            ColoringAttributes ca2 = new ColoringAttributes();
            ca2.setColor(new Color3f(0.0f, 0.0f, 0.0f));
            wireframeAppearance.setColoringAttributes(ca2);

            Shape3D fillShape = new Shape3D(geometry, fillAppearance);

            Shape3D wireframeShape = new Shape3D(geometry, wireframeAppearance);

            shape.addChild(fillShape);
            shape.addChild(wireframeShape);
        }
    }

    private PointArray loadPointCloudFromOBJ(String filename) {
        List<Point3f> points = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("v ")) { // find a vertex
                    String[] tokens = line.split("\\s+");
                    float x = Float.parseFloat(tokens[1]);
                    float y = Float.parseFloat(tokens[2]);
                    float z = Float.parseFloat(tokens[3]);
                    points.add(new Point3f(x, y, z));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        Point3f[] pointsArray = new Point3f[points.size()];
        points.toArray(pointsArray);

        PointArray pointArray = new PointArray(pointsArray.length, PointArray.COORDINATES);
        pointArray.setCoordinates(0, pointsArray);

        return pointArray;
    }

    // point cloud
    public Shape3D createPointCloudShape(PointArray points) {
        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setPointSize(2.0f);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);

        Appearance appearance = new Appearance();
        appearance.setPointAttributes(pointAttributes);

        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(new Color3f(0.0f, 0.0f, 0.0f));
        appearance.setColoringAttributes(ca);

        Shape3D pointCloud = new Shape3D();
        pointCloud.setGeometry(points);
        pointCloud.setAppearance(appearance);

        return pointCloud;
    }

    public void addPointCloudToScene(BranchGroup sceneRoot, String filename, Canvas3D canvas) {
        Transform3D t = new Transform3D();
        t.set(1, new Vector3d(0, 0, 0.0));
        TransformGroup objTrans = new TransformGroup(t);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        TransformGroup spinTg = new TransformGroup();
        spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        spinTg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        spinTg.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        PointArray points = loadPointCloudFromOBJ(filename);
        int numPoints = points.getVertexCount();

        for (int i = 0; i < numPoints; i++) {
            float[] coord = new float[3];
            points.getCoordinate(i, coord);
            System.out.println(Arrays.toString(coord));
        }
        Shape3D pointCloudShape = createPointCloudShape(points);
        BranchGroup shape = new BranchGroup();
        shape.addChild(pointCloudShape);

        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

        PickRotateBehavior behavior = new PickRotateBehavior(shape, canvas, bounds);
        shape.addChild(behavior);

        PickZoomBehavior behavior2 = new PickZoomBehavior(shape, canvas, bounds);
        shape.addChild(behavior2);

        PickTranslateBehavior behavior3 = new PickTranslateBehavior(shape, canvas, bounds);
        shape.addChild(behavior3);

        spinTg.addChild(shape);
        objTrans.addChild(spinTg);

        sceneRoot.addChild(objTrans);
    }

    private void addLight(BranchGroup scene) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = new Color3f(1.0f, 1.0f, 1.0f);
        Vector3f lightDirection = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
        light.setInfluencingBounds(bounds);
        scene.addChild(light);
    }

    private BranchGroup loadObjFile(String filename) {
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene modelScene = null;

        try {
            modelScene = loader.load(filename);
            System.out.println("OBJファイルをロードしました: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("OBJファイルが見つかりません: " + e.getMessage());
            return null;
        } catch (ParsingErrorException e) {
            System.out.println("解析エラー: " + e.getMessage());
            return null;
        } catch (IncorrectFormatException e) {
            System.out.println("フォーマットエラー: " + e.getMessage());
            return null;
        }

        return modelScene.getSceneGroup();
    }

    private void exploreNode(Node node, int depth) {
        // Indentation for hierarchy visualization
        for (int i = 0; i < depth; i++) {
            System.out.print("  ");
        }

        // Print node class and name
        System.out.println(node.getClass().getSimpleName() + ": " + node.getName());

        // If this node is a group, explore its children
        if (node instanceof Group) {
            Group group = (Group) node;
            Iterator<Node> iterator = group.getAllChildren();
            while (iterator.hasNext()) {
                exploreNode(iterator.next(), depth + 1);
            }
        }
    }

    private void displayMeshInfo(Shape3D shape) {
        Geometry geometry = shape.getGeometry();
        System.out.println("Geometry: " + geometry.getName());

        if (geometry instanceof GeometryArray) {
            GeometryArray geometryArray = (GeometryArray) geometry;

            // Get the number of vertices
            int vertexCount = geometryArray.getVertexCount();
            System.out.println("Number of vertices: " + vertexCount);

            // Get the number of edges
            if (geometryArray instanceof IndexedGeometryArray) {
                IndexedGeometryArray indexedGeometryArray = (IndexedGeometryArray) geometryArray;
                int faceCount = indexedGeometryArray.getIndexCount() / 3;
                int edgeCount = faceCount * 3 / 2;
                System.out.println("Number of edges: " + edgeCount);

                // Get the number of faces
                System.out.println("Number of faces: " + faceCount);
            }
        }
    }

    // fixme: 未完成
    private void saveObjFile(BranchGroup group, String filename) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filename)))) {
            AtomicBoolean shape3DNodeFound = new AtomicBoolean(false);
            group.getAllChildren().forEachRemaining(node -> {
                if (node instanceof Shape3D) {
                    shape3DNodeFound.set(true);
                    Shape3D shape = (Shape3D) node;
                    GeometryArray geomArray = (GeometryArray) shape.getGeometry();
                    float[] vertexData = new float[geomArray.getVertexCount() * 3];
                    geomArray.getCoordinates(0, vertexData);
                    for (int i = 0; i < vertexData.length; i += 3) {
                        out.printf("v %f %f %f%n", vertexData[i], vertexData[i + 1], vertexData[i + 2]);
                    }
                }
            });
            if (!shape3DNodeFound.get()) {
                System.out.println("Shape3D型のノードが見つかりませんでした。");
            } else {
                System.out.println("OBJファイルを保存しました: " + filename);
            }
        } catch (IOException e) {
            System.out.println("OBJファイルの保存中にエラーが発生しました: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        new App();
    }
}