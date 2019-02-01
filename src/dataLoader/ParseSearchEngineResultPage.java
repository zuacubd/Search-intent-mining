package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import dataPath.TrainingDataPath;

public class ParseSearchEngineResultPage {

	Map topicSubtopicSEHitCount;
	
	public Map getTopicSubtopicSEHitCount() {
		return this.topicSubtopicSEHitCount;
	}

	public ParseSearchEngineResultPage(String topicSubtopicSearchEngineResultPage) {

		this.topicSubtopicSEHitCount = this.parseTopicSubtopicSERPage(topicSubtopicSearchEngineResultPage);
	}

	private Map parseTopicSubtopicSERPage(String topicSubtopicSearchEngineResultPage) {

		Map topicSubtopicHitCount;

		topicSubtopicHitCount = this.getTopicSubtopicHitCount(topicSubtopicSearchEngineResultPage);

		return topicSubtopicHitCount;
	}

	private Map getTopicSubtopicHitCount(String topicSubtopicSearchEngineResultPage) {

		Map topicSubtopicHitCount = new HashMap();

		String line;
		String topicString = null;
		String hitCount = null;
		String tag;
		int position;
		int start = 0;
		int end = 0;
		Double hitSearchEngine;

		try {

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(topicSubtopicSearchEngineResultPage))));

			line = reader.readLine();
			line = line.trim();

			if(line.compareTo("<doc>")==0) {

				do 
				{
					line = reader.readLine();

					if(line.compareTo("<record>")==0) {

						do {

							line = reader.readLine();
							position = line.indexOf(">");
							tag = line.substring(0, position+1);

							if(tag.compareTo("<topic>")==0){
								start = position + 1;
								end = line.lastIndexOf("<");

								topicString = line.substring(start, end);
							}

							if(tag.compareTo("<subtopic>")==0){
								start = position + 1;
								end = line.lastIndexOf("<");

								topicString = line.substring(start, end);
							}

							if(tag.compareTo("<hit>")==0){

								start = position + 1;
								end = line.lastIndexOf("<");
								if(start == end)
									hitCount = "0.0";
								else
									hitCount = line.substring(start, end);
							}

							if(line.compareTo("</record>")==0) {
								
								hitSearchEngine = Double.valueOf(hitCount);
								topicSubtopicHitCount.put(topicString, hitSearchEngine);
								break;
							}
							
						}while(true);
					}
					
					if(line.compareTo("</doc>")==0) 
						break;
				}
				while(true);
			}
			reader.close();
		}catch(Exception e) {
			
			System.out.println(e.getMessage());
			System.out.println(topicString + ":" + start + ":"+ end);
		}

		return topicSubtopicHitCount;
	}
	
	public void showMap(Map data) {

		System.out.println(data.size());

		for(Object item:data.keySet()) {
			
			System.out.println(item +" : "+data.get(item));
		}
	}
	
	public static void main(String[] args) {

		TrainingDataPath trainingDataPath = new TrainingDataPath();
		ParseSearchEngineResultPage parseSERP = new ParseSearchEngineResultPage(trainingDataPath.getTopicSubtopicSearchEngineResultPage());
		
		parseSERP.showMap(parseSERP.topicSubtopicSEHitCount);
		System.out.println(parseSERP.topicSubtopicSEHitCount.size());
	}
}
