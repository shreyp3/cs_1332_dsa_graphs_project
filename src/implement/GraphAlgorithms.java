package implement;

import refactor.DisjointSet;
import refactor.Edge;
import refactor.StaticGraph;
import refactor.Vertex;
import refactor.VertexDistance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

/**
 * Your implementation of various different graph algorithms.
 */
public class GraphAlgorithms {
    /**
     * Performs a breadth-first search (BFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the BFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     */
    public static <T> List<Vertex<T>> bfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Start does not exist in the graph");
        }
        Set<Vertex<T>> visitedSet = new HashSet<>();
        Queue<Vertex<T>> queue = new LinkedList<>();
        List<Vertex<T>> traversal = new LinkedList<>();
        queue.add(start);
        visitedSet.add(start);
        while (!queue.isEmpty()) {
            Vertex<T> curr = queue.remove();
            traversal.add(curr);
            for (VertexDistance<T> vertex : graph.getNeighbors(curr)) {
                if (!visitedSet.contains(vertex.vertex())) {
                    queue.add(vertex.vertex());
                    visitedSet.add(vertex.vertex());
                }
            }
        }
        return traversal;
    }

    /**
     * Performs a depth-first search (DFS) on the input graph, starting at
     * the parameterized starting vertex.
     * <p>
     * When exploring a vertex, explore in the order of neighbors returned by
     * the adjacency list. Failure to do so may cause you to lose points.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the DFS on
     * @param graph the graph to search through
     * @return list of vertices in visited order
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph
     * @implSpec You MUST implement this method recursively, or else you will
     * lose all points for this method.
     */
    public static <T> List<Vertex<T>> dfs(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Start does not exist in the graph");
        }
        Set<Vertex<T>> visitedSet = new HashSet<>();
        List<Vertex<T>> traversal = new LinkedList<>();
        dfs(start, graph, visitedSet, traversal);
        return traversal;
    }

    /**
     * Recursive helper method for DFS.
     * 
     * @param <T>   the generic typing of the data
     * @param curr  the current vertex being explored
     * @param g     the graph being searched through
     * @param vSet  the set of vertices that have already been visited
     * @param list  the list of vertices in visited order
     */
    private static <T> void dfs(Vertex<T> curr, StaticGraph<T> g, Set<Vertex<T>> vSet, List<Vertex<T>> list) {
        vSet.add(curr);
        list.add(curr);
        for (VertexDistance<T> vertex : g.getNeighbors(curr)) {
            if (!vSet.contains(vertex.vertex())) {
                dfs(vertex.vertex(), g, vSet, list);
            }
        }
    }

    /**
     * Finds the single-source shortest distance between the start vertex and
     * all vertices given a weighted graph (you may assume non-negative edge
     * weights).
     * <p>
     * Return a map of the shortest distances such that the key of each entry
     * is a node in the graph and the value for the key is the shortest distance
     * to that node from start, or {@link Integer#MAX_VALUE} (representing
     * infinity) if no path exists.
     *
     * @param <T>   the generic typing of the data
     * @param start the vertex to begin the Dijkstra's on (source)
     * @param graph the graph we are applying Dijkstra's to
     * @return a map of the shortest distances from start to every
     * other node in the graph
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Map<Vertex<T>, Integer> dijkstras(Vertex<T> start,
                                                        StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Start does not exist in the graph");
        }
        Set<Vertex<T>> visitedSet = new HashSet<>();
        Map<Vertex<T>, Integer> distanceMap = new HashMap<>();
        PriorityQueue<VertexDistance<T>> priorityQueue = new PriorityQueue<>();
        for (Vertex<T> vertex : graph.getVertices()) {
            distanceMap.put(vertex, Integer.MAX_VALUE);
        }
        priorityQueue.add(new VertexDistance<>(start, 0));
        distanceMap.put(start, 0);
        while (!priorityQueue.isEmpty() && visitedSet.size() != graph.getVertexCount()) {
            VertexDistance<T> dequeued = priorityQueue.remove();
            if (!visitedSet.contains(dequeued.vertex())) {
                visitedSet.add(dequeued.vertex());
                distanceMap.put(dequeued.vertex(), dequeued.distance());
                for (VertexDistance<T> vertex : graph.getNeighbors(dequeued.vertex())) {
                    if (!visitedSet.contains(vertex.vertex())) {
                        priorityQueue.add(
                                new VertexDistance<>(vertex.vertex(), dequeued.distance() + vertex.distance()));
                    }
                }
            }
        }
        return distanceMap;
    }

    /**
     * Runs Prim's algorithm on the given graph and returns the Minimum
     * Spanning Tree (MST) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid MST exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T> the generic typing of the data
     * @param start the vertex to begin Prims on
     * @param graph the graph we are applying Prims to
     * @return the MST of the graph or null if there is no valid MST
     * @throws IllegalArgumentException if any input is null, or if start
     *                                  doesn't exist in the graph.
     */
    public static <T> Set<Edge<T>> prims(Vertex<T> start, StaticGraph<T> graph) {
        if (start == null || graph == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (!graph.containsVertex(start)) {
            throw new IllegalArgumentException("Start does not exist in the graph");
        }
        Set<Vertex<T>> visitedSet = new HashSet<>();
        Set<Edge<T>> mst = new HashSet<>();
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>();
        visitedSet.add(start);
        for (VertexDistance<T> vertex : graph.getNeighbors(start)) {
            priorityQueue.add(new Edge<>(start, vertex.vertex(), vertex.distance()));
        }
        while (!priorityQueue.isEmpty() && visitedSet.size() != graph.getVertexCount()) {
            Edge<T> dequeued = priorityQueue.remove();
            if (!visitedSet.contains(dequeued.v())) {
                mst.add(dequeued);
                visitedSet.add(dequeued.v());
                for (VertexDistance<T> vertex : graph.getNeighbors(dequeued.v())) {
                    if (!visitedSet.contains(vertex.vertex())) {
                        priorityQueue.add(new Edge<>(dequeued.v(), vertex.vertex(), vertex.distance()));
                    }
                }
            }
        }
        if (visitedSet.size() != graph.getVertexCount()) {
            return null;
        }
        return mst;
    }

    /**
     * Runs Kruskal's algorithm on the given graph and returns the Minimal
     * Spanning Tree (MST) in the form of a set of Edges. If the graph is
     * disconnected and therefore no valid MST exists, return null.
     * <p>
     * You may assume that the passed in graph is undirected.
     * <p>
     * @param <T>   the generic typing of the data
     * @param graph the graph we are applying Kruskals to
     * @return the MST of the graph or null if there is no valid MST
     * @throws IllegalArgumentException if any input is null
     */
    public static <T> Set<Edge<T>> kruskals(StaticGraph<T> graph) {
        if (graph == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        DisjointSet<T> disjointSet = new DisjointSet<>();
        Set<Edge<T>> mst = new HashSet<>();
        PriorityQueue<Edge<T>> priorityQueue = new PriorityQueue<>();
        for (Edge<T> edge : graph.getEdges()) {
            priorityQueue.add(edge);
        }
        while (!priorityQueue.isEmpty() && mst.size() < graph.getVertexCount() - 1) {
            Edge<T> dequeued = priorityQueue.remove();
            if (!disjointSet.find(dequeued.u().data()).equals(disjointSet.find(dequeued.v().data()))) {
                mst.add(dequeued);
                disjointSet.union(dequeued.u().data(), dequeued.v().data());
            }
        }
        if (mst.size() != graph.getVertexCount() - 1) {
            return null;
        }
        return mst;
    }
}