package src;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TransformGroup;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickIntersection;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;

public class PointCloud {
    MeshManager meshManager;
    ColorMapManager cmm;

    public PointCloud(MeshManager meshList, ColorMapManager cmm) {
        this.meshManager = meshList;
        this.cmm = cmm;
    }

    public Group createPointCloud(Canvas3D canvas3d) {

        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setName("Point Attributes");
        pointAttributes.setPointSize(10.0f);
        pointAttributes.setPointAntialiasingEnable(true);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);
        Appearance appearance = new Appearance();
        appearance.setName("Appearance");
        appearance.setPointAttributes(pointAttributes);

        int numVertices = meshManager.getNumVertices();
        PointArray points = new PointArray(numVertices, PointArray.COORDINATES | PointArray.COLOR_3);
        points.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
        points.setCapability(GeometryArray.ALLOW_COLOR_READ);

        for (int i = 0; i < numVertices; i++) {
            Vector vertex = meshManager.getVertex(i);
            points.setCoordinate(i, new Point3f(vertex.x, vertex.y, vertex.z));

            points.setColor(i, ColorMapManager.BLACK);
        }

        Shape3D pointCloud = new Shape3D(points, new Appearance());
        pointCloud.setName("Point Cloud");
        pointCloud.setAppearance(appearance);

        TransformGroup tg = new TransformGroup();
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");

        shape.addChild(pointCloud);

        shape.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
        shape.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
        shape.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
        pointCloud.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
        pointCloud.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
        pointCloud.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

        canvas3d.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pick(canvas3d, shape, pointCloud, points, e.getX(), e.getY());
            }
        });

        return shape;
    }

    private void pick(Canvas3D canvas3d, BranchGroup shape, Shape3D pointCloud, PointArray points, int x, int y) {
        PickCanvas pickCanvas = new PickCanvas(canvas3d, shape);
        pickCanvas.setMode(PickTool.GEOMETRY_INTERSECT_INFO);
        pickCanvas.setShapeLocation(x, y);
        PickResult result = pickCanvas.pickClosest();

        if (result != null) {
            System.out.println(x + " " + y);

            Point3d point3f = new Point3d(x, y, 0);
            PickIntersection pi = result.getClosestIntersection(point3f);
            int[] vertexIndices = pi.getPrimitiveCoordinateIndices();

            if (vertexIndices.length > 0) {
                int selectedPointIndex = vertexIndices[0];

                System.out.println("Selected Point Index: " + selectedPointIndex);

                points.setColor(selectedPointIndex, ColorMapManager.GREEN);

                meshManager.calculateVerticesDistanceWeight(selectedPointIndex);

                int numVertices = meshManager.getNumVertices();

                for (int i = 0; i < numVertices; i++) {
                    if (i == selectedPointIndex)
                        continue;

                    Vector vertex = meshManager.getVertex(i);

                    Color color = cmm.getColorFromScalar(vertex.w, meshManager.minDistance, meshManager.maxDistance);
                    Color3f color3f = new Color3f((float) color.getRed() / 255.0f,
                            (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f);

                    points.setColor(i, color3f);
                }

                pointCloud.setGeometry(points);
            }
        }
    }
}
