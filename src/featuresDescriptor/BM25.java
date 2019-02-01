package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BM25 {

	private Map topicCandidateSubtopics;
	private Map termUnigramPostingList;
	private Map subtopicBM25;

	public Map getSubtopicBM25() {
		return this.subtopicBM25;
	}

	public BM25( Map topicCandidateSubtopics, Map termUnigramSubtopicPostingList, Integer numTermUnigramFreq, String topicString, Map subtopics) {
		
		this.topicCandidateSubtopics = topicCandidateSubtopics;
		this.termUnigramPostingList = termUnigramSubtopicPostingList;
		this.subtopicBM25 = this.getBM25FeatureWeight(topicString, subtopics);
	}

	private Map getBM25FeatureWeight(String topicString, Map subtopics) {

		Map subtopicBM25FeatureWeight = new HashMap();
		Double bm25FeatureWeight;
		String subtopicString;

		Double avgSubtopicLength = new Double(0.0);
		Integer totalSubtopics = new Integer(0);
		avgSubtopicLength = this.getAverageSubtopicLength(this.topicCandidateSubtopics); 
		totalSubtopics = this.getTotalSubtopics(this.topicCandidateSubtopics);
		
		for(Object subtopic:subtopics.keySet()) {
			subtopicString = subtopic.toString();
			bm25FeatureWeight = this.getSubtopicBM25FeatureWeight(topicString, subtopicString, totalSubtopics, avgSubtopicLength);
			subtopicBM25FeatureWeight.put(subtopic, bm25FeatureWeight);
		}
		
		return subtopicBM25FeatureWeight;
	}

	private Double getSubtopicBM25FeatureWeight(String topicString, String subtopicString, Integer totalSubtopics, Double avgSubtopicLength) {

		Map topicTermMap;
		Map subtopicTermMap;
		Map termPostingMap;

		Integer subtopicLength;
		Integer termFreq;
		Integer termPostingSize;
		Double k;
		Double b;
		Double ntf;
		Double idf;
		Double termWeight;
		Double subtopicWeight = null;
		Double wNorm;
		Double normalizedTF;

		try 
		{
			topicTermMap = this.getFeatureVector(topicString);
			subtopicTermMap = this.getFeatureVector(subtopicString);
			
			k = 1.80;
			b = 0.75;
			subtopicLength = 0;

			for(Object subtopicTerm:subtopicTermMap.keySet()) {
				subtopicLength += (Integer) subtopicTermMap.get(subtopicTerm);
			}

			subtopicWeight = 0.0;

			for(Object topicTerm:topicTermMap.keySet()) {

				termPostingMap = (Map) this.termUnigramPostingList.get(topicTerm);
				if(termPostingMap == null) continue;
				
				termPostingSize = termPostingMap.size();

				termFreq = (Integer) subtopicTermMap.get(topicTerm);
				if(termFreq == null) termFreq = new Integer(0);
				
				idf = totalSubtopics.doubleValue() - termPostingSize.doubleValue() + 0.5;
				idf = idf.doubleValue() / (termPostingSize.doubleValue() + 0.5);
				idf = Math.log(idf);

				wNorm = (1.0 - b) + b * (subtopicLength.doubleValue()/avgSubtopicLength.doubleValue());

				ntf = 0.5 + (0.5 * termFreq.doubleValue())/subtopicLength.doubleValue();
				normalizedTF = (ntf.doubleValue()*(k+1)) / (k * wNorm + ntf.doubleValue());

				termWeight = normalizedTF.doubleValue() * idf.doubleValue();
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
