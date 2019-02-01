package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class AverageTermLength {

	private Map subtopicATL;

	public Map getSubtopicATL() {
		return this.subtopicATL;
	}

	public AverageTermLength( String topicString, Map subtopics) {
		this.subtopicATL = this.getAverageTermLengthWeight(topicString, subtopics);
	}
	
	private Map getAverageTermLengthWeight(String topicString, Map subtopics) {
		
		Map subtopicATLFeatureWeight = new HashMap();
		Double atlFeatureWeight;
		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			atlFeatureWeight = this.getSubtopicATLFeatureWeight(topicString, subtopicString);
			subtopicATLFeatureWeight.put(subtopic, atlFeatureWeight);
		}
		
		return subtopicATLFeatureWeight;
		}
	
	private Double getSubtopicATLFeatureWeight(String topicString, String subtopicString) {

		Double subtopicWeight;
		Double ntf;
		Double termWeight;
		Map termSubtopicMap;
		
		Integer termFreq;
		Integer termLength;
		Integer subtopicLength;
		Integer maxFreq;

		termSubtopicMap = this.getFeatureVector(subtopicString);
		subtopicLength = termSubtopicMap.size();
		maxFreq = this.getMaximumFrequency(termSubtopicMap);
		
		subtopicWeight = 0.0;
		
		for(Object termID:termSubtopicMap.keySet()) 
		{
			termFreq = (Integer) termSubtopicMap.get(termID);
			termLength = termID.toString().length();
			
//			ntf = 0.5 + 0.5 * termFreq.doubleValue() /maxFreq.doubleValue();
//			ntf = termFreq.doubleValue()/subtopicLength;

			ntf = termFreq.doubleValue()/maxFreq.doubleValue();
			termWeight = ntf.doubleValue() * termLength.doubleValue();
			subtopicWeight += termWeight;
		}
		
		subtopicWeight = subtopicWeight.doubleValue()/subtopicLength.doubleValue();

		return subtopicWeight;
	}

	private Integer getMaximumFrequency(Map termSubtopicFreqMap){
	
		Integer max = new Integer(0);
		Integer freq;
	
		for(Object term:termSubtopicFreqMap.keySet()){
	
			freq = (Integer) termSubtopicFreqMap.get(term);
			if(max<freq) max = freq;
		}	
		
		return max;
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
