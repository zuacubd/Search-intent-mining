package utility;
/*************************************************************************
 *  Compilation:  javac AdjList.java
 *  Execution:    java AdjList
 *
 *  An adjacency list of strings, implemented with a linked list.
 *
 *************************************************************************/

public class AdjList {
    private Node first;        // beginning of list
    private int N=0;             // size of list

    // helper linked list data type
    private static class Node {
        String name;
        Node next;
        Node(String name, Node next) {
            this.name = name;
            this.next = next;
        }
    }
    
    public boolean isFound(String key)
    {
    	for (Node x = first; x != null; x = x.next)
    		if(x.name.equals(key))
    			return true;
    	return false;
    }
    
    public boolean isEmpty() { return (first == null); }
    public int size()        { return N; }

    // add s to the adjacency list
    public void insert(String s) {
        if(isFound(s)) return;
    	first = new Node(s, first);
        N++;
    }
    
    public void insert(AdjList collection)
    {
    	String[] keys=collection.toArray();
    	if(keys==null) return;
    	for(int i=0;i<keys.length;i++)
    		this.insert(keys[i]);
    		
    }

    // return string representation of list (in reverse order of list)
    public String toString() {
        String s = "";
        for (Node x = first; x != null; x = x.next)
            s = x.name + ", " + s;
        return "{ " + s + "}";
    }

    // return array of strings comprising this adjacency list
    public String[] toArray() {
        String[] names = new String[N];
        int i = N;
        for (Node x = first; x != null; x = x.next)
            names[--i] = x.name;
        return names;
    }

    /*
    public static void main(String[] args) {
        AdjList adjlist = new AdjList();
        adjlist.insert("This");
        adjlist.insert("is");
        adjlist.insert("a");
        adjlist.insert("test.");
        System.out.println(adjlist);
    }
    */
}
