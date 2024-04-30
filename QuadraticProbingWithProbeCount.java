package Project1;
import java.io.*;
import java.util.*;

public class QuadraticProbingWithProbeCount {
    static int tableSize;
    static int[] hashCount;
    static double loadFactor;
    static String[] hashTable; // Declaration of hash table array

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the table size: ");
        tableSize = scanner.nextInt();
        System.out.print("Enter the load factor: ");
        loadFactor = scanner.nextDouble();

        hashCount = new int[tableSize];
        hashTable = new String[tableSize]; // Initialize the hash table array

        List<String> words = readWordsFromFile("C:/Users/natet/OneDrive/Documents/Data Structure/Project1/words.txt");

        for (String word : words) {
            int probeCount = insertWithProbeCount(word);
            System.out.println("Word: " + word + "\tProbe Count: " + probeCount);
        }
    }

    static int insertWithProbeCount(String word) {
        int index = hashFunction(word);
        int probeCount = 0;
        while (hashTable[index] != null && !hashTable[index].equals("DELETED")) {
            index = (index + probeCount * probeCount) % tableSize;
            if (index < 0) {
                index += tableSize;
            }
            probeCount++;
        }
        hashTable[index] = word;
        hashCount[index]++;
        return probeCount;
    }

    static int hashFunction(String word) {
        int hash = 0;
        for (int i = 0; i < word.length(); i++) {
            hash = (hash * 31 + word.charAt(i)) % tableSize;
        }
        return (hash < 0) ? (hash + tableSize) : hash;
    }

    static List<String> readWordsFromFile(String fileName) {
        List<String> words = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(fileName))) {
            while (fileScanner.hasNext()) {
                words.add(fileScanner.next());
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return words;
    }
}
