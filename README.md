# inverted_index_implementation

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

This is the field of attributes of the Hash dictionary data structure initially. 

NumberOfEntries is the amount of Key-Value pairs in the hash table.

DEFAULT_CAPACITY is the initial capacity as a constant for the hash table. 

TableEntry<K,V>[] hashTable variable is the array that is going to be concreted as the 
hash table and the main structure for the data structure. 

TableSize is a variable attribute that will be increased when the load factor is 
exceeded.

MAX_SIZE is another constant that can be the boundary size, this hash table can reach.

Initialized is a boolean variable, used when a Hash Dictionary object is created calling 
the constructor. When the constructor is called, the boolean variable will turn to be 
true.

Load Factor is a variable that is the rate of the number of entries and the size of the 
hash table. It is 0.5 as default, but the users can vary it as they wish.

CollisionCount is the variable that is going to be counting all the collisions during the 
process that the hash table gets formed.

CollisionCheckVariable is another boolean providing to avoid unnecessary collision 
counts. Such as rehashing all the table in order to enlarge the hash table.

FirstPlacing is another boolean, that is going to be utilized as in the same way with the 
CollisionCheckVariable. But this time it is used to get rid of counting the collision 
when a key is put in the table again, after updating its value.

public HashDictionary(int initialCapacity)


This is the main constructor of the hash dictionary. When this is called, initialized 
boolean gets true, numberOfEntries and collisionCount variables get a value of 0. Hash 
table array gets created and from the size of the next prime of the capacity value that 
is provided.

public void put(K key, V value,Hash function, CollisionTechnique technique)


This is the method to add a new entry to the hash table. It first checks if the hash 
table is already initialized. If it is, then it gets the key in order to hash it by using 
different functions, after getting the index, first it has to check if there another 
entry exists in the output index, if so, it uses one of the collision handling 
techniques, and then under the condition that the very last provided index is null, or 
available, creating a new entry there, if not, the key is already in the hash table and 
then it just sets the value of the key. After these operations, it checks if the number 
of entries exceeded the load factor, if so, it processes rehashing and doubles the size 
of the hash-table. Additional parameters here are to get different hash function and 
technique choices from the user.


public void remove(K key, Hash function, CollisionTechnique technique)
Here is another method that is utilized to remove an entry from the hash table. It uses 
the same way to find the index as the other methods. After finding the index of the key, 
it removes the key. Does not return it.


public V get(K key, Hash function, CollisionTechnique technique)
After determining the very-last index of the key, it gets the value of it. But under the 
condition that is the provided key not found in the hash table, the method returns null.
private void resize(Hash function, CollisionTechnique technique)
This method is to be used when the hash table’s load ratio gets bigger than the maximum 
load factor of the hash table. After this condition is satisfied, It creates a new array 
of hash table, adjusting its size to be the doubling the current size of the array. And 
rehashing all the values of the array into it, then assigning the new formed array to the 
current hash table. The process here is not to be taken into account as collisions get 
counted.


private boolean isHashTableTooFull()
Just checks the relation between the current load ratio and the max load factor of the 
hash table. Returning a boolean variable considering the conditions.


private int getHashIndex_ssf(K key)
The hash function SSF, this utilizes an alphabet string in order to get the index of each 
of the key’s letters and computing them to an index.
public boolean contains(K key, Hash function, CollisionTechnique technique)
This checks if the parameter key is already in the hash table. Generally used while 
putting the entries to the table. Returns a boolean value according to the result.


private int getHashIndex_paf(K key)
Uses totally the same algorithm as the SSF function. But the different thing is the 
formula for calculating the index. It utilizes the Horner’s method to determine the 
index.


private int probe(int index, K key, String functionName)
Method that is used for linear probing. This just gets the index that is formed of the 
SSF or PAF functions. And after getting the index, it just probes the array linearly, to 
find a null or available cell there, unless there is not, it searches all the array to 
the end, if still there is not, it starts from the beginning of the array again and keeps 
searching, until finding a null or available one. During this process, each time there is 
a collision, it increments the collisionCount attribute of the structure. It also gets 
the function’s name where it is used for the hash-table operations. This is provided for 
counting collision only while putting the entries to the hash table.


private int doubleHashing(int index, K key, String functionName)
The same logic as the probe function. The only different thing is there are two hashing 
functions here in order to make the probing easier.


Test methods


public static HashDictionary creatingTheHashTable(HashDictionary.Hash hash, 
HashDictionary.CollisionTechnique technique, double maxLoadFactor) throws 
FileNotFoundException


This method is in the test class, and the most important method for executing the test 
class, since all the operations are connected here. This is utilized for creating a hash 
dictionary object in the main method in the test class. User provides their desired hash 
function and technique, and load factor choices, and after that the hash dictionary will 
be created. It will scan all the .txt files that are going to be used in order to obtain 
the keys which are about to be added to the hash table. Then it will return this formed 
hash dictionary.


public static void searchAll(HashDictionary hashTable, HashDictionary.Hash function, 
HashDictionary.CollisionTechnique technique)
This is used for searching the search.txt file and calculating the times for the maximum, 
minimum, average and total times. Kindly note that, these times surely can alter from 
condition to condition, it is completely affected by the performance of the computer. It 
is a void method, so returns nothing and just prints the searcing time values.


public static void searchAndPrint(HashDictionary hashTable, HashDictionary.Hash function, 
HashDictionary.CollisionTechnique technique)
This is used for printing to the screen, all the words from search.txt that are found in 
the hash dictionary, and also printing their dictionary values, which store the .txt file 
names and frequencies there
