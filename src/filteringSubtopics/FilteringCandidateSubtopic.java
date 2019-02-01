package filteringSubtopics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import utilityStemmer.PlingStemmer;

public class FilteringCandidateSubtopic {

	private Map topicCandidateSubtopics;
	
	private PlingStemmer stemmer;
	
	public Map getTopicCandidateSubtopics() {
		return this.topicCandidateSubtopics;
	}

	public FilteringCandidateSubtopic(Map topicIDtopicString, Map topicAggregatedSubtopics) {
		this.stemmer = new PlingStemmer();
		this.topicCandidateSubtopics = this.getFilteredCanidateSubtopic(topicIDtopicString, topicAggregatedSubtopics);
	}

	private Map getFilteredCanidateSubtopic(Map topicIDtopicString,
			Map topicAggregatedSubtopics) {

		Map topicFilterredCandidateSubtopics = new HashMap(); 
		Map subtopics;
		Map filterredSubtopics;
		Vector filterredSubtopicFeature;
		Vector pastSubtopicFeature;
		
		String topicString;
		String subtopicString;
		String filterredSubtopicString;
		boolean flag;
		
		int counter;
		
		counter = 0;
		
		for(Object topicID:topicIDtopicString.keySet()) {
			topicString = (String) topicIDtopicString.get(topicID);
			
			subtopics = (Map) topicAggregatedSubtopics.get(topicID);

			filterredSubtopics = new HashMap();
			
			for(Object subtopic:subtopics.keySet()) {
				subtopicString = subtopic.toString();

				//filtering by Rule 1: is the topic contains subtopic?

				flag = isTopicContainsSubtopic(topicString, subtopicString);
				if(flag) {
					counter++;
//					System.out.println(topicString +":"+subtopicString);
					continue;
				}
				
				//filtering by Rule 2: is the topic singular forms of the subtopic?

				flag = isTopicSingularFormOfSubtopic(topicString, subtopicString);
				if(flag) {
					counter++;
//					System.out.println(topicString +":"+subtopicString);
					continue;
				}
				
				//filtering by applying stemming the subtopics except topic parts
//				filterredSubtopicString = this.getFilteredSubtopicString(topicString, subtopicString);
//				filterredSubtopicFeature = (Vector) subtopics.get(subtopic);
//				
//				System.out.println(subtopicString +" \t "+ filterredSubtopicString);
//				pastSubtopicFeature = (Vector) filterredSubtopics.get(filterredSubtopicString);
//				
//				if(pastSubtopicFeature == null)
//					filterredSubtopics.put(filterredSubtopicString, filterredSubtopicFeature);
//				else
//				{
//					if(filterredSubtopicFeature.size()>pastSubtopicFeature.size())
//						filterredSubtopics.put(filterredSubtopicString, filterredSubtopicFeature);
//					else
//						filterredSubtopics.put(filterredSubtopicString, pastSubtopicFeature);
//				}
				
				filterredSubtopics.put(subtopic, subtopics.get(subtopic));
			}
			topicFilterredCandidateSubtopics.put(topicID,filterredSubtopics);
		}
		
		return topicFilterredCandidateSubtopics;
	}

	private String getFilteredSubtopicString(String topicString, String subtopicString) {

		List topicTermList = new ArrayList();
		
		StringBuffer filterredSubtopicsTermList = new StringBuffer();
		String[] topicStringParts;
		String[] subtopicStringParts;
		String terms;
		String filterredSubtopics;
		
		int i;

		topicStringParts = topicString.split(" ");
		subtopicStringParts = subtopicString.split(" ");
		
		for(i = 0; i<topicStringParts.length; i++) {
			topicTermList.add(topicStringParts[i].trim());
		}
		
		filterredSubtopicsTermList.delete(0,  filterredSubtopicsTermList.length());

		for(i = 0; i<subtopicStringParts.length; i++) {
			terms = subtopicStringParts[i].trim();
			
			if(!topicTermList.contains(terms)) {
				terms = PlingStemmer.stem(terms);
			}
			
			filterredSubtopicsTermList.append(terms+" ");
		}
		
		filterredSubtopics = filterredSubtopicsTermList.toString().trim();
		
		return filterredSubtopics;
	}

	private boolean isTopicSingularFormOfSubtopic(String topicString,
			String subtopicString) {
		
		ArrayList topicPartList = new ArrayList();
		String[] topicParts;
		String[] subtopicParts;
		String topicPart;
		String subtopicPart;
		String stemmedTopicPart;
		String stemmedSubtopicPart;
		
		int i;
		int l;
		boolean flag;
		
		topicParts = topicString.split(" ");
		subtopicParts = subtopicString.split(" ");
		l = topicParts.length;
		
		for(i = 0; i<l; i++) {
			
			topicPart = topicParts[i].trim();
			stemmedTopicPart = this.stemmer.stem(topicPart);
			topicPartList.add(stemmedTopicPart);
		}
		
		l = subtopicParts.length;
		
		flag = true; 
		for(i = 0; i<l; i++) {
			
			subtopicPart = subtopicParts[i].trim();
			stemmedSubtopicPart = this.stemmer.stem(subtopicPart);
			
			flag = topicPartList.contains(stemmedSubtopicPart);
			
			if(!flag) 
				break;
		}
		
		return flag;

	}

	private boolean isTopicContainsSubtopic(String topicString,
			String subtopicString) {
		
		if(topicString.trim().compareTo(subtopicString.trim())==0) 
			return true;
		
		ArrayList topicPartList = new ArrayList();

		String[] topicParts;
		String[] subtopicParts;
		String topicPart;
		String subtopicPart;
		
		int i;
		int l;
		boolean flag;
		
		topicParts = topicString.split(" ");
		subtopicParts = subtopicString.split(" ");
		l = topicParts.length;
		
		for(i = 0; i<l; i++) {
			
			topicPart = topicParts[i].trim();
			topicPartList.add(topicPart);
		}
		
		l = subtopicParts.length;
		
		flag = true; 
		for(i = 0; i<l; i++) {
			
			subtopicPart = subtopicParts[i].trim();
		
			flag = topicPartList.contains(subtopicPart);
			
			if(!flag) 
				break;
		}
		
		return flag;
	}
	
}
