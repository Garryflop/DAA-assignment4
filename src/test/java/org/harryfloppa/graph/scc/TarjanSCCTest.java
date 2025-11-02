package org.harryfloppa.graph.scc;

import org.harryfloppa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Tarjan's SCC algorithm.
 */
class TarjanSCCTest {
    
    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1, true);
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        assertEquals(1, result.getComponentCount());
        assertEquals(1, result.getSCCs().get(0).size());
    }
    
    @Test
    void testSimpleCycle() {
        // 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        assertEquals(1, result.getComponentCount());
        assertEquals(3, result.getSCCs().get(0).size());
    }
    
    @Test
    void testDAG() {
        // 0 -> 1 -> 2 -> 3 (no cycles)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        assertEquals(4, result.getComponentCount());
        for (List<Integer> component : result.getSCCs()) {
            assertEquals(1, component.size());
        }
    }
    
    @Test
    void testMultipleSCCs() {
        // Two separate cycles: 0->1->0 and 2->3->2, connected by 1->2
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 2, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        assertEquals(2, result.getComponentCount());
        
        // Verify each SCC has 2 vertices
        for (List<Integer> component : result.getSCCs()) {
            assertEquals(2, component.size());
        }
    }
    
    @Test
    void testSelfLoop() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 0, 1); // self-loop
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        // Self-loop makes vertex 0 its own SCC
        assertEquals(3, result.getComponentCount());
    }
    
    @Test
    void testComplexGraph() {
        // More complex graph with multiple SCCs
        Graph graph = new Graph(8, true);
        // SCC 1: 0->1->2->0 (3 vertices)
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        // SCC 2: 3->4->3 (2 vertices)
        graph.addEdge(3, 4, 1);
        graph.addEdge(4, 3, 1);
        // Single vertices: 5, 6, 7 (each is its own SCC)
        // Connections between SCCs
        graph.addEdge(2, 3, 1);
        graph.addEdge(4, 5, 1);
        graph.addEdge(5, 6, 1);
        graph.addEdge(6, 7, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        // Expected: {0,1,2}, {3,4}, {5}, {6}, {7} = 5 SCCs
        assertEquals(5, result.getComponentCount());
    }
    
    @Test
    void testVertexToSCCMap() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 0, 1);
        graph.addEdge(2, 3, 1);
        
        TarjanSCC scc = new TarjanSCC(graph);
        TarjanSCC.SCCResult result = scc.findSCCs();
        
        int[] map = result.getVertexToSCCMap();
        
        // Vertices 0 and 1 should be in the same SCC
        assertEquals(map[0], map[1]);
        // Vertices 2 and 3 should be in different SCCs
        assertNotEquals(map[2], map[3]);
    }
}
