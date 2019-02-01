package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class PL2 {

	private Map topicCandidateSubtopics;
	private Map termUnigramPostingList;
	private Integer numTermSubtopicFreq;
	
	private Map subtopicPL2;

	public Map getSubtopicDFR() {
		return this.subtopicPL2;
	}

	public PL2( Map topicCandidateSubtopics, Map termUnigramSubtopicPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {

		this.topicCandidateSubtopics = topicCandidateSubtopics;
		this.termUnigramPostingList = termUnigramSubtopicPostingList;
		this.numTermSubtopicFreq = numTermUnigramFreq;
		
		this.subtopicPL2 = this.getEstimatedPL2DivergenceFromRandomnessFeatureWeight(topicString, subtopics);
	}
	
	private Map getEstimatedPL2DivergenceFromRandomnessFeatureWeight(String topicString, Map subtopics) {
		
		Map subtopicPL2FeatureWeight = new HashMap();
		Double pl2FeatureWeight;
		String subtopicString;

		Double avgSubtopicLength = new Double(0.0);
		Integer totalSubtopics = new Integer(0);
		avgSubtopicLength = this.getAverageSubtopicLength(this.topicCandidateSubtopics); 
		totalSubtopics = this.getTotalSubtopics(this.topicCandidateSubtopics);
		
		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			pl2FeatureWeight = this.getSubtopicPL2FeatureWeight(topicString, subtopicString, totalSubtopics, avgSubtopicLength);
			subtopicPL2FeatureWeight.put(subtopic, pl2FeatureWeight);
		}
		
		return subtopicPL2FeatureWeight;
	}

	private Double getSubtopicPL2FeatureWeight(String topicString, String subtopicString, Integer totalSubtopics, Double avgSubtopicLength) 
	{
		Map topicTermMap;
		Map subtopicTermMap;
		Map termPostingMap;

		Integer subtopicLength;
		Integer termCorpusFreq;
		Integer termSubtopicFreq;
		Double termWeight;
		Double subtopicWeight = new Double(0.0);
		Double firstTerm;
		Double firstTermLog;
		Double secondTerm;
		Double secondTermLog;
		Double termSubtopicFreq2;
		Double gammaParameter;
		Double thirdTermLog;
		Double coeff;
		
		try 
		{
			gammaParameter = 1.85;
			
			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);
			subtopicLength = subtopicTermMap.size();
			
			subtopicWeight = 0.0;
			for(Object topicTerm:topicTermMap.keySet()) {

				termPostingMap = (Map) this.termUnigramPostingList.get(topicTerm);
				if(termPostingMap == null) continue;
				
				termCorpusFreq = this.getTotalFrequency(termPostingMap);

				termSubtopicFreq = (Integer) subtopicTermMap.get(topicTerm);
				if(termSubtopicFreq == null) termSubtopicFreq = new Integer(0);
				
				termSubtopicFreq2 = termSubtopicFreq.doubleValue() + Math.log(1.0 + gammaParameter.doubleValue() * avgSubtopicLength.doubleValue()/subtopicLength.doubleValue());
				
				coeff = 1.0 / (termSubtopicFreq2.doubleValue() + 1.0);
				
				firstTerm = (totalSubtopics.doubleValue() * termSubtopicFreq2.doubleValue())/(termCorpusFreq.doubleValue());
				firstTermLog = termSubtopicFreq2.doubleValue() * Math.log(1.0 + firstTerm)/Math.log(2.0);
				
				secondTerm = (termCorpusFreq.doubleValue()/totalSubtopics.doubleValue() - termSubtopicFreq2.doubleValue());
				secondTermLog = secondTerm.doubleValue() * Math.log(Math.E)/Math.log(2.0);
				
				thirdTermLog = 0.5 * Math.log(1.0 + 2.0 * Math.PI * termSubtopicFreq2.doubleValue())/Math.log(2.0);
				
				termWeight = coeff.doubleValue() * (firstTermLog.doubleValue() + secondTermLog.doubleValue() + thirdTermLog.doubleValue());
				
				subtopicWeight += termWeight;
			}

		}catch(Exception e) {
			System.out.println("The problem is "+e.getMessage());
		}
		return subtopicWeight;
	}

	private Double getAverageSubtopicLength(Map topicCandidateSubtopics) {
		
		Double avgSubtopicLength = new Double(0.0);
		Integer totalSubtopicLength = new Integer(0);
		Integer sumLen = new Integer(0);
		Map subtopicTermMap;
		Map subtopics;
		
		String subtopicString;
		
		sumLen = 0;
		
		for(Object topicID:topicCandidateSubtopics.keySet()) {
			
			subtopics = (Map) topicCandidateSubtopics.get(topicID);
			
			for(Object subtopicID:subtopics.keySet()) {
				subtopicString = subtopicID.toString();
				
				subtopicTermMap = this.getFeatureVector(subtopicString);
				sumLen += subtopicTermMap.size();
				totalSubtopicLength++;
			}
		}
		
		avgSubtopicLength = sumLen.doubleValue()/totalSubtopicLength.doubleValue();
		return avgSubtopicLength;
	}

	private Integer getTotalSubtopics(Map topicCandidateSubtopics) {
		
		Integer totalSubtopics = new Integer(0);
		Map subtopics;
		
		for(Object topicID:topicCandidateSubtopics.keySet()) {
			subtopics = (Map) topicCandidateSubtopics.get(topicID);
			totalSubtopics += subtopics.size();
		}
		
		return totalSubtopics;
	}
	
	private Integer getTotalFrequency(Map documentTermFrequency) {
		
		Integer freq = new Integer(0);
		
		for(Object termID:documentTermFrequency.keySet()) {
		
			freq += (Integer) documentTermFrequency.get(termID);
		}
		return freq;
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
	
	public static void main(String[] args) {
		
	}
	
}