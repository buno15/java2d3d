package src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

class Dijkstra {

    public static List<Integer> findShortestPath(Graph graph, int start, int end) {
        int startNode = start;
        int endNode = end;

        PriorityQueue<Edge> pq = new PriorityQueue<>();

        float[] dist = new float[graph.edges.size()];
        Arrays.fill(dist, Float.MAX_VALUE);
        dist[startNode] = 0;

        int[] prev = new int[graph.edges.size()];
        Arrays.fill(prev, -1);

        pq.add(new Edge(startNode, 0));

        while (!pq.isEmpty()) {
            Edge currentEdge = pq.poll();

            if (dist[currentEdge.to] < currentEdge.weight) {
                continue;
            }

            for (Edge edge : graph.edges.get(currentEdge.to)) {
                if (dist[edge.to] > dist[currentEdge.to] + edge.weight) {
                    dist[edge.to] = dist[currentEdge.to] + edge.weight;
                    prev[edge.to] = currentEdge.to;
                    pq.add(new Edge(edge.to, dist[edge.to]));
                }
            }
        }

        List<Integer> path = new ArrayList<>();
        for (int at = endNode; at != -1; at = prev[at]) {
            path.add(at);
            if (at == startNode)
                break;
        }
        Collections.reverse(path);

        return path;
    }
}