package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class SearchEngineHitCount {

	Map subtopicHitCount;
	
	public Map getSubtopicHitCount() {
		return this.subtopicHitCount;
	}
	
	public SearchEngineHitCount(Map topicSubtopicHitCount, String topicString, Map subtopics) {
		
		this.subtopicHitCount = this.getSearchEngineHitCount(topicString, subtopics, topicSubtopicHitCount);
	}
	
	private Map getSearchEngineHitCount(String topicString, Map subtopics, Map topicSubtopicHitCount) {
		
		Map subtopicHitCount = new HashMap();
		Double hitCount = new Double(0.0);
		
		for(Object subtopic:subtopics.keySet()) {
			
			hitCount = (Double) topicSubtopicHitCount.get(subtopic);
			subtopicHitCount.put(subtopic, hitCount);
		}
		
		return subtopicHitCount;
	}
}

