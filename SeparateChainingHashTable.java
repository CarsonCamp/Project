import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SeparateChainingHashTable {
    private LinkedList<String>[] hashTable;
    private int tableSize;

    @SuppressWarnings("unchecked")
    public SeparateChainingHashTable(int size) {
        this.tableSize = size;
        hashTable = (LinkedList<String>[]) new LinkedList[size];
        for (int i = 0; i < size; i++) {
            hashTable[i] = new LinkedList<>();
        }
    }

    private int hash(String word) {
        return (word.hashCode() & 0x7fffffff) % tableSize;
    }

    public void insert(String word) {
        int hashValue = hash(word);
        hashTable[hashValue].add(word);
    }

    public int search(String word) {
        int hashValue = hash(word);
        List<String> chain = hashTable[hashValue];
        int probeCount = 0;

        for (String element : chain) {
            probeCount++;
            if (element.equals(word)) {
                return probeCount;
            }
        }
        return -1;
    }

    public void performSingleSearch(List<String> words) {
        if (words.isEmpty()) return;

        Random rand = new Random();
        String searchWord = words.get(rand.nextInt(words.size()));

        long startTime = System.nanoTime();
        int probes = search(searchWord);
        long endTime = System.nanoTime();

        long searchTime = endTime - startTime;
        double searchTimeMs = (double)searchTime / 1_000_000;

        System.out.printf("Word: '%s'\nTime Taken: %.3f ms\nProbes Used: %d%n", searchWord, searchTimeMs, probes);
    }

    public void performMultipleSearches(List<String> words, int numberOfSearches) {
        Random rand = new Random();
        long minTime = Long.MAX_VALUE, maxTime = 0, totalTime = 0;
        int minProbes = Integer.MAX_VALUE, maxProbes = 0, totalProbes = 0;

        for (int i = 0; i < numberOfSearches; i++) {
            String searchWord = words.get(rand.nextInt(words.size()));
            long startTime = System.nanoTime();
            int probes = search(searchWord);
            long endTime = System.nanoTime();
            long searchTime = endTime - startTime;

            minTime = Math.min(minTime, searchTime);
            maxTime = Math.max(maxTime, searchTime);
            totalTime += searchTime;

            minProbes = Math.min(minProbes, probes);
            maxProbes = Math.max(maxProbes, probes);
            totalProbes += probes;
        }

        double avgTime = (double)totalTime / numberOfSearches;
        double avgProbes = (double)totalProbes / numberOfSearches;

        System.out.printf("For %d searches: Time - Min: %.3f ms, Avg: %.3f ms, Max: %.3f ms; Probes - Min: %d, Avg: %.1f, Max: %d%n",
                          numberOfSearches, (double)minTime / 1_000_000, avgTime / 1_000_000, (double)maxTime / 1_000_000,
                          minProbes, avgProbes, maxProbes);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter table size: ");
        int tableSize = scanner.nextInt();

        SeparateChainingHashTable scht = new SeparateChainingHashTable(tableSize);
        String filePath = "/Users/carsoncampbell/Documents/vscode/SeperateChaining/bin/words.txt";

        try {
            List<String> words = Files.readAllLines(Paths.get(filePath));
            words.forEach(scht::insert);

            scht.performSingleSearch(words);

            int[] searchSets = {10, 20, 30, 40, 50};
            for (int numSearches : searchSets) {
                scht.performMultipleSearches(words, numSearches);
            }

        } catch (IOException e) {
            System.out.println("Error reading the words file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}
