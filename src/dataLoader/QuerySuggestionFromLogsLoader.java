package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import dataPath.TrainingDataPath;

public class QuerySuggestionFromLogsLoader {

	private Map topicIDQueryLogsSubtopics;
	
	public Map getQueryLogsSubtopics() { return this.topicIDQueryLogsSubtopics;}
	
	public QuerySuggestionFromLogsLoader(String queryLogsSubtopicsPath) {
	
			this.topicIDQueryLogsSubtopics = this.getTopicSuggestionList(queryLogsSubtopicsPath);	
	}
	
	private Map getTopicSuggestionList(String topicsSuggestionPath) {
		
		Map queryTopicSubtopics = new HashMap();
		String thisLine;
		String subtopic;
		String queryTopic;
		String[] lineParts;
		Integer N;
		Double weight;
		Map subtopicMap;
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(topicsSuggestionPath))));
	
			while((thisLine = reader.readLine())!=null)
			{
				lineParts = thisLine.split("\t");
				queryTopic = lineParts[0];
				N = Integer.parseInt(lineParts[1].trim());
				
				subtopicMap = new HashMap();
				
				for(int i = 0; i<N; i++)
				{
					thisLine = reader.readLine();
					lineParts = thisLine.split("\t");
					
					subtopic = lineParts[0];
					weight = Double.parseDouble(lineParts[1]);
					
					subtopicMap.put(subtopic, weight);
				}
			
				queryTopicSubtopics.put(queryTopic, subtopicMap);
			}
			reader.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return queryTopicSubtopics;
	}

	public void showMap(Map data) {
		
		System.out.println("Total Items : "+ data.size());

		for(Object id:data.keySet()) {
			System.out.println(id +" : "+ data.get(id));
		}
	}
	
	public static void main(String[] args) {
	
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		QuerySuggestionFromLogsLoader intent2QuerySuggestionLoader = new QuerySuggestionFromLogsLoader(trainingDataPath.getQueryLogsSubtopicsPath());
		intent2QuerySuggestionLoader.showMap(intent2QuerySuggestionLoader.topicIDQueryLogsSubtopics);
	}
}
