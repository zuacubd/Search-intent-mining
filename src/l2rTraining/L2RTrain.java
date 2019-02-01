package l2rTraining;

import java.util.Map;
import java.util.Vector;

import utility.FileUtils;
import utility.SimpleMath;
import dataPath.TrainingDataPath;

public class L2RTrain {

	private Map topicIDtopicString;
	private Map topicSubtopicRel;
	private Map topicSubtopicFeaturesVectors;
	
	private ExtractFeaturesVectors extractFeaturesVectors;
	
	public L2RTrain() {
		
		this.extractFeaturesVectors = new ExtractFeaturesVectors();
		this.topicSubtopicRel = this.extractFeaturesVectors.getTopicSubtopicRel();
		this.topicSubtopicFeaturesVectors = this.extractFeaturesVectors.getTopicSubtopicFeaturesVectors();
		this.topicIDtopicString = this.extractFeaturesVectors.getTopicIDTopicString();
		
		this.constructLearningToRankTrainingData();
	}
	
	public void constructLearningToRankTrainingData() {
		
		TrainingDataPath trainingDataPath = new TrainingDataPath();
		StringBuffer queryDocumentPair = new StringBuffer();
		
		Map subtopicsFeatureVector;
		Map subtopicsRel;
		int rel;
		int i;
		int n;
		int j;
		Vector featureVector;
		Double featureVal;
		String queryDocumentFeatureLine;
		
		queryDocumentFeatureLine = "";
		rel = 1;
		n = 5;

		//30 3241
		
		int totalSubtopic = 0;
		j  = 1;
		for(Object topicID:this.topicIDtopicString.keySet()) {
			
			subtopicsFeatureVector = (Map) this.topicSubtopicFeaturesVectors.get(topicID);
			subtopicsRel = (Map) this.topicSubtopicRel.get(topicID);
			
			totalSubtopic += subtopicsFeatureVector.size();
			if(j == 30) break;
			j++;
			for(Object subtopicID:subtopicsFeatureVector.keySet()) {
				
				queryDocumentPair.delete(0,  queryDocumentPair.length());

				featureVector = (Vector) subtopicsFeatureVector.get(subtopicID);
				rel = (Integer) subtopicsRel.get(subtopicID);

				queryDocumentPair.append(rel+" qid:"+topicID);
				
//				System.out.println(featureVector.size());
				for(i = 0; i<featureVector.size(); i++) {
					featureVal = (Double) featureVector.get(i);
					featureVal = SimpleMath.round(featureVal, n);
//					System.out.print(" "+(i+1)+":"+ featureVal);
					queryDocumentPair.append(" "+(i+1)+":"+ featureVal);
				}
				
				queryDocumentFeatureLine += queryDocumentPair.toString()+"\n";
			}
		}
		
		System.out.println(totalSubtopic);
//		FileUtils.write(trainingDataPath.getImineL2RTrainingDataPath(), "utf-8", queryDocumentFeatureLine);
	}
	
	public static void main(String[] args) {
		
		L2RTrain l2rTrain = new L2RTrain();
	}
}
