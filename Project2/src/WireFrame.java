package src;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.LineArray;
import org.jogamp.java3d.LineAttributes;
import org.jogamp.java3d.Shape3D;
import org.jogamp.vecmath.Point3f;

public class WireFrame {
    MeshManager meshList;

    public WireFrame(MeshManager meshList) {
        this.meshList = meshList;
    }

    public Group setWireFrame() {
        int numFaces = meshList.getNumFaces();
        LineArray lines = new LineArray(numFaces * 6, LineArray.COORDINATES);

        for (int i = 0; i < numFaces; i++) {
            Face face = meshList.getFace(i);
            for (int j = 0; j < face.getNumVertices(); j++) {
                Vector vertex1 = meshList.getVertex(i, j);
                Vector vertex2 = meshList.getVertex(i, (j + 1) % face.getNumVertices());

                lines.setCoordinate(i * 6 + j * 2, new Point3f(vertex1.x, vertex1.y, vertex1.z));
                lines.setCoordinate(i * 6 + j * 2 + 1, new Point3f(vertex2.x, vertex2.y, vertex2.z));
            }
        }

        Appearance appearance = new Appearance();
        appearance.setName("appearance");

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(2.0f);
        ColoringAttributes ca = new ColoringAttributes();
        ca.setColor(ColorMapManager.BLACK);
        appearance.setColoringAttributes(ca);
        appearance.setLineAttributes(lineAttributes);

        Shape3D wireFrame = new Shape3D(lines, new Appearance());
        wireFrame.setName("Wire Frame");
        wireFrame.setAppearance(appearance);

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(wireFrame);

        return shape;
    }
}
