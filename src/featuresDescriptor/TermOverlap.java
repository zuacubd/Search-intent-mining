package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TermOverlap {
	
	Map subtopicOverlap;
	
	public Map getSubtopicOverlap() {
		return this.subtopicOverlap;
	}
	
	public TermOverlap(String topicString, Map subtopics) {
		
		this.subtopicOverlap = this.getEstimatedSubtopicOverlap(topicString, subtopics);
	}
	
	private Map getEstimatedSubtopicOverlap(String topicString, Map subtopics) {
		
		Map subtopicOverlap = new HashMap();
		String subtopicString;
		
		Double match;
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicString = subtopic.toString();
			
			match = this.getTopicSubtopicOverlap(topicString, subtopicString);
			
			subtopicOverlap.put(subtopic, match);
		}
		
		return subtopicOverlap;
	}

	
	
	private Double getTopicSubtopicOverlap(String topicString,
			String subtopicString) {
		
		Integer counter = new Integer(0);
		Double overlapWeight = new Double(0);
		ArrayList topicTermList;
		ArrayList subtopicTermList;
		String term;
		int i;
		int l;
		
		topicTermList = this.getDocumentTermList(topicString);
		subtopicTermList = this.getDocumentTermList(subtopicString);
		
		l = topicTermList.size();

		for(i = 0; i<l; i++) {
			
			term = (String) topicTermList.get(i);
			
			if(subtopicTermList.contains(term))
				counter = counter + 1;
		}
		
		overlapWeight = counter.doubleValue()/l;
		
		return overlapWeight;
	}
	
	private ArrayList getDocumentTermList(String documentContent) {
		
		ArrayList termList = new ArrayList();
		String[] terms;
		String term;
		int i;
		int l;
		
		terms = documentContent.split(" ");
		l = terms.length;
		for(i = 0; i<l; i++) {
			term = terms[i].trim();
			
			if(!termList.contains(term))
				termList.add(term);
		}	
		return termList;
	}
	
}