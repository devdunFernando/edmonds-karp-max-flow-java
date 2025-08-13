/**
 * Student Name : Devdun Fernando
 * Student ID   : 20220912
 * UOW ID       : 20532879
 */

package networkflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Flow Network data structure using adjacency list representation
 */
public class FlowNetwork {
    private final int numNodes;  // Number of nodes in the network
    private final int source;    // Source node index
    private final int sink;      // Sink node index
    private int numEdges;        // Number of edges in the network

    // Adjacency list representation
    private final List<Edge>[] graph;

    /**
     * Edge class representing a directed edge in the flow network.
     */
    public static class Edge {
        private final int from;     // Source node
        private final int to;       // Target node
        private final int capacity; // Edge capacity
        private int flow;           // Current flow
        private Edge residual;      // Corresponding edge in residual graph

        public Edge(int from, int to, int capacity) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.flow = 0;
        }

        public int getFrom() { return from; }
        public int getTo() { return to; }
        public int getCapacity() { return capacity; }
        public int getFlow() { return flow; }

        // Residual capacity from -> to
        public int getResidualCapacity() {
            return capacity - flow;
        }

        // Add flow along this edge
        public void addFlow(int deltaFlow) {
            flow += deltaFlow;
            residual.flow -= deltaFlow; // Update residual edge
        }

        @Override
        public String toString() {
            return String.format("Edge(%d→%d, capacity=%d, flow=%d)", from, to, capacity, flow);
        }
    }

    /**
     * Constructor to create an empty flow network
     */
    @SuppressWarnings("unchecked")
    public FlowNetwork(int numNodes) {
        if (numNodes < 2) throw new IllegalArgumentException("Network should have at least 2 nodes");

        this.numNodes = numNodes;
        this.source = 0;                // Node 0 is the source
        this.sink = numNodes - 1;       // Node n-1 is the sink
        this.numEdges = 0;

        // Initialize adjacency lists
        graph = (List<Edge>[]) new List[numNodes];
        for (int i = 0; i < numNodes; i++) {
            graph[i] = new ArrayList<>();
        }
    }

    /**
     * Add a directed edge to the flow network
     */
    public void addEdge(int from, int to, int capacity) {
        if (from < 0 || from >= numNodes) throw new IndexOutOfBoundsException("Invalid source node index");
        if (to < 0 || to >= numNodes) throw new IndexOutOfBoundsException("Invalid target node index");
        if (capacity <= 0) throw new IllegalArgumentException("Capacity must be positive");

        // Create forward edge
        Edge forward = new Edge(from, to, capacity);

        // Create backward (residual) edge
        Edge backward = new Edge(to, from, 0);

        // Link the residual edges
        forward.residual = backward;
        backward.residual = forward;

        // Add edges to the graph
        graph[from].add(forward);
        graph[to].add(backward);

        numEdges++;
    }

    // Get all edges from a node
    public List<Edge> getEdges(int node) {
        if (node < 0 || node >= numNodes) throw new IndexOutOfBoundsException("Invalid node index");
        return graph[node];
    }

    // Getters
    public int getNumNodes() { return numNodes; }
    public int getSource() { return source; }
    public int getSink() { return sink; }
    public int getNumEdges() { return numEdges; }

    /**
     * Get a list of all forward edges in the graph
     */
    public List<Edge> getAllEdges() {
        List<Edge> edges = new ArrayList<>();
        for (int node = 0; node < numNodes; node++) {
            for (Edge edge : graph[node]) {
                if (edge.getResidualCapacity() > 0) {
                    edges.add(edge);
                }
            }
        }
        return edges;
    }

    /**
     * Get the total flow from the source node
     */
    public int getTotalFlow() {
        int totalFlow = 0;
        for (Edge edge : graph[source]) {
            totalFlow += edge.getFlow();
        }
        return totalFlow;
    }
}
