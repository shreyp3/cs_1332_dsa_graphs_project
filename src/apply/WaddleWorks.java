package apply;

import refactor.DisjointSet;
import refactor.Edge;
import refactor.MutableGraph;
import refactor.StaticGraph;
import refactor.Vertex;
import implement.GraphAlgorithms;
import refactor.VertexDistance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class WaddleWorks implements StaticWaddleWorks {

    private final MutableGraph<Intersection> roads;
    private final MutableGraph<Building> grid;
    private final DisjointSet<Intersection> neighborhoods;

    /**
     * A parameterized constructor for WaddleWorks that accepts an intersection graph and building graph
     * @param roads the intersection graph passed in
     * @param grid the building graph passed in.
     * O(|R| + |W| + |I| + |B|)
     */
    public WaddleWorks(MutableGraph<Intersection> roads, MutableGraph<Building> grid) {
        if (roads == null || grid == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        this.roads = roads;
        this.grid = grid;
        neighborhoods = new DisjointSet<>();

        for (Vertex<Building> building : grid.getVertices()) {
            if (building.data().closest() != null) {
                if (!roads.containsVertex(new Vertex<>(building.data().closest()))) {
                    throw new IllegalArgumentException(
                            "Grid graph has a closest intersection that does not exist in road graph");
                }
            }
        }
        for (Edge<Building> wire : grid.getEdges()) {
            if (wire.weight() < 0) {
                throw new IllegalArgumentException("Wire length cannot be negative");
            }
        }
        if (grid.getVertexCount() > 1) {
            Vertex<Building> start = grid.getVertices().iterator().next();
            List<Vertex<Building>> visitedSet = GraphAlgorithms.bfs(start, grid);
            if (visitedSet.size() != grid.getVertexCount()) {
                throw new IllegalArgumentException("Gris is not full connected");
            }
        }
        for (Edge<Intersection> road : roads.getEdges()) {
            if (road.weight() < 0) {
                throw new IllegalArgumentException("Road duration cannot be negative");
            }
        }
        for (Vertex<Intersection> intersection : roads.getVertices()) {
            neighborhoods.find(intersection.data());
        }
        for (Edge<Intersection> road : roads.getEdges()) {
            neighborhoods.union(road.u().data(), road.v().data());
        }
    }

    /**
     * @param a the Intersection at one end of the road
     * @param b the Intersection at the other end of the road
     * @param duration the time it takes to traverse the road
     * @return true if road connects two previously disjoint sets, false otherwise
     * O(1)
     */
    @Override
    public boolean addRoad(Intersection a, Intersection b, int duration) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (duration < 0) {
            throw new IllegalArgumentException("Time can't be negative");
        }
        neighborhoods.find(a);
        neighborhoods.find(b);
        Edge<Intersection> newRoad = new Edge<>(new Vertex<>(a), new Vertex<>(b), duration);
        if (!roads.containsVertex(new Vertex<>(a)) || !roads.containsVertex(new Vertex<>(b))) {
            roads.addEdge(newRoad);
            neighborhoods.union(a, b);
            return false;
        } else if (neighborhoods.find(a).equals(neighborhoods.find(b))) {
            roads.addEdge(newRoad);
            neighborhoods.union(a, b);
            return false;
        } else {
            roads.addEdge(newRoad);
            neighborhoods.union(a, b);
            return true;
        }
    }

    /**
     * @param a the Building at one end of the wire
     * @param b the Building at the other end of the wire
     * @param length the length of the wire
     * O(1)
     */
    @Override
    public void addWire(Building a, Building b, int length) {
        if (a == null || b == null) {
            throw new IllegalArgumentException("No null inputs");
        }
        if (length < 0) {
            throw new IllegalArgumentException("Length can't be negative");
        }
        if (grid.getVertexCount() > 0
                && !grid.containsVertex(new Vertex<>(a))
                && !grid.containsVertex(new Vertex<>(b))) {
            throw new IllegalArgumentException("The grid will have distinct connected components");
        }
        if (a.closest() != null) {
            if (!roads.containsVertex(new Vertex<>(a.closest()))) {
                throw new IllegalArgumentException("Closest intersection does not exist in roads");
            }
        }
        if (b.closest() != null) {
            if (!roads.containsVertex(new Vertex<>(b.closest()))) {
                throw new IllegalArgumentException("Closest intersection does not exist in roads");
            }
        }
        grid.addEdge(new Edge<>(new Vertex<>(a), new Vertex<>(b), length));
    }

    /**
     * @return number of neighbors
     * O(1)
     */
    @Override
    public int getNeighborhoodCount() {
        return neighborhoods.getRoots().size();
    }

    /**
     * @param i the lower bound of the number adjacent of roads to consider
     * @param j the upper bound of the number adjacent of roads to consider
     * @return a map of intersections and intersection sets
     * O(|I| + |R|)
     */
    @Override
    public Map<Intersection, Set<Intersection>> getNeighborhoodsByConnections(int i, int j) {
        if (i < 0 || j < 0 || i > j) {
            throw new IllegalArgumentException("Bounda are invalid");
        }
        Map<Intersection, Set<Intersection>> neighborhoodsByConnections = new HashMap<>();
        for (Intersection root : neighborhoods.getRoots()) {
            neighborhoodsByConnections.put(root, new HashSet<>());
        }
        for (Vertex<Intersection> vertex : roads.getVertices()) {
            if (roads.getNeighbors(vertex).size() >= i
                    && roads.getNeighbors(vertex).size() <= j) {
                neighborhoodsByConnections.get(neighborhoods.find(vertex.data())).add(vertex.data());
            }
        }
        return neighborhoodsByConnections;
    }

    /**
     * @param from the start Building
     * @param to the end Building
     * @return the number of roads between from and to
     * O(|R| + |I|)
     */
    @Override
    public int getMinBetween(Building from, Building to) {
        if (from == null || to == null) {
            throw new IllegalArgumentException("One or both of the buildings are null");
        }
        if (!grid.containsVertex(new Vertex<>(from)) || !grid.containsVertex(new Vertex<>(to))) {
            throw new IllegalArgumentException("One or both of the buildings do not exist in the grid");
        }
        if (from.closest() == null || to.closest() == null) {
            throw new IllegalArgumentException("One or both buildings do not have a closest intersection");
        }
        if (from.closest().equals(to.closest())) {
            return 0;
        }
        Set<Vertex<Intersection>> visitedSet = new HashSet<>();
        Queue<Vertex<Intersection>> queue = new LinkedList<>();
        queue.add(new Vertex<>(from.closest()));
        visitedSet.add(new Vertex<>(from.closest()));
        int minRoads = 0;
        while (!queue.isEmpty()) {
            int level = queue.size();
            for (int i = 0; i < level; i++) {
                Vertex<Intersection> curr = queue.remove();
                for (VertexDistance<Intersection> vertex : roads.getNeighbors(curr)) {
                    if (vertex.vertex().equals(new Vertex<>(to.closest()))) {
                        return minRoads + 1;
                    }
                    if (!visitedSet.contains(vertex.vertex())) {
                        queue.add(vertex.vertex());
                        visitedSet.add(vertex.vertex());
                    }
                }
            }
            minRoads++;
        }
        return -1;
    }

    /**
     * @param from the start Building
     * @param to the end Building
     * @param avoid the set of Intersection types to avoid when possible
     * @return the list of intersections to cross
     * O(|R|log|R|)
     */
    @Override
    public List<Intersection> calculateRoute(Building from, Building to, Set<Intersection.Type> avoid) {
        if (from == null || to == null || avoid == null) {
            throw new IllegalArgumentException("At least one input is null");
        }
        if (!grid.containsVertex(new Vertex<>(from)) || !grid.containsVertex(new Vertex<>(to))) {
            throw new IllegalArgumentException("One or both of the buildings do not exist in the grid");
        }
        if (from.closest() == null || to.closest() == null) {
            throw new IllegalArgumentException("One or both buildings do not have a closest intersection");
        }
        if (from.closest().equals(to.closest())) {
            return List.of(from.closest());
        }

        Set<Intersection> visitedSet = new HashSet<>();
        Map<Intersection, Route> bestRoute = new HashMap<>();
        Map<Intersection, Intersection> previous = new HashMap<>();
        PriorityQueue<Route> priorityQueue = new PriorityQueue<>();

        priorityQueue.add(new Route(from.closest(), 0, 0));
        previous.put(from.closest(), null);

        while (!priorityQueue.isEmpty()) {

            Route dequeued = priorityQueue.remove();

            if (!visitedSet.contains(dequeued.intersection)) {
                visitedSet.add(dequeued.intersection);
                if (dequeued.intersection.equals(to.closest())) {
                    List<Intersection> returnPath = new LinkedList<>();
                    Intersection intersectionNode = to.closest();
                    while (intersectionNode != null) {
                        returnPath.addFirst(intersectionNode);
                        intersectionNode = previous.get(intersectionNode);
                    }
                    return returnPath;
                }

                for (VertexDistance<Intersection> vertex : roads.getNeighbors(new Vertex<>(dequeued.intersection))) {
                    if (!visitedSet.contains(vertex.vertex().data())) {
                        int badIntersection;
                        if (avoid.contains(vertex.vertex().data().type())) {
                            badIntersection = 1;
                        } else {
                            badIntersection = 0;
                        }
                        Route newRoute = new Route(vertex.vertex().data(),
                                dequeued.badIntersectionCount + badIntersection,
                                dequeued.duration + vertex.distance());
                        if (!bestRoute.containsKey(vertex.vertex().data())
                                || newRoute.compareTo(bestRoute.get(vertex.vertex().data())) < 0) {
                            bestRoute.put(vertex.vertex().data(), newRoute);
                            previous.put(vertex.vertex().data(), dequeued.intersection);
                            priorityQueue.add(newRoute);
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * @param candidates the list of Building candidates
     * @return the building
     * O(k*|W|log|W|)
     */
    @Override
    public Building getBestPowerSite(List<Building> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            throw new IllegalArgumentException("Candidates list is empty or null");
        }
        for (Building building : candidates) {
            if (!grid.containsVertex(new Vertex<>(building))) {
                throw new IllegalArgumentException("At least one buildings does not exist in the grid");
            }
        }
        double minAvg = Integer.MAX_VALUE * 1.0;
        Building bestPowerSite = null;
        for (Building building : candidates) {
            Map<Vertex<Building>, Integer> distanceMap = GraphAlgorithms.dijkstras(new Vertex<>(building), grid);
            double totalDistance = 0;
            for (Integer distance : distanceMap.values()) {
                totalDistance += distance;
            }
            double avg = totalDistance / distanceMap.size();
            if (avg < minAvg) {
                minAvg = avg;
                bestPowerSite = building;
            }
        }
        return bestPowerSite;
    }

    /**
     * @return double
     * O(|W|log|W|)
     */
    @Override
    public double consolidateGrid() {
        int removedLength = 0;
        int totalLength = 0;
        Set<Edge<Building>> savedWires = GraphAlgorithms.kruskals(grid);
        Set<Edge<Building>> removedWires = new HashSet<>();
        for (Edge<Building> wire : grid.getEdges()) {
            totalLength += wire.weight();
            if (!savedWires.contains(wire)) {
                removedLength += wire.weight();
                removedWires.add(wire);
            }
        }
        if (totalLength == 0) {
            return 0.0;
        }
        for (Edge<Building> wire : removedWires) {
            grid.removeEdge(wire);
        }
        return (double) removedLength / totalLength;
    }

    @Override
    public StaticGraph<Intersection> getRoads() {
        return roads;
    }

    @Override
    public StaticGraph<Building> getGrid() {
        return grid;
    }

    private record Route(Intersection intersection, int badIntersectionCount,
                         int duration) implements Comparable<Route> {

        @Override
        public int compareTo(Route comparingRoute) {
            if (badIntersectionCount != comparingRoute.badIntersectionCount) {
                return badIntersectionCount - comparingRoute.badIntersectionCount;
            }
            return duration - comparingRoute.duration;
        }
    }
}