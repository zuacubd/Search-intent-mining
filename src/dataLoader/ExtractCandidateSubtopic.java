package dataLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import dataPath.TrainingDataPath;

public class ExtractCandidateSubtopic {

	private Map intent2TopicIDtopicString;
	private ArrayList intent2TopicList;
	
	private Map bingQuerySuggestion;
	private Map bingQueryCompletion;
	private Map googleQueryCompletion;
	private Map yahooQueryCompletion;
	
	private Map intent2TopicAggregatedCandidateSubtopics;
	
	public Map getIntent2TopicAggregatedCandidateSubtopics() { return this.intent2TopicAggregatedCandidateSubtopics;}
	public Map getIntent2TopicIDtopicString() { return this.intent2TopicIDtopicString;}
	public ArrayList getTopicsList() { return this.intent2TopicList;}
	
	public Map getBingQuerySuggestion() { return this.bingQuerySuggestion;}
	public Map getBingQueryCompletion() { return this.bingQueryCompletion;}
	public Map getGoogleQueryCompletion() { return this.googleQueryCompletion;}
	public Map getYahooQueryCompletion() { return this.yahooQueryCompletion;}
	
	private Intent2TopicLoader intent2TopicLoader;
	private Intent2QuerySuggestionLoader intent2QuerySuggestionLoader;
	
	public ExtractCandidateSubtopic(String intent2TopicsPath, String bingQuerySuggestionPath, String bingQueryCompletionPath, String googleQueryCompletionPath, String yahooQueryCompletionPath) {
	
		this.intent2TopicLoader = new Intent2TopicLoader(intent2TopicsPath);
		this.intent2TopicIDtopicString = this.intent2TopicLoader.getIntent2TopicIDtopicString();
		this.intent2TopicList = this.intent2TopicLoader.getTopicsList();
		
		this.intent2QuerySuggestionLoader = new Intent2QuerySuggestionLoader(bingQuerySuggestionPath, bingQueryCompletionPath, googleQueryCompletionPath, yahooQueryCompletionPath);
		this.bingQuerySuggestion = this.intent2QuerySuggestionLoader.getBingQuerySuggestion();
		this.bingQueryCompletion = this.intent2QuerySuggestionLoader.getBingQueryCompletion();
		this.googleQueryCompletion = this.intent2QuerySuggestionLoader.getGoogleQueryCompletion();
		this.yahooQueryCompletion = this.intent2QuerySuggestionLoader.getYahooQueryCompletion();
		
		this.intent2TopicAggregatedCandidateSubtopics = this.generateAggregatedCandidateSubtopics(this.bingQuerySuggestion, this.bingQueryCompletion,this.googleQueryCompletion, this.yahooQueryCompletion);
	}
	
	private Map generateAggregatedCandidateSubtopics(Map bingQuerySuggestion, Map bingQueryCompletion, Map googleQueryCompletion, Map yahooQueryCompletion) {
		
		HashMap topicAggregatedCandidateSubtopics = new HashMap();

		Map subtopicFeature;
		Map weightedSubtopics;
		Vector querySuggestions;
		Vector ranks;

		Integer rank;
		Double weight;
		String candidateSubtopic;
		
		boolean flag;
		
		int i;
		int l;
		int totalSuggestions;
		
		int subtopicTermLength;
		
		for(Object topicID:bingQueryCompletion.keySet()) {

			weightedSubtopics = (Map) topicAggregatedCandidateSubtopics.get(topicID);

			querySuggestions = (Vector) bingQueryCompletion.get(topicID);

			if(querySuggestions != null) {
				
				totalSuggestions = querySuggestions.size();

				if(weightedSubtopics == null)
					weightedSubtopics = new HashMap();

				for(i = 0; i<totalSuggestions; i++) 
				{
					candidateSubtopic = (String) querySuggestions.get(i);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				
				topicAggregatedCandidateSubtopics.put(topicID, weightedSubtopics);
			}
		}
		
		for(Object topicID:googleQueryCompletion.keySet()) {

			weightedSubtopics = (Map) topicAggregatedCandidateSubtopics.get(topicID);

			querySuggestions = (Vector) googleQueryCompletion.get(topicID);

			if(querySuggestions != null) {
				
				totalSuggestions = querySuggestions.size();

				if(weightedSubtopics == null)
					weightedSubtopics = new HashMap();

				for(i = 0; i<totalSuggestions; i++) 
				{
					candidateSubtopic = (String) querySuggestions.get(i);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				
				topicAggregatedCandidateSubtopics.put(topicID, weightedSubtopics);
			}
		}
		
		for(Object topicID:yahooQueryCompletion.keySet()) {

			weightedSubtopics = (Map) topicAggregatedCandidateSubtopics.get(topicID);

			querySuggestions = (Vector) yahooQueryCompletion.get(topicID);

			if(querySuggestions != null) {
				
				totalSuggestions = querySuggestions.size();

				if(weightedSubtopics == null)
					weightedSubtopics = new HashMap();

				for(i = 0; i<totalSuggestions; i++) 
				{
					candidateSubtopic = (String) querySuggestions.get(i);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				
				topicAggregatedCandidateSubtopics.put(topicID, weightedSubtopics);
			}
		}
		
		for(Object topicID:bingQuerySuggestion.keySet()) {

			weightedSubtopics = (Map) topicAggregatedCandidateSubtopics.get(topicID);

			querySuggestions = (Vector) bingQuerySuggestion.get(topicID);

			if(querySuggestions != null) {
				
				totalSuggestions = querySuggestions.size();

				if(weightedSubtopics == null)
					weightedSubtopics = new HashMap();

				for(i = 0; i<totalSuggestions; i++) 
				{
					candidateSubtopic = (String) querySuggestions.get(i);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					
					flag = false;
					if(ranks == null) {
						ranks = (Vector) weightedSubtopics.get(candidateSubtopic.toLowerCase());
						if(ranks != null) flag = true; 
					}
					
					if(ranks == null) {
						ranks = new Vector();
					}
					
					ranks.add(i+1);
					
					if(flag)
						weightedSubtopics.put(candidateSubtopic.toLowerCase(), ranks);
					else 
						weightedSubtopics.put(candidateSubtopic, ranks);
				}
				
				topicAggregatedCandidateSubtopics.put(topicID, weightedSubtopics);
			}
		}
		
		return topicAggregatedCandidateSubtopics;
	}
	
	public static void main(String[] args) {
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		
		ExtractCandidateSubtopic extractCandidateSubtopic = new ExtractCandidateSubtopic(trainingDataPath.getIntent2TopicsPath(), trainingDataPath.getBingQuerySuggestionPath(), trainingDataPath.getBingQueryCompletionPath(), trainingDataPath.getGoogleQueryCompletionPath(), trainingDataPath.getYahooQueryCompletionPath());
	}
}
