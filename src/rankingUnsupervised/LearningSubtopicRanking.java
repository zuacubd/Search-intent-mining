package rankingUnsupervised;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utility.SortMapDouble;

public class LearningSubtopicRanking {

	private Map topicSubtopicsWeight;
	
	public Map getTopicLearnedSubtopicWeight() {
		return this.topicSubtopicsWeight;
	}
	
	private SortMapDouble sortMapDouble;
	
	public LearningSubtopicRanking(Map topicSubtopicsFeatureVectors, Integer numRankers, Double[] W, int[] rankerThreshold) {
		
		this.topicSubtopicsWeight = this.getOptimalRankedSubtopics(topicSubtopicsFeatureVectors, numRankers, W, rankerThreshold);
	}

	private Map getOptimalRankedSubtopics(Map topicSubtopicsFeatureVectors, Integer numFeatures, Double[] W, int[] k) {

		Map topicOptimalRankedSubtopicsList = new HashMap();
		Map optimalSubtopicRankList;
		Vector subtopoicRankList;
		int subtopicRank;
		Map subtopicsFeatureVectors;
		Integer numRankers;
		Double Rx;
		Double subtopicWeight;
		
		int i;
		numRankers = numFeatures;
//		System.out.println("Num of rankers :" + numRankers);
		for(Object topicID:topicSubtopicsFeatureVectors.keySet())
		{
			subtopicsFeatureVectors = (Map) topicSubtopicsFeatureVectors.get(topicID);
			Map rankingFunctionRankedSubtopicList = this.getIndividualFeatureBasedSubtopicsRankList(subtopicsFeatureVectors, numRankers);
			
			optimalSubtopicRankList = new HashMap();
	
			for(Object subtopicID:subtopicsFeatureVectors.keySet()) 
			{
				Rx = 0.0;
				
				for(i = 0; i<numRankers.intValue(); i++) {
					
					subtopoicRankList = (Vector) rankingFunctionRankedSubtopicList.get(i);
					subtopicRank = subtopoicRankList.indexOf(subtopicID);
					subtopicRank = subtopicRank + 1;
					
					if(subtopicRank <= k[i])
						Rx = Rx + W[i].doubleValue() * subtopicRank;
					else 
						Rx = Rx + W[i].doubleValue() * (k[i] + 1.0);
				}
//				if(Rx == 0.0) Rx = 10000.0;

				subtopicWeight = 1.0/Rx.doubleValue();
//				subtopicWeight = Rx;
				
				optimalSubtopicRankList.put(subtopicID, subtopicWeight);
			}
			
			optimalSubtopicRankList = this.getNormalizedData(optimalSubtopicRankList);
//			System.out.println(topicID +" : " +optimalSubtopicRankList);
			
			topicOptimalRankedSubtopicsList.put(topicID, optimalSubtopicRankList);
		}
		return topicOptimalRankedSubtopicsList;
	}
	
	private Map getNormalizedData(Map data) {

		Map normalizedData = new HashMap();
		Double weight;
		Double sum;
		Double normF1;
		Double max;
		Double min;
		
		sum = 0.0;
		max = 0.0;
		min = 10.0;
		
		for(Object itemKey:data.keySet()) {
			
			weight = (Double) data.get(itemKey);
//			sum += weight;
			if(max<weight)
				max = weight;
			if(min>weight)
				min = weight;
		}
		
		for(Object itemKey:data.keySet()) {
			
			weight = (Double) data.get(itemKey);
//			normWeight = weight/sum;
			
			normF1 = (weight - min)/(max - min);
			normalizedData.put(itemKey, normF1);
		}
		
		return normalizedData;
	}

	private Map getIndividualFeatureBasedSubtopicsRankList(Map subtopicsFeatureVectors, Integer numRankers) {
		
		Map rankingFunctionRankedSubtopicList = new HashMap();
		Map subtopicFeatureWeight;
		
		Double featureValue;
		Vector featureVector;
		Vector rankedSubtopicList;
		
		int i;
		int k;
		
		for(i = 0; i<numRankers.intValue(); i++) {
			
			subtopicFeatureWeight = new HashMap();
			
			for(Object subtopicID:subtopicsFeatureVectors.keySet()) {
				
				featureVector = (Vector) subtopicsFeatureVectors.get(subtopicID);
				featureValue = (Double) featureVector.get(i);
				
				subtopicFeatureWeight.put(subtopicID, featureValue);
			}
			 
			this.sortMapDouble = new SortMapDouble();
			k = subtopicFeatureWeight.size();
			rankedSubtopicList = this.sortMapDouble.getTopKKey(subtopicFeatureWeight, k);
			
			rankingFunctionRankedSubtopicList.put(i, rankedSubtopicList);
		}
		
		return rankingFunctionRankedSubtopicList;
	}

}
