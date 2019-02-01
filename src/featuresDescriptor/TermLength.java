package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class TermLength {
	
	private Map subtopicLength;

	public Map getSubtopicLength() {
		return this.subtopicLength;
	}

	public TermLength( String topicString, Map subtopics) {
		this.subtopicLength = this.getEstimatedSubtopicLengthWeight(topicString, subtopics);
	}
	
	private Map getEstimatedSubtopicLengthWeight(String topicString, Map subtopics) {
		
		Map subtopicATLFeatureWeight = new HashMap();
		Double lengthFeatureWeight;
		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			lengthFeatureWeight = this.getSubtopicLengthFeatureWeight(topicString, subtopicString);
			subtopicATLFeatureWeight.put(subtopic, lengthFeatureWeight);
		}
		
		return subtopicATLFeatureWeight;
		}
	
	private Double getSubtopicLengthFeatureWeight(String topicString, String subtopicString) {

		Double subtopicWeight;
		Integer length;
		String[] subtopicTerms;
		
		subtopicTerms = subtopicString.split(" ");
		
		length = subtopicTerms.length;
		subtopicWeight = length.doubleValue();
		
		return subtopicWeight;
	}

}
