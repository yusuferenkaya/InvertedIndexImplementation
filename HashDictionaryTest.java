import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.sql.SQLOutput;
import java.util.*;

public class HashDictionaryTest {
    static String DELIMITERS = "[-+=" +
            " " +        //space
            "\r\n " +    //carriage return line fit
            "1234567890" + //numbers
            "’'\"" +       // apostrophe
            "(){}<>\\[\\]" + // brackets
            ":" +        // colon
            "," +        // comma
            "‒–—―" +     // dashes
            "…" +        // ellipsis
            "!" +        // exclamation mark
            "." +        // full stop/period
            "«»" +       // guillemets
            "-‐" +       // hyphen
            "?" +        // question mark
            "‘’“”" +     // quotation marks
            ";" +        // semicolon
            "/" +        // slash/stroke
            "⁄" +        // solidus
            "␠" +        // space?
            "·" +        // interpunct
            "&" +        // ampersand
            "@" +        // at sign
            "*" +        // asterisk
            "\\" +       // backslash
            "•" +        // bullet
            "^" +        // caret
            "¤¢$€£¥₩₪" + // currency
            "†‡" +       // dagger
            "°" +        // degree
            "¡" +        // inverted exclamation point
            "¿" +        // inverted question mark
            "¬" +        // negation
            "#" +        // number sign (hashtag)
            "№" +        // numero sign ()
            "%‰‱" +      // percent and related signs
            "¶" +        // pilcrow
            "′" +        // prime
            "§" +        // section sign
            "~" +        // tilde/swung dash
            "¨" +        // umlaut/diaeresis
            "_" +        // underscore/understrike
            "|¦" +       // vertical/pipe/broken bar
            "⁂" +        // asterism
            "☞" +        // index/fist
            "∴" +        // therefore sign
            "‽" +        // interrobang
            "※" +          // reference mark
            "]";

    static ArrayList<String> words_to_be_searched = readFileMakeList("src/search.txt");
    static ArrayList<String> stop_words = readFileMakeList("src/stop_words_en.txt");


    public static void main(String[] args) throws FileNotFoundException {
        HashDictionary.Hash hashChoice = null;
        HashDictionary.CollisionTechnique techniqueChoice = null;
        double maxLoadFactorChoice = 0.0;
        Scanner scanner = new Scanner(System.in);
        outerloop:
        while(true) {
            System.out.println("Please choose the options to create the hash table.");
            System.out.print("1. Provide the desired hash function. Type 'SSF' or 'PAF' : ");
            String hashInput = scanner.next().toLowerCase(Locale.ENGLISH);
            System.out.print("2. Provide the desired collision handling technique. Type 'LP' or 'DH' : ");
            String techniqueInput = scanner.next().toLowerCase(Locale.ENGLISH);
            System.out.print("3. Provide the maximum load factor. Type '0.5' or '0.8 : ");
            double loadFactor = scanner.nextDouble();
            if (hashInput.equals("ssf"))
                hashChoice = HashDictionary.Hash.SSF;
            else if (hashInput.equals("paf")) {
                hashChoice = HashDictionary.Hash.PAF;
            }
            else{
                    System.out.println("Your input for hash function choice was wrong. Program will terminate now.");
                    break;
                }

            if (techniqueInput.equals("lp"))
                techniqueChoice = HashDictionary.CollisionTechnique.LP;
            else if (techniqueInput.equals("dh"))
                techniqueChoice = HashDictionary.CollisionTechnique.DH;
            else{
                System.out.println("Your choice for collision handling technique was wrong. Program will terminate now.");
                break;
            }
            if (loadFactor == 0.5 || loadFactor == 0.8)
                maxLoadFactorChoice = loadFactor;
            else {
                System.out.println("Program will terminate now. Please use 0.5 or 0.8 as load factor.");
                break;
            }
            System.out.println("-------------------------------------------");
            System.out.println("Hash table is being created. This can take 30-80 seconds for SSF and 2-4 seconds for PAF Hash functions.");
            long startTime = System.nanoTime();
            HashDictionary<String, Dictionary> a_hash = creatingTheHashTable(hashChoice, techniqueChoice,maxLoadFactorChoice);
            long lastTime = System.nanoTime();
            double between = (lastTime - startTime)/Math.pow(10,9);
            System.out.println("Indexing time : " + between + " seconds.");
            System.out.println("Hash table is created. You can see the specialities below.");
            System.out.println("The collision occurrences while the words were being replaced : " + a_hash.getCollisionCount());
            System.out.println("The amount of unique words in the hash table : " + a_hash.getSize());
            while(true){

                System.out.println("-------------------------------------------");
            System.out.println("Choose one option below, to execute.\n");
            System.out.println("1) Search the 'search.txt' and see the time results on the hash table.");
            System.out.println("2) Input a word to be searched in the hash table.");
            System.out.println("3) Search the 'search.txt' and print the found words in the hash table.");
            System.out.println("4) Remove a key from the formed hash table.");
            System.out.println("5) Create a new hash table with the new choices.");
            System.out.println("6) Exit the program.");
            System.out.println("Type 1, 2 or 3, 4, 5 or 6 according to the process that you would like to go on.");
            int choice = scanner.nextInt();
            if (choice == 1)
                searchAll(a_hash,hashChoice,techniqueChoice);
            if (choice == 2){
                System.out.print("Please enter your word to be searched in the hash table as a key : ");
                String inputWord = scanner.next().toLowerCase(Locale.ENGLISH);
                try{
                    Dictionary<String,Integer> timeFrequency = a_hash.get(inputWord, hashChoice, techniqueChoice);
                    Iterator<String> fileNameIterator = timeFrequency.getKeyIterator();
                    System.out.println(timeFrequency.getSize() + " documents found.");
                    while (fileNameIterator.hasNext()){
                        String textFile = fileNameIterator.next();
                        System.out.println(textFile + " frequency : " + timeFrequency.getValue(textFile));
                    }
                } catch (Exception e) {
                    System.out.println("the word " + inputWord + " is not found!");
                }
            }
            if (choice == 3){
                searchAndPrint(a_hash,hashChoice,techniqueChoice);
            }
            if (choice == 4){
                System.out.print("Please enter the word to be removed from the hash table : ");
                String wordToBeRemoved = scanner.next().toLowerCase(Locale.ENGLISH);
                try{
                    a_hash.remove(wordToBeRemoved,hashChoice,techniqueChoice);
                }
                catch (Exception e){
                    System.out.println("The word you've passed " + wordToBeRemoved + " could not be found in the hash table.");
                }
            }
            if (choice == 5)
                break;
            if (choice == 6) {
                System.out.println("----------------------------------");
                System.out.println("The program is being terminated.");
                break outerloop;
            }

            }
        }
    }


    public static HashDictionary creatingTheHashTable(HashDictionary.Hash hash, HashDictionary.CollisionTechnique technique, double maxLoadFactor) throws FileNotFoundException {
        HashDictionary<String,Dictionary> hashTable = new HashDictionary<>();
        hashTable.setMaxLoadFactor(maxLoadFactor);
        File folder = new File("src/bbc/");
        File[] listOfFiles = folder.listFiles();
        Scanner scanner;
        for (File file : listOfFiles){ // opening the folders
            File subFolder = new File(file.getPath());
            File[] listOfTexts  = subFolder.listFiles();
            for (File subFile : listOfTexts){ // opening the .txt files in the folders
                scanner = new Scanner(new File(subFile.getPath()));
                while (scanner.hasNextLine()){
                    String line = scanner.nextLine();
                    if (!line.isEmpty()){
                        for (String s : line.split(DELIMITERS)){
                            s = s.toLowerCase(Locale.ENGLISH);
                            if (!stop_words.contains(s) && !s.equals("")){ // eliminating the unnecessary words here
                                if(!hashTable.contains(s,hash,technique)){ // checking if the key is already in the hash table
                                    Dictionary<String,Integer> entry_dictionary = new Dictionary<>(); // if not, creating a dictionary as the value pair, in order to store
                                    // the key-value pairs of the .txt files and the frequency of the key-word in that txt file.
                                    entry_dictionary.add(subFile.getPath(),1);
                                    hashTable.firstPlacing = true; // checking if it is  adding for the first time.
                                    hashTable.put(s,entry_dictionary,hash,technique);
                                    hashTable.firstPlacing = false; // assigning false so if it will be updating later it won't be counting the collision.
                                }
                                else{
                                    Dictionary<String,Integer> entry_dictionary = hashTable.get(s,hash,technique); // if the key is already there, it will get its value dictionary.
                                    if (entry_dictionary.contains(subFile.getPath())){ // if the key is already appeared in the specified txt file, it will just increment its frequency.
                                        int wordFrequency = entry_dictionary.getValue(subFile.getPath());
                                        entry_dictionary.add(subFile.getPath(),wordFrequency + 1);
                                        hashTable.put(s,entry_dictionary,hash,technique);
                                    }
                                    else{ // if not shown before, it will update the value dictionary as the new file added and frequency initialized as 1.
                                        entry_dictionary.add(subFile.getPath(),1);
                                        hashTable.put(s,entry_dictionary,hash,technique);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return hashTable;
    }

    public static void searchAll(HashDictionary hashTable, HashDictionary.Hash function, HashDictionary.CollisionTechnique technique){
        long searchAll = System.nanoTime(); // time started for searching all the words.
        long maxSearch = 0; // stores the maximum search time for a word
        long minSearch = 1000000000; // stores the min search time for a word
        for (String word : words_to_be_searched){
            long searchWord = System.nanoTime(); // starting the search time individually for a word
            Dictionary<String,Integer> hashTableElement = (Dictionary<String, Integer>) hashTable.get(word,function,technique); // getting it
            long searchWordFinish = System.nanoTime();
            long searchWordBetween = (searchWordFinish - searchWord); // time for searching the word individually
            if (searchWordBetween < minSearch && hashTableElement != null) // if its not null, it is found
                minSearch = searchWordBetween;
            if (searchWordBetween > maxSearch && hashTableElement != null)
                maxSearch = searchWordBetween;
        }
        long searchAllFinish = System.nanoTime();
        double searchTime = (searchAllFinish - searchAll) / Math.pow(10,9); // time for searching all the words
        System.out.println("---------------------------------------------");
        System.out.println("Max search time = " + maxSearch/Math.pow(10,9) + " seconds");
        System.out.println("Min search time = " + minSearch/Math.pow(10,9) + " seconds");
        System.out.println("Total search time = " + searchTime + " seconds");
        System.out.println("Average search time = " + searchTime/1000 + " seconds");
        System.out.println("Note : Search times can vary a bit every time.");

    }
    // used to print all the words that are both in search.txt and the hash table, displaying which documents they are found and the frequencies in those documents.
    public static void searchAndPrint(HashDictionary hashTable, HashDictionary.Hash function, HashDictionary.CollisionTechnique technique){
        for (String word : words_to_be_searched){
            Dictionary<String,Integer> hashTableElement = (Dictionary<String, Integer>) hashTable.get(word,function,technique);
            if(hashTableElement != null){
                System.out.println("-----------------------------------------------------------------");
                System.out.println("word '"+word+"' is found in " + hashTableElement.getSize() + " documents.");
                Iterator<String> fileNameIterator = hashTableElement.getKeyIterator();
                while(fileNameIterator.hasNext()){
                    String fileName = fileNameIterator.next();
                    System.out.println(fileName + " frequency : " + hashTableElement.getValue(fileName));
                }
                System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++");
            }

        }
    }

    public static ArrayList<String> readFileMakeList(String filePath){ // this is used for reading the given .txt files stop words and search words and turning them into lists.
        ArrayList<String> the_list = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()){
            String word = scanner.nextLine();
            if (!word.isEmpty())
                the_list.add(word);
        }
        return the_list;
    }
}


