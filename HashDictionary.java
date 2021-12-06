public class HashDictionary<K extends Comparable<? super K>,V> extends Dictionary<K, V> implements DictionaryInterface<K,V> {

    private int numberOfEntries;
    private static final int DEFAULT_CAPACITY = 37;
    private static final int MAX_CAPACITY = 50000;
    private TableEntry<K, V>[] hashTable;
    private int tableSize;
    private static final int MAX_SIZE = 2 * MAX_CAPACITY;
    private boolean initialized = false;
    private double loadFactor = 0.5;
    private int collisionCount;
    private boolean collisionCheckVariable = true;
    public boolean firstPlacing = false;

    public HashDictionary(){
        this(DEFAULT_CAPACITY);
    }

    public HashDictionary(int initialCapacity) {
        checkCapacity(initialCapacity);
        numberOfEntries = 0;
        tableSize = getNextPrime(initialCapacity);
        TableEntry<K,V>[] temp = (TableEntry<K, V>[])new TableEntry[tableSize];
        hashTable = temp;
        initialized = true;
        collisionCount = 0;
    }

    public void put(K key, V value,Hash function, CollisionTechnique technique){
        checkInitialization();
        if ((key == null) || (value == null))
            throw new IllegalArgumentException();
        int index = 0;
        if(function == Hash.SSF)
            index = getHashIndex_ssf(key);
        else{
            index = getHashIndex_paf(key);
        }
        if (technique == CollisionTechnique.LP)
            index = probe(index,key,"put");
        else if (technique == CollisionTechnique.DH)
            index = doubleHashing(index,key,"put");
        assert (index >= 0) && (index < hashTable.length);
        if ( (hashTable[index] == null) || hashTable[index].isRemoved())
        { // Key not found, so insert new entry
            hashTable[index] = new TableEntry<K,V>(key, value);
            numberOfEntries++;
        }
        else
        {
            hashTable[index].setValue(value);
        } // end if
        // Ensure that hash table is large enough for another add
        if (isHashTableTooFull())
            resize(function,technique);
    }

    public void remove(K key, Hash function, CollisionTechnique technique)
    {
        checkInitialization();
        int index = 0;
        if (function == Hash.SSF)
            index = getHashIndex_ssf(key);
        else if ( function == Hash.PAF)
            index = getHashIndex_paf(key);
        if (technique == CollisionTechnique.LP)
            index = probe(index,key,"remove");
        else if (technique == CollisionTechnique.DH)
            index = doubleHashing(index,key,"remove");
        if (index != -1 && hashTable[index].isIn() && hashTable[index].getKey().equals(key))
        {
            hashTable[index].setToRemoved();
            numberOfEntries--;
            System.out.println("----------------------------------------------");
            System.out.println("The word " + key + " is successfully removed.");
            System.out.println("Now the amount of unique key-words in the hash table is : " + numberOfEntries);
            System.out.println("----------------------------------------------");
        }
        // end if
        else if (hashTable[index].isRemoved()) {
            System.out.println("----------------------------------------------");
            System.out.println("The word " + key + " could not be found in the hash table. It is removed before.");
        }
    }

    public V get(K key, Hash function, CollisionTechnique technique){
        checkInitialization();
        V result = null;
        int index = 0;
        if (function == Hash.SSF)
            index = getHashIndex_ssf(key);
        else if (function == Hash.PAF)
            index = getHashIndex_paf(key);
        if (technique == CollisionTechnique.LP)
            index = probe(index,key,"get");
        else if (technique == CollisionTechnique.DH)
            index = doubleHashing(index,key,"get");
        if ((hashTable[index] != null) && hashTable[index].isIn()){
            result = hashTable[index].getValue();
        }
        return result;
    }



    private void resize(Hash function, CollisionTechnique technique) {
        TableEntry<K,V>[] oldTable = hashTable;
        int oldSize = hashTable.length;
        int newSize = getNextPrime(oldSize * 2);
        checkSize(newSize);
        TableEntry<K,V>[] temp = (TableEntry<K, V>[])new TableEntry[newSize];
        hashTable = temp;
        tableSize = hashTable.length;
        numberOfEntries = 0;
        collisionCheckVariable = false;
        for (int index = 0; index < oldSize; index++)
        {
            if ( (oldTable[index] != null) && oldTable[index].isIn() )
                put(oldTable[index].getKey(), oldTable[index].getValue(),function,technique);
        }
        collisionCheckVariable = true;
    }

    private boolean isHashTableTooFull() {
        double load_ratio = (double)numberOfEntries / (double)hashTable.length;
        if(load_ratio >= loadFactor)
            return true;
        else
            return false;
    }


    private int getHashIndex_ssf(K key){
        final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
        int hashValue = 0;
        String key_string = null;
        try{
            key_string = key.toString().toLowerCase();
        } catch (Exception e) {
            System.out.println("The key was not string, thus there can't be any generated hash index for this key");
        }
        for(int i=0; i < key_string.length(); i++){
            hashValue += ALPHABET.indexOf(key_string.charAt(i))+1;
        }
        return hashValue % tableSize;
    }


    public boolean contains(K key, Hash function, CollisionTechnique technique) {
        int index = 0;
        if(function == Hash.SSF)
            index = getHashIndex_ssf(key);
        else{
            index = getHashIndex_paf(key);
        }
        if (technique == CollisionTechnique.LP)
            index = probe(index,key,"contains");
        else if (technique == CollisionTechnique.DH)
            index = doubleHashing(index,key,"contains");
        if (hashTable[index] != null && (key.equals(hashTable[index].getKey())))
            return true;
        return false;
    }

    private int getHashIndex_paf(K key){
        final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
        int hashValue = 0;
        final int PRIME_CONSTANT = 33;
        String key_string = null;
        // Using Horner's Polynomial Method
        try{
            key_string = key.toString().toLowerCase();
        } catch (Exception e) {
            System.out.println("The key was not string, thus there can't be any generated hash index for this key");
        }
        int[] polynomials = new int[key_string.length()];
        for(int i=0;i < key_string.length(); i++){
            polynomials[i] = ALPHABET.indexOf(key_string.charAt(i))+1;
        }
        int result = polynomials[0];
        // Evaluate value of polynomial using Horner's method
        for (int i=1; i<polynomials.length; i++)
            result = (result*PRIME_CONSTANT + polynomials[i]) % tableSize ;
        hashValue = result;

        return hashValue;
    }

    private int probe(int index, K key, String functionName){
        boolean found = false;
        int removedStateIndex = -1;
        while (!found && (hashTable[index] != null)){
            if(hashTable[index].isIn()){
                if (key.equals(hashTable[index].getKey())){
                    found = true;
                }
                else{
                    index = (index+1) % hashTable.length;
                    if(functionName == "put" && collisionCheckVariable ==  true && firstPlacing == true)
                        collisionCount++;
                }
            }
            else {
                if (removedStateIndex == -1)
                    removedStateIndex = index;
                index = (index+1) % hashTable.length;
                if(functionName == "put" && collisionCheckVariable == true && firstPlacing == true)
                    collisionCount++;
            }
        }
        if(found || (removedStateIndex == -1))
            return index;
        else
            return removedStateIndex;
    }

    private int doubleHashing(int index, K key, String functionName){
        boolean found = false;
        int removedStateIndex = -1;
        int j = 0;
        int q = 5;
        index = index % hashTable.length;
        int temp = index;
        int second_hash = q - (index % q);

        while (!found && (hashTable[index] != null)){
            if (hashTable[index].isIn()){
                if (key.equals(hashTable[index].getKey())){
                    found = true;
                }
                else {
                    index = (temp + j*second_hash) % hashTable.length;
                    if(functionName == "put" && collisionCheckVariable ==  true && firstPlacing == true)
                        collisionCount++;
                }
            }
            else {
                if (removedStateIndex == -1)
                    removedStateIndex = index;
                index = (temp + j*second_hash) % hashTable.length;
                if(functionName == "put" && collisionCheckVariable ==  true && firstPlacing == true)
                    collisionCount++;
            }
            j++;

        }
        if (found || (removedStateIndex == -1))
            return index;
        else
            return removedStateIndex;
    }



    private void checkCapacity(int capacity) {
        if (capacity > MAX_CAPACITY) {
            throw new IllegalStateException("Given capacity is exceeding the maximum capacity limit.");
        }
    }

    private void checkSize(int size){
        if (size > MAX_SIZE){
            throw new IllegalStateException("The size is over bounds.");
        }
    }

    private void checkInitialization(){
        if (initialized == false)
            throw new IllegalStateException("The hash table was not initialized.");
    }

    private static boolean isPrime(int inputNumber) {
        boolean isItPrime = true;

        if (inputNumber <= 1) {
            isItPrime = false;

            return isItPrime;
        } else {
            for (int i = 2; i <= inputNumber / 2; i++) {
                if ((inputNumber % i) == 0) {
                    isItPrime = false;

                    break;
                }
            }

            return isItPrime;
        }
    }

    private static int getNextPrime(int initialCapacity){
       if(isPrime(initialCapacity)){
           return initialCapacity;
       }
       else{
           while(!isPrime(initialCapacity)){
               initialCapacity++;
           }
       }
       return initialCapacity;
    }

    enum Hash{SSF,PAF}

    enum CollisionTechnique{LP,DH}

    public int getCollisionCount(){
        return collisionCount;
    }

    public int getSize() {return numberOfEntries;}

    public void setMaxLoadFactor(double loadFactor){
        this.loadFactor = loadFactor;
    }




}
