package utility;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import utility.AdjList;
import utility.SymbolTable;
import utility.Digraph;

public class GraphStream implements Serializable{
	private Graph graph=new Graph();
	
	public Graph getGraph(){return this.graph;}
	public void setGraph(Graph graph){this.graph=graph;}
	
	private void writeObject(ObjectOutputStream out) throws IOException
	{
    	String[] list = this.graph.getVertices();
    	if(list==null)
    	{
    		out.writeInt(-1);
    		return;
    	}
    	
	 	out.writeInt(list.length);
	 	for(int v=0;v<list.length;v++)
	 	{
	 		out.writeObject(list[v]);
	 		
	 		//AdjList cnodes=new AdjList();
	 		String[] children=graph.neighbors(list[v]);
	 		if(children!=null)
	 		{
	 			out.writeInt(children.length);
	 			for(int c=0;c<children.length;c++)
	 				out.writeObject(children[c]);
	 		}
	 		else
	 			out.writeInt(0);
	 	}
	}
    
	private void readObject(ObjectInputStream in) throws Exception
	{
		String v1;
		String v2;
		Graph graph=new Graph();
		int csize;
		int no_vertices=in.readInt();
		if(no_vertices<1)
		{
			graph=null;
			return;
		}
		
	 	for(int v=0;v<no_vertices;v++)
	 	{
	 		//out.writeObject(list[v]);
	 		v1=(String) in.readObject();
	 		
	 		csize=in.readInt();
	 		for(int c=0;c<csize;c++)
	 		{	
	 			//out.writeObject(children[c]);
	 			v2=(String) in.readObject();
	 			graph.addEdge(v1, v2);
	 		}   
	 	}
	 	this.graph=graph;
	}

    
    public static void main(String[] args) {
    	GraphStream G = new GraphStream();
    	//Digraph dg;
    	//G.setDigraph(dg);
    	//System.out.println(G);
        
    	System.out.println();
        try {
            //
            // Create instances of FileOutputStream and ObjectOutputStream.
            //
            FileOutputStream fos = new FileOutputStream("c:\\ipcs.dat");
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
            
            FileInputStream fis = new FileInputStream("c:\\ipcs.dat");
            ObjectInputStream ois = new ObjectInputStream(fis);
 
            //
            // To read the Book object use the ObjectInputStream.readObject() method.
            // This method return Object type data so we need to cast it back the its
            // origin class, the Book class.
            //
            GraphStream newipcs = (GraphStream) ois.readObject();
            //Digraph newdg=newipcs.getDigraph();
            System.out.println(newipcs);
 
            ois.close();
            
        } catch (IOException e) {
            e.printStackTrace();
        } 
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

}
