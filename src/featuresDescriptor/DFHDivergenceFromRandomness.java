package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class DFHDivergenceFromRandomness {

	private Map topicCandidateSubtopics;
	private Map termUnigramPostingList;
	private Integer numTermSubtopicFreq;
	
	private Map subtopicDFHDFR;

	public Map getSubtopicDFHDFR() {
		return this.subtopicDFHDFR;
	}

	public DFHDivergenceFromRandomness( Map topicCandidateSubtopics, Map termUnigramSubtopicPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {

		this.topicCandidateSubtopics = topicCandidateSubtopics;
		this.termUnigramPostingList = termUnigramSubtopicPostingList;
		this.numTermSubtopicFreq = numTermUnigramFreq;
		
		this.subtopicDFHDFR = this.getEstimatedDFHDivergenceFromRandomnessWeight(topicString, subtopics);
	}
	
	private Map getEstimatedDFHDivergenceFromRandomnessWeight(String topicString, Map subtopics) {
		
		Map subtopicDFHDFRFeatureWeight = new HashMap();
		Double dfhDFRFeatureWeight;
		String subtopicString;

		Double avgSubtopicLength = new Double(0.0);
		Integer totalSubtopics = new Integer(0);
		avgSubtopicLength = this.getAverageSubtopicLength(this.topicCandidateSubtopics); 
		totalSubtopics = this.getTotalSubtopics(this.topicCandidateSubtopics);
		
		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			dfhDFRFeatureWeight = this.getSubtopicDFHDFRFeatureWeight(topicString, subtopicString, totalSubtopics, avgSubtopicLength);
			subtopicDFHDFRFeatureWeight.put(subtopic, dfhDFRFeatureWeight);
		}
		
		return subtopicDFHDFRFeatureWeight;
	}
	
	private Double getSubtopicDFHDFRFeatureWeight(String topicString, String subtopicString, Integer totalSubtopics, Double avgSubtopicLength) 
	{
		Map topicTermMap;
		Map subtopicTermMap;
		Map termPostingMap;

		Integer termID;
		Integer subtopicLength;
		Integer termFreq;
		Integer termCorpusFreq;
		Integer maxFreq;

		Double ntf;
		Double ntf_corpus;
		Double termWeight;
		Double subtopicWeight = null;
		Double firstTerm;
		Double firstTermLog;
		Double secondTerm;
		Double secondTermLog;

		try 
		{
			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);
			
			
			subtopicLength = 0;
			for(Object subtopicTerm:subtopicTermMap.keySet()) {
				subtopicLength += (Integer) subtopicTermMap.get(subtopicTerm);
			}
			
			subtopicWeight = 0.0;
			
			for(Object topicTerm:topicTermMap.keySet()) {

				termPostingMap = (Map) this.termUnigramPostingList.get(topicTerm);
				if(termPostingMap == null) continue;
				termCorpusFreq = this.getTotalFrequency(termPostingMap);

				termFreq = (Integer) subtopicTermMap.get(topicTerm);
				if(termFreq == null) termFreq = new Integer(0);
				
				
				ntf = 0.5 + (0.5 * termFreq.doubleValue())/subtopicLength.doubleValue();
				ntf_corpus = 0.5 + 0.5 * termCorpusFreq.doubleValue()/this.numTermSubtopicFreq.doubleValue();
				
				firstTerm = ntf.doubleValue() * Math.pow( (1.0 - ntf.doubleValue()/subtopicLength.doubleValue()), 2);
				firstTerm = firstTerm.doubleValue()/ (ntf.doubleValue() + 1.0);
				firstTermLog = (ntf.doubleValue() * avgSubtopicLength.doubleValue())/(subtopicLength.doubleValue() * ntf_corpus.doubleValue());
				firstTermLog = Math.log(firstTermLog)/Math.log(2.0);

				
				secondTerm = 0.5;
				secondTermLog = 2*Math.PI*ntf.doubleValue()*(1.0-ntf.doubleValue()/subtopicLength.doubleValue());
				secondTermLog = Math.log(secondTermLog)/Math.log(2.0);
				
				termWeight = firstTerm.doubleValue() * firstTermLog.doubleValue() + secondTerm.doubleValue() * secondTermLog.doubleValue();
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
