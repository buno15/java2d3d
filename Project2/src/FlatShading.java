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

public class FlatShading {
    MeshManager meshManager;

    public FlatShading(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    public Group setFlatShading() {
        int numFaces = meshManager.getNumFaces();
        TriangleArray triangleArray = new TriangleArray(numFaces * 3,
                TriangleArray.COORDINATES | TriangleArray.NORMALS);

        for (int i = 0; i < numFaces; i++) {
            Face face = meshManager.getFace(i);
            int index = i * 3;

            Vector normal = face.normal;
            Vector3f n3f = meshManager.convertVectorToVector3f(normal);

            Vector v1 = meshManager.getVertex(face.getVIndex(0));
            Vector v2 = meshManager.getVertex(face.getVIndex(1));
            Vector v3 = meshManager.getVertex(face.getVIndex(2));

            Point3f p1 = meshManager.convertVectorToPoint3f(v1);
            Point3f p2 = meshManager.convertVectorToPoint3f(v2);
            Point3f p3 = meshManager.convertVectorToPoint3f(v3);

            triangleArray.setCoordinate(index, p1);
            triangleArray.setNormal(index, n3f);
            triangleArray.setCoordinate(index + 1, p2);
            triangleArray.setNormal(index + 1, n3f);
            triangleArray.setCoordinate(index + 2, p3);
            triangleArray.setNormal(index + 2, n3f);
        }

        Appearance appearance = new Appearance();
        Material material = new Material();
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

        return shape;
    }
}
