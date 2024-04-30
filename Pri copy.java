import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Pri {
    private int[] table;
    private int[] probeCount;
    private int numElements;
    private double loadFactorThreshold;
    private int[] indexHitCount; // Array to count hits on each index
    private int tableSize;
    private ArrayList<Long> searchTimes;

    public Pri(int size, double loadFactor) {
        tableSize = size;
        table = new int[size];
        probeCount = new int[size]; // initializes the probe count array 
        indexHitCount = new int[size]; // Initialize the hit count array
        Arrays.fill(table, -1);
        Arrays.fill(indexHitCount, 0); // Fill hit count array with zeros
        searchTimes = new ArrayList<>();
        loadFactorThreshold = loadFactor;
    }

    private int primaryHash(String key) {
        return Math.abs(key.hashCode()) % tableSize;
    }

    public int search(String key) {
        long startTime = System.currentTimeMillis();
        int index = primaryHash(key);
        int probes = 0;

        while (true) {
            if (table[index] != -1 && key.equals(String.valueOf(table[index]))) { // Compare keys
                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                searchTimes.add(elapsedTime);
                return probes + 1; // Add 1 to count the current probe
            } else {
                index = (index + 1) % tableSize; // Linear probing
                probes++;
                if (probes >= tableSize || table[index] == -1) {
                    break; // Break the loop if all slots are probed or an empty slot is encountered
                }
            }

        }

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        searchTimes.add(elapsedTime);

        return -1; // Return -1 if key is not found after probing the entire table
    }

    public ArrayList<String> loadWords(String filePath) throws IOException {
        ArrayList<String> words = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            int lineCount = 0;
            StringBuilder output = new StringBuilder("Loading words from file:\n");
            while ((line = reader.readLine()) != null) {
                String word = line.trim();
                words.add(word);
                insert(word);
                lineCount++;
                if (lineCount % 3 == 0) {
                    output.append(String.format("%-15s %-15s %-15s \n", words.get(lineCount - 3), words.get(lineCount - 2), words.get(lineCount - 1)));
                }
            }
            // Output remaining words
            int remainingWords = lineCount % 5;
            if (remainingWords > 0) {
                StringBuilder remainingLine = new StringBuilder();
                for (int i = 0; i < remainingWords; i++) {
                    remainingLine.append(String.format("%-15s ", words.get(lineCount - remainingWords + i)));
                }
                output.append(remainingLine.toString().trim()).append("\n");
            }
            System.out.println(output);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
            throw e; // Re-throw the exception to indicate failure to load words
        }

        return words;
    }

    private void insert(String key) {
        if (numElements >= loadFactorThreshold * tableSize) {
            resize();
        }
    
        int index = primaryHash(key);
        int stepSize = secondaryHash(key);
        int probes = 0;
    
        while (table[index] != -1) {
            index = (index + stepSize) % tableSize;
            probes++;
            if (probes >= tableSize)
                throw new RuntimeException("Hash table overflow");
        }
    
        table[index] = key.hashCode();
        indexHitCount[index]++; // Increment the hit count for this index
        numElements++;
        probeCount[probes]++; // Increment probe count for this probe sequence length
    }
    
    

    private void resize() {
        int newSize = tableSize * 2;
        int[] newTable = new int[newSize];
        int[] newIndexHitCount = new int[newSize];
        Arrays.fill(newTable, -1);
        Arrays.fill(newIndexHitCount, 0);

        for (int i = 0; i < tableSize; i++) {
            if (table[i] != -1) {
                int newIndex = primaryHash(String.valueOf(table[i]));
                int stepSize = secondaryHash(String.valueOf(table[i]));
                int probes = 0;
                while (newTable[newIndex] != -1) {
                    newIndex = (newIndex + stepSize) % newSize;
                    probes++;
                    if (probes >= newSize) {
                        throw new RuntimeException("Hash table overflow during resizing");
                    }
                }
                newTable[newIndex] = table[i];
                newIndexHitCount[newIndex]++;
            }
        }
        table = newTable;
        indexHitCount = newIndexHitCount;
        tableSize = newSize;
        probeCount = new int[newSize]; // Reset probe count array for the new table
    }

    private int secondaryHash(String key) {
        // This method should provide a different step size for linear probing
        // You can implement any secondary hash function here
        // For example, you can simply return 1 for linear probing
        return 1;
    }

    public static void main(String[] args) {
        int tableSize = 500000; // Set appropriate initial size
        double loadFactor = 0.5;
        Pri hashing = new Pri(tableSize, loadFactor);
        String path = "/Users/kelvincheridor/Desktop/DS24/Hash Project/Hash functions/word.txt";
        int numRandomSearches = 10; // Specify the number of random searches
    
        try {
            ArrayList<String> words = hashing.loadWords(path);
    
            long totalSearchTime = 0;
            int totalProbes = 0;
            long maxSearchTime = Long.MIN_VALUE;
            long minSearchTime = Long.MAX_VALUE;
    
            // Perform random searches
            for (int i = 0; i < numRandomSearches; i++) {
                int randomIndex = new Random().nextInt(words.size());
                String wordToSearch = words.get(randomIndex);
                long start = System.nanoTime();
                int num_of_probes = hashing.search(wordToSearch);
                long elapsedTime = System.nanoTime() - start;
                totalSearchTime += elapsedTime;
                totalProbes += num_of_probes;
    
                // Update max and min search times
                if (elapsedTime > maxSearchTime) {
                    maxSearchTime = elapsedTime;
                }
                if (elapsedTime < minSearchTime) {
                    minSearchTime = elapsedTime;
                }
    
                System.out.println("Search #" + (i + 1) + ": Word: " + wordToSearch + ", Probes: " + num_of_probes + ", Time: " + elapsedTime + "ns");
            }
    
            // Calculate and display average search time and probes
            double averageSearchTime = (double) totalSearchTime / numRandomSearches;
            double averageProbes = (double) totalProbes / numRandomSearches;
            System.out.println("Average search time: " + averageSearchTime + "ns");
            System.out.println("Average probes: " + averageProbes);
    
            // Output max and min search times
            System.out.println("Max search time: " + maxSearchTime + "ns");
            System.out.println("Min search time: " + minSearchTime + "ns");
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    
    
    
    }

