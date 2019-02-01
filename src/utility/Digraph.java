package utility;
/*************************************************************************
 *  Compilation:  javac Digraph.java
 *  Dependencies: SymbolTable.java
 *  
 *  Directed graph data type implemented using a symbol table of
 *  vertices, where each vertex contains a list of its neighbors.
 *
 *************************************************************************/
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Vector;

import utility.SymbolTable;
import utility.Queue;
import utility.AdjList;

public class Digraph extends Graph{
    
	public void addEdge(String v, String w) { 
        if (st.get(v) == null) addVertex(v);
        if (st.get(w) == null) addVertex(w);
        AdjList adjlist = (AdjList) st.get(v);
        adjlist.insert(w);
    } 
        
    //return the array of all children of v
    public Vector allChildren(String v) {
        Queue q=new Queue();
        Vector children=new Vector();
        
    	AdjList adjlist = (AdjList) st.get(v);
    	
        if (adjlist == null) return null;
        else
        {
        	String[] s= adjlist.toArray();
        	for(int i=0; i<s.length; i++)
        	{
        		q.enqueue(s[i]);
        	}
        }
        
        while (!q.isEmpty())
        {
        	String t=(String) q.dequeue();
        	children.add(t);
        	adjlist = (AdjList) st.get(t);
        	if (adjlist != null)
        	{
        		String[] s= adjlist.toArray();
            	for(int i=0; i<s.length; i++)
            	{
            		if(children.indexOf(s[i])==-1) 
            			q.enqueue(s[i]);
            	}
        	}
        }
        return children;
    }
    
    public boolean isChild(String parent, String child)
    {
    	return (allChildren(parent).indexOf(child)>=0); 
    }
    
    public static void main(String[] args) {
        Digraph G = new Digraph();
        G.addEdge("A", "B");
        G.addEdge("A", "C");
        G.addEdge("C", "D");
        G.addEdge("D", "E");
        G.addEdge("D", "G");
        G.addEdge("E", "G");
        System.out.println(G);

    }
	

}
