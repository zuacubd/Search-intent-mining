package utility;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/*************************************************************************
 *  Compilation:  javac Digraph.java
 *  Dependencies: SymbolTable.java
 *  
 *  Directed graph data type implemented using a symbol table of
 *  vertices, where each vertex contains a list of its neighbors.
 *
 *************************************************************************/

public class Graph {
	SymbolTable st = new SymbolTable();

    // helper data type for adjacency lists
    // add v to w's list of neighbors and w to v's list of neighbors
    // parallel edges allowed
    public void addEdge(String v, String w) { 
    	if (st.get(v) == null) addVertex(v);
        if (st.get(w) == null) addVertex(w);
        AdjList adjlist = (AdjList) st.get(v);
        adjlist.insert(w);
        //AdjList adjlist1 = (AdjList) st.get(w);
        //adjlist1.insert(v);
    } 
    
    // add a vertex v with no neighbors
    public void addVertex(String v) {
        AdjList adjlist = new AdjList();   // sentinel
        st.put(v, adjlist);
    }
    // return the out degree of vertex v
    public int outdegree(String v) {
        AdjList adjlist = (AdjList) st.get(v);
        if (adjlist == null) return 0;
        else                 return adjlist.size();
    }

    // return the array of vertices incident to v
    public String[] neighbors(String v) {
        AdjList adjlist = (AdjList) st.get(v);
        if (adjlist == null) return null;
        else if(adjlist.size()<1) return null;
        else                 return adjlist.toArray();
    }
    
    public AdjList listNeighbors(String v){return (AdjList) st.get(v);}
    
    public int childrenSize(String v)
    {
    	AdjList temp=(AdjList) st.get(v);
    	if(temp==null) 
    		return 0;
    	else 
    		return temp.size();
    }
    
    
    public String toString() {
        String s = "";
        String[] vertices = st.keys();
        for (int v = 0; v < vertices.length; v++) {
            AdjList adjlist = (AdjList) st.get(vertices[v]);
            s += vertices[v] + ": " + adjlist + "\n";
        }
        return s;
    }

    public boolean isVertex(String v)
	{
    	if (st.get(v) == null) return false;
    	else return true;
	}
    
	public String[] getVertices()
	{
		return st.keys();
	}
	
	public Graph reverse()
	{
		Graph rgraph=new Graph();
		String[] vertices = st.keys();
		String[] children;
        for (int v = 0; v < vertices.length; v++) 
        {
            AdjList adjlist = (AdjList) st.get(vertices[v]);
            children=adjlist.toArray();
            for(int c=0;c<children.length;c++)
            	rgraph.addEdge(children[c], vertices[v]);
        } 
		return rgraph;
	}
	
	public void writeTo(String file)
	{
		GraphStream G = new GraphStream();
		G.setGraph(this);
    	try {
            //
            // Create instances of FileOutputStream and ObjectOutputStream.
            //
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
 
            //
            // By using writeObject() method of the ObjectOutputStream we can
            // make the book object persistent on the books.dat file.
            //
            oos.writeObject(G);
 
            //
            // Flush and close the ObjectOutputStream.
            //
            oos.flush();
            oos.close();
 
            //
            // We have the book saved. Now it is time to read it back and display
            // its detail information.
            //
            
            //FileInputStream fis = new FileInputStream("c:\\ipcs.dat");
            //ObjectInputStream ois = new ObjectInputStream(fis);
 
            //
            // To read the Book object use the ObjectInputStream.readObject() method.
            // This method return Object type data so we need to cast it back the its
            // origin class, the Book class.
            //
            //GraphStream newipcs = (GraphStream) ois.readObject();
            //Digraph newdg=newipcs.getDigraph();
            //System.out.println(newipcs);
 
            //ois.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
        
	}
	
	   public static void main(String[] args) {
	        Graph G = new Graph();
	        G.addEdge("A", "B");
	        G.addEdge("A", "C");
	        G.addEdge("C", "D");
	        G.addEdge("D", "E");
	        G.addEdge("D", "G");
	        G.addEdge("E", "G");
	        System.out.println(G.toString());

	    }

}
