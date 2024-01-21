package src;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.vecmath.Point3f;

public class PointCloud {
    MeshList meshList;

    public PointCloud(MeshList meshList) {
        this.meshList = meshList;
    }

    public Group createPointCloud() {
        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setName("Point Attributes");
        pointAttributes.setPointSize(2.0f);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(Color.GRAY);
        Appearance appearance = new Appearance();
        appearance.setName("Appearance");
        appearance.setPointAttributes(pointAttributes);
        appearance.setColoringAttributes(ca);

        int numVertices = meshList.getNumVertices();
        PointArray points = new PointArray(numVertices, PointArray.COORDINATES);

        for (int i = 0; i < numVertices; i++) {
            Vector vertex = meshList.getVertex(i);
            points.setCoordinate(i, new Point3f(vertex.x, vertex.y, vertex.z));
        }

        Shape3D pointCloud = new Shape3D(points, new Appearance());
        pointCloud.setName("Point Cloud");
        pointCloud.setAppearance(appearance);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(pointCloud);

        return shape;
    }
}
