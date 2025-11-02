package org.harryfloppa.graph.dagsp;

import org.harryfloppa.common.Metrics;
import org.harryfloppa.common.MetricsImpl;
import org.harryfloppa.graph.Graph;
import org.harryfloppa.graph.topo.KahnTopologicalSort;

import java.util.*;

/**
 * Single-source shortest paths in a Directed Acyclic Graph (DAG).
 * Time complexity: O(V + E)
 */
public class DAGShortestPath {
    private final Graph graph;
    private final Metrics metrics;

    public DAGShortestPath(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("DAG shortest path requires a directed graph");
        }
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public ShortestPathResult computeShortestPaths(int source) {
        int n = graph.getVertexCount();
        
        metrics.reset();
        metrics.startTiming();
        
        // Get topological order
        KahnTopologicalSort topoSort = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult topoResult = topoSort.sort();
        
        if (topoResult.hasCycle()) {
            metrics.stopTiming();
            throw new IllegalArgumentException("Graph contains a cycle - not a DAG");
        }
        
        List<Integer> topoOrder = topoResult.getOrder();
        
        // Initialize distances
        int[] dist = new int[n];
        int[] parent = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dist[source] = 0;
        
        // Process vertices in topological order
        for (int u : topoOrder) {
            if (dist[u] != Integer.MAX_VALUE) {
                for (Graph.Edge edge : graph.getAdjacent(u)) {
                    int v = edge.to;
                    int newDist = dist[u] + edge.weight;
                    metrics.incrementCounter("relaxations");
                    
                    if (newDist < dist[v]) {
                        dist[v] = newDist;
                        parent[v] = u;
                        metrics.incrementCounter("distance_updates");
                    }
                }
            }
        }
        
        metrics.stopTiming();
        
        return new ShortestPathResult(source, dist, parent, metrics);
    }

    public static class ShortestPathResult {
        private final int source;
        private final int[] distances;
        private final int[] parent;
        private final Metrics metrics;
        
        public ShortestPathResult(int source, int[] distances, int[] parent, Metrics metrics) {
            this.source = source;
            this.distances = distances;
            this.parent = parent;
            this.metrics = metrics;
        }
        
        public int getSource() {
            return source;
        }
        
        public int[] getDistances() {
            return distances;
        }
        
        public int getDistance(int v) {
            return distances[v];
        }
        
        public boolean isReachable(int v) {
            return distances[v] != Integer.MAX_VALUE;
        }
        
        public Metrics getMetrics() {
            return metrics;
        }

        public List<Integer> getPath(int dest) {
            if (!isReachable(dest)) {
                return Collections.emptyList();
            }
            
            List<Integer> path = new ArrayList<>();
            int current = dest;
            while (current != -1) {
                path.add(current);
                current = parent[current];
            }
            Collections.reverse(path);
            return path;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Shortest Paths from source ").append(source).append(":\n");
            for (int v = 0; v < distances.length; v++) {
                sb.append("  To ").append(v).append(": ");
                if (isReachable(v)) {
                    sb.append("distance = ").append(distances[v]);
                    sb.append(", path = ").append(getPath(v));
                } else {
                    sb.append("unreachable");
                }
                sb.append("\n");
            }
            sb.append("\n").append(metrics.getReport());
            return sb.toString();
        }
    }
}
