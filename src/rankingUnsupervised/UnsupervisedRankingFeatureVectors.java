package rankingUnsupervised;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utility.SortMapDouble;

public class UnsupervisedRankingFeatureVectors {

	private Map topicSubtopicsWeight;
	private Double[] learnedParameters;
	
	private SortMapDouble sortMapDouble;
	private LearningAggregateRankers learningAggregateRankers;
	private LearningSubtopicRanking learningSubtopicRanking;
	
	public Map getTopicSubtopicsUnsupervisedRankingWeight() {
		return this.topicSubtopicsWeight;
	}
	
	public UnsupervisedRankingFeatureVectors(Map topicSubtopicsFeaturesVectors, Integer numFeatures) {

		int[] rankerThreshold = this.getAggregateRankersThreshold(numFeatures);
		
		this.learningAggregateRankers = new LearningAggregateRankers(topicSubtopicsFeaturesVectors, numFeatures, rankerThreshold);
		this.learnedParameters = this.learningAggregateRankers.getLearningParameters();

		this.showLearnedParameters(learnedParameters);

		this.learningSubtopicRanking = new LearningSubtopicRanking(topicSubtopicsFeaturesVectors, numFeatures, this.learnedParameters, rankerThreshold);
		this.topicSubtopicsWeight = this.learningSubtopicRanking.getTopicLearnedSubtopicWeight();
		
//		this.topicSubtopicsWeight = this.getTopicSubtopicsRankingFeature(topicSubtopicsFeaturesVectors);
//		this.topicSubtopicsWeight = this.getTopicSubtopicsRecipocalRankFusionFeature(topicSubtopicsFeaturesVectors);
	}
	
	private int[] getAggregateRankersThreshold(int numFeatures) {
		
		int[] k = new int[numFeatures];
		
		//Query independent features
		k[0] = 15; k[1] = 18; k[2] = 7; k[3] = 2; k[4] = 1;
		
		//Query dependent features
		k[5] = 19; k[6] = 14; k[7] = 15; k[8] = 15;
		
		k[9] = 15; k[10] = 15; k[11] = 16; k[12] = 20;
		
		k[13] = 15; k[14] = 16; k[15] = 15; k[16] = 15; 
		
		k[17] = 15; k[18] = 15; 

		k[19] = 15; 
		k[20] = 20; 
		
		k[21] = 15; k[22] = 19; k[23] = 15;

		return k;
	}

	private void showLearnedParameters(Double[] learnedParameters) {
		
		for(int i = 0; i<learnedParameters.length; i++) {
			System.out.println(i + " : "+ learnedParameters[i]);
		}
	}
	
	private Map getTopicSubtopicsRecipocalRankFusionFeature(Map topicSubtopicsFeatureVectors) {
		
		Map topicSubtopicsRecipocalRankFusionWeight = new HashMap();
		Map subtopicsRecipocalRankFusionWeight;
		
		Map subtopicsFeatureVector;
		
		Vector votingFeatureRankList;  //Voting (good feature)
		Vector rrFeatureRankList; //RR (not good)
		Vector semEditDistanceRankList; //SED (not good)
		Vector vsmRankList; //VSM (not good)
		Vector tlmRankList; //TLM (not good)
		Vector bm25RankList; //BM25 (not good)
		Vector dfrRankList; //DFR (good feature)
		Vector atlRankList; //ATL (not good)
		Vector tdmrfRankList; //TDMRF 
		Vector ucRankList;
		
		int featureNum;
		
		for(Object topicID:topicSubtopicsFeatureVectors.keySet()) {
			subtopicsFeatureVector = (Map) topicSubtopicsFeatureVectors.get(topicID);
			
			featureNum = 0;
			votingFeatureRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 1;
			rrFeatureRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 2;
			semEditDistanceRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 3;
			vsmRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 4;
			tlmRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 5;
			bm25RankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 6;
			dfrRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 7;
			atlRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 8;
			tdmrfRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
			
			featureNum = 9;
			ucRankList = this.getSubtopicRankList(subtopicsFeatureVector, featureNum);
		
			subtopicsRecipocalRankFusionWeight = this.getSubtopicRecipocalRankFusionWeight(subtopicsFeatureVector, 
					votingFeatureRankList, rrFeatureRankList, semEditDistanceRankList, vsmRankList, tlmRankList, 
					bm25RankList, dfrRankList, atlRankList, tdmrfRankList, ucRankList);
		
			topicSubtopicsRecipocalRankFusionWeight.put(topicID, subtopicsRecipocalRankFusionWeight);
		}
		return topicSubtopicsRecipocalRankFusionWeight;
	}
	
	private Map getSubtopicRecipocalRankFusionWeight(
			Map subtopicsFeatureVector, Vector votingFeatureRankList, Vector rrFeatureRankList,
			Vector semEditDistanceRankList, Vector vsmRankList,
			Vector tlmRankList, Vector bm25RankList, Vector dfrRankList,
			Vector atlRankList, Vector tdmrfRankList, Vector ucRankList) {
		
		Map subtopicsRRFScore = new HashMap();
		int[] r = new int[10];
		Double recipocalRankFusion = new Double(0.0);
		
		int i;
		int l;
		int k;
		
		l = 10; //no. of features
		k = 1;
		for(Object subtopicID:subtopicsFeatureVector.keySet()) {
			
			r[0] = votingFeatureRankList.indexOf(subtopicID);  //Voting (good feature)
			r[1] = rrFeatureRankList.indexOf(subtopicID); //RR (not good)
			r[2] = semEditDistanceRankList.indexOf(subtopicID); //SED (not good)
			r[3] = vsmRankList.indexOf(subtopicID); //VSM (not good)
			r[4] = tlmRankList.indexOf(subtopicID); //TLM (not good)
			r[5] = bm25RankList.indexOf(subtopicID); //BM25 (not good)
			r[6] = dfrRankList.indexOf(subtopicID); //DFR (good feature)
			r[7] = atlRankList.indexOf(subtopicID); //ATL (not good)
			r[8] = tdmrfRankList.indexOf(subtopicID); //TDMRF 
			r[9] = ucRankList.indexOf(subtopicID);
			
			recipocalRankFusion = 0.0;
			for(i = 0; i<l; i++) {
				recipocalRankFusion += 1.0/(r[i] + k);
			}
			subtopicsRRFScore.put(subtopicID, recipocalRankFusion);
		}
		
		return subtopicsRRFScore;
	}

	private Vector getSubtopicRankList(Map subtopicsFeatureVector,
			int featureNum) {

		Vector features;
		Vector subtopicRankList;
		Map subtopicFeatureWeight = new HashMap();
		Double featureVal;
		int k;
		
		for(Object subtopicID:subtopicsFeatureVector.keySet()) {
			
			features = (Vector) subtopicsFeatureVector.get(subtopicID);
			featureVal = (Double) features.get(featureNum);
			subtopicFeatureWeight.put(subtopicID, featureVal);
		}
		
		this.sortMapDouble = new SortMapDouble();
		k = subtopicFeatureWeight.size();
		subtopicRankList =  this.sortMapDouble.getTopKKey(subtopicFeatureWeight, k);
		
		return subtopicRankList;
	}

	private Map getTopicSubtopicsRankingFeature(Map topicSubtopicsFeaturesVectors) {
		
		Map topicSubtopicsRankingFeatureWeight = new HashMap();
		Map subtopicsFeaturesVectors;
		Map subtopicFeatureWeight;
		Vector featureVector;
		
		Vector theta = new Vector();
		theta.add(1.0); //Voting (good feature)
		theta.add(0.5); //RR (not good)
		theta.add(0.5); //SED (not good)
		theta.add(0.5); //VSM (not good)
		theta.add(0.5); //TLM (not good)
		theta.add(0.5); //BM25 (not good)
		theta.add(1.0); //DFR (good feature)
		theta.add(0.5); //ATL (not good)
		theta.add(1.0); //TDMRF 
		theta.add(0.5); //Uppercase
		
		Double featureWeight;
		
		for(Object topicID:topicSubtopicsFeaturesVectors.keySet()) {
			
			subtopicsFeaturesVectors = (Map) topicSubtopicsFeaturesVectors.get(topicID);
			
			subtopicFeatureWeight = new HashMap();
			for(Object subtopicID:subtopicsFeaturesVectors.keySet()) {
				
				featureVector = (Vector) subtopicsFeaturesVectors.get(subtopicID);
//				featureWeight = this.getMean(featureVector);
				featureWeight = this.getWeightedMean(featureVector, theta);
				
				subtopicFeatureWeight.put(subtopicID, featureWeight);
			}
			topicSubtopicsRankingFeatureWeight.put(topicID, subtopicFeatureWeight);
		}
		
		return topicSubtopicsRankingFeatureWeight;
	}

	private Double getMean(Vector featureVector) {
		
		Double mean = new Double(0.0);
		Double sum = new Double(0.0);
		Double featureValue;
		int i;
		int l;
		
		sum = 0.0;
		l = featureVector.size();
		for(i = 0; i<l; i++) {
			featureValue = (Double) featureVector.get(i);
			sum += featureValue;
		}
		
		mean = sum.doubleValue()/l;
		
		return mean;
	}
	
	private Double getWeightedMean(Vector featureVector, Vector theta) {
		
		Double weightedMean = new Double(0.0);
		Double sumProduct = new Double(0.0);
		Double sumTheta = new Double(0.0);
		Double featureValue;
		Double featureTheta;
		int i;
		int l;
		
		sumProduct = 0.0;
		l = featureVector.size();
		
		for(i = 0; i<l; i++) {
			featureValue = (Double) featureVector.get(i);
			featureTheta = (Double) theta.get(i);
			sumProduct += featureValue * featureTheta;
			sumTheta += featureTheta;
		}
		
		weightedMean = sumProduct.doubleValue()/sumTheta.doubleValue();
		
		return weightedMean;
	}
}

