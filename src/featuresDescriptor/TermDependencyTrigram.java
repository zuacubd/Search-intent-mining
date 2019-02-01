package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class TermDependencyTrigram {

	private Map termTrigramSubtopicPostingList;
	private Integer numTermTrigramFreq;

	private Map subtopicTDTrigram;

	public Map getSubtopicTDTrigram() {
		return this.subtopicTDTrigram;
	}

	public TermDependencyTrigram(Map termTrigramSubtopicPostingList, Integer numTermTrigramFreq, String topicString, Map subtopics) {
		
		this.termTrigramSubtopicPostingList = termTrigramSubtopicPostingList;
		this.numTermTrigramFreq = numTermTrigramFreq;

		this.subtopicTDTrigram = this.getTermDependencyFeatureWeight(topicString, subtopics);
	}
	
	private Map getTermDependencyFeatureWeight(String topicString, Map subtopics) {
		
		Map subtopicTermDependencyTrigramFeatureWeight = new HashMap();
		Double tdTrigramFeatureWeight;
		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			tdTrigramFeatureWeight = this.getSubtopicTermDependencyTrigramFeatureWeight(topicString, subtopicString);
			subtopicTermDependencyTrigramFeatureWeight.put(subtopic, tdTrigramFeatureWeight);
		}
		return subtopicTermDependencyTrigramFeatureWeight;
	}
	
	private Double getSubtopicTermDependencyTrigramFeatureWeight(String topicString, String subtopicString) {

		Double subtopicWeight;
		Double subtopicTrigramWeight;
		Double termSubtopicProbability;
		Double termCorpusProbability;
		Double termProbability;
		Double logTermProbability;
		Double lemda;
		
		Map termPostingList;
		Map topicTermFreq;
		Map subtopicTermFreq;
		
		Integer niu;
		Integer termSubtopicFreq;
		Integer termCorpusFreq;
		Integer totalTermSubtopicFreq;
		Integer subtopicLength;
		
		niu = 2500;
		
		subtopicWeight = 0.0;

		//estimating term 3-gram probability
		topicTermFreq = new HashMap();
		subtopicTermFreq = new HashMap();
		
//		this.extractTriGramFeature(topicString, topicTermFreq);
//		this.extractTriGramFeature(subtopicString, subtopicTermFreq);

		this.extractTermFeatures(topicString, topicTermFreq);
		this.extractTermFeatures(subtopicString, subtopicTermFreq);
		
		subtopicLength = subtopicTermFreq.size();
		totalTermSubtopicFreq = this.getTotalFreq(subtopicTermFreq);
		lemda = niu.doubleValue()/(subtopicLength.doubleValue() + niu.doubleValue());

		subtopicTrigramWeight = 0.0;
		
		for(Object termTopic:topicTermFreq.keySet()) 
		{
			termPostingList = (Map) this.termTrigramSubtopicPostingList.get(termTopic);
			if(termPostingList == null) continue;

			termSubtopicFreq = (Integer) subtopicTermFreq.get(termTopic);
			if(termSubtopicFreq == null) termSubtopicFreq = new Integer(0);

			termCorpusFreq = this.getTotalFreq(termPostingList);

			termSubtopicProbability = termSubtopicFreq.doubleValue()/totalTermSubtopicFreq.doubleValue();
			termCorpusProbability = termCorpusFreq.doubleValue()/this.numTermTrigramFreq.doubleValue();

			termProbability = lemda.doubleValue() * termSubtopicProbability.doubleValue() + (1.0 - lemda.doubleValue()) * termCorpusProbability.doubleValue();

			logTermProbability = Math.log(1.0 + termProbability);
			subtopicTrigramWeight += logTermProbability;
		}

		subtopicWeight = subtopicTrigramWeight.doubleValue();

		return subtopicWeight;
	}
	
	private Integer getTotalFreq(Map subtopicTermFreq) {
	
		Integer totalFreq = new Integer(0);
		
		for(Object termID:subtopicTermFreq.keySet()) {
			totalFreq += (Integer) subtopicTermFreq.get(termID);
		}
		
		return totalFreq;
	}

	private void extractTermFeatures(String documentContent, Map termFrequency) {
		
		this.extractUniGramFeature(documentContent, termFrequency);
		this.extractBiGramFeature(documentContent, termFrequency);
		this.extractTriGramFeature(documentContent, termFrequency);
	}
	

	private void extractUniGramFeature(String documentContent, Map termFrequency){

		String[] documentTerms = documentContent.split(" ");
		Integer freq;
		String keyTerm;

		//Window size = 1
		for(int i= 0; i<documentTerms.length; i++){

			keyTerm = documentTerms[i];
			freq = (Integer) termFrequency.get(keyTerm);
			if(freq == null) freq = new Integer(1);
			else freq = freq + 1;

			termFrequency.put(keyTerm, freq);
		}
	}

	private void extractBiGramFeature(String documentContent, Map termFrequency){

		StringBuffer keyTerms = new StringBuffer();
		String[] documentTerms = documentContent.split(" ");
		String keyTerm;
		Integer freq;
		int n;
		int m;
		int i;
		int w;
		int j;

		n = documentTerms.length;

		m = n - 1;
		w = 2; //Window size = 2

		for( i= 0; i< m; i++){
			keyTerms.delete(0, keyTerms.length());

			for(j = i; j< (w+i) ; j++) {
				keyTerms.append(documentTerms[j]+" ");
			}

			keyTerm = keyTerms.toString().trim();

			freq = (Integer) termFrequency.get(keyTerm);
			if(freq==null) freq = new Integer(1);
			else freq = freq + 1;
			termFrequency.put(keyTerm, freq);
		}
	}
	
	private void extractTriGramFeature(String documentContent, Map termFrequency) {

		StringBuffer keyTerms = new StringBuffer();
		String[] documentTerms = documentContent.split(" ");
		String keyTerm;
		Integer freq;
		int n;
		int m;
		int i;
		int w;
		int j;

		n = documentTerms.length;

		//extracting trigram feature
		m = n - 2; 
		w = 3; //Window size = 3

		for( i= 0; i< m; i++){
			keyTerms.delete(0, keyTerms.length());

			for(j = i; j< (w+i) ; j++) {
				keyTerms.append(documentTerms[j]+" ");
			}

			keyTerm = keyTerms.toString().trim();

			freq = (Integer) termFrequency.get(keyTerm);
			if(freq==null) freq = new Integer(1);
			else freq = freq + 1;

			termFrequency.put(keyTerm, freq);
		}
	}
	
}