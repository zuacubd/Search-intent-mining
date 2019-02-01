package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ElectionVoting {

	private Map subtopicVote;
	
	public Map getSubtopicVote() { return this.subtopicVote;}

	public ElectionVoting(Map subtopics) {
		
		this.subtopicVote = this.getEstimatedVote(subtopics);
	}
	
	private Map getEstimatedVote(Map subtopics) {
		
		Map subtopicVote = new HashMap();
		Vector ranks;
		
		Integer vote;
		Double voteFeature;
		
		for(Object subtopic:subtopics.keySet()) {
			
			ranks = (Vector) subtopics.get(subtopic);
			
			vote = ranks.size();
			voteFeature = vote.doubleValue();
			subtopicVote.put(subtopic, voteFeature);
		}
		
		return subtopicVote;
	}
	
	public static void main(String[] agrs) {
		
	}
}
