package org.harryfloppa.graph.topo;

import org.harryfloppa.graph.Graph;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Kahn's topological sort algorithm.
 */
class KahnTopologicalSortTest {
    
    @Test
    void testSimpleDAG() {
        // 0 -> 1 -> 2
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.isValid());
        List<Integer> order = result.getOrder();
        assertEquals(3, order.size());
        
        // Verify topological property: 0 before 1, 1 before 2
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(1) < order.indexOf(2));
    }
    
    @Test
    void testDAGWithMultiplePaths() {
        // Diamond: 0 -> 1, 0 -> 2, 1 -> 3, 2 -> 3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(0, 2, 1);
        graph.addEdge(1, 3, 1);
        graph.addEdge(2, 3, 1);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.isValid());
        List<Integer> order = result.getOrder();
        assertEquals(4, order.size());
        
        // Verify 0 is first and 3 is last
        assertEquals(0, order.get(0).intValue());
        assertEquals(3, order.get(3).intValue());
    }
    
    @Test
    void testCycleDetection() {
        // Cycle: 0 -> 1 -> 2 -> 0
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(1, 2, 1);
        graph.addEdge(2, 0, 1);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.hasCycle());
        assertFalse(result.isValid());
    }
    
    @Test
    void testSingleVertex() {
        Graph graph = new Graph(1, true);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.isValid());
        assertEquals(1, result.getOrder().size());
        assertEquals(0, result.getOrder().get(0).intValue());
    }
    
    @Test
    void testDisconnectedGraph() {
        // Two separate chains: 0->1 and 2->3
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 1);
        graph.addEdge(2, 3, 1);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.isValid());
        List<Integer> order = result.getOrder();
        assertEquals(4, order.size());
        
        // Verify dependencies are preserved
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }
    
    @Test
    void testComplexDAG() {
        // More complex DAG
        Graph graph = new Graph(6, true);
        graph.addEdge(5, 2, 1);
        graph.addEdge(5, 0, 1);
        graph.addEdge(4, 0, 1);
        graph.addEdge(4, 1, 1);
        graph.addEdge(2, 3, 1);
        graph.addEdge(3, 1, 1);
        
        KahnTopologicalSort topo = new KahnTopologicalSort(graph);
        KahnTopologicalSort.TopoSortResult result = topo.sort();
        
        assertTrue(result.isValid());
        List<Integer> order = result.getOrder();
        assertEquals(6, order.size());
        
        // Verify some dependencies
        assertTrue(order.indexOf(5) < order.indexOf(2));
        assertTrue(order.indexOf(2) < order.indexOf(3));
        assertTrue(order.indexOf(3) < order.indexOf(1));
    }
}
