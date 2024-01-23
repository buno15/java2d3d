package src;

import java.awt.Color;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Canvas3D;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class SmoothShading {
    MeshList meshList;

    public SmoothShading(MeshList meshList) {
        this.meshList = meshList;
    }

    public Group setSmoothShading(Canvas3D canvas, int chooseVertexIndex, float minScalar, float maxScalar) {
        PointArray choosePoint = new PointArray(1, PointArray.COORDINATES | PointArray.COLOR_3);
        choosePoint.setCoordinate(0, new Point3f(meshList.getVertex(chooseVertexIndex).x,
                meshList.getVertex(chooseVertexIndex).y, meshList.getVertex(chooseVertexIndex).z));
        choosePoint.setColor(0, PointColor.YELLOW);

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

        return shape;
    }
}
