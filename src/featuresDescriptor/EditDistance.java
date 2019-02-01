package featuresDescriptor;

import java.util.HashMap;
import java.util.Map;

public class EditDistance {

	Map subtopicEditDistance;
	
	public Map getSubtopicEditDistance() {
		return this.subtopicEditDistance;
	}
	
	public EditDistance(String topicString, Map subtopics) {
		
		this.subtopicEditDistance = this.getEstimatedEditDistance(topicString, subtopics);
	}
	
	private Map getEstimatedEditDistance(String topicString, Map subtopics) {
		
		Map subtopicEditDistance = new HashMap();
		String subtopicString;
		
		String[] subtopicParts;
		String[] topicParts;
		int distance;
		Integer maxLen;
		
		Double sim = new Double(0.0);
		
		topicParts = topicString.split(" ");
		
		
		for(Object subtopic:subtopics.keySet()) {
			
			subtopicString = subtopic.toString();
			subtopicParts = subtopicString.split(" ");
			
			sim = 0.0;
			distance = this.editDistance(topicParts, subtopicParts);
			maxLen = Math.max(topicParts.length, subtopicParts.length);
			
			sim = 1.0 - distance/maxLen.doubleValue(); //similarity based on edit distance
			
			subtopicEditDistance.put(subtopic, sim);
		}
		
		return subtopicEditDistance;
	}
	
	private int editDistance(String[] s, String[] t){
		
		int m = s.length;
		int n = t.length;
		int i;
		int j;
		
		int[][]d = new int[m+1][n+1];
		
		for(i=0; i<=m; i++){
			d[i][0]=i;
		}
		
		for(j=0; j<=n; j++){
			d[0][j]=j;
		}
		
		for(j=1;j<=n;j++){
			for(i=1;i<=m;i++){
				if(s[i-1].compareTo(t[j-1]) == 0){
					d[i][j] = d[i-1][j-1];
				}
				else{
					d[i][j] = min((d[i-1][j]+1),(d[i][j-1]+1),(d[i-1][j-1]+1));
				}
			}
		}
		return(d[m][n]);
	}
	
	private int min(int a,int b,int c){
		return(Math.min(Math.min(a,b),c));
	}
}
