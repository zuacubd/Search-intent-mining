package rankingDiversified;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.lemurproject.kstem.KrovetzStemmer;

import dataLoader.StopWordLoader;
import dataPath.TrainingDataPath;
import utility.SortMapDouble;

public class DiversifiedRanking {

	private ArrayList stopWords;
	private Map topicSubtopicRankingScore;
	private Map topicSubtopicFeatureVectors;
	
	private Map topicSubtopicsDiversifiedRankingScore;
	
	public Map getTopicSubtopicsDiversifiedRankingScore() {
		return this.topicSubtopicsDiversifiedRankingScore;
	}
	
	private SortMapDouble sortMapDouble;
	private StopWordLoader stopWordLoader;
	private KrovetzStemmer krovetzStemmer;
	
	public DiversifiedRanking(Map topicSubtopicFeatureVectors, Map topicSubtopicRankingScore) {

		this.stopWordLoader = new StopWordLoader(TrainingDataPath.getStopWordDataPath());
		this.stopWords = this.stopWordLoader.getStopWords();
		this.krovetzStemmer = new KrovetzStemmer();
		
		this.topicSubtopicFeatureVectors = topicSubtopicFeatureVectors;
		this.topicSubtopicRankingScore = topicSubtopicRankingScore;
		
		this.topicSubtopicsDiversifiedRankingScore = this.getTopSubtopicsDiversifiedRankedList(this.topicSubtopicFeatureVectors, this.topicSubtopicRankingScore);
	}

	private Map getTopSubtopicsDiversifiedRankedList(Map topicSubtopicsFeatureVectors, Map topicSubtopicRankingScore) {

		Map topicSubtopicMMRDiversifiedRankingScore = new HashMap();
		Vector R;
		Vector S;
		Map subtopicsMMRDiversifiedRankingScore;
		
		Map subtopicsRankingScore;
		Map subtopicsFeatureVectors;
		
		int k;
		
		for(Object topicID:topicSubtopicRankingScore.keySet()) {
			
			subtopicsRankingScore = (Map) topicSubtopicRankingScore.get(topicID);
			subtopicsFeatureVectors = (Map) topicSubtopicsFeatureVectors.get(topicID);
		
			this.sortMapDouble = new SortMapDouble();
			
			k = subtopicsRankingScore.size();

			S = new Vector();
			R = this.sortMapDouble.getTopKKey(subtopicsRankingScore, k);
		
			subtopicsMMRDiversifiedRankingScore = this.getSubtopicDiversifiedRankedList(R, subtopicsRankingScore, S, subtopicsFeatureVectors);
		
			topicSubtopicMMRDiversifiedRankingScore.put(topicID, subtopicsMMRDiversifiedRankingScore);
		}
		
		return topicSubtopicMMRDiversifiedRankingScore;
	}

	private Map getSubtopicDiversifiedRankedList(Vector R, Map subtopicsRankingScore, Vector S, Map subtopicsFeatureVectors) {
		
		Map subtopicsMMRWeights = new HashMap();
		ArrayList candidateSubtopicWeight;
		String subtopic;
		double weight;
		double maximumMarginalRelevance;
		int idx;
		
//		System.out.println("Start processing diversified ranked list.. |R| = " + R.size() +" |Subtopics| = "+ subtopicsRankingScore.size() + " |S|= "+ S.size());
		
		idx = 0;
		
		if(R.size()>idx) {
			subtopic = (String) R.get(idx);
			weight = (Double) subtopicsRankingScore.get(subtopic);
			
			subtopicsMMRWeights.put(subtopic, weight);
			S.add(subtopic);
//			R.remove(idx);
			R.remove(subtopic);
		}
	
//		System.out.println("Top ranked subtopics added to subset S...|R| = " + R.size() +" |Subtopics| = "+ subtopicsRankingScore.size() + " |S|= "+ S.size());

		
		while(R.size()>0) {
			
//			System.out.println("Start processing maximum marginal relevance");

			candidateSubtopicWeight = this.getMaximumMarginalRelevance(R, subtopicsRankingScore, S, subtopicsFeatureVectors);
			
			subtopic = (String) candidateSubtopicWeight.get(0);
			weight = (Double) candidateSubtopicWeight.get(1);
			
//			System.out.println("Current top ranked diversified subtopic is "+ subtopic +" : " + weight);
			
			S.add(subtopic);
			subtopicsMMRWeights.put(subtopic, weight);
//			idx = R.indexOf(subtopic);
//			R.remove(idx);
			R.remove(subtopic);
			
//			System.out.println("Top ranked subtopics added to subset S...|R| = " + R.size() +" |Subtopics| = "+ subtopicsRankingScore.size() + " |S|= "+ S.size());
		}
		
		return subtopicsMMRWeights;
	}

	private ArrayList getMaximumMarginalRelevance(Vector R, Map subtopicsRankingScore, Vector S, Map subtopicsFeatureVectors) {
		
		ArrayList subtopicMMR = new ArrayList();
		String subtopic;
		int idx; 
		double lembda;
		double oneMinusLembda;
		double sim1;
		double sim2;
		double weight;
		double maxWeight;
		String candidateSubtopic = null;
		
		lembda = 0.80;
		oneMinusLembda = 1.0 - lembda;
		
		idx = 0;
		maxWeight = 0.0;
	
		if(idx <R.size()) {
			subtopic = (String) R.get(idx);
			sim1 = (Double) subtopicsRankingScore.get(subtopic);
			sim2 = this.getMaximumSimilarity(subtopic, S, subtopicsFeatureVectors);
	
			weight = lembda * sim1 - oneMinusLembda * sim2;
			
			maxWeight = weight;
			candidateSubtopic = subtopic;
			idx++;
		}
		
		while(idx<R.size()) {
			
			subtopic = (String) R.get(idx);
			sim1 = (Double) subtopicsRankingScore.get(subtopic);
			sim2 = this.getMaximumSimilarity(subtopic, S, subtopicsFeatureVectors);
		
			weight = lembda * sim1 - oneMinusLembda * sim2;
		
//			System.out.println(" subtopic :" + subtopic +":"+weight);
			
			if(maxWeight<weight) 
			{
				maxWeight = weight;
				candidateSubtopic = subtopic;
			}
			idx++;
		}
		
		subtopicMMR.add(candidateSubtopic);
		subtopicMMR.add(maxWeight);
		
		return subtopicMMR;
	}

	private double getMaximumSimilarity(String subtopic1, Vector S, Map subtopicsFeatureVectors) {

		int idx;
		String subtopic2;
		double sim;
		double max;
		
		idx = 0;
		max = 0.0;
		
		while(idx<S.size()) {
			
			subtopic2 = (String) S.get(idx);
			//			sim = this.getCosineSimilarity(featureVector1, featureVector2);
			sim = this.getEditDistanceSimilarity(subtopic1, subtopic2);
			
			if(max<sim) max = sim;
			idx++;
		}
		
		return max;
	}
	
	private double getEditDistanceSimilarity(String subtopic1, String subtopic2) {
		
		double sim;
		int distance;
		Integer maxLen;
		int i;
		String term;
		String stemmedTerm;
		String[] SubTopic1 = subtopic1.toLowerCase().split(" ");
		String[] SubTopic2 = subtopic2.toLowerCase().split(" ");
		
		Arrays.sort(SubTopic1);
		Arrays.sort(SubTopic2);
		
		for(i = 0; i<SubTopic1.length; i++) {
			term = SubTopic1[i].trim();
			
			if(this.stopWords.contains(term)) continue;
			
			stemmedTerm = this.krovetzStemmer.stem(term);
			if(this.stopWords.contains(stemmedTerm)) continue;

			SubTopic1[i] = stemmedTerm;
		}
		
		for(i = 0; i<SubTopic2.length; i++) {
			term = SubTopic2[i].trim();

			if(this.stopWords.contains(term)) continue;

			stemmedTerm = this.krovetzStemmer.stem(term);
			if(this.stopWords.contains(stemmedTerm)) continue;

			SubTopic2[i] = stemmedTerm;
		}
		
		distance = this.editDistance(SubTopic1, SubTopic2);
		
		maxLen = Math.max(SubTopic1.length, SubTopic2.length);
		sim = 1.0 - (distance * 1.0)/maxLen.doubleValue(); //similarity based on edit distance
//		System.out.println(maxLen +" : "+ distance + " : "+sim);
		
		return sim;
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
