package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class RecipocalRank {

	private Map subtopicRecipocalRank;
	
	public Map getSubtopicRecipocalRank() { return this.subtopicRecipocalRank;}
	
	public RecipocalRank(Map subtopics) {
		
		this.subtopicRecipocalRank = this.getEstimatedRecipocalLength(subtopics);
		
	}
	
	private Map getEstimatedRecipocalLength(Map subtopics) {
		
		Map subtopicRecipocalRank = new HashMap();
		Vector ranks;
		Integer rank;
		Double weight = new Double(0.0);
		
		int i;
		int l;
		
		for(Object subtopic:subtopics.keySet()) {
			
			ranks = (Vector) subtopics.get(subtopic);
			
			weight = 0.0;
			l = ranks.size();
			for(i = 0; i<l; i++) {
				rank = (Integer) ranks.get(i);
				weight += (1.0/rank.doubleValue());
			}
			
			subtopicRecipocalRank.put(subtopic, weight);
		}
		
		return subtopicRecipocalRank;
	}
	
	public static void main(String[] args) {
		
	}
}
