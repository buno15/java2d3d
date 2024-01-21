package src;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.Material;
import org.jogamp.java3d.PolygonAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class SmoothShading {
    MeshList meshList;

    public SmoothShading(MeshList meshList) {
        this.meshList = meshList;
    }

    public Group setSmoothShading() {
        Point3f[] vertices = meshList.getVerticesArray();
        Vector3f[] normals = meshList.getVertexNormalsArray();

        int numFaces = meshList.getNumFaces();
        TriangleArray triangleArray = new TriangleArray(numFaces * 3,
                TriangleArray.COORDINATES | TriangleArray.NORMALS);

        for (int i = 0; i < numFaces; i++) {
            Face face = meshList.getFace(i);
            int index = i * 3;
            triangleArray.setCoordinate(index, vertices[face.getVIndex(0)]);
            triangleArray.setNormal(index, normals[face.getVIndex(0)]);
            triangleArray.setCoordinate(index + 1, vertices[face.getVIndex(1)]);
            triangleArray.setNormal(index + 1, normals[face.getVIndex(1)]);
            triangleArray.setCoordinate(index + 2, vertices[face.getVIndex(2)]);
            triangleArray.setNormal(index + 2, normals[face.getVIndex(2)]);
        }

        Appearance appearance = new Appearance();
        Material material = new Material();
        appearance.setMaterial(material);

        PolygonAttributes polyAttr = new PolygonAttributes();
        polyAttr.setPolygonMode(PolygonAttributes.POLYGON_FILL);
        polyAttr.setCullFace(PolygonAttributes.CULL_NONE);
        appearance.setPolygonAttributes(polyAttr);

        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(Color.GRAY);
        appearance.setColoringAttributes(ca);

        Shape3D flatShading = new Shape3D(triangleArray, appearance);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(flatShading);

        return shape;
    }
}
