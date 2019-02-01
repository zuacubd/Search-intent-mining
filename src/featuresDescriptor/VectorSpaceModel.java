package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class VectorSpaceModel {

	private Map termUnigramSubtopicPostingList;
	private Integer numTermUnigramFreq;
	
	private Map subtopicVSM;
	
	public Map getSubtopicVSM() {
		return this.subtopicVSM;
	}
	
	public VectorSpaceModel(Map termUnigramSubtopicPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		this.termUnigramSubtopicPostingList = termUnigramSubtopicPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		
		this.subtopicVSM = this.getVSMFeatureWeight(topicString, subtopics);
	}
	
	private Map getVSMFeatureWeight(String topicString, Map subtopics) {
		
		Map subtopicVSMFeatureWeight = new HashMap();
		Double vsmFeatureWeight;

		String subtopicString;

		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();

			vsmFeatureWeight = this.getVectorSpaceModelFeatureWeight(topicString, subtopicString);
			subtopicVSMFeatureWeight.put(subtopic, vsmFeatureWeight);
			
		}
		return subtopicVSMFeatureWeight;
	}
	
	private Double getVectorSpaceModelFeatureWeight(String topicString, String subtopicString){

		Vector topicFeatureVector;
		Vector subtopicFeatureVector;
		Double sim = null;
		
		topicFeatureVector = this.getFeatureVector(topicString);
		subtopicFeatureVector = this.getFeatureVector(subtopicString);
			
		sim = this.getCosineSimilarity(topicFeatureVector, subtopicFeatureVector);
		
		return sim;
	}
	
	private Double getCosineSimilarity(Vector topicFeatureVector, Vector subtopicFeatureVector){
		
		Double sim = new Double(0.0);
		Double magnitudeOfA = new Double(0);
		Double magnitudeOfB = new Double(0);
		Double dotProduct = new Double(0.0);
		
		Integer freqA;
		Integer freqB;
		int i;
		int vocabularySize;
		
		i = 0;
		vocabularySize = topicFeatureVector.size();
		
		dotProduct = 0.0;
		magnitudeOfA = 0.0;
		magnitudeOfB = 0.0;
		
		for(i = 0; i<vocabularySize; i++) {
			freqA = (Integer) topicFeatureVector.get(i);
			freqB = (Integer) subtopicFeatureVector.get(i);
			dotProduct += freqA.doubleValue() * freqB.doubleValue();
			magnitudeOfA += freqA.doubleValue() * freqA.doubleValue();
			magnitudeOfB += freqB.doubleValue() * freqB.doubleValue(); 
		}
		
			magnitudeOfA = Math.sqrt(magnitudeOfA);
			magnitudeOfB = Math.sqrt(magnitudeOfB);

			sim = dotProduct.doubleValue()/(magnitudeOfA.doubleValue()*magnitudeOfB.doubleValue());
		
		return sim;
	}
	
	private Vector getFeatureVector(String documentContent){
		
		String[] keywordTerms = documentContent.split(" ");
		
		Map termFrequencyMap = new HashMap();
		Integer freq;
		String keywordTerm;
		Integer termFreq;
		Object[] termIDList;
		ArrayList termList = new ArrayList();
		
		Vector featureVector;
		Integer vocabularySize;
		int i;
		int position;
		
		for(i= 0; i<keywordTerms.length; i++){

			keywordTerm = keywordTerms[i];
			freq = (Integer) termFrequencyMap.get(keywordTerm);
			if(freq==null) freq = new Integer(1);
			else freq = freq + 1;
			
			termFrequencyMap.put(keywordTerm, freq);
		}
		
		vocabularySize = this.termUnigramSubtopicPostingList.size();
		featureVector = new Vector();
		
		termIDList = this.termUnigramSubtopicPostingList.keySet().toArray();

		for(i = 0; i<vocabularySize; i++) {
			featureVector.add(0);
			termList.add(termIDList[i]);
		}

		for(Object termID:termFrequencyMap.keySet()) {
			
			position = termList.indexOf(termID);
			
			termFreq = (Integer) termFrequencyMap.get(termID);
			
			if(position>=0) {
				featureVector.set(position, termFreq);
			}
		}
		
		return featureVector;
	}
}
