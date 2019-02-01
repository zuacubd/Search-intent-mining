package l2rTraining;

import java.util.Map;

import dataLoader.Intent2TopicLoader;
import dataLoader.ParseSearchEngineResultPage;
import dataPath.TrainingDataPath;
import featureExtraction.FeatureExtraction;

public class ExtractFeaturesVectors {

	private Map topicIDtopicString;
	private Map topicSubtopicRel; 
	private Map topicSubtopicFeatureVecotrs;
	private Map topicSubtopicHitCount;
	
	private TrainingDataPath trainingDataPath;
	private ParseRelevanceJudgementDataFile parseRelevanceJudgementDataFile;
	private Intent2TopicLoader intent2TopicLoader;
	private FeatureExtraction featureExtraction;
	private ParseSearchEngineResultPage parseSearchEngineResultPage;
	
	public Map getTopicSubtopicFeaturesVectors() {
		return this.topicSubtopicFeatureVecotrs;
	}
	
	public Map getTopicSubtopicRel() {
		return this.topicSubtopicRel;
	}
	
	public Map getTopicIDTopicString() {
		return this.topicIDtopicString;
	}
	
	public ExtractFeaturesVectors() {
		
		this.trainingDataPath = new TrainingDataPath();
		this.parseRelevanceJudgementDataFile = new ParseRelevanceJudgementDataFile();
		this.topicSubtopicRel = this.parseRelevanceJudgementDataFile.getTopicSubtopicRel();
		
		this.intent2TopicLoader = new Intent2TopicLoader(this.trainingDataPath.getIntent2TopicsPath());
		this.topicIDtopicString = this.intent2TopicLoader.getIntent2TopicIDtopicString();
	
		this.parseSearchEngineResultPage = new ParseSearchEngineResultPage(this.trainingDataPath.getTopicSubtopicSearchEngineResultPage());
		this.topicSubtopicHitCount = this.parseSearchEngineResultPage.getTopicSubtopicSEHitCount();
		
		this.featureExtraction = new FeatureExtraction(this.topicIDtopicString, this.topicSubtopicRel, this.topicSubtopicHitCount);
		this.topicSubtopicFeatureVecotrs = this.featureExtraction.getTopicSubtopicFeaturesVectors();
	}

	public static void main(String[] args) {
		
		ExtractFeaturesVectors extractFeaturesVectors = new ExtractFeaturesVectors();
	}
}
