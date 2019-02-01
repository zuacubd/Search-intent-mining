package runSubmission;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Topics {
	private Map topicsIdName=new HashMap();
	
	public Map getTopicsIdName(){ return topicsIdName;}
	
	public Topics(){
		process("./data/INTENT2EtopicsQS.txt");
	}

	void process(String file) {
		String thisLine;
		String[] parts;
		String topicsId;
		String topicsName;
		
		try{
		FileInputStream fin=new FileInputStream(file);
		BufferedReader br=new BufferedReader(new InputStreamReader(fin));	
		while((thisLine=br.readLine())!=null){
			parts=thisLine.split("\t");
		//	System.out.println(thisLine);
			
			if(parts.length<3){
					topicsId=parts[0];
					topicsName=parts[1];
				//	System.out.println("Id: "+topicsId+" : "+"Name: "+topicsName);
					topicsIdName.put(topicsId, topicsName);
			}
			
		}
		}catch(Exception e){
			System.out.println(e);
		}
	}
	public static void main(String arg[]){
		Topics topics=new Topics();
		//System.out.println(topics.topicsIdName);
	}
}
