# DAA Assignment 4: Smart City/Campus Scheduling

**Author:** Saparbekov Nurdaulet SE-2402  
**Course:** Design and Analysis of Algorithms  
**Topic:** Strongly Connected Components & Shortest Paths in DAGs

## Overview

This project implements algorithms for analyzing task dependencies in a Smart City/Campus scheduling system. The implementation covers:

1. **Strongly Connected Components (SCC)** - Tarjan's Algorithm
2. **Topological Sorting** - Kahn's Algorithm  
3. **Shortest Paths in DAGs** - Dynamic Programming on Topological Order
4. **Longest Paths in DAGs** - Critical Path Analysis

## Project Structure

```
DAA-assignment4/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/
│   │           └── harryfloppa/
│   │               ├── Main.java                    # Main driver program
│   │               ├── common/                      # Common interfaces
│   │               │   ├── Metrics.java
│   │               │   └── MetricsImpl.java
│   │               ├── graph/                       # Graph representation
│   │               │   ├── Graph.java
│   │               │   ├── scc/                     # SCC algorithms
│   │               │   │   ├── TarjanSCC.java
│   │               │   │   └── CondensationGraph.java
│   │               │   ├── topo/                    # Topological sorting
│   │               │   │   └── KahnTopologicalSort.java
│   │               │   └── dagsp/                   # DAG shortest/longest paths
│   │               │       ├── DAGShortestPath.java
│   │               │       └── DAGLongestPath.java
│   │               └── util/                        # Utilities
│   │                   └── GraphLoader.java
│   └── test/
│       └── java/                                    # JUnit tests
│           └── org/
│               └── harryfloppa/
│                   └── graph/
│                       ├── scc/
│                       │   └── TarjanSCCTest.java
│                       ├── topo/
│                       │   └── KahnTopologicalSortTest.java
│                       └── dagsp/
│                           ├── DAGShortestPathTest.java
│                           └── DAGLongestPathTest.java
├── data/                                            # Test datasets (generated)
│   ├── small_dag_sparse.json
│   ├── small_cyclic.json
│   ├── small_dag_dense.json
│   ├── medium_dag.json
│   ├── medium_cyclic.json
│   ├── medium_mixed.json
│   ├── large_dag_sparse.json
│   ├── large_cyclic.json
│   └── large_dag_performance.json
├── tasks.json                                       # Sample task graph
├── pom.xml                                          # Maven configuration
└── README.md                                        # This file
```

## Building and Running

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build

```bash
mvn clean compile
```

### Run Tests

```bash
mvn test
```

### Run Main Program

To process the default `tasks.json`:
```bash
mvn exec:java -Dexec.mainClass="org.harryfloppa.Main"
```

To process a specific file:
```bash
mvn exec:java -Dexec.mainClass="org.harryfloppa.Main" -Dexec.args="data/small_cyclic.json"
```

## Dataset Descriptions

### Small Datasets (6-10 vertices)

| Dataset | Vertices | Edges | Type | Description |
|---------|----------|-------|------|-------------|
| `small_dag_sparse.json` | 6 | ~5 | DAG | Simple acyclic structure for basic testing |
| `small_cyclic.json` | 8 | ~12 | Cyclic | Contains 2 SCCs with cycles |
| `small_dag_dense.json` | 10 | ~18 | DAG | Dense acyclic graph |

### Medium Datasets (10-20 vertices)

| Dataset | Vertices | Edges | Type | Description |
|---------|----------|-------|------|-------------|
| `medium_dag.json` | 12 | ~18 | DAG | Medium acyclic structure |
| `medium_cyclic.json` | 15 | ~30 | Cyclic | Contains 3 SCCs |
| `medium_mixed.json` | 20 | ~45 | Cyclic | Complex structure with 4 SCCs |

### Large Datasets (20-50 vertices)

| Dataset | Vertices | Edges | Type | Description |
|---------|----------|-------|------|-------------|
| `large_dag_sparse.json` | 25 | ~63 | DAG | Large sparse DAG |
| `large_cyclic.json` | 35 | ~147 | Cyclic | Contains 5 SCCs |
| `large_dag_performance.json` | 50 | ~188 | DAG | Performance testing |

## Algorithm Implementation Details

### 1. Strongly Connected Components (Tarjan's Algorithm)

**File:** `graph/scc/TarjanSCC.java`

**Time Complexity:** O(V + E)  
**Space Complexity:** O(V)

**Key Features:**
- Single DFS pass to identify all SCCs
- Uses discovery times and low-link values
- Stack-based approach for SCC extraction

**Metrics Tracked:**
- `dfs_visits`: Number of DFS recursive calls
- `edges_explored`: Number of edges examined
- `stack_pops`: Number of vertices popped from stack

### 2. Condensation Graph

**File:** `graph/scc/CondensationGraph.java`

**Purpose:** Converts the original graph with cycles into a DAG where each SCC becomes a single node.

**Features:**
- Builds DAG of SCCs
- Preserves inter-SCC edges
- Enables topological sorting on cyclic graphs

### 3. Topological Sorting (Kahn's Algorithm)

**File:** `graph/topo/KahnTopologicalSort.java`

**Time Complexity:** O(V + E)  
**Space Complexity:** O(V)

**Key Features:**
- BFS-based approach using in-degree counting
- Detects cycles (no valid topological order if cycle exists)
- Queue-based implementation

**Metrics Tracked:**
- `edges_scanned`: Edges processed for in-degree calculation
- `queue_pushes`: Vertices added to queue
- `queue_pops`: Vertices removed from queue
- `in_degree_updates`: In-degree modifications

### 4. DAG Shortest Path

**File:** `graph/dagsp/DAGShortestPath.java`

**Time Complexity:** O(V + E)  
**Space Complexity:** O(V)

**Algorithm:**
1. Compute topological ordering
2. Initialize distances (source = 0, others = ∞)
3. Process vertices in topological order
4. Relax all outgoing edges

**Metrics Tracked:**
- `relaxations`: Number of edge relaxation attempts
- `distance_updates`: Successful distance improvements

**Features:**
- Single-source shortest paths
- Path reconstruction
- Handles unreachable vertices

### 5. DAG Longest Path (Critical Path)

**File:** `graph/dagsp/DAGLongestPath.java`

**Time Complexity:** O(V + E)  
**Space Complexity:** O(V)

**Algorithm:**
1. Compute topological ordering
2. Initialize distances (all = 0 or -∞)
3. Process vertices in topological order
4. Maximize distances instead of minimize

**Metrics Tracked:**
- `relaxations`: Number of edge relaxation attempts
- `distance_updates`: Successful distance improvements

**Features:**
- Single-source longest paths
- Critical path computation (longest path in entire DAG)
- Path reconstruction

## Weight Model

**Current Implementation:** Edge-weighted graphs

All datasets use `"weight_model": "edge"`, meaning weights are associated with edges (representing task dependencies and their costs/durations).

## Performance Analysis

### Time Complexity Summary

| Algorithm | Time | Space | Notes |
|-----------|------|-------|-------|
| Tarjan SCC | O(V+E) | O(V) | Single DFS pass |
| Condensation | O(V+E) | O(V+E) | Processes all edges once |
| Kahn's Topo | O(V+E) | O(V) | BFS with in-degree tracking |
| DAG Shortest | O(V+E) | O(V) | Processes edges in topo order |
| DAG Longest | O(V+E) | O(V) | Same as shortest path |

### Experimental Results

Run the program to see detailed metrics for each dataset:

```bash
mvn exec:java -Dexec.mainClass="org.harryfloppa.Main"
```

Expected observations:
- **SCC Detection:** Linear in V+E, efficient even for large graphs
- **Topological Sort:** Queue operations proportional to graph size
- **DAG Paths:** Relaxation count = E, very efficient

### Bottleneck Analysis

1. **SCC (Tarjan):** 
   - Bottleneck: DFS recursion depth (limited by stack size)
   - Mitigation: Could implement iterative version for very deep graphs

2. **Topological Sort (Kahn):**
   - Bottleneck: In-degree calculation requires scanning all edges
   - Already optimal for this approach

3. **DAG Shortest/Longest Paths:**
   - Bottleneck: Topological sort preprocessing
   - Very efficient once topo order is computed

### Effect of Graph Structure

- **Density:** Higher edge density increases relaxation operations but maintains O(E) complexity
- **SCC Size:** Larger SCCs in condensation reduce the DAG vertex count
- **Connectivity:** Sparse graphs have fewer relaxations but same asymptotic complexity

## Code Quality Features

### Design Patterns
- **Strategy Pattern:** Different algorithms implement common interfaces
- **Builder Pattern:** Graph construction with fluent API
- **Factory Pattern:** Metrics creation

### Testing
- Comprehensive JUnit 5 tests for all algorithms
- Edge cases: single vertex, cycles, disconnected graphs, empty graphs
- Deterministic tests with known outputs

### Documentation
- Javadoc for all public classes and methods
- Inline comments for complex logic
- Clear variable naming

### Modularity
- Separate packages for each algorithm category
- Common interfaces for metrics tracking
- Reusable Graph class

## Practical Recommendations

### When to Use Each Algorithm

1. **SCC (Tarjan):**
   - Detect cyclic dependencies in task scheduling
   - Identify mutually dependent components
   - Simplify complex dependency graphs

2. **Topological Sort:**
   - Schedule tasks respecting dependencies
   - Determine valid execution order
   - Detect impossible scheduling (cycles)

3. **DAG Shortest Path:**
   - Find minimum cost/time to complete tasks
   - Optimize resource usage
   - Critical for budget-constrained projects

4. **DAG Longest Path:**
   - Find critical path in project scheduling
   - Identify bottleneck tasks
   - Calculate minimum project completion time

### Real-World Applications

- **Smart City:** Schedule infrastructure maintenance tasks
- **Smart Campus:** Coordinate facility management
- **Project Management:** PERT/CPM analysis
- **Course Prerequisites:** Academic program planning
- **Build Systems:** Dependency resolution

