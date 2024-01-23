package src;

import java.util.ArrayList;

class Graph {
    ArrayList<ArrayList<Edge>> edges;

    public Graph(int n) {
        edges = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            edges.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, float weight) {
        edges.get(from).add(new Edge(to, weight));
    }
}

class Edge implements Comparable<Edge> {
    int to;
    float weight;

    Edge(int to, float weight) {
        this.to = to;
        this.weight = weight;
    }

    @Override
    public int compareTo(Edge target) {
        return Float.compare(this.weight, target.weight);
    }
}