package runSubmission;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;

import dataPath.TrainingDataPath;
import model.SubtopicModel;
import utility.SortMapDouble;

public class ExportingRun {

	private Map topicSubtopicsUnsupervisedRankingWeight;
	private Map topicSubtopicsLearningToRankWeight;
	private Map topicSubtopicsLearningToRankRecipocalRankWeight;
	private Map topicSubtopicsDiversifiedRankingWeight;
	private Map topicSubtopicsFilterredRankingWeight;
	private ArrayList topicList;

	private SortMapDouble sortMapDouble;
	private SubtopicModel subtopicModel;
	
	ExportingRun(String intent2TopicsPath, String bingQuerySuggestionPath,
			String bingQueryCompletionPath, String googleQueryCompletionPath,
			String yahooQueryCompletionPath, String queryLogsSubtopicsPath, String topicSubtopicHitCountPath){

		this.subtopicModel = new SubtopicModel(intent2TopicsPath, bingQuerySuggestionPath, bingQueryCompletionPath, googleQueryCompletionPath, yahooQueryCompletionPath, topicSubtopicHitCountPath);
		this.topicList = this.subtopicModel.getTopicList();

//		this.topicSubtopicsUnsupervisedRankingWeight = this.subtopicModel.getTopicSubtopicsUnsupervisedRankingWeight();
//		this.topicSubtopicsLearningToRankWeight= this.subtopicModel.getTopicSubtopicsLearningToRankWeight();
//		this.topicSubtopicsLearningToRankRecipocalRankWeight = this.subtopicModel.getTopicSubtopicsLearningToRankRecipocalRankWeight();
		this.topicSubtopicsDiversifiedRankingWeight = this.subtopicModel.getTopicSubtopicsDiversifiedRankingWeight();
		this.topicSubtopicsFilterredRankingWeight = this.subtopicModel.getTopicSubtopicsFilterredRankingWeights();
	}

	public void processQueryTopic(String testRunFile) {

//		this.makeTestRun(topicSubtopicsUnsupervisedRankingWeight, testRunFile);
//		this.makeTestRun(this.topicSubtopicsLearningToRankWeight, testRunFile);
//		this.makeTestRun(this.topicSubtopicsLearningToRankRecipocalRankWeight, testRunFile);
		this.makeTestRun(this.topicSubtopicsDiversifiedRankingWeight, testRunFile);
//		this.makeTestRun(this.topicSubtopicsFilterredRankingWeight, testRunFile);
	}
	
	public void makeTestRun(Map topicSubtopicsRankingFeature, String testRunFile) {

		int i;
		int j;
		int l;
		int N;
		int rank;

		Vector sortedSubtopicsKey;
		Map weightedSubtopics;
		String topicID;
		String subtopic;
		Double prob;

		N = this.topicList.size();

		try{
			File file=new File(testRunFile);			
			PrintWriter runs = null;
			boolean append = false;
			runs = new PrintWriter(new FileWriter(file, append));

			for(i = 0; i<N; i++) {

				topicID = (String) this.topicList.get(i);

				weightedSubtopics = (Map) topicSubtopicsRankingFeature.get(topicID);

				this.sortMapDouble = new SortMapDouble();

				sortedSubtopicsKey = this.sortMapDouble.getTopKKey(weightedSubtopics, weightedSubtopics.size());

				if(i==0){
					runs.write("<SYSDESC>Query Logs, Query Dependent Features, Query Independent Features, Query Suggestion Ranklist</SYSDESC>"+"\n");
					System.out.println("<SYSDESC>Query Logs, Query Dependent Features, Query Independent Features, Query Suggestion Ranklist</SYSDESC>");
				}

				rank = 1;
				l = sortedSubtopicsKey.size();
				for(j = 0; j<l; j++){

					subtopic = (String) sortedSubtopicsKey.get(j);
					prob = (Double) weightedSubtopics.get(subtopic);

					runs.write(topicID+";"+0+";"+subtopic+";"+rank+";"+prob+";"+"TestRUN"+"\n");
					System.out.println(topicID+";"+0+";"+subtopic+";"+rank+";"+prob+";"+"TestRUN");
					rank++;
				}
			}
			runs.close();
		}
		catch(Exception e){
			System.out.println(e);
		}

	}

	public static void main(String arg[]){
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();

		ExportingRun exportingRun = new ExportingRun(trainingDataPath.getIntent2TopicsPath(), trainingDataPath.getBingQuerySuggestionPath(), trainingDataPath.getBingQueryCompletionPath(), trainingDataPath.getGoogleQueryCompletionPath(), trainingDataPath.getYahooQueryCompletionPath(), trainingDataPath.getQueryLogsSubtopicsPath(), trainingDataPath.getTopicSubtopicSearchEngineResultPage());
		exportingRun.processQueryTopic(trainingDataPath.getTestRunFilePath());
	}
}
