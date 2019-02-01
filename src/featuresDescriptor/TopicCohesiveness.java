package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TopicCohesiveness {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	private Map subtopicTC;
	
	public Map getSubtopicTopicCohesiveness() {
		return this.subtopicTC;
	}

	public TopicCohesiveness(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicTC = this.getEstimatedTopicCohesivenessFeatureWeight(topicString, subtopics);
	}

	private Map getEstimatedTopicCohesivenessFeatureWeight(String topicString, Map subtopics) {

		Map topicCohesivenessModelFeatureWeight = new HashMap();

		Double tCFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			tCFeatureWeight = this.getTopicCohesivenessModelFeatureWeight(topicString, subtopicString);

			topicCohesivenessModelFeatureWeight.put(subtopic, tCFeatureWeight);
		}
		
		return topicCohesivenessModelFeatureWeight;
	}

	private Double getTopicCohesivenessModelFeatureWeight(String topicString,
			String subtopicString) {
		
		Map subtopicTermMap;
		Map termCorpusFreqMap;

		Integer totalSubtopicTermFreq;
		Integer subtopicLength;
		Integer subtopicTermFreq;
		Integer termCorpusFreq;
		Integer freq;
		Double probabilityOfTermGivenSubtopic = new Double(0.0);
		Double probabilityOfTermGivenCorpus = new Double(0.0);
		Double termProbabilityGivenSubtopicModel = new Double(0.0);
		Double alphaSubtopic = new Double(0.0);
		Double niu = new Double(0.0);
		Double termWeight = new Double(0.0);
		Double tCWeight = new Double(0.0);
		Double minusOne = new Double(-1.0);

		try {

			subtopicTermMap = this.getFeatureVector(subtopicString);

			niu = 2500.0;
			subtopicLength = subtopicTermMap.size();
			
			totalSubtopicTermFreq = 0;
			
			for(Object term:subtopicTermMap.keySet()) {
				totalSubtopicTermFreq += (Integer) subtopicTermMap.get(term);
			}
			
			alphaSubtopic = niu.doubleValue()/(subtopicLength.doubleValue() + niu.doubleValue());
			
			tCWeight = 0.0;

			for(Object subtopicTerm:subtopicTermMap.keySet())
			{
				termCorpusFreqMap = (Map) this.termUnigramPostingList.get(subtopicTerm);
				
				subtopicTermFreq = (Integer) subtopicTermMap.get(subtopicTerm);
				
				termCorpusFreq = 0;
				for(Object subtopic:termCorpusFreqMap.keySet()) {
					freq = (Integer) termCorpusFreqMap.get(subtopic);
					termCorpusFreq += freq;
				}

				probabilityOfTermGivenCorpus = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();

				probabilityOfTermGivenSubtopic = subtopicTermFreq.doubleValue()/totalSubtopicTermFreq.doubleValue();
				
				termProbabilityGivenSubtopicModel = alphaSubtopic.doubleValue() * probabilityOfTermGivenSubtopic.doubleValue() + (1.0 -alphaSubtopic.doubleValue()) * probabilityOfTermGivenCorpus.doubleValue();

				termWeight = termProbabilityGivenSubtopicModel.doubleValue() * Math.log(termProbabilityGivenSubtopicModel.doubleValue());
				
				tCWeight = tCWeight.doubleValue() + termWeight.doubleValue();
			}
			
		}catch(Exception e) {
			System.out.println("The problem is"+e.getMessage());
		}
		
		tCWeight = minusOne.doubleValue() * tCWeight.doubleValue(); 

		return tCWeight;
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