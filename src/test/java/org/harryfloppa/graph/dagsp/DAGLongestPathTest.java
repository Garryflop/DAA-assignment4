package org.harryfloppa.graph.dagsp;

import org.harryfloppa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for DAG longest path algorithm.
 */
class DAGLongestPathTest {
    
    @Test
    void testSimplePath() {
        // 0 -> 1 -> 2
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(1, 2, 3);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.LongestPathResult result = lp.computeLongestPaths(0);
        
        assertEquals(0, result.getDistance(0));
        assertEquals(5, result.getDistance(1));
        assertEquals(8, result.getDistance(2));
    }
    
    @Test
    void testDiamondGraph() {
        // Diamond: 0 -> 1 (5), 0 -> 2 (3), 1 -> 3 (2), 2 -> 3 (6)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 6);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.LongestPathResult result = lp.computeLongestPaths(0);
        
        assertEquals(0, result.getDistance(0));
        assertEquals(5, result.getDistance(1));
        assertEquals(3, result.getDistance(2));
        assertEquals(9, result.getDistance(3)); // Through 0->2->3
        
        List<Integer> path = result.getPath(3);
        assertEquals(List.of(0, 2, 3), path);
    }
    
    @Test
    void testCriticalPath() {
        // 0 -> 1 (5), 0 -> 2 (3), 1 -> 3 (2), 2 -> 3 (6)
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 5);
        graph.addEdge(0, 2, 3);
        graph.addEdge(1, 3, 2);
        graph.addEdge(2, 3, 6);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.CriticalPathResult result = lp.computeCriticalPath();
        
        assertEquals(9, result.getLength());
        assertEquals(List.of(0, 2, 3), result.getPath());
    }
    
    @Test
    void testComplexCriticalPath() {
        // More complex graph
        Graph graph = new Graph(6, true);
        graph.addEdge(0, 1, 3);
        graph.addEdge(0, 2, 2);
        graph.addEdge(1, 3, 4);
        graph.addEdge(2, 3, 1);
        graph.addEdge(2, 4, 5);
        graph.addEdge(3, 5, 2);
        graph.addEdge(4, 5, 3);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.CriticalPathResult result = lp.computeCriticalPath();
        
        // Critical path should be 0->2->4->5 = 2+5+3 = 10
        assertEquals(10, result.getLength());
        assertEquals(List.of(0, 2, 4, 5), result.getPath());
    }
    
    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1, true);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        DAGLongestPath.CriticalPathResult result = lp.computeCriticalPath();
        
        assertEquals(0, result.getLength());
        assertEquals(List.of(0), result.getPath());
    }
    
    @Test
    void testCycleThrowsException() {
        // Graph with cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        
        DAGLongestPath lp = new DAGLongestPath(graph);
        assertThrows(IllegalArgumentException.class, () -> lp.computeCriticalPath());
    }
}
