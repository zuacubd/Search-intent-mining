package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class KulbackLaibler {

	private Map termUnigramPostingList;
	private Integer numTermUnigramFreq;
	private Map subtopicKL;
	
	public Map getSubtopicKL() {
		return this.subtopicKL;
	}

	public KulbackLaibler(Map termUnigramPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramPostingList = termUnigramPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicKL = this.getEstimatedKLFeatureWeight(topicString, subtopics);
	}

	private Map getEstimatedKLFeatureWeight(String topicString, Map subtopics) {

		Map kulbackLaiblerModelFeatureWeight = new HashMap();

		Double klFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			klFeatureWeight = this.getTopicLikeLihoodModelFeatureWeight(topicString, subtopicString);

			kulbackLaiblerModelFeatureWeight.put(subtopic, klFeatureWeight);
		}
		
		return kulbackLaiblerModelFeatureWeight;
	}

	private Double getTopicLikeLihoodModelFeatureWeight(String topicString,
			String subtopicString) {
		
		Map topicTermMap;
		Map subtopicTermMap;
		Map termCorpusFreqMap;

		Integer totalSubtopicTermFreq;
		Integer totalTopicTermFreq;
		Integer subtopicLength;
		Integer topicLength;
		Integer subtopicTermFreq;
		Integer topicTermFreq;
		
		Integer termCorpusFreq;
		Integer freq;
		Double probabilityOfTermGivenSubtopic = new Double(0.0);
		Double probabilityOfTermGivenTopic = new Double(0.0);
		Double probabilityOfTermGivenCorpus = new Double(0.0);
		Double termProbabilityGivenTopicModel = new Double(0.0);
		Double termProbabilityGivenSubtopicModel = new Double(0.0);
		Double alphaSubtopic = new Double(0.0);
		Double alphaTopic = new Double(0.0);
		Double niu = new Double(0.0);
		Double termWeight = new Double(0.0);
		Double klWeight = new Double(0.0);
		Double minusOne = new Double(-1.0);

		try {

			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);

			niu = 2500.0;
			subtopicLength = subtopicTermMap.size();
			topicLength = topicTermMap.size();
			
			totalSubtopicTermFreq = 0;
			
			for(Object term:subtopicTermMap.keySet()) {
				totalSubtopicTermFreq += (Integer) subtopicTermMap.get(term);
			}
			
			totalTopicTermFreq = 0;
			
			for(Object term:topicTermMap.keySet()) {
				totalTopicTermFreq += (Integer) topicTermMap.get(term);
			}
			
			alphaSubtopic = niu.doubleValue()/(subtopicLength.doubleValue() + niu.doubleValue());
			
			alphaTopic = niu.doubleValue()/(topicLength.doubleValue() + niu.doubleValue());
			
			klWeight = 0.0;

			for(Object topicTerm:topicTermMap.keySet())
			{
				termCorpusFreqMap = (Map) this.termUnigramPostingList.get(topicTerm);
				if(termCorpusFreqMap == null) continue;

				subtopicTermFreq = (Integer) subtopicTermMap.get(topicTerm);
				if(subtopicTermFreq == null) subtopicTermFreq = new Integer(0);

				topicTermFreq = (Integer) topicTermMap.get(topicTerm);
				if(topicTermFreq == null) topicTermFreq = new Integer(0);

				termCorpusFreq = 0;

				for(Object subtopic:termCorpusFreqMap.keySet()) {
					freq = (Integer) termCorpusFreqMap.get(subtopic);
					termCorpusFreq += freq;
				}

				probabilityOfTermGivenCorpus = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();

				probabilityOfTermGivenSubtopic = subtopicTermFreq.doubleValue()/totalSubtopicTermFreq.doubleValue();
				probabilityOfTermGivenTopic = topicTermFreq.doubleValue()/totalTopicTermFreq.doubleValue();
				
				termProbabilityGivenTopicModel = alphaTopic.doubleValue() * probabilityOfTermGivenTopic.doubleValue() + (1.0 -alphaTopic.doubleValue()) * probabilityOfTermGivenCorpus.doubleValue();
				termProbabilityGivenSubtopicModel = alphaSubtopic.doubleValue() * probabilityOfTermGivenSubtopic.doubleValue() + (1.0 -alphaSubtopic.doubleValue()) * probabilityOfTermGivenCorpus.doubleValue();

//				System.out.println(termProbabilityGivenTopicModel +":"+ termProbabilityGivenSubtopicModel);
//				termWeight = termProbabilityGivenTopicModel.doubleValue() * Math.log(termProbabilityGivenTopicModel.doubleValue()/termProbabilityGivenSubtopicModel.doubleValue());
				termWeight = termProbabilityGivenSubtopicModel.doubleValue() * Math.log(termProbabilityGivenSubtopicModel.doubleValue()/termProbabilityGivenTopicModel.doubleValue());
				
//				System.out.println(termWeight);
				klWeight += termWeight.doubleValue();
			}
			
		}catch(Exception e) {
			System.out.println("The problem is"+e.getMessage());
		}
		
		klWeight = minusOne.doubleValue() * klWeight.doubleValue(); 

		return klWeight;
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