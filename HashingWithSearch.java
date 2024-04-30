package Project1;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class HashingWithSearch {
    private int[] table;
    private int[] probeCount;
    private int[] indexHitCount; // Array to count hits on each index
    private int tableSize;
    private int numElements;

    public HashingWithSearch(int size) {
        tableSize = size;
        table = new int[size];
        probeCount = new int[size];
        indexHitCount = new int[size]; // Initialize the hit count array
        Arrays.fill(table, -1);
        Arrays.fill(indexHitCount, 0); // Fill hit count array with zeros
        numElements = 0;
    }

    private int primaryHash(String key) {
        return Math.abs(key.hashCode()) % tableSize;
    }

    private int secondaryHash(String key) {
        return 1 + (Math.abs(key.hashCode()) % (tableSize - 2)); // Quadratic probing
    }

    public int search(String key) {
        long startTime = System.nanoTime();

        int index = primaryHash(key);
        int step = secondaryHash(key);
        int probes = 0;
        int i = index;

        while (true) {
            if (table[i] == key.hashCode()) { // Assuming table stores hash codes of keys
                long endTime = System.nanoTime();
                long elapsedTime = endTime - startTime;
                System.out.println("Search time: " + elapsedTime + " nanoseconds");
                return probes;
            } else {
                i = (i + step) % table.length; // Apply modulo to ensure we don't go out of bounds
                probes++;
            }

            if (probes == table.length) { // If we've checked all elements in the table
                break; // Break the loop
            }
        }

        return -1; // Return -1 if key is not found after probing the entire table
    }

    /*private void resize() {
        int newSize = tableSize * 2;
        int[] newTable = new int[newSize];
        int[] newIndexHitCount = new int[newSize];
        Arrays.fill(newTable, -1);
        Arrays.fill(newIndexHitCount, 0);

        for (int i = 0; i < tableSize; i++) {
            if (table[i] != -1) {
                int newIndex = primaryHash(String.valueOf(table[i]));
                int stepSize = secondaryHash(String.valueOf(table[i]));
                while (newTable[newIndex] != -1) {
                    newIndex = (newIndex + stepSize) % newSize;
                }
                newTable[newIndex] = table[i];
                newIndexHitCount[newIndex]++;
            }
        }
        table = newTable;
        indexHitCount = newIndexHitCount;
        tableSize = newSize;
        probeCount = new int[newSize]; // Reset probe count array for the new table
    }*/
    private void resize() {
        int newSize = tableSize * 2;
        int[] newTable = new int[newSize];
        int[] newIndexHitCount = new int[newSize];
        Arrays.fill(newTable, -1);
        Arrays.fill(newIndexHitCount, 0);
    
        // Rehash and insert elements from the old table into the new table
        for (int i = 0; i < tableSize; i++) {
            if (table[i] != -1) {
                int newIndex = primaryHash(String.valueOf(table[i]));
                int stepSize = secondaryHash(String.valueOf(table[i]));
                while (newTable[newIndex] != -1) {
                    newIndex = (newIndex + stepSize) % newSize;
                }
                newTable[newIndex] = table[i];
                newIndexHitCount[newIndex]++;
            }
        }
    
        // Update hash table, index hit count array, and table size
        table = newTable;
        indexHitCount = newIndexHitCount;
        tableSize = newSize;
        probeCount = new int[newSize]; // Reset probe count array for the new table
    }
    

    public void insert(String key) {
        if (numElements >= tableSize) {
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
        probeCount[probes]++;
    }

    public ArrayList<String> loadWords(String filePath) throws IOException {
        ArrayList<String> words = new ArrayList<>();

        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        while ((line = reader.readLine()) != null) {
            String word = line.trim();
            words.add(word);
            insert(word);
        }
        reader.close();

        return words;
    }

    public void plotDistribution() {
        System.out.println("Index Hit Distribution:");
        for (int i = 0; i < tableSize; i++) {
            System.out.println("Index " + i + " hit " + indexHitCount[i] + " times");
        }

        System.out.println("Probe count distribution:");
        for (int i = 0; i < probeCount.length; i++) {
            if (probeCount[i] > 0) {
                System.out.println("Probes " + i + ": " + probeCount[i]);
            }
        }
    }

    public static void main(String[] args) {
        int tableSize = 600000; // Set appropriate initial size
        HashingWithSearch hashing = new HashingWithSearch(tableSize);
        String path = "C:/Users/natet/OneDrive/Documents/Data Structure/Project1/words.txt";

        try {
            ArrayList<String> words = hashing.loadWords(path);
            int random = (int) (Math.random() * words.size());
            String randomWord = words.get(random);

            System.out.println("Searching for random word: " + randomWord);
            int num_of_probes = hashing.search(randomWord);
            System.out.println("Number of probes: " + num_of_probes);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}