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
    MeshManager meshManager;

    public WireFrame(MeshManager meshManager) {
        this.meshManager = meshManager;
    }

    public Group setWireFrame() {
        int numFaces = meshManager.getNumFaces();
        LineArray lines;

        if (numFaces == 0) {
            int edgeSize = 0;
            for (int i = 0; i < meshManager.getGraph().edges.size(); i++) {
                edgeSize += meshManager.getGraph().edges.get(i).size();
            }

            System.out.println("edgeSize: " + edgeSize);

            lines = new LineArray(edgeSize, LineArray.COORDINATES);

            Graph graph = meshManager.getGraph();
            for (int i = 0; i < graph.edges.size(); i++) {
                Vector vertex1 = meshManager.getVertex(i);
                for (int j = 0; j < graph.edges.get(i).size(); j++) {
                    Vector vertex2 = meshManager.getVertex(graph.edges.get(i).get(j).to);
                    lines.setCoordinate(i * 6 + j * 2, new Point3f(vertex1.x, vertex1.y, vertex1.z));
                    lines.setCoordinate(i * 6 + j * 2 + 1, new Point3f(vertex2.x, vertex2.y, vertex2.z));
                }
            }
        } else {
            lines = new LineArray(numFaces * 6, LineArray.COORDINATES);

            for (int i = 0; i < numFaces; i++) {
                Face face = meshManager.getFace(i);
                for (int j = 0; j < face.getNumVertices(); j++) {
                    Vector vertex1 = meshManager.getVertex(i, j);
                    Vector vertex2 = meshManager.getVertex(i, (j + 1) % face.getNumVertices());

                    lines.setCoordinate(i * 6 + j * 2, new Point3f(vertex1.x, vertex1.y, vertex1.z));
                    lines.setCoordinate(i * 6 + j * 2 + 1, new Point3f(vertex2.x, vertex2.y, vertex2.z));
                }
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
