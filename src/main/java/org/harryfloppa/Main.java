package org.harryfloppa;

import org.harryfloppa.graph.Graph;
import org.harryfloppa.graph.dagsp.DAGLongestPath;
import org.harryfloppa.graph.dagsp.DAGShortestPath;
import org.harryfloppa.graph.scc.CondensationGraph;
import org.harryfloppa.graph.scc.TarjanSCC;
import org.harryfloppa.graph.topo.KahnTopologicalSort;
import org.harryfloppa.util.GraphLoader;

import java.io.File;
import java.util.List;

public class Main {
    
    public static void main(String[] args) {
        System.out.println("=".repeat(80));
        System.out.println("DAA Assignment 4: Smart City Scheduling");
        System.out.println("Strongly Connected Components & Shortest Paths in DAGs");
        System.out.println("=".repeat(80));
        System.out.println();

        if (args.length > 0) {
            processFile(args[0]);
        } else {
            processFile("tasks.json");

            System.out.println("\n" + "=".repeat(80));
            System.out.println("Processing all datasets from 'data' directory:");
            System.out.println("=".repeat(80));
            processDataDirectory();
        }
    }

    private static void processFile(String filename) {
        System.out.println("\n" + "-".repeat(80));
        System.out.println("Processing: " + filename);
        System.out.println("-".repeat(80));
        
        try {
            // Load graph from JSON
            GraphLoader.GraphData data = GraphLoader.loadFromJSON(filename);
            Graph graph = data.getGraph();
            int source = data.getSource();
            
            System.out.println("Graph loaded successfully:");
            System.out.println("  Vertices: " + graph.getVertexCount());
            System.out.println("  Edges: " + graph.getEdgeCount());
            System.out.println("  Source vertex: " + source);
            System.out.println("  Weight model: " + data.getWeightModel());
            System.out.println();
            
            // 1. Find Strongly Connected Components
            System.out.println("1. STRONGLY CONNECTED COMPONENTS (Tarjan's Algorithm)");
            System.out.println("-".repeat(80));
            TarjanSCC sccFinder = new TarjanSCC(graph);
            TarjanSCC.SCCResult sccResult = sccFinder.findSCCs();
            System.out.println(sccResult);
            
            // 2. Build Condensation Graph
            System.out.println("\n2. CONDENSATION GRAPH");
            System.out.println("-".repeat(80));
            CondensationGraph condensation = new CondensationGraph(graph, sccResult);
            System.out.println(condensation);
            
            // 3. Topological Sort on Condensation
            System.out.println("\n3. TOPOLOGICAL SORT (Kahn's Algorithm)");
            System.out.println("-".repeat(80));
            Graph condensationDAG = condensation.getCondensation();
            KahnTopologicalSort topoSort = new KahnTopologicalSort(condensationDAG);
            KahnTopologicalSort.TopoSortResult topoResult = topoSort.sort();
            System.out.println(topoResult);
            
            // Create task order from topo order of SCCs
            System.out.println("Original task order (by SCC):");
            List<Integer> topoOrder = topoResult.getOrder();
            List<List<Integer>> sccs = sccResult.getSCCs();
            for (int sccIndex : topoOrder) {
                System.out.println("  SCC " + sccIndex + ": " + sccs.get(sccIndex));
            }
            System.out.println();
            
            // 4. Shortest Paths on DAG (if it's a DAG)
            if (sccResult.getComponentCount() == graph.getVertexCount()) {
                System.out.println("\n4. SHORTEST PATHS IN DAG");
                System.out.println("-".repeat(80));
                try {
                    DAGShortestPath shortestPath = new DAGShortestPath(graph);
                    DAGShortestPath.ShortestPathResult spResult = shortestPath.computeShortestPaths(source);
                    System.out.println(spResult);
                } catch (IllegalArgumentException e) {
                    System.out.println("Graph contains cycles - cannot compute DAG shortest paths on original graph.");
                    System.out.println("Computing on condensation DAG instead...\n");
                    computeDAGPaths(condensationDAG, 0);
                }
            } else {
                System.out.println("\n4. SHORTEST PATHS (on Condensation DAG)");
                System.out.println("-".repeat(80));
                System.out.println("Original graph has cycles. Computing paths on condensation DAG...\n");
                computeDAGPaths(condensationDAG, 0);
            }
            
        } catch (Exception e) {
            System.err.println("Error processing file " + filename + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void computeDAGPaths(Graph dag, int source) {
        try {
            // Shortest paths
            DAGShortestPath shortestPath = new DAGShortestPath(dag);
            DAGShortestPath.ShortestPathResult spResult = shortestPath.computeShortestPaths(source);
            System.out.println(spResult);
            
            // Longest paths
            System.out.println("\n5. LONGEST PATHS IN DAG (Critical Path)");
            System.out.println("-".repeat(80));
            DAGLongestPath longestPath = new DAGLongestPath(dag);
            DAGLongestPath.CriticalPathResult cpResult = longestPath.computeCriticalPath();
            System.out.println(cpResult);
            
            // Also show longest paths from source
            DAGLongestPath.LongestPathResult lpResult = longestPath.computeLongestPaths(source);
            System.out.println("Longest paths from source " + source + ":");
            System.out.println(lpResult);
            
        } catch (Exception e) {
            System.err.println("Error computing DAG paths: " + e.getMessage());
        }
    }

    private static void processDataDirectory() {
        File dataDir = new File("data");
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            System.out.println("Data directory not found. Skipping batch processing.");
            return;
        }
        
        File[] files = dataDir.listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null || files.length == 0) {
            System.out.println("No JSON files found in data directory.");
            return;
        }
        
        for (File file : files) {
            processFile(file.getPath());
        }
        
        System.out.println("\n" + "=".repeat(80));
        System.out.println("Batch processing complete. Processed " + files.length + " datasets.");
        System.out.println("=".repeat(80));
    }
}