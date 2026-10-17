package refactor;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Graph<T> extends MutableGraph<T> {
    private final HashMap<Vertex<T>, List<Edge<T>>> adjList;
    private final HashMap<Vertex<T>, List<VertexDistance<T>>> adjListWeights;
    /**
     * Constructor for a MutableGraph
     * Takes in a set of vertices and edges and initializes the graph.
     *
     * @param vertices the set of vertices
     * @param edges the set of edges
     */
    public Graph(Set<Vertex<T>> vertices, Set<Edge<T>> edges) {
        if (vertices == null || edges == null) {
            throw new IllegalArgumentException("No null arguments");
        }
        super(vertices, edges);
        adjList = new HashMap<>();
        adjListWeights = new HashMap<>();
        for (Vertex<T> vertex : vertices) {
            adjList.put(vertex, new LinkedList<>());
            adjListWeights.put(vertex, new LinkedList<>());
        }
        for (Edge<T> edge : edges) {
            if (!vertices.contains(edge.u()) || !vertices.contains(edge.v())) {
                throw new IllegalArgumentException("Vertex set does not contain all vertices from edge set");
            }
            if (edge.u().equals(edge.v())) {
                throw new IllegalArgumentException("No self-looping allowed");
            }
            adjList.get(edge.u()).add(edge);
            adjList.get(edge.v()).add(new Edge<>(edge.v(), edge.u(), edge.weight()));
            adjListWeights.get(edge.u()).add(new VertexDistance<>(edge.v(), edge.weight()));
            adjListWeights.get(edge.v()).add(new VertexDistance<>(edge.u(), edge.weight()));
        }
    }

    @Override
    public void addVertex(Vertex<T> vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("Vertex is null");
        }
        if (adjList.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex already exists");
        }
        adjList.put(vertex, new LinkedList<>());
        adjListWeights.put(vertex, new LinkedList<>());
        vertices.add(vertex);
    }

    @Override
    public void removeVertex(Vertex<T> vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("No null arguments");
        }
        if (!adjList.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex does not exist");
        }
        List<Edge<T>> removedEdges = adjList.get(vertex);
        adjList.remove(vertex);
        adjListWeights.remove(vertex);
        vertices.remove(vertex);
        for (Edge<T> edge : removedEdges) {
            Edge<T> oppositeEdge = new Edge<>(edge.v(), edge.u(), edge.weight());
            adjList.get(edge.v()).remove(oppositeEdge);
            edges.remove(oppositeEdge);
            adjListWeights.get(edge.v()).remove(new VertexDistance<>(edge.u(), edge.weight()));
        }
    }

    @Override
    public void addEdge(Edge<T> edge) {
        if (edge == null) {
            throw new IllegalArgumentException("No null arguments");
        }
        if (!adjList.containsKey(edge.u())) {
            addVertex(edge.u());
        }
        if (!adjList.containsKey(edge.v())) {
            addVertex(edge.v());
        }
        if (edge.u().equals(edge.v())) {
            throw new IllegalArgumentException("No self-looping allowed");
        }
        for (Edge<T> vertexEdge : adjList.get(edge.u())) {
            if (vertexEdge.equals(edge)) {
                throw new IllegalArgumentException("Edge already exists");
            }
        }
        edges.add(edge);
        adjList.get(edge.u()).add(edge);
        adjListWeights.get(edge.u()).add(new VertexDistance<>(edge.v(), edge.weight()));
        adjList.get(edge.v()).add(new Edge<>(edge.v(), edge.u(), edge.weight()));
        adjListWeights.get(edge.v()).add(new VertexDistance<>(edge.u(), edge.weight()));
    }

    @Override
    public void removeEdge(Edge<T> edge) {
        if (edge == null) {
            throw new IllegalArgumentException("No null arguments");
        }
        if (edge.u().equals(edge.v())) {
            throw new IllegalArgumentException("Self-looping is not allowed to beging with");
        }
        if (adjList.containsKey(edge.u()) && adjList.containsKey(edge.v())) {
            if (!adjList.get(edge.u()).contains(edge)) {
                throw new IllegalArgumentException("Edge does not exist");
            }
        } else {
            throw new IllegalArgumentException("Vertex/ices do(es) not exist");
        }
        edges.remove(edge);
        adjList.get(edge.u()).remove(edge);
        adjListWeights.get(edge.u()).remove(new VertexDistance<>(edge.v(), edge.weight()));
        adjList.get(edge.v()).remove(new Edge<>(edge.v(), edge.u(), edge.weight()));
        adjListWeights.get(edge.v()).remove(new VertexDistance<>(edge.u(), edge.weight()));
    }

    @Override
    public Map<Vertex<T>, List<VertexDistance<T>>> getAdjList() {
        return adjListWeights;
    }

    @Override
    public List<VertexDistance<T>> getNeighbors(Vertex<T> vertex) {
        if (vertex == null) {
            throw new IllegalArgumentException("No null arguments");
        }
        if (!adjList.containsKey(vertex)) {
            throw new IllegalArgumentException("Vertex does not exist");
        }
        return adjListWeights.get(vertex);
    }
}
