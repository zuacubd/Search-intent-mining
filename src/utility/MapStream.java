package utility;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

import utility.AdjList;
import utility.SymbolTable;
import utility.Digraph;

public class MapStream implements Serializable{
	private Map map=new HashMap();
	
	public Map getMap(){return this.map;}
	public void setMap(Map map){this.map=map;}
	
	public void writeObject(ObjectOutputStream out) throws IOException
	{
    	Map submap;
    	String aid;
    	Integer weight;
    	
    	if(this.map==null)
    	{
    		out.writeInt(-1);
    		return;
    	}
    	
	 	out.writeInt(this.map.size());
	 	
	 	for(Object id:this.map.keySet())
	 	{
	 		out.writeObject((String)id);
	 		submap=(Map)this.map.get(id);
	 		
	 		if(submap==null)
	 			out.writeInt(0);
	 		else
	 		{
	 			out.writeInt(submap.size());
	 			for(Iterator it=submap.keySet().iterator(); it.hasNext();)
	 			{
	 				aid=(String)it.next();
	 				out.writeObject(aid);
	 				weight=(Integer) submap.get(aid);
	 				out.writeInt(weight);
	 			}
	 		}
	 	}
	}
	
	public void writeWeightedObject(ObjectOutputStream out) throws IOException
	{
    	Map submap;
    	String aid;
  //  	Integer weight;
    	Double weight;
    	
    	if(this.map==null)
    	{
    		out.writeInt(-1);
    		return;
    	}
    	
	 	out.writeInt(this.map.size());
	 	
	 	for(Object id:this.map.keySet())
	 	{
	 		out.writeObject((String)id);
	 		submap=(Map)this.map.get(id);
	 		
	 		if(submap==null)
	 			out.writeInt(0);
	 		else
	 		{
	 			out.writeInt(submap.size());
	 			for(Iterator it=submap.keySet().iterator(); it.hasNext();)
	 			{
	 				aid=(String)it.next();
	 				out.writeObject(aid);
//	 				weight=(Integer) submap.get(aid);
//	 				out.writeInt(weight);
	 				weight=(Double) submap.get(aid);
	 				out.writeDouble(weight);
	 			}
	 		}
	 	}
	}

/*	
	public void readObject(ObjectInputStream in) throws Exception
	{
		Map submap = null;
		
		try{
	
			submap=(Map) in.readObject();
//§			System.out.println(submap);
		}catch(Exception e){
			System.out.println(e);
		}
		
		this.map=submap;
	}
*/
	public void readObject(ObjectInputStream in) throws Exception
	{
		Map submap = null;
		String id;
		String subid;
		Integer subval;
		int size;
		int subsize;
		
		size=in.readInt();
//		System.out.println(size);
		if(size==-1)
    	{
			return;
    	}
    	
	 	for(int i=0;i<size;i++)
	 	{
	 		id=(String)in.readObject();
	 		
	 		subsize=in.readInt();
	 		
//	 		System.out.println(i+": "+id+" "+subsize);
	 		submap=new HashMap();
	 		if(subsize!=0)
	 		{
	 			for(int j=0;j<subsize;j++)
	 			{
	 				subid=(String)in.readObject();
	 				subval=in.readInt();
	 				submap.put(subid, subval);
	 			}
	 		}
	 		this.map.put(id, submap);
	 	}		
	}
	
	public void readWeightedObject(ObjectInputStream in) throws Exception
	{
		Map submap;
		String id;
		String subid;
		double subval;
		int size;
		int subsize;
		
		size=in.readInt();
//		System.out.println(size);
		if(size==-1)
    	{
			return;
    	}
    	
	 	for(int i=0;i<size;i++)
	 	{
	 		id=(String)in.readObject();
	 		
	 		subsize=in.readInt();
	 		
//	 		System.out.println(i+": "+id+" "+subsize);
	 		submap=new HashMap();
	 		if(subsize!=0)
	 		{
	 			for(int j=0;j<subsize;j++)
	 			{
	 				subid=(String)in.readObject();
	 				subval=in.readDouble();
	 				submap.put(subid, Double.valueOf(subval));
	 			}
	 		}
	 		this.map.put(id, submap);
	 	}
	}

	
	public void readMap(String source){

		File file;
		FileReader fin;
		BufferedReader read;
		String line;
		String[] parts;
		String[] subParts;
		Map rawMap;
		int N;
		int n;
		
		try{
			file=new File(source);
			fin=new FileReader(file);
			read=new BufferedReader(fin);
			
			line=read.readLine();
			N=Integer.parseInt(line);
			
			for(int i=0;i<N;i++){
				line=read.readLine();
				parts=line.split("\t");
				n=Integer.parseInt(parts[1]);
				
				if(n==0)
					this.map.put(parts[0], null);
				else{
					rawMap=new HashMap();
					for(int j=0;j<n;j++){
						line=read.readLine();
						subParts=line.split("\t");
						rawMap.put(subParts[0], subParts[1]);
					}
					this.map.put(parts[0], rawMap);
				}				
			}
//			System.out.println(this.map);
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	
	public static void main(String[] args) {
    	MapStream ms = new MapStream();

    	ms.readMap("ptd.txt");        
 
    	String datafile="./data/phenotype-to-disease-bipartite.txt";
   
 //   	System.out.println("Hello "+datafile);
  /*      
        try {
            //
            // Create instances of FileOutputStream and ObjectOutputStream.
            //
            FileOutputStream fos = new FileOutputStream(datafile);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
 
            //
            // By using writeObject() method of the ObjectOutputStream we can
            // make the book object persistent on the books.dat file.
            //
            oos.writeObject(ms.map);
 
            //
            // Flush and close the ObjectOutputStream.
            //
            oos.flush();
            oos.close();
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
    */
/*    
    	try{
    			File file=new File(datafile);
 //   			System.out.println(file.isFile());
    			FileInputStream fis = new FileInputStream(file);
                ObjectInputStream ois = new ObjectInputStream(fis);     
                //
                // To read the Book object use the ObjectInputStream.readObject() method.
                // This method return Object type data so we need to cast it back the its
                // origin class, the Book class.
                //
                ms.readObject(ois);           
    	}
    	catch(Exception e)
    	{
    		System.out.println(e);
    	}
*/    	
    	System.out.println();

    }
	

}
