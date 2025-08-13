/**
 * Student Name : Devdun Fernando
 * Student ID   : 20220912
 * UOW ID       : 20532879
 */

package networkflow;

import java.util.*;

// Implementation of the Edmonds-Karp algorithm for maximum flow
public class NetworkFlow {
    private final FlowNetwork network;  // The flow network
    private final List<String> steps;  // To store the execution steps for reporting

    // Constructor: Initialize with a flow network
    public NetworkFlow(FlowNetwork network) {
        this.network = network;
        this.steps = new ArrayList<>();
    }

    /**
     * Compute the maximum flow in the network using the Edmonds-Karp algorithm
     * @return The maximum flow value
     */
    public int computeMaxFlow() {
        int maxFlow = 0;
        steps.add("Starting Edmonds-Karp algorithm\n");

        // Continue finding augmenting paths and augmenting flow until no more paths exist
        int iteration = 1;
        while (true) {
            // Find an augmenting path using BFS
            List<FlowNetwork.Edge> path = findAugmentingPath();

            // If no path exists, we're done
            if (path == null) {
                steps.add("\nNo more augmenting paths found. Algorithm terminates.\n");
                break;
            }

            // Calculate the bottleneck capacity (minimum residual capacity along the path)
            int bottleneck = calculateBottleneck(path);
            maxFlow += bottleneck;

            // Log this iteration
            steps.add("Iteration " + iteration + ":");
            steps.add("  Found augmenting path: " + pathToString(path));
            steps.add("  Bottleneck capacity: " + bottleneck);
            steps.add("  Current max flow: " + maxFlow);

            // Augment flow along the path
            augmentFlow(path, bottleneck);

            iteration++;
        }

        // Add final result
        steps.add("Maximum flow: " + maxFlow);
        logFinalEdgeFlows();

        return maxFlow;
    }

    /**
     * Find an augmenting path using Breadth-First Search (BFS)
     * @return List of edges forming an augmenting path, or null if no path exists
     */
    private List<FlowNetwork.Edge> findAugmentingPath() {
        int source = network.getSource();
        int sink = network.getSink();
        int numNodes = network.getNumNodes();

        // Initialize BFS data structures
        boolean[] visited = new boolean[numNodes];
        FlowNetwork.Edge[] edgeTo = new FlowNetwork.Edge[numNodes];
        Queue<Integer> queue = new LinkedList<>();

        // Start BFS from source
        visited[source] = true;
        queue.add(source);

        // BFS loop
        while (!queue.isEmpty() && !visited[sink]) {
            int current = queue.poll();

            // Explore all edges from current node
            for (FlowNetwork.Edge edge : network.getEdges(current)) {
                int to = edge.getTo();

                // If we haven't visited this node and there is residual capacity
                if (!visited[to] && edge.getResidualCapacity() > 0) {
                    visited[to] = true;
                    edgeTo[to] = edge;
                    queue.add(to);
                }
            }
        }

        // If we didn't reach the sink, there's no augmenting path
        if (!visited[sink]) {
            return null;
        }

        // Reconstruct the path from sink to source using edgeTo[]
        List<FlowNetwork.Edge> path = new ArrayList<>();
        for (int v = sink; v != source; v = edgeTo[v].getFrom()) {
            path.add(edgeTo[v]);
        }
        Collections.reverse(path);  // Reverse the path to get correct order from source to sink

        return path;
    }

    /**
     * Calculate the bottleneck capacity (minimum residual capacity) of an augmenting path
     * @param path The augmenting path
     * @return The bottleneck value
     */
    private int calculateBottleneck(List<FlowNetwork.Edge> path) {
        int bottleneck = Integer.MAX_VALUE;
        for (FlowNetwork.Edge edge : path) {
            bottleneck = Math.min(bottleneck, edge.getResidualCapacity());
        }
        return bottleneck;
    }

    /**
     * Augment flow along a path by the given amount
     * @param path The path to augment
     * @param amount The amount of flow to add
     */
    private void augmentFlow(List<FlowNetwork.Edge> path, int amount) {
        for (FlowNetwork.Edge edge : path) {
            edge.addFlow(amount);
        }
    }

    /**
     * Convert a path to a readable string for debugging/logging
     * @param path The path
     * @return A string representation of the path
     */
    private String pathToString(List<FlowNetwork.Edge> path) {
        StringBuilder sb = new StringBuilder();
        sb.append(network.getSource());
        for (FlowNetwork.Edge edge : path) {
            sb.append(" → ").append(edge.getTo());
        }
        return sb.toString();
    }

    /**
     * Log the final flow values on each edge after the algorithm completes
     */
    private void logFinalEdgeFlows() {
        steps.add("Final flow values on edges:");
        for (int node = 0; node < network.getNumNodes(); node++) {
            for (FlowNetwork.Edge edge : network.getEdges(node)) {
                // Only consider original edges (not residual ones)
                if (edge.getCapacity() > 0) {
                    steps.add(String.format("  Edge (%d→%d): flow = %d/%d",
                            edge.getFrom(), edge.getTo(), edge.getFlow(), edge.getCapacity()));
                }
            }
        }
    }

    /**
     * Get the execution steps of the algorithm
     * @return A list of strings representing each step
     */
    public List<String> getSteps() {
        return steps;
    }
}
