package org.harryfloppa.graph.scc;

import org.harryfloppa.graph.Graph;

import java.util.List;

/**
 * Builds the condensation graph (DAG of SCCs) from the original graph and its SCCs.
 */
public class CondensationGraph {
    private final Graph originalGraph;
    private final List<List<Integer>> sccs;
    private final int[] vertexToSCC;
    private Graph condensation;

    public CondensationGraph(Graph originalGraph, TarjanSCC.SCCResult sccResult) {
        this.originalGraph = originalGraph;
        this.sccs = sccResult.getSCCs();
        this.vertexToSCC = sccResult.getVertexToSCCMap();
        buildCondensation();
    }

    private void buildCondensation() {
        int numSCCs = sccs.size();
        condensation = new Graph(numSCCs, true);
        
        // Add edges between different SCCs
        boolean[][] hasEdge = new boolean[numSCCs][numSCCs];
        
        for (int u = 0; u < originalGraph.getVertexCount(); u++) {
            int sccU = vertexToSCC[u];
            for (Graph.Edge edge : originalGraph.getAdjacent(u)) {
                int v = edge.to;
                int sccV = vertexToSCC[v];
                
                // Add edge only if it connects different SCCs and not already added
                if (sccU != sccV && !hasEdge[sccU][sccV]) {
                    condensation.addEdge(sccU, sccV, edge.weight);
                    hasEdge[sccU][sccV] = true;
                }
            }
        }
    }

    public Graph getCondensation() {
        return condensation;
    }

    public List<List<Integer>> getSCCs() {
        return sccs;
    }

    public int[] getVertexToSCCMap() {
        return vertexToSCC;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Condensation Graph:\n");
        sb.append("Number of components: ").append(sccs.size()).append("\n");
        sb.append("Edges in condensation: ").append(condensation.getEdgeCount()).append("\n\n");
        sb.append(condensation.toString());
        return sb.toString();
    }
}
