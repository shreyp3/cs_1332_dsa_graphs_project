# 🗺️ Graphs & WaddleWorks

**A Java graph library and city-planning toolkit: adjacency-list graphs, union-find, five classic graph algorithms, and a real-world application built on top of them.**

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)
![Data Structures](https://img.shields.io/badge/Topic-Data%20Structures%20%26%20Algorithms-blue)
![Course](https://img.shields.io/badge/Georgia%20Tech-CS%201332-b3a369)
![Status](https://img.shields.io/badge/Status-Complete-brightgreen)

---

## 📖 Overview

This project builds a graph framework from the ground up and then uses it to solve city-infrastructure problems. It is organized in three layers, where each layer depends on the one before it:

| Part | Focus | What it covers |
|------|-------|----------------|
| **1. Mutable Graph** | Data structures | Adjacency-list graph, disjoint set (union-find) |
| **2. Graph Algorithms** | Algorithms | DFS, BFS, Dijkstra's, Prim's, Kruskal's |
| **3. WaddleWorks** | Application | Road network and electrical grid planning |

---

## ✨ Features

### Part 1: Mutable Graph & Disjoint Set

- **Simple, weighted, undirected graph** backed by a `HashMap` adjacency list (no self-loops, at most one edge per vertex pair)
- **Safe mutation:** `addVertex`, `addEdge`, `removeVertex`, `removeEdge`. Removing a vertex also cleans up every dangling edge so the graph never ends up in an inconsistent state
- **O(1)** adjacency-list and neighbor lookups
- **Strict input validation** with `IllegalArgumentException` for null, duplicate, missing, or self-looping inputs
- **Disjoint set** with union by rank, path compression, and an **O(1) `getRoots()`** that tracks connected components as they merge

### Part 2: Graph Algorithms

| Algorithm | Purpose | Returns |
|-----------|---------|---------|
| **Breadth-First Search** | Layer-by-layer traversal | Vertices in visit order |
| **Depth-First Search** | Deep traversal | Vertices in visit order |
| **Dijkstra's** | Single-source shortest paths | Map of vertex → shortest distance |
| **Prim's** | Minimum spanning tree (vertex-driven, priority queue) | Set of MST edges (or `null` if disconnected) |
| **Kruskal's** | Minimum spanning tree (edge-driven, union-find) | Set of MST edges (or `null` if disconnected) |

Ties are broken in lexicographic order so results are deterministic. Unreachable vertices in Dijkstra's are reported at `Integer.MAX_VALUE`.

### Part 3: WaddleWorks 🦆

A city-services application that models two connected systems:

- **Road graph:** Intersections (vertices) joined by roads weighted by travel time
- **Electrical grid:** Buildings (vertices) joined by wires weighted by length

| Method | What it does | Target complexity |
|--------|--------------|-------------------|
| `addRoad` | Adds a road and reports whether it merged two neighborhoods | O(1) |
| `addWire` | Adds a wire while keeping the grid fully connected | O(1) |
| `getNeighborhoodCount` | Number of connected components in the road network | O(1) |
| `getNeighborhoodsByConnections` | Groups intersections by neighborhood, filtered by road degree range | O(\|R\| + \|I\|) |
| `getMinBetween` | Fewest roads between two buildings (for emergency routing) | O(\|R\| + \|I\|) |
| `calculateRoute` | Fastest route that minimizes the number of "bad" intersection types (tunnels, roundabouts, ...), using duration as the tiebreaker | O(\|R\| log \|R\|) |
| `getBestPowerSite` | Picks the most central candidate building by lowest average shortest distance | O(k · \|W\| log \|W\|) |
| `consolidateGrid` | Strips redundant wiring down to a minimum spanning tree and returns the fraction of wire saved | O(\|W\| log \|W\|) |

---

## 🧠 Highlights

- **Union-find doing double duty.** The same disjoint set powers Kruskal's cycle detection *and* WaddleWorks' constant-time neighborhood tracking.
- **Lexicographic multi-criteria routing.** `calculateRoute` optimizes two objectives in priority order (fewest bad intersections first, then shortest duration) while staying within Dijkstra-level complexity.
- **Complexity-driven design.** Several methods have hard time bounds, so the data structures are built up front (for example in the constructor) to make later operations cheap.
- **Reuse over rewrite.** The application layer is built almost entirely from the graph and algorithm layers rather than from new traversal code.

---

## 🛠️ Tech Stack

- **Language:** Java
- **Core structures:** `HashMap`, `HashSet`, `PriorityQueue`, queues/stacks, disjoint set
- **Testing:** JUnit
- **Style:** Checkstyle-compliant

---

## 📂 Project Structure

```
.
├── src/        # Graph, DisjointSet, GraphAlgorithms, WaddleWorks, and supporting classes
├── tst/        # JUnit test files
└── README.md
```

---

## 🚀 Getting Started

**Prerequisites:** JDK 17+ (or the version your IDE is configured for) and IntelliJ IDEA (recommended).

```bash
# Clone the repository
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>
```

1. Open the project root in IntelliJ.
2. Mark `src/` as the **Sources Root** and `tst/` as the **Test Sources Root**.
3. Run the JUnit tests in `tst/` to verify everything works.

---

## 🔍 Example

For the sample graph used throughout the project, starting at vertex **A**:

```
BFS      →  [A, B, C, D, G, E, F, H]
DFS      →  [A, B, C, E, G, F, D, H]
Dijkstra →  {A: 0, B: 3, C: 2, D: 4, E: 5, F: 7, G: 7, H: 8}
Prim's   →  {AC, CB, BD, CE, EG, GF, GH}
Kruskal's → {FG, BC, BD, FH, AC, EG, CE}
```

Calling `consolidateGrid()` on a sample 8-building, 12-wire grid removes 5 wires (29 of 46 total length units), a savings of about **63%**.

---

## 📚 What I Learned

- Designing a graph representation that stays consistent under mutation
- Choosing data structures to hit specific time-complexity targets
- Translating algorithm concepts (greedy selection, union-find, shortest paths) into clean, tested code
- Adapting textbook algorithms to multi-criteria, real-world problems

---

## 🎓 Academic Note

This project was completed for **CS 1332: Data Structures & Algorithms** at the Georgia Institute of Technology.

---

## 👤 Author

**Shrey**
Computer Engineering @ Georgia Tech

[LinkedIn](https://www.linkedin.com/in/your-profile) · [GitHub](https://github.com/your-username)
