package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class CoordinationLevelMatching {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	
	private Map subtopicCLM;
	
	public Map getSubtopicCLM() {
		return this.subtopicCLM;
	}
	
	public CoordinationLevelMatching(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicCLM = this.getCLMFeatureWeight(topicString, subtopics);
	}
	
	private Map getCLMFeatureWeight(String topicString, Map subtopics) {
		
		Map subtopicCLMFeatureWeight = new HashMap();
		Double clmFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			clmFeatureWeight = this.getCoordincationLevelMatching(topicString, subtopicString);

			subtopicCLMFeatureWeight.put(subtopic, clmFeatureWeight);
		}
		return subtopicCLMFeatureWeight;
	}
	
	private Double getCoordincationLevelMatching(String topicString, String subtopicString){

		Map topicFeatureMap;
		Map subtopicFeatureMap;
		Double sim = null;
		
		topicFeatureMap = this.getFeatureVector(topicString);
		subtopicFeatureMap = this.getFeatureVector(subtopicString);
			
		sim = this.getCLM(topicFeatureMap, subtopicFeatureMap);
		
		return sim;
	}
	
	private Double getCLM(Map topicFeatureMap, Map subtopicFeatureMap){
		
		Double sim = new Double(0.0);
		
		Integer termFreqTopic;
		Integer termFreqSubtopic;
		
		for(Object term:topicFeatureMap.keySet()) {
			
			termFreqTopic = (Integer) topicFeatureMap.get(term);

			termFreqSubtopic = (Integer) subtopicFeatureMap.get(term);
			if(termFreqSubtopic == null)
				termFreqSubtopic = new Integer(0);
			
			sim += termFreqTopic.doubleValue() * termFreqSubtopic.doubleValue();
		}
		
		return sim;
	}
	
	private Map getFeatureVector(String documentContent){
		
		String[] documentTerms = documentContent.split(" ");
		Map termFrquency = new HashMap();
		Integer freq;
		String keyTerm;

		for(int i= 0; i<documentTerms.length; i++){

			keyTerm = documentTerms[i];
			freq = (Integer) termFrquency.get(keyTerm);
			if(freq==null) freq = new Integer(1);
			else freq = freq + 1;

			termFrquency.put(keyTerm, freq);
		}
		return termFrquency;
		
	}
}
