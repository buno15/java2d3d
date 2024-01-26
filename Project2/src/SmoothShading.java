package src;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.GeometryArray;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickIntersection;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class SmoothShading {
        MeshManager meshManager;
        ColorMapManager cmm;

        public SmoothShading(MeshManager meshManager, ColorMapManager cmm) {
                this.meshManager = meshManager;
                this.cmm = cmm;
        }

        public Group setSmoothShading(Canvas3D canvas) {
                meshManager.calculateVerticesNorlmal();

                int numFaces = meshManager.getNumFaces();
                TriangleArray triangleArray = new TriangleArray(numFaces * 3,
                                TriangleArray.COORDINATES | TriangleArray.NORMALS | TriangleArray.COLOR_3);
                triangleArray.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
                triangleArray.setCapability(GeometryArray.ALLOW_COLOR_READ);

                for (int i = 0; i < numFaces; i++) {
                        Face face = meshManager.getFace(i);
                        int index = i * 3;

                        Vector v1 = meshManager.getVertex(face.getVIndex(0));
                        Vector v2 = meshManager.getVertex(face.getVIndex(1));
                        Vector v3 = meshManager.getVertex(face.getVIndex(2));

                        Point3f p1 = meshManager.convertVectorToPoint3f(v1);
                        Point3f p2 = meshManager.convertVectorToPoint3f(v2);
                        Point3f p3 = meshManager.convertVectorToPoint3f(v3);

                        Vector3f vn1 = meshManager.convertVectorToVector3f(v1.normal);
                        Vector3f vn2 = meshManager.convertVectorToVector3f(v2.normal);
                        Vector3f vn3 = meshManager.convertVectorToVector3f(v3.normal);

                        triangleArray.setCoordinate(index, p1);
                        triangleArray.setNormal(index, vn1);
                        triangleArray.setCoordinate(index + 1, p2);
                        triangleArray.setNormal(index + 1, vn2);
                        triangleArray.setCoordinate(index + 2, p3);
                        triangleArray.setNormal(index + 2, vn3);

                        float w1 = v1.w;
                        float w2 = v2.w;
                        float w3 = v3.w;

                        Color color1 = cmm.getColorFromScalar(w1,
                                        meshManager.minDistance, meshManager.maxDistance);
                        Color3f color3f1 = new Color3f((float) color1.getRed() / 255.0f,
                                        (float) color1.getGreen() / 255.0f, (float) color1.getBlue() / 255.0f);

                        Color color2 = cmm.getColorFromScalar(w2,
                                        meshManager.minDistance, meshManager.maxDistance);
                        Color3f color3f2 = new Color3f((float) color2.getRed() / 255.0f,
                                        (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f);

                        Color color3 = cmm.getColorFromScalar(w3,
                                        meshManager.minDistance, meshManager.maxDistance);
                        Color3f color3f3 = new Color3f((float) color3.getRed() / 255.0f,
                                        (float) color3.getGreen() / 255.0f, (float) color3.getBlue() / 255.0f);

                        triangleArray.setColor(index, color3f1);
                        triangleArray.setColor(index + 1, color3f2);
                        triangleArray.setColor(index + 2, color3f3);
                }

                Appearance appearance = new Appearance();
                Material material = new Material();
                material.setLightingEnable(true);
                appearance.setMaterial(material);

                PolygonAttributes polyAttr = new PolygonAttributes();
                polyAttr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
                polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
                appearance.setPolygonAttributes(polyAttr);

                ColoringAttributes ca = new ColoringAttributes();
                ca.setColor(ColorMapManager.GRAY);
                appearance.setColoringAttributes(ca);

                Shape3D flatShading = new Shape3D(triangleArray, appearance);

                BranchGroup shape = new BranchGroup();
                shape.setName("Shape Scene");
                shape.addChild(flatShading);

                shape.setCapability(BranchGroup.ALLOW_CHILDREN_EXTEND);
                shape.setCapability(BranchGroup.ALLOW_CHILDREN_WRITE);
                shape.setCapability(BranchGroup.ALLOW_CHILDREN_READ);
                flatShading.setCapability(Shape3D.ALLOW_GEOMETRY_READ);
                flatShading.setCapability(Shape3D.ALLOW_GEOMETRY_WRITE);
                flatShading.setCapability(Shape3D.ALLOW_APPEARANCE_READ);

                canvas.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                                pick(canvas, shape, flatShading, triangleArray, e.getX(), e.getY());
                        }
                });

                return shape;
        }

        // meshが選択されてしまう問題
        private void pick(Canvas3D canvas3d, BranchGroup shape, Shape3D pointCloud,
                        TriangleArray triangleArray, int x, int y) {
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

                                triangleArray.setColor(selectedPointIndex, ColorMapManager.GREEN);

                                meshManager.calculateVerticesDistanceWeight(selectedPointIndex);

                                int numFaces = meshManager.getNumFaces();

                                for (int i = 0; i < numFaces; i++) {
                                        Face face = meshManager.getFace(i);
                                        int index = i * 3;

                                        Vector v1 = meshManager.getVertex(face.getVIndex(0));
                                        Vector v2 = meshManager.getVertex(face.getVIndex(1));
                                        Vector v3 = meshManager.getVertex(face.getVIndex(2));

                                        float w1 = v1.w;
                                        float w2 = v2.w;
                                        float w3 = v3.w;

                                        Color color1 = cmm.getColorFromScalar(w1,
                                                        meshManager.minDistance, meshManager.maxDistance);
                                        Color3f color3f1 = new Color3f((float) color1.getRed() / 255.0f,
                                                        (float) color1.getGreen() / 255.0f,
                                                        (float) color1.getBlue() / 255.0f);

                                        Color color2 = cmm.getColorFromScalar(w2,
                                                        meshManager.minDistance, meshManager.maxDistance);
                                        Color3f color3f2 = new Color3f((float) color2.getRed() / 255.0f,
                                                        (float) color2.getGreen() / 255.0f,
                                                        (float) color2.getBlue() / 255.0f);

                                        Color color3 = cmm.getColorFromScalar(w3,
                                                        meshManager.minDistance, meshManager.maxDistance);
                                        Color3f color3f3 = new Color3f((float) color3.getRed() / 255.0f,
                                                        (float) color3.getGreen() / 255.0f,
                                                        (float) color3.getBlue() / 255.0f);

                                        triangleArray.setColor(index, color3f1);
                                        triangleArray.setColor(index + 1, color3f2);
                                        triangleArray.setColor(index + 2, color3f3);
                                }

                                pointCloud.setGeometry(triangleArray);
                        }
                }
        }
}
