package src;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.PointArray;
import org.jogamp.java3d.PointAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.vecmath.Color3f;
import org.jogamp.vecmath.Point3f;
import java.awt.Color;

public class PointCloud {
    MeshList meshList;

    public PointCloud(MeshList meshList) {
        this.meshList = meshList;
    }

    public Group createPointCloud(int chooseVertexIndex, float minScalar, float maxScalar) {
        PointArray choosePoint = new PointArray(1, PointArray.COORDINATES | PointArray.COLOR_3);
        choosePoint.setCoordinate(0, new Point3f(meshList.getVertex(chooseVertexIndex).x,
                meshList.getVertex(chooseVertexIndex).y, meshList.getVertex(chooseVertexIndex).z));
        choosePoint.setColor(0, PointColor.BLACK);

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

        PointAttributes pointAttributes = new PointAttributes();
        pointAttributes.setName("Point Attributes");
        pointAttributes.setPointSize(5.0f);
        pointAttributes.setPointAntialiasingEnable(true);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_WRITE);
        pointAttributes.setCapability(PointAttributes.ALLOW_SIZE_READ);
        Appearance appearance = new Appearance();
        appearance.setName("Appearance");
        appearance.setPointAttributes(pointAttributes);

        int numVertices = meshList.getNumVertices();
        PointArray points = new PointArray(numVertices, PointArray.COORDINATES | PointArray.COLOR_3);

        for (int i = 0; i < numVertices; i++) {
            Vector vertex = meshList.getVertex(i);
            points.setCoordinate(i, new Point3f(vertex.x, vertex.y, vertex.z));

            Color color = ColorMap.getColorFromScalar(vertex.w, minScalar, maxScalar, ColorMapReader.colorMaps);
            Color3f color3f = new Color3f((float) color.getRed() / 255.0f,
                    (float) color.getGreen() / 255.0f, (float) color.getBlue() / 255.0f);

            points.setColor(i, color3f);
        }

        Shape3D pointCloud = new Shape3D(points, new Appearance());
        pointCloud.setName("Point Cloud");
        pointCloud.setAppearance(appearance);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(pointCloud);
        shape.addChild(chooseS);

        return shape;
    }
}
