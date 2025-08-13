/**
 * Student Name : Devdun Fernando
 * Student ID   : 20220912
 * UOW ID       : 20532879
 */

package networkflow;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    private static final String BENCHMARKS_DIRECTORY = "benchmarks";

    public static void main(String[] args) {
        Map<String, Integer> results = new HashMap<>(); // To store file name -> max flow

        try {
            // List available network files
            File networksDir = new File(BENCHMARKS_DIRECTORY);
            if (!networksDir.exists() || !networksDir.isDirectory()) {
                System.err.println("Error: Networks directory not found. Please create a 'benchmarks' directory and place network files inside it.");
                return;
            }

            File[] networkFiles = networksDir.listFiles((dir, name) -> name.endsWith(".txt"));
            if (networkFiles == null || networkFiles.length == 0) {
                System.err.println("Error: No network files found in the benchmarks directory.");
                return;
            }

            // Sort files numerically
            Arrays.sort(networkFiles, (f1, f2) -> {
                String name1 = f1.getName();
                String name2 = f2.getName();

                // Extract the prefix (like "bridge_" or "ladder_")
                String prefix1 = name1.replaceAll("[0-9].*", "");
                String prefix2 = name2.replaceAll("[0-9].*", "");

                // Compare prefixes first
                int prefixCompare = prefix1.compareTo(prefix2);
                if (prefixCompare != 0) {
                    return prefixCompare;
                }

                // Extract the numbers
                String numStr1 = name1.substring(prefix1.length()).replace(".txt", "");
                String numStr2 = name2.substring(prefix2.length()).replace(".txt", "");

                try {
                    int num1 = Integer.parseInt(numStr1);
                    int num2 = Integer.parseInt(numStr2);
                    return Integer.compare(num1, num2);
                } catch (NumberFormatException e) {
                    // Fall back to string comparison if parsing fails
                    return name1.compareTo(name2);
                }
            });

            // Display available networks to the user
            System.out.println("Available network files:");
            for (int i = 0; i < networkFiles.length; i++) {
                System.out.println((i + 1) + ". " + networkFiles[i].getName());
            }

            // Get user selection with validation
            Scanner scanner = new Scanner(System.in);
            int selection = -1;
            while (true) {
                System.out.print("\nEnter the number of the network file you want to analyze (example: Enter'1' for bridge_1) : ");
                if (scanner.hasNextInt()) {
                    selection = scanner.nextInt();
                    if (selection >= 1 && selection <= networkFiles.length) {
                        break; // valid selection
                    } else {
                        System.out.println("Invalid selection. Please enter a number between 1 and " + networkFiles.length + ".");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a valid number from the given list to select a network file.");
                    scanner.next(); // Clear invalid input
                }
            }

            // Get the selected file path
            File selectedFile = networkFiles[selection - 1];
            String selectedFilePath = selectedFile.getPath();
            System.out.println("\nSelected network: " + selectedFile.getName());

            // Parse network and compute max flow
            FlowNetwork network = NetworkParser.parseFromFile(selectedFilePath);
            System.out.println("Network parsed successfully with " +
                    network.getNumNodes() + " nodes and " +
                    network.getNumEdges() + " edges.");

            System.out.println("\nComputing maximum flow...");
            NetworkFlow solver = new NetworkFlow(network);
            int maxFlow = solver.computeMaxFlow();

            System.out.println("\n=== Maximum Flow Results ===");
            System.out.println("Maximum flow value: " + maxFlow);

            System.out.println("\n=== Algorithm Execution Steps ===");
            for (String step : solver.getSteps()) {
                System.out.println(step);
            }

            // Store result
            results.put(selectedFile.getName(), maxFlow);

            scanner.close();

        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }

        // Print summary
        if (!results.isEmpty()) {
            System.out.println("\n=== Summary of Maximum Flows ===");
            for (Map.Entry<String, Integer> entry : results.entrySet()) {
                System.out.println("File: " + entry.getKey() + " => Maximum Flow: " + entry.getValue());
            }
        }
    }
}