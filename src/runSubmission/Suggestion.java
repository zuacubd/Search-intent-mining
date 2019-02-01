package runSubmission;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Suggestion {
	
	private Map bingSuggestion=new HashMap();
	private Map bingCompletion=new HashMap();
	private Map googleCompletion=new HashMap();
	private Map yahooCompletion=new HashMap();

	public Map getBingSuggestion(){return bingSuggestion;}
	public Map getBingCompletion(){return bingCompletion;}
	public Map getGoogleCompletion(){return googleCompletion;}
	public Map getYahooCompletion(){return yahooCompletion;}
	
	public Suggestion(){	
		this.processBingSuggestion("./data/INTENT2EtopicsQS_BingSuggestion.txt");
		this.processBingCompletion("./data/INTENT2EtopicsQS_BingCompletion.txt");
		this.processGoogleCompletion("./data/INTENT2EtopicsQS_GoogleCompletion.txt");
		this.processYahooCompletion("./data/INTENT2EtopicsQS_YahooCompletion.txt");		
	}
	
	void processBingSuggestion(String file){
		String id;
		String subTopicString;
		String thisLine;
		int pos;
//		int count = 0;
		
		try{
			FileInputStream fin=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fin));	
//			count=0;
			while((thisLine=br.readLine())!=null){
				pos=thisLine.indexOf("\t");
				if(pos>-1){
//					count++;
					id=thisLine.substring(0,pos);
					subTopicString=thisLine.substring(pos+1, thisLine.length());
					bingSuggestion.put(id, subTopicString);
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		
		}
//		System.out.println(count);
	}
	
	void processBingCompletion(String file){
		String id;
		String subTopicString;
		String thisLine;
		int pos;
//		int count = 0;
		
		try{
			FileInputStream fin=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fin));	
//			count=0;
			while((thisLine=br.readLine())!=null){
				pos=thisLine.indexOf("\t");
				if(pos>-1){
//					count++;
					id=thisLine.substring(0,pos);
					subTopicString=thisLine.substring(pos+1, thisLine.length());
					bingCompletion.put(id, subTopicString);
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		
		}
//		System.out.println(count);
	}
	void processGoogleCompletion(String file){
		String id;
		String subTopicString;
		String thisLine;
		int pos;
//		int count = 0;
		
		try{
			FileInputStream fin=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fin));	
//			count=0;
			while((thisLine=br.readLine())!=null){
				pos=thisLine.indexOf("\t");
				if(pos>-1){
//					count++;
					id=thisLine.substring(0,pos);
					subTopicString=thisLine.substring(pos+1, thisLine.length());
					googleCompletion.put(id, subTopicString);
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		
		}
//		System.out.println(count);
	}
	void processYahooCompletion(String file){
		String id;
		String subTopicString;
		String thisLine;
		int pos;
//		int count = 0;
		
		try{
			FileInputStream fin=new FileInputStream(file);
			BufferedReader br=new BufferedReader(new InputStreamReader(fin));	
//			count=0;
			while((thisLine=br.readLine())!=null){
				pos=thisLine.indexOf("\t");
				if(pos>-1){
//					count++;
					id=thisLine.substring(0,pos);
					subTopicString=thisLine.substring(pos+1, thisLine.length());
					yahooCompletion.put(id, subTopicString);
				}
			}	
		}catch(Exception e){
			System.out.println(e);
		
		}
//		System.out.println(count);
	}
	
	public void showMapData(Map mapData){
		
		try{
			for(Object id:mapData.keySet()){
				System.out.println(id+":"+mapData.get(id));
			}
		}catch(Exception e){
			System.out.println(e);
		}	
	}
	
	public void processMapData(Map mapData){
		String topicID;
		String subTopicString;
		String[] subTopicParts;
		try{
			
			for(Object id:mapData.keySet()){
				topicID=(String) id;
				subTopicString=(String) mapData.get(id);
				//System.out.println(topicID+":"+subTopicString);
				subTopicParts=subTopicString.split("\t");
				System.out.println(topicID);
				for(int i=0;i<subTopicParts.length;i++){
					System.out.println(subTopicParts[i]);
				}
			}			
			
		}catch(Exception e){
			System.out.println(e);
		}
		
	}
	
	public static void main(String arg[]){
		Suggestion suggestion=new Suggestion();
		
		suggestion.processBingSuggestion("./data/INTENT2EtopicsQS_BingSuggestion.txt");
//		suggestion.showMapData(suggestion.bingSuggestion);
		System.out.println(suggestion.bingSuggestion);
		suggestion.processBingCompletion("./data/INTENT2EtopicsQS_BingCompletion.txt");
//		suggestion.showMapData(suggestion.bingCompletion);
		System.out.println(suggestion.bingCompletion);
		suggestion.processGoogleCompletion("./data/INTENT2EtopicsQS_GoogleCompletion.txt");
//		suggestion.showMapData(suggestion.googleCompletion);
		System.out.println(suggestion.googleCompletion);
		suggestion.processYahooCompletion("./data/INTENT2EtopicsQS_YahooCompletion.txt");
//		suggestion.showMapData(suggestion.yahooCompletion);
		System.out.println(suggestion.yahooCompletion);
	}
}
