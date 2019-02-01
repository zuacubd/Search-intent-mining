package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import dataPath.TrainingDataPath;

public class Intent2QuerySuggestionLoader {

	private Map bingQuerySuggestion;
	private Map bingQueryCompletion;
	private Map googleQueryCompletion;
	private Map yahooQueryCompletion;
	
	public Map getBingQuerySuggestion() { return this.bingQuerySuggestion;}
	public Map getBingQueryCompletion() { return this.bingQueryCompletion;}
	public Map getGoogleQueryCompletion() { return this.googleQueryCompletion;}
	public Map getYahooQueryCompletion() { return this.yahooQueryCompletion;}
	
	public Intent2QuerySuggestionLoader(String bingQuerySuggestionPath,
			String bingQueryCompletionPath, String googleQueryCompletionPath,
			String yahooQueryCompletionPath) {
	
			this.bingQuerySuggestion = this.getTopicsSuggestion(bingQuerySuggestionPath);
			this.bingQueryCompletion = this.getTopicsSuggestion(bingQueryCompletionPath);
			this.googleQueryCompletion = this.getTopicsSuggestion(googleQueryCompletionPath);
			this.yahooQueryCompletion = this.getTopicsSuggestion(yahooQueryCompletionPath);
	
	}
	
	@SuppressWarnings("unchecked")
	private Map getTopicsSuggestion(String topicSuggestionPath) {
		
		@SuppressWarnings("rawtypes")
		Map topicsSuggestionTable = new HashMap();
	
		StringBuffer topicSuggestionLines;
		
		String topicID;
		String suggestionString;
		String suggestionLine;
		String[] suggestionParts;
		String[] suggestionLines;
		Vector suggestions;
		
		int i;
		int j;
		int n;
		int l;
		
		topicSuggestionLines = this.getTopicSuggestionList(topicSuggestionPath);
		suggestionLines = topicSuggestionLines.toString().split("\n");
		
		n = suggestionLines.length;
		
		for(i = 0; i<n; i++) {
			suggestionLine = suggestionLines[i];
			suggestionParts = suggestionLine.split("\t");
			
			if(suggestionParts != null) {
				topicID = suggestionParts[0].trim();
				
				l = suggestionParts.length;
				suggestions = new Vector();
				for(j = 1; j<l; j++ ) {
					suggestionString = suggestionParts[j].trim();
					suggestions.add(suggestionString);
				}			
				topicsSuggestionTable.put(topicID, suggestions);
			}
		}
		
		return topicsSuggestionTable;
	}
	
	private StringBuffer getTopicSuggestionList(String topicsSuggestionPath) {
		
		StringBuffer suggestionLines = new StringBuffer();
		String thisLine;
		
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(topicsSuggestionPath))));
	
			thisLine = reader.readLine();
			if(thisLine != null) 
				suggestionLines.append(thisLine.trim());
			while((thisLine = reader.readLine())!=null) {
				suggestionLines.append("\n"+thisLine.trim());
			}
			reader.close();
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		
		return suggestionLines;
	}

	public void showMap(Map data) {
		
		System.out.println("Total Items : "+ data.size());

		for(Object id:data.keySet()) {
			System.out.println(id +" : "+ data.get(id));
		}
	}
	
	public static void main(String[] args) {
	
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		Intent2QuerySuggestionLoader intent2QuerySuggestionLoader = new Intent2QuerySuggestionLoader(trainingDataPath.getBingQuerySuggestionPath(), trainingDataPath.getBingQueryCompletionPath(), trainingDataPath.getGoogleQueryCompletionPath(), trainingDataPath.getYahooQueryCompletionPath());

		intent2QuerySuggestionLoader.showMap(intent2QuerySuggestionLoader.bingQuerySuggestion);

		intent2QuerySuggestionLoader.showMap(intent2QuerySuggestionLoader.bingQueryCompletion);
		
		intent2QuerySuggestionLoader.showMap(intent2QuerySuggestionLoader.googleQueryCompletion);
		
		intent2QuerySuggestionLoader.showMap(intent2QuerySuggestionLoader.yahooQueryCompletion);
	}
}
