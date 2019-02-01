package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class JelinkMercerQueryLikelihoodModelLanguageModel {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	private Map subtopicTLM;
	
	public Map getSubtopicTLM() {
		return this.subtopicTLM;
	}

	public JelinkMercerQueryLikelihoodModelLanguageModel(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicTLM = this.getEstimatedTLMFeatureWeight(topicString, subtopics);
	}

	private Map getEstimatedTLMFeatureWeight(String topicString, Map subtopics) {

		Map topicLikelihoodModelFeatureWeight = new HashMap();

		Double tlmFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			tlmFeatureWeight = this.getTopicLikeLihoodModelFeatureWeight(topicString, subtopicString);

			topicLikelihoodModelFeatureWeight.put(subtopic, tlmFeatureWeight);
		}
		
		return topicLikelihoodModelFeatureWeight;
	}

	private Double getTopicLikeLihoodModelFeatureWeight(String topicString,
			String subtopicString) {
		
		Map topicTermMap;
		Map subtopicTermMap;
		Map termCorpusFreqMap;

		Integer totalSubtopicTermFreq;
		Integer subtopicLength;
		Integer subtopicTermFreq;
		Integer termCorpusFreq;
		Integer freq;
		Double probabilityOfTermGivenSubtopic = new Double(0.0);
		Double probabilityOfTermGivenCorpus = new Double(0.0);
		Double termProbability = new Double(0.0) ;
		Double subtopicProbability = new Double(0.0);
		Double alpha = new Double(0.0);
		Double niu = new Double(0.0);


		try {

			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);

			niu = 2500.0;
			subtopicLength = subtopicTermMap.size();

			totalSubtopicTermFreq = 0;
			
			for(Object term:subtopicTermMap.keySet()) {
				totalSubtopicTermFreq += (Integer) subtopicTermMap.get(term);
			}

			alpha = niu.doubleValue()/(subtopicLength.doubleValue() + niu.doubleValue());
			subtopicProbability = 0.0;

			for(Object topicTerm:topicTermMap.keySet())
			{

				termCorpusFreqMap = (Map) this.termUnigramPostingList.get(topicTerm);
				if(termCorpusFreqMap == null) continue;

				subtopicTermFreq = (Integer) subtopicTermMap.get(topicTerm);
				if(subtopicTermFreq == null) subtopicTermFreq = new Integer(0);


				termCorpusFreq = 0;

				for(Object subtopic:termCorpusFreqMap.keySet()) {
					freq = (Integer) termCorpusFreqMap.get(subtopic);
					termCorpusFreq += freq;
				}

				probabilityOfTermGivenSubtopic = subtopicTermFreq.doubleValue()/totalSubtopicTermFreq.doubleValue();
				probabilityOfTermGivenCorpus = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();

				termProbability = alpha.doubleValue() * probabilityOfTermGivenSubtopic.doubleValue() + (1.0 -alpha.doubleValue()) * probabilityOfTermGivenCorpus.doubleValue();

				subtopicProbability = subtopicProbability.doubleValue() + Math.log( 1 + termProbability.doubleValue());
			}
			
		}catch(Exception e) {
			System.out.println("The problem is"+e.getMessage());
		}
		return subtopicProbability;
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
