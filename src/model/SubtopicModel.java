package model;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import rankingSupervised.SupervisedRankingFeatureVectors;
import rankingUnsupervised.UnsupervisedRankingFeatureVectors;



import rankingDiversified.DiversifiedRanking;
import subtopicimportance.SubtopicImportanceEstimation;
//import rankingSupervised.RankingFeatureVectors;
import dataLoader.ExtractCandidateSubtopic;
import dataLoader.ParseSearchEngineResultPage;
import dataPath.TrainingDataPath;
import featureExtraction.FeatureExtraction;
import featureExtraction.TopicSubtopicFeatures;
import filteringSubtopics.FilteringCandidateSubtopic;
import filteringSubtopics.FilteringDuplicateSubtopics;
import utility.FileUtils;
import utility.SimpleMath;
import utility.SortMapDouble;

public class SubtopicModel {
	
	private ArrayList topicList;
	private Map topicIDtopicString;
	private Map topicAggregatedCandidateSubtopics;
	private Map topicCandidateSubtopics;
	private Map topicSubtopicsHitCount;
	private Map topicSubtopicsFeaturesVector;

	private Map topicSubtopicsWeights;
	
	private Map topicSubtopicsUnsupervisedRankingWeight;
	private Map topicSubtopicsLearningToRankWeight;
	private Map topicSubtopicsLearningToRankRecipocalRankWeights;
	private Map topicSubtopicsSmoothRankingWeight;
	
	private Map topicSubtopicsDiversifiedRakingWeights;
	
	private Map topicSubtopicsFilterredRankingWeight;
	private Integer numFeatures;
	
	public Map getTopicSubtopicsUnsupervisedRankingWeight() {
		return this.topicSubtopicsUnsupervisedRankingWeight;
	}
	
	public Map getTopicSubtopicsLearningToRankWeight() {
		return this.topicSubtopicsLearningToRankWeight;
	}
	
	public Map getTopicSubtopicsLearningToRankRecipocalRankWeight() {
		return this.topicSubtopicsLearningToRankRecipocalRankWeights;
	}
	
	public Map getTopicSubtopicsDiversifiedRankingWeight() {
		return this.topicSubtopicsDiversifiedRakingWeights;
	}
	
	public Map getTopicSubtopicsFilterredRankingWeights() {
		return this.topicSubtopicsFilterredRankingWeight;
	}
	
	public ArrayList getTopicList() {
		return this.topicList;
	}
	
	private ExtractCandidateSubtopic extractCandidateSubtopic;
	private FilteringCandidateSubtopic filteringCandidateSubtopics;
	private ParseSearchEngineResultPage parseSearchEngineResultPage;
	private FeatureExtraction featureExtraction;
	
	private SubtopicImportanceEstimation subtopicImportanceEstimation;
	private UnsupervisedRankingFeatureVectors unsupervisedRankingFeatureVectors;
	private SupervisedRankingFeatureVectors supervisedRankingFeatureVectors;
	private FilteringDuplicateSubtopics filteringDuplicateSubtopics;
	
	private DiversifiedRanking diversifiedRanking;
	
	private SortMapDouble sortMapDouble;
	
	public SubtopicModel(String intent2TopicsPath, String bingQuerySuggestionPath, String bingQueryCompletionPath, String googleQueryCompletionPath, String yahooQueryCompletionPath, String topicSubtopicSearchEngineResultPagePath) {
		
		//Extracting and Aggregating candidate subtopics from the Search Engine Service
		this.extractCandidateSubtopic = new ExtractCandidateSubtopic(intent2TopicsPath, bingQuerySuggestionPath, bingQueryCompletionPath, googleQueryCompletionPath, yahooQueryCompletionPath);
		this.topicAggregatedCandidateSubtopics = this.extractCandidateSubtopic.getIntent2TopicAggregatedCandidateSubtopics();
		this.topicIDtopicString = this.extractCandidateSubtopic.getIntent2TopicIDtopicString();
		this.topicList = this.extractCandidateSubtopic.getTopicsList();
		
		//Filtering candidate subtopics (Rule 1 and Rule 2)
		this.filteringCandidateSubtopics = new FilteringCandidateSubtopic(this.topicIDtopicString, this.topicAggregatedCandidateSubtopics);
		this.topicCandidateSubtopics = this.filteringCandidateSubtopics.getTopicCandidateSubtopics();
		
		//Search Engine Result Page
		this.parseSearchEngineResultPage = new ParseSearchEngineResultPage(topicSubtopicSearchEngineResultPagePath);
		this.topicSubtopicsHitCount = this.parseSearchEngineResultPage.getTopicSubtopicSEHitCount();
		
		//Estimating features for ranking subtopics
		this.featureExtraction = new FeatureExtraction(this.topicIDtopicString, this.topicCandidateSubtopics, this.topicSubtopicsHitCount);
		this.topicSubtopicsFeaturesVector = this.featureExtraction.getTopicSubtopicFeaturesVectors();
		this.numFeatures = this.featureExtraction.getNumFeatures();
		
		this.subtopicImportanceEstimation = new SubtopicImportanceEstimation(this.topicSubtopicsFeaturesVector, this.numFeatures);
		this.topicSubtopicsWeights = this.subtopicImportanceEstimation.getTopicSubtopicsImportanceWeight();
		
		//unsupervised Learning
//		this.unsupervisedRankingFeatureVectors = new UnsupervisedRankingFeatureVectors(this.topicSubtopicsFeaturesVector, this.numFeatures);
//		this.topicSubtopicsUnsupervisedRankingWeight = this.unsupervisedRankingFeatureVectors.getTopicSubtopicsUnsupervisedRankingWeight();
		
		//supervised learning
//		this.supervisedRankingFeatureVectors = new SupervisedRankingFeatureVectors(this.topicSubtopicsFeaturesVector);
//		this.topicSubtopicsLearningToRankWeight = this.supervisedRankingFeatureVectors.getTopicSubtopicLearnedRankingWeights();
//		this.topicSubtopicsLearningToRankRecipocalRankWeights = this.supervisedRankingFeatureVectors.getTopicSubtopicRecipocalRankWeights();
		
//		this.topicSubtopicsSmoothRankingWeight = this.getTopicSubtopicsSmoothRankingWeight(this.topicSubtopicsUnsupervisedRankingWeight);
		
//		this.diversifiedRanking = new DiversifiedRanking(this.topicSubtopicsFeaturesVector,this.topicSubtopicsUnsupervisedRankingWeight);
//		this.diversifiedRanking = new DiversifiedRanking(this.topicSubtopicsFeaturesVector,this.topicSubtopicsSmoothRankingWeight);
		this.diversifiedRanking = new DiversifiedRanking(this.topicSubtopicsFeaturesVector, this.topicSubtopicsWeights);
		
		this.topicSubtopicsDiversifiedRakingWeights = this.diversifiedRanking.getTopicSubtopicsDiversifiedRankingScore();
//		this.showTopicSubtopicWeights(this.topicSubtopicsRankingWeight, this.topicSubtopicsDiversifiedRakingWeights);
		
//		this.diversifiedRanking = new DiversifiedRanking(this.topicSubtopicsFeaturesVector,this.topicSubtopicsLearningToRankRecipocalRankWeights);
//		this.topicSubtopicsDiversifiedRakingWeights = this.diversifiedRanking.getTopicSubtopicsDiversifiedRankingScore();
		
//		this.filteringDuplicateSubtopics = new FilteringDuplicateSubtopics(this.topicSubtopicsUnsupervisedRankingWeight);
//		this.filteringDuplicateSubtopics = new FilteringDuplicateSubtopics(this.topicSubtopicsLearningToRankRecipocalRankWeights);
		this.filteringDuplicateSubtopics = new FilteringDuplicateSubtopics(this.topicSubtopicsDiversifiedRakingWeights);
		this.topicSubtopicsFilterredRankingWeight = this.filteringDuplicateSubtopics.getTopicDuplicateFilterredSubtopicsWeights();
	
//		this.showTopicSubtopicFeaturesVectors();
//		this.constructLearningToRankTrainingData();
	}
	
	private Map getTopicSubtopicsSmoothRankingWeight(Map topicSubtopicsLearnedRankingWeight) {
		
		Map topicSubtopicsSmoothRankingWeight = new HashMap();
		Map subtopicSmoothWeight;
		Map subtopicsWeight;
		Double subtopicWeight;
		
		for(Object topicID:topicSubtopicsLearnedRankingWeight.keySet()) {
			
			subtopicsWeight = (Map) topicSubtopicsLearnedRankingWeight.get(topicID);
			
//			System.out.println(topicID);
			subtopicSmoothWeight = new HashMap();
			
			for(Object subtopicID:subtopicsWeight.keySet()) {
				subtopicWeight = (Double) subtopicsWeight.get(subtopicID);
//				System.out.println(subtopicID +" : "+ subtopicWeight);
				subtopicWeight = subtopicWeight + 1.0;
				subtopicWeight = subtopicWeight/2.0;
				subtopicSmoothWeight.put(subtopicID, subtopicWeight);
			}
			topicSubtopicsSmoothRankingWeight.put(topicID, subtopicSmoothWeight);
		}
		
		return topicSubtopicsSmoothRankingWeight;
	}

	public void showTopicSubtopicWeights(Map machineLearnedWeights, Map diversifiedRankingWeights) {
		
		Map subtopicsML;
		Map subtopicsDR;
		double weight1;
		double weight2;
		Vector subtopics;
		String subtopicID;
		int i;
		
		for(Object topicID:this.topicIDtopicString.keySet()) {
			
			subtopicsML = (Map) machineLearnedWeights.get(topicID);
			subtopicsDR = (Map) diversifiedRankingWeights.get(topicID);
			
			this.sortMapDouble = new SortMapDouble();
			subtopics = this.sortMapDouble.getTopKKey(subtopicsML, subtopicsML.size());
			
			System.out.println(topicID + " : "+ this.topicIDtopicString.get(topicID));
			
			for(i = 0; i<subtopics.size(); i++) 
			{
				subtopicID = (String) subtopics.get(i);
				
				weight1 = (Double) subtopicsML.get(subtopicID);
				weight2 = (Double) subtopicsDR.get(subtopicID);
				
				System.out.println("\t\t"+subtopicID +" : "+ weight1 +" : "+ weight2);
			}
		}
	}
	
	public void showTopicSubtopicFeaturesVectors() {
		
		Map subtopics;
		int rel;
		int i;
		int n;
		Vector featureVector;
		Double featureVal;
		
		rel = 1;
		n = 5;
		for(Object topicID:this.topicIDtopicString.keySet()) {
			
			subtopics = (Map) this.topicSubtopicsFeaturesVector.get(topicID);
			
			for(Object subtopicID:subtopics.keySet()) {
				
				featureVector = (Vector) subtopics.get(subtopicID);
				
				rel = this.getRandomNumber(1);
				System.out.print(rel+" qid:"+topicID);
				
				for(i = 0; i<featureVector.size(); i++) {
					featureVal = (Double) featureVector.get(i);
					featureVal = SimpleMath.round(featureVal, n);
					System.out.print(" "+(i+1)+":"+ featureVal);
				}
				System.out.println();
			}
		}
	}
	
	public void constructLearningToRankTrainingData() {
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		StringBuffer queryDocumentPair = new StringBuffer();
		
		Map subtopics;
		int rel;
		int i;
		int n;
		Vector featureVector;
		Double featureVal;
		String queryDocumentFeatureLine;
		
		queryDocumentFeatureLine = "";
		rel = 1;
		n = 5;
		for(Object topicID:this.topicIDtopicString.keySet()) {
			
			subtopics = (Map) this.topicSubtopicsFeaturesVector.get(topicID);
			
			for(Object subtopicID:subtopics.keySet()) {
				
				queryDocumentPair.delete(0,  queryDocumentPair.length());

				featureVector = (Vector) subtopics.get(subtopicID);				
//				rel = this.getRandomNumber(1);
				rel = 1;
//				System.out.print(rel+" qid:"+topicID);
				queryDocumentPair.append(rel+" qid:"+topicID);
				
				for(i = 0; i<featureVector.size(); i++) {
					featureVal = (Double) featureVector.get(i);
					featureVal = SimpleMath.round(featureVal, n);
//					System.out.print(" "+(i+1)+":"+ featureVal);
					queryDocumentPair.append(" "+(i+1)+":"+ featureVal);
				}
				
				queryDocumentFeatureLine += queryDocumentPair.toString()+"\n";
			}
		}
		
		FileUtils.write(trainingDataPath.getImineL2RTestingDataPath(), "utf-8", queryDocumentFeatureLine);
	}
	
	private int getRandomNumber(int numDigit) {
		
		int randomNumber;
		double rand;
		int i;
		
		while(true) {
			
			rand = Math.random();
			
			for(i = 0; i<numDigit; i++)
				rand = rand * 10;
			
			rand = Math.floor(rand);
			randomNumber = (int) rand;
			
			if(randomNumber<5) break;
		}
		
		return randomNumber;
	}
	
	public void saveTopicSubtopicString(Map topicCandidateSubtopics, String topicSubtopicFile) {
		
		String topicString;
		Map subtopics;
		
		try {
			PrintWriter output = new PrintWriter(new FileWriter(new File(topicSubtopicFile), false));
		
			for(Object topicID:topicCandidateSubtopics.keySet()) {
				subtopics = (Map) topicCandidateSubtopics.get(topicID);
				
				topicString = (String) this.topicIDtopicString.get(topicID); 
				output.write("<topic>"+topicString+"</topic>\n");
				
				for(Object subtopic:subtopics.keySet()) {
					
					output.write("<subtopic>"+subtopic.toString() +"</subtopic>\n");
				}
			}
			output.close();
		}catch(Exception e) {
			
		}
	}
	
	public static void main(String[] args) {
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		
		SubtopicModel subtopicModel = new SubtopicModel(trainingDataPath.getIntent2TopicsPath(), trainingDataPath.getBingQuerySuggestionPath(), trainingDataPath.getBingQueryCompletionPath(), trainingDataPath.getGoogleQueryCompletionPath(), trainingDataPath.getYahooQueryCompletionPath(), trainingDataPath.getTopicSubtopicSearchEngineResultPage());
//		System.out.println(subtopicModel.topicCandidateSubtopics);
//		subtopicModel.showTopicSubtopicFeaturesVectors();
//		subtopicModel.showTopicSubtopicRankingFeature();
//		subtopicModel.saveTopicSubtopicString(subtopicModel.topicCandidateSubtopics, trainingDataPath.getTopicSubtopicsPath());
	}
}
