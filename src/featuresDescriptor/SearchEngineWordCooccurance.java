package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class SearchEngineWordCooccurance {

	Map subtopicWordCooccurance;
	
	public Map getSubtopicWordCooccurance() {
		return this.subtopicWordCooccurance;
	}
	
	public SearchEngineWordCooccurance(Map topicSubtopicHitCount, String topicString, Map subtopics) {
		
		this.subtopicWordCooccurance = this.getEstimatedSearchEngineWordCooccurance(topicString, subtopics, topicSubtopicHitCount);
	}
	
	private Map getEstimatedSearchEngineWordCooccurance(String topicString, Map subtopics, Map topicSubtopicHitCount) {
		
		Map subtopicsWordCooccurance = new HashMap();
		
		Double subtopicHitCount = new Double(0.0);
		Double topicHitCount = new Double(0.0);
		Double wco;
		
		topicHitCount = (Double) topicSubtopicHitCount.get(topicString);
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicHitCount = (Double) topicSubtopicHitCount.get(subtopic);
			
			wco = this.getEstimatedWordCooccurance(topicHitCount, subtopicHitCount);
			subtopicsWordCooccurance.put(subtopic, wco);
		}
		return subtopicsWordCooccurance;
	}

	private Double getEstimatedWordCooccurance(Double topicHitCount,
			Double subtopicHitCount) {

		Double wco = new Double(0.0);
		
		wco = subtopicHitCount.doubleValue() / ( topicHitCount);
		
		return wco;
	}
}