package src;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class KNearestNeighborsGraph {
    ArrayList<Vector> vertices;
    int k;

    public KNearestNeighborsGraph(ArrayList<Vector> vertices, int k) {
        this.vertices = vertices;
        this.k = k;
    }

    public Graph buildGraph() {
        Graph graph = new Graph(vertices.size());

        for (int i = 0; i < vertices.size(); i++) {
            PriorityQueue<VectorDistancePair> nearestNeighbors = findKNearestNeighbors(i);

            while (!nearestNeighbors.isEmpty()) {
                VectorDistancePair pair = nearestNeighbors.poll();
                graph.addEdge(i, pair.index, pair.distance);
            }
        }

        return graph;
    }

    private PriorityQueue<VectorDistancePair> findKNearestNeighbors(int index) {
        PriorityQueue<VectorDistancePair> pq = new PriorityQueue<>(
                (a, b) -> Float.compare(a.distance, b.distance));

        Vector current = vertices.get(index);
        for (int i = 0; i < vertices.size(); i++) {
            if (i == index)
                continue;
            float distance = current.distanceTo(vertices.get(i));
            pq.add(new VectorDistancePair(i, distance));

            if (pq.size() > k) {
                pq.poll();
            }
        }

        return pq;
    }

    class VectorDistancePair {
        int index;
        float distance;

        VectorDistancePair(int index, float distance) {
            this.index = index;
            this.distance = distance;
        }
    }
}