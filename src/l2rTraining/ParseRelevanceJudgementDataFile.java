package l2rTraining;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utility.FileUtils;
import dataLoader.Intent2TopicLoader;
import dataPath.TrainingDataPath;

public class ParseRelevanceJudgementDataFile {

	private TrainingDataPath trainingDataPath;
	private ArrayList intent2TopicsList;
	private Map topicSubtopicRel;
	
	private Intent2TopicLoader intent2TopicLoader;
	
	public Map getTopicSubtopicRel() {
		return this.topicSubtopicRel;
	}
	
	public ParseRelevanceJudgementDataFile() {
		
		this.trainingDataPath = new TrainingDataPath();
		
		this.intent2TopicLoader = new Intent2TopicLoader(this.trainingDataPath.getIntent2TopicsPath());
		this.intent2TopicsList = this.intent2TopicLoader.getTopicsList();
		
		this.topicSubtopicRel = this.getTopicSubtopicRelevanceLevel(this.trainingDataPath.getImineRelevanceJudgementDataPath());
		
	}

	private Map getTopicSubtopicRelevanceLevel(
			String imineRelevanceJudgementDataFilePath) {

		Map topicSubtopicRel = new HashMap();
		Map subtopicsRels;

		String content = FileUtils.read(imineRelevanceJudgementDataFilePath, "utf-8");
		String[] lines = content.split("\n");
		String[] lineParts;
		
		String topicID;
		String subtopicString;
		
		int i;
		int n;
		int rel;
		
		n = lines.length;
		
		for(i = 0; i<n; i++) {
			
			lineParts = lines[i].split(";");
			topicID = lineParts[0];
			rel = Integer.parseInt(lineParts[1]);
			subtopicString = lineParts[2];
			
			subtopicsRels = (Map) topicSubtopicRel.get(topicID);
			if(subtopicsRels == null) subtopicsRels = new HashMap();
			
			subtopicsRels.put(subtopicString, rel);
			topicSubtopicRel.put(topicID, subtopicsRels);
		}
		
		return topicSubtopicRel;
	}

	private Map getRelevancyNormalized(Map data) {
		
		Map submap;
		int max;
		int val;
		
		for(Object id:data.keySet()) {
			submap = (Map) data.get(id);
			
			max = this.getMaximum(submap);
		
			for(Object subid:submap.keySet()) {
				val = (Integer) submap.get(subid);
				val = max - val;
				submap.put(subid, val);
			}
			data.put(id, submap);
		}
		return data;
	}
	
	private int getMaximum(Map submap) {

		int max;
		int val;
		
		max = 0;
		for(Object id:submap.keySet()) {
		
			val = (Integer) submap.get(id);
			if(max<val) max = val;
		}
		
		return max;
	}

	public static void main(String[] args) {
		
		ParseRelevanceJudgementDataFile parseRelevanceJudgementDataFile = new ParseRelevanceJudgementDataFile();
	}
	
}
