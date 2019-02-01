package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DirichletDocumentLikelihoodModelLanguageModel {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	private Map subtopicDSDLMLM;
	
	public Map getSubtopicDSDLMLM() {
		return this.subtopicDSDLMLM;
	}

	public DirichletDocumentLikelihoodModelLanguageModel(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicDSDLMLM = this.getEstimatedDSDLMLMFeatureWeight(topicString, subtopics);
	}

	private Map getEstimatedDSDLMLMFeatureWeight(String topicString, Map subtopics) {

		Map dsDocumentLikelihoodModelFeatureWeight = new HashMap();

		Double dsDLMFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			dsDLMFeatureWeight = this.getDirichletSmoothingDocumentLikeLihoodModelFeatureWeight(topicString, subtopicString);

			dsDocumentLikelihoodModelFeatureWeight.put(subtopic, dsDLMFeatureWeight);
		}
		
		return dsDocumentLikelihoodModelFeatureWeight;
	}

	private Double getDirichletSmoothingDocumentLikeLihoodModelFeatureWeight(String topicString,
			String subtopicString) {
		
		Map topicTermMap;
		Map subtopicTermMap;
		Map termCorpusFreqMap;

		Integer totalTopicTermFreq;
		Integer topicLength;
		Integer subtopicTermFreq;
		Integer topicTermFreq;
		Integer termCorpusFreq;
		Integer freq;
		Double probabilityOfTermGivenTopic = new Double(0.0);
		Double probabilityOfTermGivenCorpus = new Double(0.0);
		Double termProbability = new Double(0.0) ;
		Double subtopicProbability = new Double(0.0);
		Double alpha = new Double(0.0);
		Double niu = new Double(0.0);


		try {

			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);

			niu = 2500.0;
			topicLength = topicTermMap.size();

			totalTopicTermFreq = 0;
			
			for(Object term:topicTermMap.keySet()) {
				totalTopicTermFreq += (Integer) topicTermMap.get(term);
			}
			
			alpha = niu.doubleValue()/(topicLength.doubleValue() + niu.doubleValue());

			subtopicProbability = 0.0;

			for(Object subtopicTerm:subtopicTermMap.keySet())
			{

				termCorpusFreqMap = (Map) this.termUnigramPostingList.get(subtopicTerm);
				if(termCorpusFreqMap == null) continue;

				topicTermFreq = (Integer) topicTermMap.get(subtopicTerm);
				if(topicTermFreq == null) topicTermFreq = new Integer(0);

				subtopicTermFreq = (Integer) subtopicTermMap.get(subtopicTerm);
				
				termCorpusFreq = 0;

				for(Object subtopic:termCorpusFreqMap.keySet()) {
					freq = (Integer) termCorpusFreqMap.get(subtopic);
					termCorpusFreq += freq;
				}

				probabilityOfTermGivenTopic = topicTermFreq.doubleValue()/totalTopicTermFreq.doubleValue();
				probabilityOfTermGivenCorpus = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();

				termProbability = topicTermFreq.doubleValue() + niu.doubleValue() * probabilityOfTermGivenCorpus.doubleValue();
				termProbability = termProbability.doubleValue()/(topicLength.doubleValue() + niu.doubleValue());
				
				termProbability = Math.pow(termProbability.doubleValue(), subtopicTermFreq.doubleValue());
				subtopicProbability = subtopicProbability.doubleValue() + Math.log( 1.0 + termProbability.doubleValue());
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

	private ArrayList getNormalizedList(ArrayList dataList) {

		ArrayList listItems = new ArrayList();

		Double weight;
		Double maximumWeight;
		Double weightSum;
		Double minimumWeight;
		Double normF1;

		int i;
		int N;

		N = dataList.size();
		maximumWeight = 0.0;
		minimumWeight = 1000.0;
		weightSum = 0.0;

		for(i = 0; i<N; i++) {
			weight = (Double) dataList.get(i);
			if(maximumWeight < weight)
				maximumWeight = weight;
			if(minimumWeight>weight) minimumWeight = weight;

			weightSum += weight;
		}

		//		System.out.println(maximumWeight);
		//		System.out.println(weightSum);

		for(i = 0; i<N; i++) {
			weight = (Double) dataList.get(i);
			//			weight = weight/maximumWeight;	
			//			weight = weight.doubleValue()/weightSum.doubleValue();
			normF1 = (weight.doubleValue() - minimumWeight.doubleValue())/(maximumWeight.doubleValue() - minimumWeight.doubleValue());
			listItems.add(normF1);
		}

		return listItems;
	}
}