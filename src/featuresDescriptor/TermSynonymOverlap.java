package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import edu.smu.tspell.wordnet.SynsetType;
import utility.POSTaggerStanford;
import utility.WordNet;


public class TermSynonymOverlap {
	
	private Map subtopicOverlapSyn;
	private POSTaggerStanford posTaggerStanford;
	private WordNet wordNet;
	
	public Map getSubtopicOverlapSyn() {
		return this.subtopicOverlapSyn;
	}
	
	public TermSynonymOverlap(String topicString, Map subtopics, POSTaggerStanford posTaggerStanford) {
		
		this.posTaggerStanford = posTaggerStanford;
		this.wordNet = new WordNet();
		this.subtopicOverlapSyn = this.getEstimatedSubtopicOverlapSyn(topicString, subtopics);
	}
	
	private Map getEstimatedSubtopicOverlapSyn(String topicString, Map subtopics) {
		
		Map subtopicOverlap = new HashMap();
		String subtopicString;
		
		Double match;
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicString = subtopic.toString();
			
			match = this.getTopicSubtopicOverlapSyn(topicString, subtopicString);
			
			subtopicOverlap.put(subtopic, match);
		}
		
		return subtopicOverlap;
	}

	private Double getTopicSubtopicOverlapSyn(String topicString,
			String subtopicString) {
		
		Integer counter = new Integer(0);
		Double overlapWeight = new Double(0);
		ArrayList topicTermList;
		ArrayList subtopicTermList;
		ArrayList termSynonymList;
		String topicTerm;
		String synonymTerm;
		int i;
		int topicLength;
		int subtopicLength;
		int j;
		boolean flag;
		
		topicString = topicString.toLowerCase();
		subtopicString = subtopicString.toLowerCase();
		
		topicTermList = this.getDocumentTermList(topicString);
		subtopicTermList = this.getDocumentTermList(subtopicString);
		
		topicLength = topicTermList.size();
		subtopicLength = subtopicTermList.size();

		counter = 0;
		for(i = 0; i<topicLength; i++) {
			
			topicTerm = (String) topicTermList.get(i);
			termSynonymList = this.getTermSynonymList(topicTerm);
			
			flag = false;
			for(j = 0; j<termSynonymList.size(); j++) {
				synonymTerm = (String) termSynonymList.get(j);
				
				if(subtopicTermList.contains(synonymTerm.trim())){
					flag = true;
					break;
				}
			}
			
			if(flag)
				counter = counter + 1;
		}

		overlapWeight = counter.doubleValue()/Math.max(topicLength, subtopicLength);
		
		return overlapWeight;
	}
	
	private ArrayList getTermSynonymList(String term) {

		ArrayList synonymList = new ArrayList();
		ArrayList tempSynonymList1;
		ArrayList tempSynonymList2;
		
		String posTag;
		SynsetType synsetType;
		int i;
		
		posTag = this.posTaggerStanford.getPOSTag(term);
		
		if(posTag.compareTo("adjective")==0){
			synsetType = SynsetType.ADJECTIVE;
			tempSynonymList1 = this.wordNet.getSynset(term, synsetType);
			
			synsetType = SynsetType.ADJECTIVE_SATELLITE;
			tempSynonymList2 = this.wordNet.getSynset(term, synsetType);
			
			for(i = 0; i<tempSynonymList1.size(); i++){
				if(!synonymList.contains(tempSynonymList1.get(i).toString().trim()))
					synonymList.add(tempSynonymList1.get(i).toString().trim());
			}
			
			for(i = 0; i<tempSynonymList2.size(); i++){
				if(!synonymList.contains(tempSynonymList2.get(i).toString().trim()))
					synonymList.add(tempSynonymList2.get(i).toString().trim());
			}
		}
		else if(posTag.compareTo("noun")==0) {
			synsetType = SynsetType.NOUN;
			
			tempSynonymList1 = this.wordNet.getSynset(term, synsetType);
			for(i = 0; i<tempSynonymList1.size(); i++){
				if(!synonymList.contains(tempSynonymList1.get(i).toString().trim()))
					synonymList.add(tempSynonymList1.get(i).toString().trim());
			}
		}
		else if(posTag.compareTo("verb")==0) {
			synsetType = SynsetType.VERB;
			
			tempSynonymList1 = this.wordNet.getSynset(term, synsetType);
			for(i = 0; i<tempSynonymList1.size(); i++){
				if(!synonymList.contains(tempSynonymList1.get(i).toString().trim()))
					synonymList.add(tempSynonymList1.get(i).toString().trim());
			}
		}
		else if(posTag.compareTo("adverb")==0) {
			synsetType = SynsetType.ADVERB;
			
			tempSynonymList1 = this.wordNet.getSynset(term, synsetType);
			for(i = 0; i<tempSynonymList1.size(); i++){
				if(!synonymList.contains(tempSynonymList1.get(i).toString().trim()))
					synonymList.add(tempSynonymList1.get(i).toString().trim());
			}
		}
		else{
			synonymList.add(term);
		}
		
		if(!synonymList.contains(term))
			synonymList.add(term);
		
		return synonymList;
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