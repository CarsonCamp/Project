package Project1;

import java.io.*;
import java.util.*;

public class QuadraticProbingWithProbeCount1 {
    static int tableSize;
    static int[] hashCount;
    static String[] hashTable;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Number of Words Searched: 10");
        searchAndPrintResults(10);
        System.out.println();
        System.out.println("Number of Words Searched: 20");
        searchAndPrintResults(20);
        System.out.println();
        System.out.println("Number of Words Searched: 30");
        searchAndPrintResults(30);
        System.out.println();
        System.out.println("Number of Words Searched: 40");
        searchAndPrintResults(40);
        System.out.println();
        System.out.println("Number of Words Searched: 50");
        searchAndPrintResults(50);
    }

    static void searchAndPrintResults(int numWords) {
        initializeHashTable(numWords);
        initializeWordLists(numWords);

        for (String word : hashTable) {
            if(word != null){
                int probeCount = insertWithProbeCount(word);
                System.out.println("Word: " + word + "\tProbe Count: " + probeCount);
            }
            /*int probeCount = insertWithProbeCount(word);
            System.out.println("Word: " + word + "\tProbe Count: " + probeCount);*/
        }

        printProbeStatistics(hashCount);
    }
    static void initializeHashTable(int numWords) {
        tableSize = numWords * 2;
        hashTable = new String[tableSize];
        hashCount = new int[tableSize]; // Initialize hashCount array
    }
    

    static void initializeWordLists(int numWords) {
        String[] searchWords = {
            "ZZZ", "Xina", "Worlded", "wjc", "whisp","volk", "Volkan", "urnmaker", "umiry", "encrisp",
            "Edmond", "ECL", "dunk", "drek", "distort", "2", "5-T", "4th", "3rd", "abime",
            "Afrika", "AGL", "Agle", "AIR", "Algy", "Elope", "Ammi", "ammer", "AMP", "Anax",
            "onbleach", "nonmason", "nonsolids", "severals", "sever", "SG",  "sfz", "overprice", "overpress",
            "look", "lungs", "Macomb", "magnet", "Mandy", "Mandell", "Mead", "ME", "tights", "tightness", "Timex"
        };

        List<List<String>> searchWordLists = new ArrayList<>();
        for (int i = 0; i < numWords; i++) {
            List<String> wordList = new ArrayList<>();
            for (int j = 0; j < numWords; j++) {
                wordList.add(searchWords[j]);
            }
            searchWordLists.add(wordList);
        }

        Collections.shuffle(searchWordLists);
        for (int i = 0; i < numWords; i++) {
            hashTable[i] = searchWordLists.get(i).get(i);
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

    static void printProbeStatistics(int[] hashCount) {
        int minProbes = Integer.MAX_VALUE;
        int maxProbes = Integer.MIN_VALUE;
        int totalProbes = 0;

        for (int count : hashCount) {
            minProbes = Math.min(minProbes, count);
            maxProbes = Math.max(maxProbes, count);
            totalProbes += count;
        }

        double avgProbes = totalProbes / (double) hashCount.length;

        System.out.println("Min Probes: " + minProbes);
        System.out.println("Max Probes: " + maxProbes);
        System.out.println("Avg Probes: " + avgProbes);
    }
}