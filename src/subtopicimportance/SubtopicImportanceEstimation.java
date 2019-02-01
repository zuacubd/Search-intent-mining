package subtopicimportance;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SubtopicImportanceEstimation {

	private Map topicSubtopicsFeaturesVectors;
	private Map topicSubtopicsImportanceWeight;
	private Integer numFeatures;

	public Map getTopicSubtopicsImportanceWeight() { return this.topicSubtopicsImportanceWeight;}
	
	public SubtopicImportanceEstimation(Map topicSubtopicsFeaturesVectors, Integer numFeatures) {
		this.topicSubtopicsFeaturesVectors = topicSubtopicsFeaturesVectors;
		this.numFeatures = numFeatures;
		
//		this.topicSubtopicsImportanceWeight = this.estimateWeightedAverageBasedTopicSubtopicImportanceWeights(this.topicSubtopicsFeaturesVectors, this.numFeatures);
		this.topicSubtopicsImportanceWeight = this.estimateMeanVectorBasedTopicSubtopicImportanceWeights(this.topicSubtopicsFeaturesVectors, this.numFeatures);
//		this.topicSubtopicsImportanceWeight = this.getNormalizedTopicSubtopicWeights(topicSubtopicsImportanceWeight);
	}
	
	private Map estimateWeightedAverageBasedTopicSubtopicImportanceWeights(Map topicSubtopicsFeaturesVectors, Integer numFeatures) {
		
		Map topicSubtopicsWeights = new HashMap();
		Map subtopicsFeaturesVectors;
		Map subtopicsWeights;
		Vector featureVector;
		Double subtopicWeight;
		Double featureVal;
		Double param;
		int i;
		int n;
		int[] k = new int[numFeatures];
		
		n = this.numFeatures.intValue();
				
		//Query independent features
		k[0] = 15; k[1] = 18; k[2] = 7; k[3] = 2; k[4] = 1;
		
		//Query dependent features
		k[5] = 19; k[6] = 14; k[7] = 15; k[8] = 15;
		k[9] = 15; k[10] = 15; k[11] = 16; k[12] = 20;
		k[13] = 15; k[14] = 16; k[15] = 15; k[16] = 15; 
		k[17] = 15; k[18] = 15; k[19] = 15; k[20] = 20; 
		k[21] = 15; k[22] = 19; k[23] = 15;
		
		param = 0.0;
		for(i = 0; i<n; i++) {
			param += k[i];
		}
		
		for(Object topicId:topicSubtopicsFeaturesVectors.keySet()) {
			
			subtopicsFeaturesVectors = (Map) topicSubtopicsFeaturesVectors.get(topicId);
			subtopicsWeights = new HashMap();
			
			for(Object subtopicId:subtopicsFeaturesVectors.keySet()) {
				featureVector = (Vector) subtopicsFeaturesVectors.get(subtopicId);
				
				subtopicWeight = 0.0;
				for(i = 0; i<n; i++) {
					featureVal = (Double) featureVector.get(i);
					subtopicWeight += k[i] * featureVal;
				}
				subtopicWeight = subtopicWeight/param;
				subtopicsWeights.put(subtopicId, subtopicWeight);
			}
			topicSubtopicsWeights.put(topicId, subtopicsWeights);
			System.out.println(topicId +" : "+ subtopicsWeights);
		}
		
		return topicSubtopicsWeights;
	}

	private Map estimateMeanVectorBasedTopicSubtopicImportanceWeights(Map topicSubtopicsFeaturesVectors, Integer numFeatures) {
		
		Map topicSubtopicsWeights = new HashMap();
		Map subtopicsFeaturesVectors;
		Map subtopicsWeights;
		Vector featureVector;
		Vector meanVector;
		Double sumWeight;
		Double featureVal;
		Double param;
		Double cosineSim;
		int i;
		int n;
		Integer numSubtopics;
		
		n = this.numFeatures.intValue();
		meanVector = new Vector();
		for(i = 0; i<n; i++){
			meanVector.add(0);
		}
		//Query independent features
		
		for(Object topicId:topicSubtopicsFeaturesVectors.keySet()) {
			
			subtopicsFeaturesVectors = (Map) topicSubtopicsFeaturesVectors.get(topicId);
			subtopicsWeights = new HashMap();

			for(i = 0; i<n; i++){
				meanVector.set(i, (Double) 0.0);
			}

			for(Object subtopicId:subtopicsFeaturesVectors.keySet()) {
				featureVector = (Vector) subtopicsFeaturesVectors.get(subtopicId);
				
				for(i = 0; i<n; i++) {
					featureVal = (Double) featureVector.get(i);
					sumWeight = (Double) meanVector.get(i);
					sumWeight += featureVal;
					meanVector.set(i, sumWeight);
				}
			}
			
			numSubtopics = subtopicsFeaturesVectors.size();
			
			for(i = 0; i<n; i++){
				sumWeight = (Double) meanVector.get(i);
				sumWeight = sumWeight/numSubtopics.doubleValue();
				meanVector.set(i, sumWeight);
			}
			
			for(Object subtopicId:subtopicsFeaturesVectors.keySet()) {
				featureVector = (Vector) subtopicsFeaturesVectors.get(subtopicId);
				
				cosineSim = this.getCosineSimilarity(featureVector, meanVector);
				subtopicsWeights.put(subtopicId, cosineSim);
			}
			
			topicSubtopicsWeights.put(topicId, subtopicsWeights);
			System.out.println(topicId +" : "+ subtopicsWeights);
		}
		
		return topicSubtopicsWeights;
	}
	
	private Double getCosineSimilarity(Vector featureVector, Vector meanVector) {
	
		int i;
		int n;
		Double numerator;
		Double sumXX;
		Double sumYY;
		Double x;
		Double y;
		Double cosineSim;
		
		n = featureVector.size();
		
		numerator = 0.0;
		sumXX = 0.0;
		sumYY = 0.0;
		
		for(i = 0; i<n; i++){
			x = (Double) featureVector.get(i);
			y = (Double) meanVector.get(i);
			
			numerator += x*y;
			sumXX += x*x; 
			sumYY += y*y;
		}
		
		sumXX = Math.sqrt(sumXX);
		sumYY = Math.sqrt(sumYY);
		
		cosineSim = numerator/sumXX;
		cosineSim = cosineSim/sumYY;
		
		return cosineSim;
	}

	private Map getNormalizedTopicSubtopicWeights(Map topicSubtopicsWeights) {
		
		Map topicSubtopicsNormalizedWeights = new HashMap(); 
		Map subtopicsWeights;
		Map subtopicsNormalizedWeight;
		Double max;
		Double subtopicWeight;
		
		for(Object topicId:topicSubtopicsWeights.keySet()) {
			
			subtopicsWeights = (Map) topicSubtopicsWeights.get(topicId);
			
			subtopicsNormalizedWeight = new HashMap();
			max = 0.0;
			for(Object subtopicId:subtopicsWeights.keySet()) {
				
				subtopicWeight = (Double) subtopicsWeights.get(subtopicId);
				if(max<subtopicWeight) max = subtopicWeight;
			}
		
			for(Object subtopicId:subtopicsWeights.keySet()) {
				
				subtopicWeight = (Double) subtopicsWeights.get(subtopicId);
				subtopicWeight = subtopicWeight/max;
				subtopicsNormalizedWeight.put(subtopicId, subtopicWeight);
			}
			topicSubtopicsNormalizedWeights.put(topicId, subtopicsNormalizedWeight);
		}
		
		return topicSubtopicsNormalizedWeights;
	}
	
	public static void main(String[] args) {
		
	}
}
