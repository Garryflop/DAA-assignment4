package org.harryfloppa.graph.dagsp;

import org.harryfloppa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DAG shortest path algorithm.
 */
class DAGShortestPathTest {
    
    @Test
    void testSimplePath() {
        // 0 -> 1 -> 2
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        
        DAGShortestPath sp = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = sp.computeShortestPaths(0);
        
        assertEquals(0, result.getDistance(0));
        assertEquals(5, result.getDistance(1));
        assertEquals(8, result.getDistance(2));
        
        List<Integer> path = result.getPath(2);
        assertEquals(List.of(0, 1, 2), path);
    }
    
    @Test
    void testDiamondGraph() {
        // Diamond: 0 -> 1 (5), 0 -> 2 (3), 1 -> 3 (2), 2 -> 3 (6)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 6);
        
        DAGShortestPath sp = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = sp.computeShortestPaths(0);
        
        assertEquals(0, result.getDistance(0));
        assertEquals(5, result.getDistance(1));
        assertEquals(3, result.getDistance(2));
        assertEquals(7, result.getDistance(3)); // Through 0->1->3
        
        List<Integer> path = result.getPath(3);
        assertEquals(List.of(0, 1, 3), path);
    }
    
    @Test
    void testUnreachableVertex() {
        // 0 -> 1, 2 (isolated)
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);
        
        DAGShortestPath sp = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = sp.computeShortestPaths(0);
        
        assertTrue(result.isReachable(1));
        assertFalse(result.isReachable(2));
        
        List<Integer> path = result.getPath(2);
        assertTrue(path.isEmpty());
    }
    
    @Test
    void testCycleThrowsException() {
        // Graph with cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        
        DAGShortestPath sp = new DAGShortestPath(graph);
        assertThrows(IllegalArgumentException.class, () -> sp.computeShortestPaths(0));
    }
    
    @Test
    void testComplexDAG() {
        // More complex DAG
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 2);
        graph.addEdge(0, 2, 4);
        graph.addEdge(1, 2, 1);
        graph.addEdge(1, 3, 7);
        graph.addEdge(2, 4, 3);
        graph.addEdge(3, 4, 2);
        graph.addEdge(3, 5, 1);
        graph.addEdge(4, 5, 5);
        
        DAGShortestPath sp = new DAGShortestPath(graph);
        DAGShortestPath.ShortestPathResult result = sp.computeShortestPaths(0);
        
        assertEquals(0, result.getDistance(0));
        assertEquals(2, result.getDistance(1));
        assertEquals(3, result.getDistance(2));
        assertEquals(6, result.getDistance(4));
        assertEquals(10, result.getDistance(5));
    }
}
