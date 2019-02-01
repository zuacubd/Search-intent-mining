package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import dataPath.TrainingDataPath;

public class Intent2TopicLoader {

	private Map intent2TopicIDtopicString;
	private ArrayList intent2TopicsList = new ArrayList();
	
	public Map getIntent2TopicIDtopicString() { return this.intent2TopicIDtopicString;}
	public ArrayList getTopicsList() { return this.intent2TopicsList;}
	
	public Intent2TopicLoader(String intent2TopicsSource) {
	
		this.intent2TopicIDtopicString = this.getTopics(intent2TopicsSource);
	}
	
	@SuppressWarnings("unchecked")
	private Map getTopics(String intent2TopicsPath) {
		
		Map topicsTable = new HashMap();
	
		StringBuffer topicLines;
		
		String topicID;
		String topicString;
		String topicLine;
		String[] topicParts;
		String[] lines;
	
		int i;
		int n;
		
		topicLines = this.getTopicList(intent2TopicsPath);
		lines = topicLines.toString().split("\n");
		
		n = lines.length;
		
		for(i = 0; i<n; i++) {
			topicLine = lines[i];
			topicParts = topicLine.split("\t");
			
			if(topicParts != null) {
				topicID = topicParts[0].trim();
				topicString = topicParts[1].trim();
			
				this.intent2TopicsList.add(topicID);
				topicsTable.put(topicID, topicString);
			}
		}
		
		return topicsTable;
	}
	
	private StringBuffer getTopicList(String intent2TopicsPath) {
		
		StringBuffer lines = new StringBuffer();
		String thisLine;
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(intent2TopicsPath))));
	
			thisLine = reader.readLine();
			if(thisLine != null) 
				lines.append(thisLine);
			while((thisLine = reader.readLine())!=null) {
				lines.append("\n"+thisLine);
			}
			reader.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return lines;
	}
	
	public void showMap(Map data) {
		
		System.out.println("Total Items : "+ data.size());

		for(Object id:data.keySet()) {
			System.out.println(id +" : "+ data.get(id));
		}
	}
	
	public static void main(String[] args) {
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();

		Intent2TopicLoader intent2TopicsLoader = new Intent2TopicLoader(trainingDataPath.getIntent2TopicsPath());
		intent2TopicsLoader.showMap(intent2TopicsLoader.intent2TopicIDtopicString);
	}
}
