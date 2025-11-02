package org.harryfloppa.graph;

import java.util.*;


public class Graph {
    private final int n; // number of vertices
    private final List<List<Edge>> adjList;
    private final boolean directed;

    public static class Edge {
        public final int to;
        public final int weight;
        
        public Edge(int to, int weight) {
            this.to = to;
            this.weight = weight;
        }
        
        @Override
        public String toString() {
            return "(" + to + ", w=" + weight + ")";
        }
    }

    public Graph(int n, boolean directed) {
        this.n = n;
        this.directed = directed;
        this.adjList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }
    }

    public void addEdge(int from, int to, int weight) {
        if (from < 0 || from >= n || to < 0 || to >= n) {
            throw new IllegalArgumentException("Vertex out of bounds");
        }
        adjList.get(from).add(new Edge(to, weight));
        if (!directed) {
            adjList.get(to).add(new Edge(from, weight));
        }
    }

    public List<Edge> getAdjacent(int v) {
        return adjList.get(v);
    }

    public int getVertexCount() {
        return n;
    }

    public int getEdgeCount() {
        int count = 0;
        for (List<Edge> edges : adjList) {
            count += edges.size();
        }
        return directed ? count : count / 2;
    }

    public boolean isDirected() {
        return directed;
    }

    public Graph reverse() {
        if (!directed) {
            throw new UnsupportedOperationException("Cannot reverse an undirected graph");
        }
        Graph reversed = new Graph(n, true);
        for (int u = 0; u < n; u++) {
            for (Edge e : adjList.get(u)) {
                reversed.addEdge(e.to, u, e.weight);
            }
        }
        return reversed;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Graph (").append(directed ? "directed" : "undirected")
          .append(", n=").append(n).append(", edges=").append(getEdgeCount()).append(")\n");
        for (int i = 0; i < n; i++) {
            sb.append(i).append(": ");
            for (Edge e : adjList.get(i)) {
                sb.append(e).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
