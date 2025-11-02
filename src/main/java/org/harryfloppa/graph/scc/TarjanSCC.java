package org.harryfloppa.graph.scc;

import org.harryfloppa.common.Metrics;
import org.harryfloppa.common.MetricsImpl;
import org.harryfloppa.graph.Graph;

import java.util.*;

/**
 * Implementation of Tarjan's algorithm for finding Strongly Connected Components.
 * Time complexity: O(V + E)
 */
public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;
    
    private int time;
    private int[] disc;      // discovery time
    private int[] low;       // lowest reachable vertex
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph) {
        if (!graph.isDirected()) {
            throw new IllegalArgumentException("SCC algorithm requires a directed graph");
        }
        this.graph = graph;
        this.metrics = new MetricsImpl();
    }

    public SCCResult findSCCs() {
        int n = graph.getVertexCount();
        time = 0;
        disc = new int[n];
        low = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        
        Arrays.fill(disc, -1);
        Arrays.fill(low, -1);
        
        metrics.reset();
        metrics.startTiming();
        
        // Run DFS from all unvisited vertices
        for (int v = 0; v < n; v++) {
            if (disc[v] == -1) {
                dfs(v);
            }
        }
        
        metrics.stopTiming();
        
        return new SCCResult(sccs, metrics);
    }

    private void dfs(int u) {
        disc[u] = low[u] = time++;
        stack.push(u);
        onStack[u] = true;
        
        metrics.incrementCounter("dfs_visits");
        
        // Visit all adjacent vertices
        for (Graph.Edge edge : graph.getAdjacent(u)) {
            int v = edge.to;
            metrics.incrementCounter("edges_explored");
            
            if (disc[v] == -1) {
                // Tree edge
                dfs(v);
                low[u] = Math.min(low[u], low[v]);
            } else if (onStack[v]) {
                // Back edge to a vertex in current SCC
                low[u] = Math.min(low[u], disc[v]);
            }
        }
        
        // If u is a root node, pop the stack to get the SCC
        if (low[u] == disc[u]) {
            List<Integer> scc = new ArrayList<>();
            int v;
            do {
                v = stack.pop();
                onStack[v] = false;
                scc.add(v);
                metrics.incrementCounter("stack_pops");
            } while (v != u);
            
            sccs.add(scc);
        }
    }

    public static class SCCResult {
        private final List<List<Integer>> sccs;
        private final Metrics metrics;
        
        public SCCResult(List<List<Integer>> sccs, Metrics metrics) {
            this.sccs = sccs;
            this.metrics = metrics;
        }
        
        public List<List<Integer>> getSCCs() {
            return sccs;
        }
        
        public Metrics getMetrics() {
            return metrics;
        }
        
        public int getComponentCount() {
            return sccs.size();
        }

        public int[] getVertexToSCCMap() {
            int maxVertex = sccs.stream()
                .flatMap(List::stream)
                .max(Integer::compareTo)
                .orElse(0);
            
            int[] map = new int[maxVertex + 1];
            Arrays.fill(map, -1);
            
            for (int i = 0; i < sccs.size(); i++) {
                for (int v : sccs.get(i)) {
                    map[v] = i;
                }
            }
            return map;
        }
        
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Strongly Connected Components: ").append(sccs.size()).append("\n");
            for (int i = 0; i < sccs.size(); i++) {
                sb.append("SCC ").append(i).append(" (size ").append(sccs.get(i).size())
                  .append("): ").append(sccs.get(i)).append("\n");
            }
            sb.append("\n").append(metrics.getReport());
            return sb.toString();
        }
    }
}
