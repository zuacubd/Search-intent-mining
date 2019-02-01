package filteringSubtopics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilteringDuplicateSubtopics {

	private Map topicFilterredSubtopicsWeight;
	
	public Map getTopicDuplicateFilterredSubtopicsWeights() {
		return this.topicFilterredSubtopicsWeight;
	}
	
	public FilteringDuplicateSubtopics(Map topicSubtopicsWeight) {
		
		this.topicFilterredSubtopicsWeight = this.getTopicFilterredSubtopics(topicSubtopicsWeight);
	}

	private Map getTopicFilterredSubtopics(Map topicSubtopicsWeight) {
	
		Map topicfilterredSubtopics = new HashMap();
		Map filterredSubtopicsWeight;
		Map subtopicsWeight;
		String subtopicString;
		String originalSubtopic;
		String lowerCaseSubtopic;
		
		ArrayList subtopicIDList;
		ArrayList subtopicLowerCaseIDList;
		ArrayList uniqueSubtopicList;
		int i;
		int j;
		int l;
		boolean flag;
		
		for(Object topicID:topicSubtopicsWeight.keySet()) {
			
			subtopicsWeight = (Map) topicSubtopicsWeight.get(topicID);
			
			filterredSubtopicsWeight = new HashMap();
			
			subtopicIDList = new ArrayList();
			subtopicLowerCaseIDList = new ArrayList();
			uniqueSubtopicList = new ArrayList();
			
			for(Object subtopic:subtopicsWeight.keySet()) {
				
				subtopicIDList.add(subtopic);
				
				subtopicString = subtopic.toString().toLowerCase();
				subtopicLowerCaseIDList.add(subtopicString);
			}
			
			l = subtopicIDList.size();
			
			for(i = 0; i<l; i++) {

				originalSubtopic = (String) subtopicIDList.get(i);
				flag = true;
				
				for(j = 0; j<l; j++) {
					
					if(i == j) continue;
					
					lowerCaseSubtopic = (String) subtopicLowerCaseIDList.get(j);
					if(originalSubtopic.compareTo(lowerCaseSubtopic)==0)
					{
						flag = false;
						uniqueSubtopicList.add(originalSubtopic);
						break;
					}
					
					if(originalSubtopic.toLowerCase().compareTo(lowerCaseSubtopic)==0) {
						flag = false;
						break;
					}
				}
				
				if(flag) 
					uniqueSubtopicList.add(originalSubtopic);
			}
			
			for(i = 0; i<uniqueSubtopicList.size(); i++) {
				filterredSubtopicsWeight.put(uniqueSubtopicList.get(i), subtopicsWeight.get(uniqueSubtopicList.get(i)));
			}
			
//			System.out.println(topicID + ":" + subtopicsWeight);
//			System.out.println(topicID + ":" + filterredSubtopicsWeight);
			
			topicfilterredSubtopics.put(topicID, filterredSubtopicsWeight);
		}
		
		return topicfilterredSubtopics;
	}
}