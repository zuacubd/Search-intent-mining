package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class SearchEnginePMI {

	Map subtopicPMI;
	
	public Map getSubtopicPMI() {
		return this.subtopicPMI;
	}
	
	public SearchEnginePMI(Map topicSubtopicHitCount, String topicString, Map subtopics) {
		
		this.subtopicPMI = this.getEstimatedSearchEnginePMI(topicString, subtopics, topicSubtopicHitCount);
	}
	
	private Map getEstimatedSearchEnginePMI(String topicString, Map subtopics, Map topicSubtopicHitCount) {
		
		Map subtopicsPMI = new HashMap();
		
		Double subtopicHitCount = new Double(0.0);
		Double topicHitCount = new Double(0.0);
		Double pmi;
		
		topicHitCount = (Double) topicSubtopicHitCount.get(topicString);
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicHitCount = (Double) topicSubtopicHitCount.get(subtopic);
			
			pmi = this.getEstimatedPointWiseMutualInformation(topicHitCount, subtopicHitCount);
			subtopicsPMI.put(subtopic, pmi);
		}
		return subtopicsPMI;
	}

	private Double getEstimatedPointWiseMutualInformation(Double topicHitCount,
			Double subtopicHitCount) {

		Double pmi = new Double(0.0);
		Double st;
		
		st = Math.abs(subtopicHitCount.doubleValue() - topicHitCount.doubleValue());
		if(st == 0.0) st = 1.0;
		pmi = Math.log(1.0+ subtopicHitCount.doubleValue() / ( topicHitCount.doubleValue() * st.doubleValue()));
		
		return pmi;
	}
}
