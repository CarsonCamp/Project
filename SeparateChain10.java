import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SeparateChain10 {
    private LinkedList<String>[] hashTable;
    private int tableSize;

    @SuppressWarnings("unchecked")
    public SeparateChain10(int size) {
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
        return -1; // Indicate the word was not found
    }

    public void searchFirstTenWords(List<String> words) {
        if (words.size() < 10) {
            System.out.println("Not enough words in the list.");
            return;
        }
    
        long totalStartTime = System.nanoTime();
        System.out.println("Search Results for the First 10 Words:");
    
        for (int i = 0; i < 10; i++) {
            String word = words.get(i);
            int hashValue = hash(word); 
            int probes = search(word);
            System.out.printf("Word: '%s', Hash Index: %d, Probes Used: %d%n", word, hashValue, probes);
        }
    
        long totalEndTime = System.nanoTime();
        double totalTimeMs = (double)(totalEndTime - totalStartTime) / 1_000_000;
        System.out.printf("Total Time Taken: %.3f ms%n", totalTimeMs);
    }
    

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter table size: ");
        int tableSize = scanner.nextInt();
    
        SeparateChain10 scht = new SeparateChain10(tableSize);
        String filePath = "/Users/carsoncampbell/Documents/vscode/SeperateChaining/bin/words.txt";
    
        try {
            List<String> words = Files.readAllLines(Paths.get(filePath));
            words.forEach(scht::insert);  // Insert words into the hash table
    
            scht.searchFirstTenWords(words);  // Perform search on the first 10 words
    
        } catch (IOException e) {
            System.out.println("Error reading the words file: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }    
}    