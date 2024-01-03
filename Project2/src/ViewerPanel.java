package src;

import java.awt.BorderLayout;
import java.awt.GraphicsConfiguration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JPanel;

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

public class ViewerPanel extends JPanel {
    private final Color3f BLACK = new Color3f(0.0f, 0.0f, 0.0f);
    private final Color3f WHITE = new Color3f(1.0f, 1.0f, 1.0f);
    private final Color3f GRAY = new Color3f(0.4f, 0.4f, 0.4f);

    static final int MODE_POINT_CLOUD = 1;
    static final int MODE_WIREFRAME = 2;
    static final int MODE_WIREFRAME_FILL = 3;
    static final int MODE_SMOOTH_SHADING = 4;
    static final int MODE_FLAT_SHADING = 5;

    private String fileName;
    private int viewMode = MODE_POINT_CLOUD;

    private Canvas3D canvas;
    private BranchGroup contentScene;

    @Override
    public void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
    }

    public ViewerPanel(int viewMode, String fileName) {
        this.viewMode = viewMode;
        this.fileName = fileName;
        System.out.println(this.viewMode + " : " + this.fileName);

        GraphicsConfiguration cf = SimpleUniverse.getPreferredConfiguration();

        setLayout(new BorderLayout());
        canvas = new Canvas3D(cf);
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

        contentScene = new BranchGroup();
        contentScene.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        contentScene.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        contentScene.setCapability(BranchGroup.ALLOW_DETACH);
        contentScene.setName("Content Scene");

        // camera position
        TransformGroup viewTransformGroup = viewingPlatform.getViewPlatformTransform();
        Transform3D viewTransform3D = new Transform3D();
        Point3d cameraPosition = new Point3d(0, 0, 0);
        viewTransform3D.setTranslation(new Vector3d(cameraPosition));
        viewTransformGroup.setTransform(viewTransform3D);

        // view direction
        View view = universe.getViewer().getView();
        double frontClipDistance = 0.001;
        double backClipDistance = 1000.0;
        view.setFrontClipDistance(frontClipDistance);
        view.setBackClipDistance(backClipDistance);
        universe.getViewingPlatform().setNominalViewingTransform();

        // background color
        Background background = new Background(WHITE);
        background.setName("Background");
        background.setApplicationBounds(new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0));
        rootScene.addChild(background);
        rootScene.addChild(getBaseLight());
        rootScene.addChild(contentScene);

        if (viewMode == MODE_POINT_CLOUD) {
            contentScene.addChild(createPointCloud(canvas, fileName, 0.5));
        } else {
            contentScene.addChild(createSphere(canvas, fileName, viewMode, 0.5));
        }

        contentScene.compile();
        rootScene.compile();
        universe.addBranchGraph(rootScene);

        saveObjFile(rootScene, "./test_data/teapot.xyz");
        exploreNodes(rootScene, 0);
    }

    private Group createSphere(Canvas3D canvas, String fileName, int viewMode, double scale) {
        Transform3D t = new Transform3D();
        t.set(scale, new Vector3d(0, 0, 0));

        TransformGroup objTrans = new TransformGroup(t);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        Appearance appearance = new Appearance();
        appearance.setMaterial(new Material(BLACK, BLACK, GRAY, WHITE, 80.0f));

        BranchGroup shape = loadObjFile(fileName);
        if (shape != null) {
            shape.setName("Loaded OBJ");
            switch (viewMode) {
                case MODE_WIREFRAME:
                    setWireFrame(shape);
                    break;
                case MODE_WIREFRAME_FILL:
                    setWireframeAndFill(shape);
                    break;
                case MODE_SMOOTH_SHADING:
                    setSmoothShading(shape);
                    break;
                case MODE_FLAT_SHADING:
                    setFlatShading(shape);
                    break;
                default:
                    setWireFrame(shape);
                    break;
            }
        } else {
            System.out.println("OBJ object is null");
        }

        setBehavior(shape, canvas);
        objTrans.addChild(shape);

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

    public void setWireframeAndFill(BranchGroup shape) {
        Node node = shape.getChild(0);
        if (node instanceof Shape3D) {
            Shape3D originalShape = (Shape3D) node;
            Geometry geometry = originalShape.getGeometry();

            // fill
            Appearance fillAppearance = new Appearance();
            ColoringAttributes ca = new ColoringAttributes();
            ca.setColor(GRAY);
            fillAppearance.setColoringAttributes(ca);

            // wireframe
            Appearance wireframeAppearance = new Appearance();
            PolygonAttributes polyAttr = new PolygonAttributes();
            polyAttr.setPolygonMode(PolygonAttributes.POLYGON_LINE);
            polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
            wireframeAppearance.setPolygonAttributes(polyAttr);
            ColoringAttributes ca2 = new ColoringAttributes();
            ca2.setColor(BLACK);
            wireframeAppearance.setColoringAttributes(ca2);

            Shape3D fillShape = new Shape3D(geometry, fillAppearance);

            Shape3D wireframeShape = new Shape3D(geometry, wireframeAppearance);

            shape.addChild(fillShape);
            shape.addChild(wireframeShape);
        }
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

    public Group createPointCloud(Canvas3D canvas, String filename, double scale) {
        Transform3D t = new Transform3D();
        t.set(scale, new Vector3d(0, 0, 0));

        TransformGroup objTrans = new TransformGroup(t);
        objTrans.setName("Transform Group");
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTrans.setCapability(TransformGroup.ENABLE_PICK_REPORTING);

        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setName("Point Attributes");
        pointAttributes.setPointSize(2.0f);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);

        Appearance appearance = new Appearance();
        appearance.setName("Appearance");
        appearance.setPointAttributes(pointAttributes);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(GRAY);
        appearance.setColoringAttributes(ca);

        PointArray points = loadPointCloudFromOBJ(filename);
        int numPoints = points.getVertexCount();

        for (int i = 0; i < numPoints; i++) {
            float[] coord = new float[3];
            points.getCoordinate(i, coord);
        }

        Shape3D pointCloudShape = createPointCloudShape(points);
        pointCloudShape.setName("Point Cloud Shape");
        pointCloudShape.setAppearance(appearance);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(pointCloudShape);
        setBehavior(shape, canvas);

        objTrans.addChild(shape);

        return objTrans;
    }

    public Shape3D createPointCloudShape(PointArray points) {
        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setName("Point Attributes");
        pointAttributes.setPointSize(2.0f);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);

        Appearance appearance = new Appearance();
        appearance.setName("appearance");
        appearance.setPointAttributes(pointAttributes);

        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(BLACK);
        appearance.setColoringAttributes(ca);

        Shape3D pointCloud = new Shape3D();
        pointCloud.setGeometry(points);
        pointCloud.setAppearance(appearance);

        return pointCloud;
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

    private void setBehavior(BranchGroup shape, Canvas3D canvas) {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        PickRotateBehavior behavior1 = new PickRotateBehavior(shape, canvas, bounds);
        shape.addChild(behavior1);
        PickZoomBehavior behavior2 = new PickZoomBehavior(shape, canvas, bounds);
        shape.addChild(behavior2);
        PickTranslateBehavior behavior3 = new PickTranslateBehavior(shape, canvas, bounds);
        shape.addChild(behavior3);
    }

    private DirectionalLight getBaseLight() {
        BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);
        Color3f lightColor = WHITE;
        Vector3f lightDirection = new Vector3f(-1.0f, -1.0f, -1.0f);
        DirectionalLight light = new DirectionalLight(lightColor, lightDirection);
        light.setInfluencingBounds(bounds);
        light.setName("Base Light");
        return light;
    }

    private BranchGroup loadObjFile(String filename) {
        ObjectFile loader = new ObjectFile(ObjectFile.RESIZE);
        Scene modelScene = null;

        try {
            modelScene = loader.load(filename);
            System.out.println("Find obj: " + filename);
        } catch (FileNotFoundException e) {
            System.out.println("Do not find obj: " + e.getMessage());
            return null;
        } catch (ParsingErrorException e) {
            System.out.println("Reading error: " + e.getMessage());
            return null;
        } catch (IncorrectFormatException e) {
            System.out.println("File format error: " + e.getMessage());
            return null;
        }

        return modelScene.getSceneGroup();
    }

    private void exploreNodes(Node node, int depth) {
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
                exploreNodes(iterator.next(), depth + 1);
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
}