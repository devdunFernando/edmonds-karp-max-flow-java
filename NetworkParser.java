/**
 * Student Name : Devdun Fernando
 * Student ID   : 20220912
 * UOW ID       : 20532879
 */

package networkflow;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


//Parser for flow network input files
public class NetworkParser {

    /**
     * Parse a flow network from a file
     * @param filePath Path to the input file
     * @return FlowNetwork object representing the network
     * @throws IOException If an I/O error occurs
     */
    public static FlowNetwork parseFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            // Read the number of nodes
            String line = reader.readLine().trim();
            int numNodes = Integer.parseInt(line);

            // Create a flow network
            FlowNetwork network = new FlowNetwork(numNodes);

            // Read edges
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;

                String[] parts = line.split("\\s+");
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid edge format: " + line);
                }

                int from = Integer.parseInt(parts[0]);
                int to = Integer.parseInt(parts[1]);
                int capacity = Integer.parseInt(parts[2]);

                network.addEdge(from, to, capacity);
            }

            return network;
        }
    }

    /**
     * Simple test method to demonstrate how to use the parser
     */
    public static void main(String[] args) {
        try {
            FlowNetwork network = parseFromFile("network.txt");
            System.out.println("Successfully parsed network with " +
                    network.getNumNodes() + " nodes and " +
                    network.getNumEdges() + " edges.");
        } catch (IOException e) {
            System.err.println("Error parsing file: " + e.getMessage());
        }
    }
}
