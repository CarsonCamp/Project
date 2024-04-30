package Project1;


import java.io.*;
import java.util.*;

public class QuadraticProbingpj {
    static int tableSize;
    static int[] hashCount;
    static double loadFactor;
        public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the table size: ");
        tableSize = scanner.nextInt();
        System.out.print("Enter the load factor: ");
        loadFactor = scanner.nextDouble();

        hashCount = new int[tableSize];

        String[] hashTable = new String[tableSize];
        Arrays.fill(hashTable, "");

        List <String> words = readWordsFromFile("C:/Users/natet/OneDrive/Documents/Data Structure/Project1/words.txt");

        for(String word : words){
            int index = hashFunction(word);
            int count = 1;
            while(!hashTable[index].isEmpty() && !hashTable[index].equals("DELETED")){
                index = (index + count * count) % tableSize;
                if(index < 0){
                    index += tableSize;
                }
                count++;
            }
            hashCount[index]++;
        }
        printProbeCounts(hashCount);
    }
    static int hashFunction(String word){
        int hash = 0;
        for(int i = 0; i < word.length(); i++){
            hash = (hash * 31 + word.charAt(i)) % tableSize;
        }
        return (hash < 0) ? (hash + tableSize) : hash;
    }
    static List<String> readWordsFromFile(String fileName){
        List<String> words = new ArrayList<>();
        try(Scanner fileScanner = new Scanner(new File(fileName))){
            while(fileScanner.hasNext()){
                words.add(fileScanner.next());
            }
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        return words;
    }
    public void insert(String word){

    }
    static void printProbeCounts(int[] hashCount){
        System.out.println("Quadratic Probe Count: ");
        System.out.println("Index\tProbe Count");
        for(int i = 0; i < hashCount.length; i++){
                System.out.println(i + "\t" + (hashCount[i] > 0 ? hashCount[i] - 1 : 0));
            }
        System.out.println();
    }

}