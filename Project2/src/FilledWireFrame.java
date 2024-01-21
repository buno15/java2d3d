package src;

import org.jogamp.java3d.Appearance;
import org.jogamp.java3d.BranchGroup;
import org.jogamp.java3d.ColoringAttributes;
import org.jogamp.java3d.Group;
import org.jogamp.java3d.LineArray;
import org.jogamp.java3d.LineAttributes;
import org.jogamp.java3d.LineStripArray;
import org.jogamp.java3d.Shape3D;
import org.jogamp.java3d.TriangleArray;
import org.jogamp.vecmath.Point3f;

public class FilledWireFrame {
    MeshList meshList;

    public FilledWireFrame(MeshList meshList) {
        this.meshList = meshList;
    }

    public Group setFilledWireFrame() {
        BranchGroup filledWireFrame = new BranchGroup();
        int numFaces = meshList.getNumFaces();

        LineAttributes lineAttributes = new LineAttributes();
        lineAttributes.setLineWidth(2.0f);
        ColoringAttributes lineColor = new ColoringAttributes();
        lineColor.setColor(Color.BLACK);
        Appearance lineAppearance = new Appearance();
        lineAppearance.setColoringAttributes(lineColor);
        lineAppearance.setLineAttributes(lineAttributes);

        ColoringAttributes faceColor = new ColoringAttributes();
        faceColor.setColor(Color.GRAY);
        Appearance faceAppearance = new Appearance();
        faceAppearance.setColoringAttributes(faceColor);

        for (int i = 0; i < numFaces; i++) {
            Face face = meshList.getFace(i);

            Point3f[] points = new Point3f[face.getNumVertices()];
            for (int j = 0; j < face.getNumVertices(); j++) {
                Vector vertex = meshList.getVertex(i, j);
                points[j] = new Point3f(vertex.x, vertex.y, vertex.z);
            }

            TriangleArray triangle = new TriangleArray(face.getNumVertices(), TriangleArray.COORDINATES);
            triangle.setCoordinates(0, points);
            filledWireFrame.addChild(new Shape3D(triangle, faceAppearance));

            LineStripArray lines = new LineStripArray(face.getNumVertices(), LineArray.COORDINATES,
                    new int[] { face.getNumVertices() });
            lines.setCoordinates(0, points);
            filledWireFrame.addChild(new Shape3D(lines, lineAppearance));
        }

        BranchGroup shape = new BranchGroup();
        shape.setName("Shape Scene");
        shape.addChild(filledWireFrame);

        return shape;
    }
}
