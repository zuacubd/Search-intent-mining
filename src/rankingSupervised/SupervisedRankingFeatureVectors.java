package rankingSupervised;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import utility.SortMapDouble;
import learningToRank.ciir.umass.edu.learning.DataPoint;
import learningToRank.ciir.umass.edu.learning.DenseDataPoint;
import learningToRank.ciir.umass.edu.learning.RankList;
import learningToRank.ciir.umass.edu.learning.Ranker;
import learningToRank.ciir.umass.edu.learning.RankerFactory;
import learningToRank.ciir.umass.edu.learning.SparseDataPoint;

public class SupervisedRankingFeatureVectors {

	private Map topicSubtopicFeatureVectors;
	private Map topicSubtopicPairFeatureVectors;

	private Map topicSubtopicsLearnedRankingScore;
	private Map topicSubtopicsList;
	private Map topicSubtopicsLearnedRankingWeights;
	
	private Map topicSubtopicsRecipocalRankWeights;
	
	private RankerFactory rFact; 
	private Ranker ranker; 
	private int[] features;
	private SortMapDouble sortMapDouble;
	
	public Map getTopicSubtopicLearnedRankingWeights() {
		return this.topicSubtopicsLearnedRankingWeights;
	}
	
	public Map getTopicSubtopicRecipocalRankWeights() {
		return this.topicSubtopicsRecipocalRankWeights;
	}
	
	public SupervisedRankingFeatureVectors(Map topicSubtopicFeatureVectors) {
		
		this.topicSubtopicFeatureVectors = topicSubtopicFeatureVectors;
		this.loadLearningToRankModel();

		this.topicSubtopicPairFeatureVectors = this.getTopicSubtopicPairRankingFeatureVectors(this.topicSubtopicFeatureVectors);

		this.topicSubtopicsLearnedRankingScore = this.getTopicSubtopicLearningToRankScore(this.topicSubtopicPairFeatureVectors);
		this.topicSubtopicsLearnedRankingWeights = this.getTopicSubtopicsRankingWeights(this.topicSubtopicsLearnedRankingScore);
		
		this.topicSubtopicsRecipocalRankWeights = this.getTopicSubtopicsRecipocalRankWeights(this.topicSubtopicsLearnedRankingWeights);
	}
	
	private Map getTopicSubtopicsRecipocalRankWeights(Map topicSubtopicsLearnedRankingWeight) {
		
		Map topicSubtopicsRRW = new HashMap();
		Map subtopicsWeights;
		Vector rankedSubtopics;
		int k;
		int i;
		double rrWeight;
		String subtopic;
		
		Map subtopicsRRW;
		
		for(Object topicID:topicSubtopicsLearnedRankingWeight.keySet()) {
			
			subtopicsWeights = (Map) topicSubtopicsLearnedRankingWeight.get(topicID);
			
			this.sortMapDouble = new SortMapDouble();
			k = subtopicsWeights.size();
//			if(k>15) k = 15;
			rankedSubtopics = this.sortMapDouble.getTopKKey(subtopicsWeights, k);
			
			subtopicsRRW = new HashMap();
			for(i = 0; i<k; i++) {
				
				subtopic = (String) rankedSubtopics.get(i);
				rrWeight = 1.0/ (i+1); 
				subtopicsRRW.put(subtopic, rrWeight); 
			}
			topicSubtopicsRRW.put(topicID, subtopicsRRW);
		}
		
		return topicSubtopicsRRW;
	}
	
	private Map getTopicSubtopicsRankingWeights(Map topicSubtopicLearnedRankingScore) {
		
		Map topicSubtopicsRankingWeights = new HashMap();
		
		Map subtopicsRankingWeight;
		ArrayList subtopicRankingWeightList;
		ArrayList subtopicList;
		int i;
		String subtopic;
		double weight;
		
		for(Object topicID:topicSubtopicLearnedRankingScore.keySet()) {
			
			subtopicList = (ArrayList) this.topicSubtopicsList.get(topicID);
			subtopicRankingWeightList = (ArrayList) topicSubtopicLearnedRankingScore.get(topicID);
			
			subtopicsRankingWeight = new HashMap();
			
			for(i = 0; i<subtopicList.size(); i++) {
				
				subtopic = (String) subtopicList.get(i);
				weight = (Double) subtopicRankingWeightList.get(i);
				subtopicsRankingWeight.put(subtopic, weight);
			}
			topicSubtopicsRankingWeights.put(topicID, subtopicsRankingWeight);
		}
		return topicSubtopicsRankingWeights;
	}
	
	private Map getTopicSubtopicLearningToRankScore(Map topicSubtopicPairFeatureVectors) {

		Map topicSubtopicsLearningToRankScores = new HashMap();
		
		String featureVectors; 
		boolean mustHaveRelDoc;
		boolean useSparseRepresentation;
		List<RankList> topicSubtopicInitialRankedList;
		ArrayList topicSubtopicsLearnedRankingScore;
		
		mustHaveRelDoc = false;
		useSparseRepresentation = false;
		
		for(Object topicID:topicSubtopicPairFeatureVectors.keySet()) {
			
			featureVectors = (String) topicSubtopicPairFeatureVectors.get(topicID);
			topicSubtopicInitialRankedList = readInput(featureVectors, mustHaveRelDoc, useSparseRepresentation);

			topicSubtopicsLearnedRankingScore = (ArrayList) this.getSubtopicMachineLearnedRankScore(topicSubtopicInitialRankedList);
		
			topicSubtopicsLearningToRankScores.put(topicID, topicSubtopicsLearnedRankingScore);
		}
		return topicSubtopicsLearningToRankScores;
	}

	private ArrayList getSubtopicMachineLearnedRankScore(List<RankList> test) {
		
		ArrayList itemScore = new ArrayList();
		int i;
		int j;
		RankList l;
		double score;
		
		for( i = 0; i<test.size(); i++) {
			l = test.get(i);
			
			for(j = 0; j<l.size(); j++) {
				score = this.ranker.eval(l.get(j));
				itemScore.add(score);
			}
		}
		
		return itemScore;
	}

	private String getFeatureString(Vector featureVectors) {
		
		int i;
		int numFeature;
		StringBuffer featureString = new StringBuffer();
		String featureVectorString;
		
		numFeature = featureVectors.size();
		
		for(i = 0; i<numFeature; i++) 
		{
			featureString.append((i+1)+":"+featureVectors.get(i)+" ");
		}
		
		featureVectorString = featureString.toString().trim();
		return featureVectorString;
	}

	public static List<RankList> readInput(String testData, boolean mustHaveRelDoc, boolean useSparseRepresentation)
	{
		List<RankList> samples = new ArrayList<RankList>();
		
		int i;
		int n;
		int countEntries = 0;
		String[] lines = testData.split("\n");
		
		n = lines.length;
		
		try {
			
			String content = "";
			
			String lastID = "";
			boolean hasRel = false;
			List<DataPoint> rl = new ArrayList<DataPoint>();
			
			for(i = 0; i<n; i++)
			{
				content = lines[i];
				content = content.trim();
				
				if(content.length() == 0)
					continue;
				if(content.indexOf("#")==0)
					continue;
				
				DataPoint qp = null;
				if(useSparseRepresentation)
					qp = new SparseDataPoint(content);
				else
					qp = new DenseDataPoint(content);

				if(lastID.compareTo("")!=0 && lastID.compareTo(qp.getID())!=0)
				{
					if(!mustHaveRelDoc || hasRel)
						samples.add(new RankList(rl));
					rl = new ArrayList<DataPoint>();
					hasRel = false;
				}
				
				if(qp.getLabel() > 0)
					hasRel = true;
				lastID = qp.getID();
				rl.add(qp);
				countEntries++;
			}
			if(rl.size() > 0 && (!mustHaveRelDoc || hasRel))
				samples.add(new RankList(rl));
			System.out.println("(" + samples.size() + " ranked lists, " + countEntries + " entries read)");
		}
		catch(Exception ex)
		{
			System.out.println("Error in FeatureManager::readInput(): " + ex.toString());
			System.exit(1);
		}
		return samples;
	}
	
	private Map getTopicSubtopicPairRankingFeatureVectors(Map topicSubtopicFeatureVectors) {

		StringBuffer lines = new StringBuffer();

		this.topicSubtopicsList = new HashMap();
		Map topicSubtopicFeatureVector = new HashMap();
		Map subtopicFeatures;
		Vector featureVectors;
		String featureString;
		String subtopicsFeatureVectors;
		int i;
		int l;
		
		ArrayList subtopicList;
		
		for(Object topicID:topicSubtopicFeatureVectors.keySet()) {
			
			subtopicFeatures = (Map) topicSubtopicFeatureVectors.get(topicID);
//			System.out.println(topicID);
			lines.delete(0,  lines.length());
			
			i = 1;
			l = subtopicFeatures.size();
			
			subtopicList = new ArrayList();
			
			for(Object subtopicID:subtopicFeatures.keySet()) {
		
				subtopicList.add(subtopicID);
				featureVectors = (Vector) subtopicFeatures.get(subtopicID);
				featureString = this.getFeatureString(featureVectors);
				
				if(i != l)
					lines.append("0 0:"+topicID+" "+featureString+"\n");
//					lines.append(featureString+"\n");
				else 
					lines.append("0 0:"+topicID+" "+featureString);
//					lines.append(featureString);
				
				i++;
			}
		
			subtopicsFeatureVectors = lines.toString().trim();
			topicSubtopicFeatureVector.put(topicID, subtopicsFeatureVectors);
			
			this.topicSubtopicsList.put(topicID, subtopicList);
		}
		return topicSubtopicFeatureVector;
	}
	
	private void loadLearningToRankModel() {
		
		this.rFact = new RankerFactory();
		this.ranker = this.rFact.loadRanker("./src/data/ltr/ensemble.txt");
	}
	
	public static void main(String[] args) {
		
	}
}
