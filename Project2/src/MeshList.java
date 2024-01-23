package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class MeshList {
    private ArrayList<Vector> vertices;
    private ArrayList<Vector> normals;
    private ArrayList<Face> faces;

    public MeshList() {
        vertices = new ArrayList<Vector>();
        normals = new ArrayList<Vector>();
        faces = new ArrayList<Face>();
    }

    public MeshList(ArrayList<Vector> vertices, ArrayList<Vector> normals, ArrayList<Face> faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.faces = faces;
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public int getNumFaces() {
        return faces.size();
    }

    public Face getFace(int i) {
        return faces.get(i);
    }

    public Vector getVertex(int vertIdx) {
        return vertices.get(vertIdx);
    }

    public void setVertexWeight(int vertIdx, float weight) {
        vertices.get(vertIdx).setWeight(weight);
    }

    public Vector getVertex(int faceIdx, int vertIdx) {
        Face f = faces.get(faceIdx);
        return vertices.get(f.getVIndex(vertIdx));
    }

    public Vector getVertexNormal(int faceIdx, int vertIdx) {
        Face f = faces.get(faceIdx);
        return normals.get(f.getVNIndex(vertIdx));
    }

    public Point3f[] getVerticesArray() {
        Point3f[] verticesArray = new Point3f[vertices.size()];
        for (int i = 0; i < vertices.size(); i++) {
            Vector vertex = vertices.get(i);
            verticesArray[i] = new Point3f(vertex.x, vertex.y, vertex.z);
        }
        return verticesArray;
    }

    public Vector3f[] getNormalsArray() {
        Vector3f[] normalsArray = new Vector3f[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            Vector normal = normals.get(i);
            normalsArray[i] = new Vector3f(normal.x, normal.y, normal.z);
        }
        return normalsArray;
    }

    public Vector3f[] getVertexNormalsArray() {
        ArrayList<Vector> normals = new ArrayList<>(Collections.nCopies(vertices.size(), new Vector(0, 0, 0, 0)));

        // Accumulate normals for each face
        for (Face face : faces) {
            Vector normal = face.normal; // Calculate face normal

            // Add this normal to the normals of all vertices in the face
            for (int i = 0; i < 3; i++) {
                int index = face.getVIndex(i);
                normals.set(index, normal);
            }
        }

        for (Vector normal : normals) {
            normal.normalize();
        }

        Vector3f[] normalsArray = new Vector3f[normals.size()];
        for (int i = 0; i < normals.size(); i++) {
            Vector normal = normals.get(i);
            normalsArray[i] = new Vector3f(normal.x, normal.y, normal.z);
        }
        return normalsArray;
    }

    public void setNormals() {
        for (Face face : this.faces) {
            Vector v1 = vertices.get(face.getVIndex(0));
            Vector v2 = vertices.get(face.getVIndex(1));
            Vector v3 = vertices.get(face.getVIndex(2));

            Vector normal = calculateFaceNormal(v1, v2, v3);

            face.setNormal(normal);
            normals.add(normal);
        }
    }

    private Vector calculateFaceNormal(Vector v1, Vector v2, Vector v3) {
        Vector edge1 = new Vector(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z, 1.0f);
        Vector edge2 = new Vector(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z, 1.0f);

        Vector normal = edge1.cross(edge2);
        normal.normalize();

        return normal;
    }

    public void clearVerticesWeight() {
        for (Vector vertex : vertices) {
            vertex.clearWeight();
        }
    }

    public float getGeodesicDistance(int start, int end) {
        Graph graph = createGraph();
        List<Integer> path = Dijkstra.findShortestPath(graph, start, end);
        return calculatePathDistance(path);
    }

    private Graph createGraph() {
        Graph graph = new Graph(getNumVertices());

        for (Face face : faces) {
            for (int i = 0; i < face.getNumVertices(); i++) {
                int v1 = face.getVIndex(i);
                int v2 = face.getVIndex((i + 1) % face.getNumVertices());
                float weight = vertices.get(v1).distanceTo(vertices.get(v2));
                graph.addEdge(v1, v2, weight);
                graph.addEdge(v2, v1, weight);
            }
        }

        return graph;
    }

    private float calculatePathDistance(List<Integer> path) {
        float totalDistance = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Vector current = vertices.get(path.get(i));
            Vector next = vertices.get(path.get(i + 1));
            totalDistance += current.distanceTo(next);
        }
        return totalDistance;
    }
}
