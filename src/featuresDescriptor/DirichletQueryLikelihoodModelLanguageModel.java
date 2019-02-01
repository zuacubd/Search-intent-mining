package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class DirichletQueryLikelihoodModelLanguageModel {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	private Map subtopicTLMDS;
	
	public Map getSubtopicTLMDS() {
		return this.subtopicTLMDS;
	}

	public DirichletQueryLikelihoodModelLanguageModel(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicTLMDS = this.getEstimatedTLMDSFeatureWeight(topicString, subtopics);
	}

	private Map getEstimatedTLMDSFeatureWeight(String topicString, Map subtopics) {

		Map topicLikelihoodModelDSFeatureWeight = new HashMap();

		Double tlmDSFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			tlmDSFeatureWeight = this.getTopicLikeLihoodModelDSFeatureWeight(topicString, subtopicString);

			topicLikelihoodModelDSFeatureWeight.put(subtopic, tlmDSFeatureWeight);
		}
		
		return topicLikelihoodModelDSFeatureWeight;
	}

	private Double getTopicLikeLihoodModelDSFeatureWeight(String topicString,
			String subtopicString) {
		
		Map topicTermMap;
		Map subtopicTermMap;
		Map termCorpusFreqMap;

		Integer subtopicLength;
		Integer subtopicTermFreq;
		Integer termCorpusFreq;
		Integer freq;
		Double dirichletSmoothing;
		Double probabilityOfTermGivenCorpus;
		Double subtopicProbability = new Double(0.0);
		Double niu;


		try {

			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);

			niu = 2500.0;
			subtopicLength = subtopicTermMap.size();

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

				probabilityOfTermGivenCorpus = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();
				
				dirichletSmoothing = subtopicTermFreq.doubleValue() + niu.doubleValue() * probabilityOfTermGivenCorpus.doubleValue();
				dirichletSmoothing = dirichletSmoothing/(subtopicLength.doubleValue() + niu.doubleValue());
				
				subtopicProbability = subtopicProbability.doubleValue() + Math.log( 1.0 + dirichletSmoothing.doubleValue());
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
