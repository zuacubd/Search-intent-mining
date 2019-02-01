package algorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import utility.MapStream;

public class BIW {

	private Map query_url;
	private Map url_query;
	private Map weighted_query_url;
	private Map weighted_url_query;
	
	public Map getWeigetedQueryURL() { return this.weighted_query_url;}
	public Map getWeightedURLQuery() { return this.weighted_url_query;}
	
	public BIW(Map queryURL, Map urlQuery){

		this.query_url = queryURL;
		this.url_query = urlQuery;
		
		System.out.println("BIW Algorithm started...");
		this.weighted_query_url = this.BidirectionallyInducedWeight(this.query_url, this.url_query);
		System.out.println("BIW Algorithm ended.");
		
		System.out.println("Start generating weighted inverted index");
		this.weighted_url_query = this.invertedIndex(this.weighted_query_url);
		System.out.println("Weighted inverted index generated.");

	}

	public Map BidirectionallyInducedWeight(Map AB, Map BA){

		Map docFreq;
		Map termFreq;
		Map docWeighted;
		Map weightedData = new HashMap();
		
		Double avgTermLength = this.avgTermLength(AB);
		
		System.out.println("Avg. Length of Query :"+ avgTermLength);
		
		Double avgDocumentLength = this.avgDocumentLength(BA);
		
		System.out.println("Avg. Length of URL :"+ avgDocumentLength);
		
		Integer totalDocFreq = new Integer(0);
		Integer totalTermFreq = new Integer(0);
		Integer docLength= new Integer(0);
		Integer termLength = new Integer(0);	
		Integer commonFreq = new Integer(0);
		Double normalizedTermWeight = new Double(0.0);
		Double normalizedDocumentWeight = new Double(0.0);
		Double alpha = new Double(0.0);
		Double beta = new Double(0.0);
		Double biw_weight = new Double(0.0);
		
		Object currentTerm;
		
		for(Object term:AB.keySet()){
			docFreq = (Map) AB.get(term);
			currentTerm = term;
			
			if(docFreq == null) continue;
			
			docWeighted = new HashMap();
			
			termLength = docFreq.size();

			totalDocFreq = 0;
			for(Object docID: docFreq.keySet()){
					totalDocFreq += (Integer) docFreq.get(docID);
				}						
			
			for(Object docID:docFreq.keySet())
			{	
				termFreq = (Map) BA.get(docID);
				
				docLength = termFreq.size();

				totalTermFreq = 0;
				for(Object termID:termFreq.keySet())
					totalTermFreq = totalTermFreq + (Integer) termFreq.get(termID);
				
				commonFreq = (Integer) docFreq.get(docID);
				
				normalizedTermWeight = commonFreq.doubleValue()/totalTermFreq.doubleValue();
				normalizedDocumentWeight = commonFreq.doubleValue()/totalDocFreq.doubleValue();
				
				
//				System.out.println("TermID: "+currentTerm+" TermLength:"+termLength+"\t TotalTermFreq: "+totalTermFreq+"\t DocID:"+docID+"\t DocLength: "+docLength+"\t TotalDocFreq: "+totalDocFreq+" term-docFreq: "+ commonFreq);
				
				alpha = avgDocumentLength.doubleValue()/termLength.doubleValue();
				beta = avgTermLength.doubleValue()/docLength.doubleValue();
				
				biw_weight = alpha.doubleValue()*normalizedDocumentWeight.doubleValue() + beta.doubleValue()*normalizedTermWeight.doubleValue();
	
//				System.out.println("TermID: "+currentTerm + "\t DocID: "+docID+ "\t Alpha: "+ alpha  +"\t Normalized Document Weight: "+ normalizedDocumentWeight +"\t Beta: "+ beta +"\t Normalized Term Weight: "+ normalizedTermWeight +"\t BIW_Weight: "+ biw_weight);
				
				docWeighted.put(docID, biw_weight.doubleValue());
			}
			
//			System.out.println("Current Term: "+ currentTerm + "\t docWeighted: "+docWeighted);
			weightedData.put(currentTerm,docWeighted);
		}
		return weightedData;
	}

	public double avgTermLength(Map AB){
		
		Map docFreq;
		double avgTermLength=0.0F;
		Integer freq=new Integer(0);
		int numTerm = AB.size();
		
		for(Object term:AB.keySet()){
			docFreq=(Map) AB.get(term);
			
			if(docFreq!=null)
				freq+=docFreq.size();
		}
		
		avgTermLength=(freq.intValue()*1.0)/numTerm;

		return avgTermLength;
		
	}
	
	public double avgDocumentLength(Map BA){
		
		Map termFreq;			
		double avgDocumentLength=0.0F;
		Integer freq=new Integer(0);
		int numDocument = BA.size();
		
		for(Object doc:BA.keySet()){
			termFreq = (Map) BA.get(doc);
			if(termFreq!=null)
				freq += termFreq.size();
		}
		
		avgDocumentLength = (freq.intValue()*1.0)/numDocument;
		return avgDocumentLength;
	}
	
	public Map invertedIndex(Map data){
			
			Map submap;
			Map temp;
			Double freqDouble = new Double(0.0);
			Map invertedIndex = new HashMap();
			
			for(Object id: data.keySet()){
				temp = (Map) data.get(id);
				if(temp!=null){
					for(Object subid:temp.keySet()){
						freqDouble = (Double) temp.get(subid);
						submap = (Map) this.url_query.get(subid);
						if(submap == null){
							submap = new HashMap();
						}
						submap.put(id, freqDouble);
						invertedIndex.put(subid, submap);
					}
				}
			}
			return invertedIndex;
	}
	
	public void displayMultiMap(Map data){
		System.out.println("Total record	"+data.size());
		
		Map temp;
		for(Object id:data.keySet()){
			temp = (Map) data.get(id);
			System.out.println(id + ":" + temp);
		}
		System.out.println();
	}
	
	public Map calculateHistrogram(Map data){
		HashMap hist = new HashMap();
		
		Integer degree = new Integer(0);
		Map temp;
		Integer count = new Integer(0);
		
		for(Object id:data.keySet()){
			temp = (Map) data.get(id);
			if(temp != null)
				degree = temp.size();
			
			count = (Integer) hist.get(degree);
			if(count==null)
				hist.put(degree, Integer.valueOf(1));
			else
				hist.put(degree, count+Integer.valueOf(1));
		}
		return hist;
	}
	
	public void displayHistogram(Map data){
		
		Map hist = this.calculateHistrogram(data);
		
		System.out.println("Histogram is starting....");
		
		for(Object id:hist.keySet()){
			System.out.println(id +" : "+hist.get(id));
		}
		
		System.out.println("Histogram is ending....");
	}
	
	public void storeHistogram(Map map, String dest){
		
		Map data = this.calculateHistrogram(map);
		
		System.out.println("Histogram writing started to "+dest+"...");

		try{
			File file = new File(dest);
			boolean append = false;
			PrintWriter output = new PrintWriter(new FileWriter(file, append));
			
			output.write(data.size()+"\n");
			
			for(Object id:data.keySet()){
				output.write(id + "\t "+data.get(id)+"\n");
			}
			output.close();
			
		System.out.println("Histogram writing ended.");	
		
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	public void writeTo(Map data, String file)
	{
		System.out.println("Weighted query-url writing started to "+file+"....");

		MapStream ms = new MapStream();
    	ms.setMap(data);
        
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
            oos.writeObject(ms);
 
            //
            // Flush and close the ObjectOutputStream.
            //
            oos.flush();
            oos.close();
            System.out.println("Weighed query-url ended.");
        }
        catch(Exception e)
        {
        	System.out.println(e);
        }
 
	}
	
	public void readFrom(String file)
	{
	
		
		Map newmap=new HashMap();
		
		System.out.println("Hello"+file);
        
		try{
			FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);
 
            //
            // To read the Book object use the ObjectInputStream.readObject() method.
            // This method return Object type data so we need to cast it back the its
            // origin class, the Book class.
            //
            MapStream newstream = (MapStream) ois.readObject();
            newmap=newstream.getMap();
            System.out.println(newmap);
            ois.close();
            
		}
		catch(Exception e)
		{
			System.out.println(e);
		}
		
		//return newmap;
	}
	
	
	public static void main(String[] args){
	}
}
