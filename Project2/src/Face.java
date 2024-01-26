package src;

import java.util.ArrayList;

public class Face {
    private int numVertices;
    private ArrayList<Integer> vertexIndices;
    Vector normal;

    public Face(int nV) {
        numVertices = nV;
        vertexIndices = new ArrayList<Integer>();
    }

    public int getNumVertices() {
        return numVertices;
    }

    public void addVertexIndex(int v) {
        vertexIndices.add(v);
    }

    public int getVIndex(int idx) {
        return vertexIndices.get(idx);
    }

    public void setNormal(Vector normal) {
        this.normal = normal;
    }
}