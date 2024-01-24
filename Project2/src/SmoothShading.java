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
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3d;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

import org.jogamp.java3d.utils.picking.PickCanvas;
import org.jogamp.java3d.utils.picking.PickIntersection;
import org.jogamp.java3d.utils.picking.PickResult;
import org.jogamp.java3d.utils.picking.PickTool;

public class SmoothShading {
        MeshList meshList;
        float minScalar;
        float maxScalar;

        public SmoothShading(MeshList meshList) {
                this.meshList = meshList;
        }

        public Group setSmoothShading(Canvas3D canvas, int chooseVertexIndex, float minScalar, float maxScalar) {
                this.minScalar = minScalar;
                this.maxScalar = maxScalar;

                PointArray choosePoint = new PointArray(1, PointArray.COORDINATES | PointArray.COLOR_3);
                choosePoint.setCoordinate(0, new Point3f(meshList.getVertex(chooseVertexIndex).x,
                                meshList.getVertex(chooseVertexIndex).y, meshList.getVertex(chooseVertexIndex).z));
                choosePoint.setColor(0, PointColor.GREEN);

                PointAttributes choosePA = new PointAttributes();
                choosePA.setName("Point Attributes");
                choosePA.setPointSize(15.0f);
                choosePA.setPointAntialiasingEnable(true);
                choosePA.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
                choosePA.setCapability(PointAttributes.ALLOW_SIZE_READ);
                Appearance chooseA = new Appearance();
                chooseA.setName("Appearance");
                chooseA.setPointAttributes(choosePA);

                Shape3D chooseS = new Shape3D(choosePoint, new Appearance());
                chooseS.setAppearance(chooseA);

                Point3f[] vertices = meshList.getVerticesArray();
                Vector3f[] normals = meshList.getVertexNormalsArray();

                int numFaces = meshList.getNumFaces();
                TriangleArray triangleArray = new TriangleArray(numFaces * 3,
                                TriangleArray.COORDINATES | TriangleArray.NORMALS | TriangleArray.COLOR_3);
                triangleArray.setCapability(GeometryArray.ALLOW_COLOR_WRITE);
                triangleArray.setCapability(GeometryArray.ALLOW_COLOR_READ);

                for (int i = 0; i < numFaces; i++) {
                        Face face = meshList.getFace(i);
                        int index = i * 3;
                        triangleArray.setCoordinate(index, vertices[face.getVIndex(0)]);
                        triangleArray.setNormal(index, normals[face.getVIndex(0)]);
                        triangleArray.setCoordinate(index + 1, vertices[face.getVIndex(1)]);
                        triangleArray.setNormal(index + 1, normals[face.getVIndex(1)]);
                        triangleArray.setCoordinate(index + 2, vertices[face.getVIndex(2)]);
                        triangleArray.setNormal(index + 2, normals[face.getVIndex(2)]);

                        float w1 = meshList.getVertex(face.getVIndex(0)).w;
                        float w2 = meshList.getVertex(face.getVIndex(1)).w;
                        float w3 = meshList.getVertex(face.getVIndex(2)).w;

                        Color color1 = ColorMap.getColorFromScalar(w1,
                                        minScalar, maxScalar, ColorMapReader.colorMaps);
                        Color3f color3f1 = new Color3f((float) color1.getRed() / 255.0f,
                                        (float) color1.getGreen() / 255.0f, (float) color1.getBlue() / 255.0f);

                        Color color2 = ColorMap.getColorFromScalar(w2,
                                        minScalar, maxScalar, ColorMapReader.colorMaps);
                        Color3f color3f2 = new Color3f((float) color2.getRed() / 255.0f,
                                        (float) color2.getGreen() / 255.0f, (float) color2.getBlue() / 255.0f);

                        Color color3 = ColorMap.getColorFromScalar(w3,
                                        minScalar, maxScalar, ColorMapReader.colorMaps);
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
                ca.setColor(PointColor.GRAY);
                appearance.setColoringAttributes(ca);

                Shape3D flatShading = new Shape3D(triangleArray, appearance);

                BranchGroup shape = new BranchGroup();
                shape.setName("Shape Scene");
                shape.addChild(flatShading);
                shape.addChild(chooseS);

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

                                triangleArray.setColor(selectedPointIndex, PointColor.GREEN);

                                float maxDistance = Float.MIN_VALUE;
                                float minDistance = Float.MAX_VALUE;

                                for (int i = 0; i < meshList.getNumVertices(); i++) {
                                        float distance = meshList.getGeodesicDistance(selectedPointIndex, i);
                                        maxDistance = Math.max(maxDistance, distance);
                                        minDistance = Math.min(minDistance, distance);
                                        meshList.setVertexWeight(i, distance);
                                }

                                int numFaces = meshList.getNumFaces();

                                for (int i = 0; i < numFaces; i++) {
                                        Face face = meshList.getFace(i);
                                        int index = i * 3;

                                        float w1 = meshList.getVertex(face.getVIndex(0)).w;
                                        float w2 = meshList.getVertex(face.getVIndex(1)).w;
                                        float w3 = meshList.getVertex(face.getVIndex(2)).w;

                                        Color color1 = ColorMap.getColorFromScalar(w1,
                                                        minScalar, maxScalar, ColorMapReader.colorMaps);
                                        Color3f color3f1 = new Color3f((float) color1.getRed() / 255.0f,
                                                        (float) color1.getGreen() / 255.0f,
                                                        (float) color1.getBlue() / 255.0f);

                                        Color color2 = ColorMap.getColorFromScalar(w2,
                                                        minScalar, maxScalar, ColorMapReader.colorMaps);
                                        Color3f color3f2 = new Color3f((float) color2.getRed() / 255.0f,
                                                        (float) color2.getGreen() / 255.0f,
                                                        (float) color2.getBlue() / 255.0f);

                                        Color color3 = ColorMap.getColorFromScalar(w3,
                                                        minScalar, maxScalar, ColorMapReader.colorMaps);
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
