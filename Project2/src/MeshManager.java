package src;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import org.jogamp.vecmath.Point3f;
import org.jogamp.vecmath.Vector3f;

public class MeshManager {
    private ArrayList<Vector> vertices;
    private ArrayList<Vector> normals;
    private ArrayList<Face> faces;
    private Graph graph;

    float maxDistance = Float.MIN_VALUE;
    float minDistance = Float.MAX_VALUE;
    int chooseVertexIndex = 0;

    public MeshManager() {
        vertices = new ArrayList<Vector>();
        normals = new ArrayList<Vector>();
        faces = new ArrayList<Face>();
    }

    public MeshManager(ArrayList<Vector> vertices, ArrayList<Vector> normals, ArrayList<Face> faces) {
        this.vertices = vertices;
        this.normals = normals;
        this.faces = faces;
    }

    public Graph getGraph() {
        return graph;
    }

    public int getNumVertices() {
        return vertices.size();
    }

    public int getNumEdges() {
        if (graph == null)
            return 0;
        int size = 0;
        for (int i = 0; i < graph.edges.size(); i++) {
            size += graph.edges.get(i).size();
        }
        return size / 2;
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

    public Point3f convertVectorToPoint3f(Vector v) {
        return new Point3f(v.x, v.y, v.z);
    }

    public Vector3f convertVectorToVector3f(Vector v) {
        return new Vector3f(v.x, v.y, v.z);
    }

    public void calculateVerticesNorlmal() {
        ArrayList<Vector> tmpNormals = new ArrayList<>(Collections.nCopies(vertices.size(), new Vector(0, 0, 0, 0)));

        for (Face face : faces) {
            Vector normal = face.normal;

            for (int i = 0; i < 3; i++) {
                int index = face.getVIndex(i);
                tmpNormals.set(index, normal);
            }
        }

        for (Vector normal : tmpNormals) {
            normal.normalize();
        }

        for (int i = 0; i < tmpNormals.size(); i++) {
            getVertex(i).setNormal(tmpNormals.get(i));
        }
    }

    public void calculateFacesNormal() {
        for (Face face : this.faces) {
            Vector v1 = vertices.get(face.getVIndex(0));
            Vector v2 = vertices.get(face.getVIndex(1));
            Vector v3 = vertices.get(face.getVIndex(2));

            Vector normal = calculateFaceNormal(v1, v2, v3);

            face.setNormal(normal);
        }
    }

    private Vector calculateFaceNormal(Vector v1, Vector v2, Vector v3) {
        Vector edge1 = new Vector(v2.x - v1.x, v2.y - v1.y, v2.z - v1.z, 1.0f);
        Vector edge2 = new Vector(v3.x - v1.x, v3.y - v1.y, v3.z - v1.z, 1.0f);

        Vector normal = edge1.cross(edge2);
        normal.normalize();

        return normal;
    }

    public void initVerticesWeight() {
        for (Vector vertex : vertices) {
            vertex.initWeight();
        }
    }

    public void calculateVerticesDistanceWeight(int chooseVertexIndex) {
        minDistance = Float.MAX_VALUE;
        maxDistance = Float.MIN_VALUE;
        this.chooseVertexIndex = chooseVertexIndex;

        if (getNumFaces() == 0)
            createKNNGraph(300);

        for (int i = 0; i < getNumVertices(); i++) {
            float distance = getGeodesicDistance(chooseVertexIndex, i);
            maxDistance = Math.max(maxDistance, distance);
            minDistance = Math.min(minDistance, distance);
            setVertexWeight(i, distance);
        }
    }

    public float getGeodesicDistance(int start, int end) {
        if (getNumFaces() != 0)
            createGraph();

        List<Integer> path = Dijkstra.findShortestPath(graph, start, end);
        return calculatePathDistance(path);
    }

    private void createGraph() {
        graph = new Graph(getNumVertices());

        for (Face face : faces) {
            for (int i = 0; i < face.getNumVertices(); i++) {
                int v1 = face.getVIndex(i);
                int v2 = face.getVIndex((i + 1) % face.getNumVertices());
                float weight = vertices.get(v1).distanceTo(vertices.get(v2));
                graph.addEdge(v1, v2, weight);
                graph.addEdge(v2, v1, weight);
            }
        }
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

    public void createKNNGraph(int k) {
        graph = new Graph(getNumVertices());

        for (int i = 0; i < getNumVertices(); i++) {
            Vector currentVertex = vertices.get(i);
            PriorityQueue<VertexDistancePair> nearestNeighbors = new PriorityQueue<>(k,
                    Comparator.comparingDouble(VertexDistancePair::getDistance));

            for (int j = 0; j < getNumVertices(); j++) {
                if (i != j) {
                    Vector otherVertex = vertices.get(j);
                    float distance = currentVertex.distanceTo(otherVertex);
                    nearestNeighbors.add(new VertexDistancePair(j, distance));

                    if (nearestNeighbors.size() > k) {
                        nearestNeighbors.poll();
                    }
                }
            }

            for (VertexDistancePair pair : nearestNeighbors) {
                graph.addEdge(i, pair.getVertexIndex(), pair.getDistance());
            }
        }
    }

    class VertexDistancePair {
        private int vertexIndex;
        private float distance;

        public VertexDistancePair(int vertexIndex, float distance) {
            this.vertexIndex = vertexIndex;
            this.distance = distance;
        }

        public int getVertexIndex() {
            return vertexIndex;
        }

        public float getDistance() {
            return distance;
        }
    }
}
