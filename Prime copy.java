import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Prime {

    // Read words from file
    static String[] readWordsFromFile(String filename, int numWords) throws IOException {
        try (FileReader fr = new FileReader(filename);
             BufferedReader br = new BufferedReader(fr)) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null && numWords > 0) {
                sb.append(line).append(" ");
                numWords--;
            }
            return sb.toString().split("\\s+");
        }
    }

    // Hashing using linear probing
    static long hashing(String arr[], int tsize, Map<Integer, String> hashTable, int[] hashCount) {
        long startTime = System.nanoTime();
        int totalProbes = 0;
        for (String word : arr) {
            int hashValue = Math.abs(word.hashCode()) % tsize;
            int probes = 0;
            while (hashTable.containsKey(hashValue)) {
                probes++;
                totalProbes++;
                hashValue = (hashValue + 1) % tsize; // Linear probing
            }
            hashCount[hashValue]++; // Increment count for hash position
            hashTable.put(hashValue, word);
        }
        long endTime = System.nanoTime();
        System.out.println("Total Probes Used: " + totalProbes);
        return (endTime - startTime) / 1000000; // Return duration in milliseconds
    }

    // Perform random searches and calculate average, min, and max probes for each number of searches
    static void performRandomSearches(Map<Integer, String> hashTable, int[] hashCount, int numRandomSearches) {
        int totalProbes = 0;
        int maxProbes = Integer.MIN_VALUE;
        int minProbes = Integer.MAX_VALUE;
        int[] probeResults = new int[numRandomSearches];

        for (int i = 0; i < numRandomSearches; i++) {
            int randomIndex = new Random().nextInt(hashTable.size());
            int probes = countProbes(hashTable, hashCount, randomIndex);
            totalProbes += probes;
            probeResults[i] = probes;
            maxProbes = Math.max(maxProbes, probes);
            minProbes = Math.min(minProbes, probes);
        }

        double averageProbes = (double) totalProbes / numRandomSearches;
        System.out.println("Average probes: " + averageProbes);
        System.out.println("Max probes: " + maxProbes);
        System.out.println("Min probes: " + minProbes);

        // Write probe results to CSV file
        writeProbeResultsToCSV(probeResults);
    }

    // Count probes for a random word
    private static int countProbes(Map<Integer, String> hashTable, int[] hashCount, int randomIndex) {
        int probes = 0;
        while (hashTable.containsKey(randomIndex)) {
            probes++;
            randomIndex = (randomIndex + 1) % hashCount.length; // Linear probing
        }
        return probes;
    }

    // Write probe results to a CSV file
    private static void writeProbeResultsToCSV(int[] probeResults) {
        String csvFile = "probe_results.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(csvFile))) {
            writer.write("Search Number,Probes\n");
            for (int i = 0; i < probeResults.length; i++) {
                writer.write((i + 1) + "," + probeResults[i] + "\n");
            }
            System.out.println("Probe results written to " + csvFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Driver code
    public static void main(String args[]) {
        String filename = "/Users/kelvincheridor/Desktop/DS24/Hash Project/Hash functions/word.txt"; // File containing words

        int numWords = 16; // Maximum number of words to read
        if (args.length >= 2) {
            numWords = Integer.parseInt(args[1]);
        }
        try {
            String[] arr = readWordsFromFile(filename, numWords);
            int N = arr.length;

            // Calculate the size of the hash table based on load factor 0.7
            int L = (int) (N / 0.7); // Set hash size to achieve load factor of 0.7
            Map<Integer, String> hashTable = new HashMap<>(); // Hash table to store words
            int[] hashCount = new int[L]; // Array to store count of hashes for each position

            // Perform hashing
            long hashingDuration = hashing(arr, L, hashTable, hashCount);

            // Display table
            displayTable(hashTable, hashCount);

            // Count total probes
            int totalProbes = Arrays.stream(hashCount).sum();
            System.out.println("Total probes: " + totalProbes);

            // Perform random searches and calculate average, min, and max probes
            int numRandomSearches = 10; // Specify the number of random searches
            performRandomSearches(hashTable, hashCount, numRandomSearches);

            // Print total duration
            System.out.println("\nTime Taken for Hashing (ms): " + hashingDuration);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Display the table with index, word, and hash count
    public static void displayTable(Map<Integer, String> hashTable, int[] hashCount) {
        System.out.println("Hash Table:");
        System.out.println("--------------------------------------------");
        System.out.printf("%-10s %-20s %-10s%n", "Index", "Word", "Hash Count");
        System.out.println("--------------------------------------------");

        for (int i = 0; i < hashCount.length; i++) {
            String word = hashTable.get(i);
            int count = hashCount[i];
            System.out.printf("%-10d %-20s %-10d%n", i, word == null ? "" : word, count);
        }
    }
}
