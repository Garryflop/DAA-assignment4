package org.harryfloppa.graph.topo;

import org.harryfloppa.common.Metrics;
import org.harryfloppa.common.MetricsImpl;
import org.harryfloppa.graph.Graph;

import java.util.*;

/**
 * Implementation of Kahn's algorithm for topological sorting.
 * Time complexity: O(V + E)
 */
public class KahnTopologicalSort {
    private final Graph graph;
    private final Metrics metrics;

    public KahnTopologicalSort(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("Topological sort requires a directed graph");
        }
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public TopoSortResult sort() {
        int n = graph.getVertexCount();
        int[] inDegree = new int[n];
        
        metrics.reset();
        metrics.startTiming();
        
        // Calculate in-degrees
        for (int u = 0; u < n; u++) {
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                inDegree[edge.to]++;
                metrics.incrementCounter("edges_scanned");
            }
        }
        
        // Add all vertices with in-degree 0 to queue
        Queue<Integer> queue = new LinkedList<>();
        for (int v = 0; v < n; v++) {
            if (inDegree[v] == 0) {
                queue.offer(v);
                metrics.incrementCounter("queue_pushes");
            }
        }
        
        List<Integer> topoOrder = new ArrayList<>();
        
        // Process vertices in topological order
        while (!queue.isEmpty()) {
            int u = queue.poll();
            metrics.incrementCounter("queue_pops");
            topoOrder.add(u);
            
            // Reduce in-degree of adjacent vertices
            for (Graph.Edge edge : graph.getAdjacent(u)) {
                int v = edge.to;
                inDegree[v]--;
                metrics.incrementCounter("in_degree_updates");
                
                if (inDegree[v] == 0) {
                    queue.offer(v);
                    metrics.incrementCounter("queue_pushes");
                }
            }
        }
        
        metrics.stopTiming();
        
        // Check if the graph has a cycle
        boolean hasCycle = topoOrder.size() != n;
        
        return new TopoSortResult(topoOrder, hasCycle, metrics);
    }

    public static class TopoSortResult {
        private final List<Integer> order;
        private final boolean hasCycle;
        private final Metrics metrics;
        
        public TopoSortResult(List<Integer> order, boolean hasCycle, Metrics metrics) {
            this.order = order;
            this.hasCycle = hasCycle;
            this.metrics = metrics;
        }
        
        public List<Integer> getOrder() {
            return order;
        }
        
        public boolean hasCycle() {
            return hasCycle;
        }
        
        public Metrics getMetrics() {
            return metrics;
        }
        
        public boolean isValid() {
            return !hasCycle;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (hasCycle) {
                sb.append("Graph contains a cycle - no valid topological order\n");
                sb.append("Partial order: ").append(order).append("\n");
            } else {
                sb.append("Topological Order: ").append(order).append("\n");
            }
            sb.append("\n").append(metrics.getReport());
            return sb.toString();
        }
    }
}
