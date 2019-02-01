package rankingUnsupervised;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utility.SortMapDouble;

public class LearningAggregateRankers {

	private Double[] learningParameters;
	
	public Double[] getLearningParameters() {
		return this.learningParameters;
	}
	
	private SortMapDouble sortMapDouble;
	
	public LearningAggregateRankers(Map topicSubtopicsFeatureVectors, Integer numRankers, int[] rankerThreshold) {
		
		this.learningParameters = this.getAdditiveLearningOptimalParameters(topicSubtopicsFeatureVectors, numRankers, rankerThreshold);
//		this.learningParameters = this.getExponentialLearningOptimalParameters(topicSubtopicsFeatureVectors, numRankers, rankerThreshold);
		
	}
	
	private Double[] getExponentialLearningOptimalParameters(Map topicSubtopicsFeatureVectors, Integer numFeatures, int[] k) {

		Map subtopicsFeatureVectors;
		Integer Nx = new Integer(0);
		Integer numRankers = new Integer(0);
		Integer theta = new Integer(0);
		Double lemda = new Double(0);
		Double[] W = new Double[numFeatures]; 
		Double subtopicMeanRank = new Double(0.0);
		Double gradient = new Double(0.0);
		Double numeratorW = new Double(0.0);
		Double denominatorW = new Double(0.0);
		
		int i;
		int t;
		int j;
		
		numRankers = numFeatures;
		
		lemda = Math.pow(numFeatures.intValue(), -2); //Learning rate
		
		theta = 6; //number of valid ranked (co-operated rankers)
		
		for(i = 0; i<numRankers; i++) {
			W[i] = 1.0/numRankers.doubleValue();
		}

		t = 1;

		for(Object topicID:topicSubtopicsFeatureVectors.keySet())
		{
			subtopicsFeatureVectors = (Map) topicSubtopicsFeatureVectors.get(topicID);
			Map rankingFunctionRankedSubtopicList = this.getIndividualFeatureBasedSubtopicsRankList(subtopicsFeatureVectors, numRankers);
			
			for(Object subtopicID:subtopicsFeatureVectors.keySet()) 
			{
				Nx = this.getNumberOFValidRankingFuntions(subtopicID, rankingFunctionRankedSubtopicList, k, numRankers);
			
				if(Nx >= theta) 
				{
					subtopicMeanRank = this.getSubtopicMeanRanker(subtopicID, rankingFunctionRankedSubtopicList, k, numRankers);
					
					for(i = 0; i<numRankers.intValue(); i++) 
					{
						Vector rankedSubtopicList = (Vector) rankingFunctionRankedSubtopicList.get(i);
						int subtopicRank = rankedSubtopicList.indexOf(subtopicID);
						subtopicRank = subtopicRank + 1;

						if(subtopicRank <= k[i]) 
						{
							gradient = (subtopicRank - subtopicMeanRank) * (subtopicRank - subtopicMeanRank);
						}
						else 
						{
							gradient = (k[i] + 1.0 - subtopicMeanRank) * (k[i] + 1.0 - subtopicMeanRank);
						}
						
						numeratorW = W[i] * Math.pow( Math.E, ((-1) * lemda.doubleValue() * gradient.doubleValue()) );

						denominatorW = 0.0;
						for(j = 0; j<numRankers.intValue(); j++) {
							denominatorW += W[j] * Math.pow(Math.E, ((-1) * lemda.doubleValue() * gradient.doubleValue())); 
						}
						
						W[i] = numeratorW.doubleValue()/denominatorW.doubleValue();
					}
					t = t + 1;
				}
			}
		}
		
		return W;
	}

	private Double[] getAdditiveLearningOptimalParameters(Map topicSubtopicsFeatureVectors, Integer numFeatures, int[] k) {

		Map subtopicsFeatureVectors;
		
		Integer Nx;
		Integer numRankers;
		Integer theta;
		
		Double lemda;
		Double[] W = new Double[numFeatures]; 
		Double subtopicMeanRank = new Double(0.0);
		Double gradient;
		int subtopicRank;
		
		int i;
		int t;
		
		numRankers = numFeatures;
		lemda = 1.0;
		theta = 6; //number of valid ranked (co-operated rankers)
		
		for(i = 0; i<numRankers; i++) {
			W[i] = 1/numRankers.doubleValue();
		}

		t = 1;

		for(Object topicID:topicSubtopicsFeatureVectors.keySet())
		{
			subtopicsFeatureVectors = (Map) topicSubtopicsFeatureVectors.get(topicID);
			Map rankingFunctionRankedSubtopicList = this.getIndividualFeatureBasedSubtopicsRankList(subtopicsFeatureVectors, numRankers);
			
			for(Object subtopicID:subtopicsFeatureVectors.keySet()) 
			{
				Nx = this.getNumberOFValidRankingFuntions(subtopicID, rankingFunctionRankedSubtopicList, k, numRankers);
			
				if(Nx >= theta) 
				{
					subtopicMeanRank = this.getSubtopicMeanRanker(subtopicID, rankingFunctionRankedSubtopicList, k, numRankers);
					
					for(i = 0; i<numRankers.intValue(); i++) 
					{
						Vector rankedSubtopicList = (Vector) rankingFunctionRankedSubtopicList.get(i);
						subtopicRank = rankedSubtopicList.indexOf(subtopicID);
						subtopicRank = subtopicRank + 1;
						
						if(subtopicRank <= k[i]) 
						{
							gradient = (subtopicRank - subtopicMeanRank);
							gradient = Math.pow(gradient, 2);
//							gradient =  gradient/2.0;
							gradient = gradient/Math.sqrt(W[i]);
						}
						else 
						{
							gradient = (k[i] + 1.0 - subtopicMeanRank);
							gradient = Math.pow(gradient, 2);
//							gradient =  gradient/2.0;
							gradient = gradient/Math.sqrt(W[i]);
						}
						
						System.out.println(t + " : "+W[i] + " : "+ gradient);
						
						W[i] = W[i] + lemda.doubleValue() * gradient.doubleValue();
					}
					t = t + 1;
				}
			}
		}
		
		//Normalized W
		System.out.println("total valid steps :" + t);
		W = this.getNormalizedParameters(W, numRankers);
		return W;
	}
	
	private Double[] getNormalizedParameters(Double[] w, Integer numRankers) {

		Double sum = new Double(0.0);
		int i;
		
		sum = 0.0;
		for(i = 0; i<numRankers.intValue(); i++) {
			sum += w[i];
		}
		
		for(i = 0; i<numRankers.intValue(); i++) {
			w[i] = w[i].doubleValue()/sum.doubleValue();
		}
		
		return w;
	}

	private Double getSubtopicMeanRanker(Object subtopicID,
			Map rankingFunctionRankedSubtopicList, int[] k, Integer numRankers) {
		
		Vector subtopicRankedList;
		Integer Nx = new Integer(0);
		Integer sumSubtopicRank = new Integer(0);
		Double meanSubtopicRank;
		
		int i;
		int subtopicRank;
		
		Nx = 0;
		sumSubtopicRank = 0;
		
		for(i = 0; i<numRankers.intValue(); i++) {
		
			subtopicRankedList = (Vector) rankingFunctionRankedSubtopicList.get(i);
			
			subtopicRank = subtopicRankedList.indexOf(subtopicID);
			subtopicRank = subtopicRank + 1;
			
			if(subtopicRank <= k[i]) {
				Nx = Nx + 1;
				sumSubtopicRank = sumSubtopicRank + subtopicRank;
			}
		}
		
		meanSubtopicRank = sumSubtopicRank.doubleValue()/Nx.doubleValue();
		
		return meanSubtopicRank;
	}

	private Integer getNumberOFValidRankingFuntions( Object subtopicID,
			Map rankingFunctionRankedSubtopicList, int[] k, Integer numRankers) {
	
		Vector subtopicRankedList;
		Integer Nx = new Integer(0);
		int i;
		int subtopicRank;
		
		Nx = 0;
		for(i = 0; i<numRankers.intValue(); i++) {
		
			subtopicRankedList = (Vector) rankingFunctionRankedSubtopicList.get(i);
			
			subtopicRank = subtopicRankedList.indexOf(subtopicID);
			subtopicRank = subtopicRank + 1;
			
			if(subtopicRank <= k[i])
				Nx = Nx + 1;
		}
		
		return Nx;
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
