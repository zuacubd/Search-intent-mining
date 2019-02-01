package featureExtraction;

import java.util.HashMap;
import java.util.Map;

import utility.POSTaggerStanford;

public class FeatureExtraction {

	private Map topicIDtopicString;
	private Map topicCandidateSubtopics; 
	private Map topicSubtopicHitCount;

	private Map termUnigramSubtopicPostingList;
	private Map termBigramSubtopicPostingList;
	private Map termTrigramSubtopicPostingList;
	
	private Integer numTermUnigramFreq;
	private Integer numTermBigramFreq;
	private Integer numTermTrigramFreq;
	
	private Integer numFeatures;
	
	public Integer getNumFeatures() {
		return this.numFeatures;
	}
	
	private TopicSubtopicFeatures topicDependentFeatures;
	private POSTaggerStanford posTaggerStanford;

	public FeatureExtraction(Map topicIDtopicString, Map topicCandidateSubtopics, Map topicSubtopicHitCount) {

		this.topicIDtopicString = topicIDtopicString;
		this.topicCandidateSubtopics = topicCandidateSubtopics;
		this.topicSubtopicHitCount = topicSubtopicHitCount;
		
		this.termUnigramSubtopicPostingList = this.getUnigramIndexing(this.topicCandidateSubtopics);
		this.termBigramSubtopicPostingList = this.getBigramIndexing(this.topicCandidateSubtopics);
		this.termTrigramSubtopicPostingList = this.getTrigramIndexing(this.topicCandidateSubtopics);
	
		this.numTermUnigramFreq = this.getTermTotalFreq(this.termUnigramSubtopicPostingList);
		this.numTermBigramFreq = this.getTermTotalFreq(this.termBigramSubtopicPostingList);
		this.numTermTrigramFreq = this.getTermTotalFreq(this.termTrigramSubtopicPostingList);
		
		this.posTaggerStanford = new POSTaggerStanford();
		this.topicDependentFeatures = new TopicSubtopicFeatures(this.topicIDtopicString, this.topicCandidateSubtopics, this.termUnigramSubtopicPostingList, this.termBigramSubtopicPostingList, this.termTrigramSubtopicPostingList, this.numTermUnigramFreq, this.numTermBigramFreq, this.numTermTrigramFreq, this.topicSubtopicHitCount, this.posTaggerStanford);
	}

	private Integer getTermTotalFreq(Map termSubtopicFreqMap) {
		
		Integer termFreq = new Integer(0);
		Map subtopicFreqMap;
		
		for(Object termID:termSubtopicFreqMap.keySet()) {
			
			subtopicFreqMap = (Map) termSubtopicFreqMap.get(termID);
			
			for(Object subtopicID:subtopicFreqMap.keySet()) {
				
				termFreq += (Integer) subtopicFreqMap.get(subtopicID);
			}
		}
		
		return termFreq;
	}

	public Map getTopicSubtopicFeaturesVectors() {

		Map topicSubtopicFeatureVectors = new HashMap();

		String topicString;

		Map subtopics;
		Map subtopicFeatureVectors;


		for(Object topicID:this.topicIDtopicString.keySet()) {

			topicString = (String) this.topicIDtopicString.get(topicID);
			subtopics = (Map) this.topicCandidateSubtopics.get(topicID);

			subtopicFeatureVectors = this.topicDependentFeatures.getTopicSubtopicFeatureVectors(topicID.toString(), topicString, subtopics);

			topicSubtopicFeatureVectors.put(topicID, subtopicFeatureVectors);
		}
		
		this.numFeatures = this.topicDependentFeatures.getNumFeatures();
		
		return topicSubtopicFeatureVectors;
	}

	private Map getUnigramIndexing(Map topicCandidateSubtopics) {
		
		Map termSubtopicPostingList = new HashMap();
		
		String subtopicString;
		Map subtopics;
		Map subtopicPostingList;
		Map termUnigramFeaturesMap;
		Integer termFreq;
		Integer freq;

		for(Object topicID:topicCandidateSubtopics.keySet()) {
			subtopics = (Map) topicCandidateSubtopics.get(topicID);

			for(Object subtopicID:subtopics.keySet()) {
				subtopicString = subtopicID.toString();

				termUnigramFeaturesMap = new HashMap();
				this.extractUniGramFeature(subtopicString, termUnigramFeaturesMap);

				for(Object termID:termUnigramFeaturesMap.keySet()) {
					termFreq = (Integer) termUnigramFeaturesMap.get(termID);

					subtopicPostingList = (Map) termSubtopicPostingList.get(termID);

					if(subtopicPostingList == null)
						subtopicPostingList = new HashMap();

					subtopicPostingList.put(subtopicString, termFreq);
					
					termSubtopicPostingList.put(termID, subtopicPostingList);
				}
			}
		}
		return termSubtopicPostingList;
	}

	private Map getBigramIndexing(Map topicCandidateSubtopics) {
		
		Map termSubtopicPostingList = new HashMap();
		
		String subtopicString;
		Map subtopics;
		Map subtopicPostingList;
		Map termBigramFeaturesMap;
		Integer termFreq;
		Integer freq;

		for(Object topicID:topicCandidateSubtopics.keySet()) {
			subtopics = (Map) topicCandidateSubtopics.get(topicID);

			for(Object subtopicID:subtopics.keySet()) {
				subtopicString = subtopicID.toString();

				termBigramFeaturesMap = new HashMap();
				this.extractBiGramFeature(subtopicString, termBigramFeaturesMap);

				for(Object termID:termBigramFeaturesMap.keySet()) {
					termFreq = (Integer) termBigramFeaturesMap.get(termID);

					subtopicPostingList = (Map) termSubtopicPostingList.get(termID);

					if(subtopicPostingList == null)
						subtopicPostingList = new HashMap();

					subtopicPostingList.put(subtopicString, termFreq);
					
					termSubtopicPostingList.put(termID, subtopicPostingList);
				}
			}
		}
		return termSubtopicPostingList;
	}
	
	private Map getTrigramIndexing(Map topicCandidateSubtopics) {

		Map termSubtopicPostingList = new HashMap();

		String subtopicString;
		Map subtopics;
		Map subtopicPostingList;
		Map termTrigramFeatures;
		Integer termFreq;
		Integer freq;

		for(Object topicID:topicCandidateSubtopics.keySet()) {
			subtopics = (Map) topicCandidateSubtopics.get(topicID);

			for(Object subtopicID:subtopics.keySet()) {
				subtopicString = subtopicID.toString();

				termTrigramFeatures = new HashMap();
				this.extractUniGramFeature(subtopicString, termTrigramFeatures);
				this.extractBiGramFeature(subtopicString, termTrigramFeatures);
				this.extractTriGramFeature(subtopicString, termTrigramFeatures);

				for(Object termID:termTrigramFeatures.keySet()) {
					termFreq = (Integer) termTrigramFeatures.get(termID);

					subtopicPostingList = (Map) termSubtopicPostingList.get(termID);

					if(subtopicPostingList == null)
						subtopicPostingList = new HashMap();

					subtopicPostingList.put(subtopicString, termFreq);

					termSubtopicPostingList.put(termID, subtopicPostingList);
				}
			}
		}
		return termSubtopicPostingList;
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