package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class SubStringMatch {
	
	Map subtopicExactMatch;
	
	public Map getSubtopicSubStringMatch() {
		return this.subtopicExactMatch;
	}
	
	public SubStringMatch(String topicString, Map subtopics) {
		
		this.subtopicExactMatch = this.getEstimatedSubtopicExactMatch(topicString, subtopics);
	}
	
	private Map getEstimatedSubtopicExactMatch(String topicString, Map subtopics) {
		
		Map subtopicExactMatch = new HashMap();
		String subtopicString;
		
		Double match;
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicString = subtopic.toString();
			
			match = this.isTopicSubStringOfSubtopic(topicString, subtopicString);
			
			subtopicExactMatch.put(subtopic, match);
		}
		
		return subtopicExactMatch;
	}

	private Double isTopicSubStringOfSubtopic(String topicString,
			String subtopicString) {
		
		int match;
		Double one = new Double(1.0);
		Double zero = new Double(0.0);
		
		match = subtopicString.indexOf(topicString);
		
		if(match >= 0)
			return one;
		else return zero;
	}
	
}
