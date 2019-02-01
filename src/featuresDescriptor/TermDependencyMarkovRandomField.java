package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class TermDependencyMarkovRandomField {

	private Map termUnigramSubtopicPostingList;
	private Map termBigramSubtopicPostingList;
	private Integer numTermUnigramFreq;
	private Integer numTermBigramFreq;

	private Map subtopicTDMRF;

	public Map getSubtopicTDMRF() {
		return this.subtopicTDMRF;
	}

	public TermDependencyMarkovRandomField(Map termUnigramSubtopicPostingList, Map termBigramSubtopicPostingList, Integer numTermUnigramFreq, Integer numTermBigramFreq, String topicString, Map subtopics) {
		
		this.termUnigramSubtopicPostingList = termUnigramSubtopicPostingList;
		this.termBigramSubtopicPostingList = termBigramSubtopicPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		this.numTermBigramFreq = numTermBigramFreq;

		this.subtopicTDMRF = this.getTermDependencyFeatureWeight(topicString, subtopics);
	}
	
	private Map getTermDependencyFeatureWeight(String topicString, Map subtopics) {
		
		Map subtopicTDMRFFeatureWeight = new HashMap();
		Double tdMRFFeatureWeight;
		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			tdMRFFeatureWeight = this.getSubtopicTermDependencyMarkovRandomField(topicString, subtopicString);
			subtopicTDMRFFeatureWeight.put(subtopic, tdMRFFeatureWeight);
		}
		return subtopicTDMRFFeatureWeight;
	}
	
	private Double getSubtopicTermDependencyMarkovRandomField(String topicString, String subtopicString) {

		Double alphaUnigram;
		Double alphaBigram;
		Double alphaBigramFull;
		Double subtopicWeight;
		Double subtopicUnigramWeight;
		Double subtopicBigramWeight;
		Double subtopicBigramFullWeight;
		
		Double termSubtopicProbability;
		Double termCorpusProbability;
		Double termProbability;
		Double logTermProbability;
		Double lemda;
		
		Map termPostingList;
		Map topicTermFreq;
		Map subtopicTermFreq;
		
		Integer niu;
		Integer N;
		Integer termSubtopicFreq;
		Integer termCorpusFreq;
		Integer totalTermSubtopicFreq;
		
		niu = 2500;
		alphaUnigram = 1.0/3.0;
		alphaBigram = 1.0/3.0;
		alphaBigramFull = 1.0/3.0;

		subtopicWeight = 0.0;

		//Estimating unigram probability
		topicTermFreq = new HashMap();
		subtopicTermFreq = new HashMap();

		this.extractUniGramFeature(topicString, topicTermFreq);
		this.extractUniGramFeature(subtopicString, subtopicTermFreq);

		totalTermSubtopicFreq = this.getTotalFreq(subtopicTermFreq);
		lemda = niu.doubleValue()/(totalTermSubtopicFreq.doubleValue()+niu.doubleValue());

		subtopicUnigramWeight = 0.0;
		for(Object termTopic:topicTermFreq.keySet()) 
		{
			termPostingList = (Map) this.termUnigramSubtopicPostingList.get(termTopic);
			if(termPostingList == null) continue;

			termSubtopicFreq = (Integer) subtopicTermFreq.get(termTopic);
			if(termSubtopicFreq == null) termSubtopicFreq = new Integer(0);

			termCorpusFreq = this.getTotalFreq(termPostingList);

			termSubtopicProbability = termSubtopicFreq.doubleValue()/totalTermSubtopicFreq.doubleValue();
			termCorpusProbability = termCorpusFreq.doubleValue()/this.numTermUnigramFreq.doubleValue();

			termProbability = lemda.doubleValue() * termSubtopicProbability.doubleValue() + (1.0 - lemda.doubleValue()) * termCorpusProbability.doubleValue();

			logTermProbability = Math.log(1.0 + termProbability);
			subtopicUnigramWeight += logTermProbability;
		}

		subtopicWeight = alphaUnigram.doubleValue() * subtopicUnigramWeight.doubleValue();
//		System.out.println("unigram: "+ subtopicWeight);

		//Estimating sequential bigram probability
		topicTermFreq = new HashMap();
		subtopicTermFreq = new HashMap();

		this.extractBiGramFeature(topicString, topicTermFreq);
		this.extractBiGramFeature(subtopicString, subtopicTermFreq);

		totalTermSubtopicFreq = this.getTotalFreq(subtopicTermFreq);
		lemda = niu.doubleValue()/(totalTermSubtopicFreq.doubleValue()+niu.doubleValue());
		subtopicBigramWeight = 0.0;

		for(Object termTopic:topicTermFreq.keySet()) 
		{
			termPostingList = (Map) this.termBigramSubtopicPostingList.get(termTopic);
			if(termPostingList == null) continue;

			termSubtopicFreq = (Integer) subtopicTermFreq.get(termTopic);
			if(termSubtopicFreq == null) termSubtopicFreq = new Integer(0);

			termCorpusFreq = this.getTotalFreq(termPostingList);

			termSubtopicProbability = termSubtopicFreq.doubleValue()/totalTermSubtopicFreq.doubleValue();
			termCorpusProbability = termCorpusFreq.doubleValue()/this.numTermBigramFreq.doubleValue();

			termProbability = lemda.doubleValue() * termSubtopicProbability.doubleValue() + (1.0 - lemda.doubleValue()) * termCorpusProbability.doubleValue();

			logTermProbability = Math.log(1.0 + termProbability);
			subtopicBigramWeight += logTermProbability;
		}
		
		subtopicWeight += alphaBigram.doubleValue() * subtopicBigramWeight.doubleValue();
//		System.out.println("bigram: "+ subtopicWeight);

		//Estimating Full Bigram probability

		topicTermFreq = new HashMap();
		subtopicTermFreq = new HashMap();

		this.extractBiGramFullFeature(topicString, topicTermFreq);
		this.extractBiGramFeature(subtopicString, subtopicTermFreq);

		subtopicBigramFullWeight = 0.0;
		totalTermSubtopicFreq = this.getTotalFreq(subtopicTermFreq);
		lemda = niu.doubleValue()/(totalTermSubtopicFreq.doubleValue()+niu.doubleValue());

		for(Object termTopic:topicTermFreq.keySet()) 
		{
			termPostingList = (Map) this.termBigramSubtopicPostingList.get(termTopic);
			if(termPostingList == null) continue;

			termSubtopicFreq = (Integer) subtopicTermFreq.get(termTopic);
			if(termSubtopicFreq == null) termSubtopicFreq = new Integer(0);

			termCorpusFreq = this.getTotalFreq(termPostingList);

			termSubtopicProbability = termSubtopicFreq.doubleValue()/totalTermSubtopicFreq.doubleValue();
			termCorpusProbability = termCorpusFreq.doubleValue()/this.numTermBigramFreq.doubleValue();

			termProbability = lemda.doubleValue() * termSubtopicProbability.doubleValue() + (1.0 - lemda.doubleValue()) * termCorpusProbability.doubleValue();

			logTermProbability = Math.log(1.0 + termProbability);
			subtopicBigramFullWeight += logTermProbability;
		}

		subtopicWeight += alphaBigramFull.doubleValue() *subtopicBigramFullWeight.doubleValue();
		
		return subtopicWeight;
	}
	
	private Integer getTotalFreq(Map subtopicTermFreq) {
	
		Integer totalFreq = new Integer(0);
		
		for(Object termID:subtopicTermFreq.keySet()) {
			totalFreq += (Integer) subtopicTermFreq.get(termID);
		}
		
		return totalFreq;
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
	
	private void extractBiGramFullFeature(String documentContent, Map termFrequency){

		StringBuffer keyTerms = new StringBuffer();
		String[] documentTerms = documentContent.split(" ");
		String keyTerm;
		Integer freq;
		int n;
		int i;
		int j;
		
		n = documentTerms.length;
		
		for(i = 0; i<n; i++) {

			for(j = 0; j<n; j++) {
				keyTerms.delete(0, keyTerms.length());
				if(i == j) continue;
				
				keyTerms.append(documentTerms[i]);
				keyTerms.append(" "+documentTerms[j]);
			
				keyTerm = keyTerms.toString().trim();
				
				freq = (Integer) termFrequency.get(keyTerm);
				if(freq==null) freq = new Integer(1);
				else freq = freq + 1;
				termFrequency.put(keyTerm, freq);
			}
		}
		
	}
	
	public static void main(String[] args) {
		
	}
}

