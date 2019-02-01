package runSubmission;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import utility.SortMapDouble;
import utilityStemmer.PlingStemmer;
import dataLoader.Intent2QuerySuggestionLoader;
import dataLoader.Intent2TopicLoader;
import dataLoader.QuerySuggestionFromLogsLoader;
import dataPath.TrainingDataPath;

public class MapReduce {

	private static final int NumTopics = 0;
	private Map bingSuggestion;
	private Map bingCompletion;
	private Map googleCompletion;
	private Map yahooCompletion;
	private Map queryLogSubtopics;
	private Map topicsIDName;
	private ArrayList topicList;

	private SortMapDouble sortMapDouble;
	private Intent2TopicLoader topics;
	private Intent2QuerySuggestionLoader querySuggestion;
	private QuerySuggestionFromLogsLoader queryLogsSuggestion;
	private PlingStemmer stemmer;
	
	MapReduce(String intent2TopicsPath, String bingQuerySuggestionPath,
			String bingQueryCompletionPath, String googleQueryCompletionPath,
			String yahooQueryCompletionPath, String queryLogsSubtopicsPath){

		this.topics=new Intent2TopicLoader(intent2TopicsPath);
		this.topicsIDName = this.topics.getIntent2TopicIDtopicString();
		this.topicList = this.topics.getTopicsList();

		this.querySuggestion = new Intent2QuerySuggestionLoader(bingQuerySuggestionPath,
				bingQueryCompletionPath, googleQueryCompletionPath,
				yahooQueryCompletionPath);

		this.bingSuggestion = this.querySuggestion.getBingQuerySuggestion();
		this.bingCompletion = this.querySuggestion.getBingQueryCompletion();
		this.googleCompletion = this.querySuggestion.getGoogleQueryCompletion();
		this.yahooCompletion = this.querySuggestion.getYahooQueryCompletion();

		this.queryLogsSuggestion = new QuerySuggestionFromLogsLoader(queryLogsSubtopicsPath);
		this.queryLogSubtopics = this.queryLogsSuggestion.getQueryLogsSubtopics();
	
		this.stemmer = new PlingStemmer();
	}

	public void processSubtopics(){

		int NumTopics=0;
		String topicID;
		Object[] topicsID;
		String subTopicString;
		String[] subTopicsParts;
		Map subTopic;
		//		int count=1;
		Integer Freq;
		String Topic;

		topicsID=this.topicsIDName.keySet().toArray();
		NumTopics=topicsID.length;
		Arrays.sort(topicsID, 0, NumTopics);

		for(int i=0;i<NumTopics;i++){
			subTopic=new HashMap();

			//BingSuggestion

			subTopicString=(String) this.bingSuggestion.get(topicsID[i]);
			subTopicsParts=subTopicString.split("\t");

			for(int j=0;j<subTopicsParts.length;j++){
				Freq=(Integer) subTopic.get(subTopicsParts[j]);
				if(Freq==null){
					subTopic.put(subTopicsParts[j], 1);
				}
				else{
					subTopic.put(subTopicsParts[j], Freq+1);
				}
			}
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic.keySet().size());
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic);

			//BingCompletion

			subTopicString=(String) this.bingCompletion.get(topicsID[i]);
			subTopicsParts=subTopicString.split("\t");

			for(int j=0;j<subTopicsParts.length;j++){
				Freq=(Integer) subTopic.get(subTopicsParts[j]);
				if(Freq==null){
					subTopic.put(subTopicsParts[j], 1);
				}
				else{
					subTopic.put(subTopicsParts[j], Freq+1);
				}
			}
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic.keySet().size());
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic);


			//GoogleCompletion

			subTopicString=(String) this.googleCompletion.get(topicsID[i]);
			subTopicsParts=subTopicString.split("\t");

			for(int j=0;j<subTopicsParts.length;j++){
				Freq=(Integer) subTopic.get(subTopicsParts[j]);
				if(Freq==null){
					subTopic.put(subTopicsParts[j], 1);
				}
				else{
					subTopic.put(subTopicsParts[j], Freq+1);
				}
			}
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic.keySet().size());
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic);

			//yahooCompletion

			subTopicString=(String) this.yahooCompletion.get(topicsID[i]);
			subTopicsParts=subTopicString.split("\t");

			for(int j=0;j<subTopicsParts.length;j++){
				Freq=(Integer) subTopic.get(subTopicsParts[j]);
				if(Freq==null){
					subTopic.put(subTopicsParts[j], 1);
				}
				else{
					subTopic.put(subTopicsParts[j], Freq+1);
				}
			}
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic.keySet().size());
			//			System.out.println((i+1)+":"+topicsID[i]+":"+subTopic);
			//			this.makeRun1A(i, topicsID[i].toString(), subTopic);
			//			this.makeRun2A(i, topicsID[i].toString(), subTopic);
			//			this.makeRun3A(i, topicsID[i].toString(), subTopic);
			Topic=(String) this.topicsIDName.get(topicsID[i]);
			//			this.makeRun4A(i, Topic, topicsID[i].toString(), subTopic);
			//			this.makeRun5A(i, Topic, topicsID[i].toString(), subTopic);
		}
		//		System.out.println(count);
	}

	public void processQueryTopicSubtopics(String testRunFile) {

		Map queryTopicsSortedSubtopicQS = this.getSearhEnginesQuerySuggestionsByRank();
//		this.showMap(queryTopicsSortedSubtopicQS);
//		this.showMap(this.queryLogSubtopics);
		Map queryTopicsCombinedSubtopics = this.getCombinedSubtopics(this.queryLogSubtopics, queryTopicsSortedSubtopicQS);
		
		//Normalizing weights

		Map queryTopicsNormalizedSubtopics = new HashMap();
		Map weightedSubtopics;


		for(Object queryTopic:queryTopicsCombinedSubtopics.keySet()) {
			weightedSubtopics = (Map) queryTopicsCombinedSubtopics.get(queryTopic);
			
			weightedSubtopics = this.removeDuplicateSubtopic(weightedSubtopics, queryTopic.toString());
			
			weightedSubtopics = this.getNormalizedMap(weightedSubtopics);
//			weightedSubtopics = this.getMeanVarianceBasedNormalization(weightedSubtopics);

			queryTopicsNormalizedSubtopics.put(queryTopic, weightedSubtopics);
		}

		this.makeTestRun(queryTopicsNormalizedSubtopics, testRunFile);
	}

	
	private Map removeDuplicateSubtopic(Map weightedSubtopics, String queryTopic) {

		String topicString;
		
		topicString = (String) this.topicsIDName.get(queryTopic);
		
		weightedSubtopics.remove(topicString);
		
		topicString = topicString.toLowerCase();
		
		weightedSubtopics.remove(topicString);
		
		return weightedSubtopics;
	}

	public Map getSearhEnginesQuerySuggestionsByRank() {

		HashMap queryTopicsSESubtopics = new HashMap();
		
		HashMap queryTopicsSortedSubtopic = new HashMap();

		Map subtopicFeature;
		Map weightedSubtopics;
		Vector suggestions;
		Vector ranks;

		Integer rank;
		Double weight;
		String candidateSubtopic;

		int i;
		int l;
		int subtopicTermLength;
		
		for(Object topicID:this.bingSuggestion.keySet()) {

			weightedSubtopics = (Map) queryTopicsSESubtopics.get(topicID);

			suggestions = (Vector) this.bingSuggestion.get(topicID);

			if(suggestions != null) {
				l = suggestions.size();

				if(weightedSubtopics == null) 
					weightedSubtopics = new HashMap();

				for(i = 0; i<l; i++) {
					candidateSubtopic = (String) suggestions.get(i);
					
//					candidateSubtopic = this.getLowerCaseOfSubtopic(candidateSubtopic);
//					candidateSubtopic = this.getSingularFormOfSubtopic(candidateSubtopic);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				
				queryTopicsSESubtopics.put(topicID, weightedSubtopics);
//				System.out.println(topicID +":"+ weightedSubtopics);
			}
		}

		for(Object topicID:this.bingCompletion.keySet()) {

			weightedSubtopics = (Map) queryTopicsSESubtopics.get(topicID);

			suggestions = (Vector) this.bingCompletion.get(topicID);
			
			if(suggestions != null) {
				l = suggestions.size();

				if(weightedSubtopics == null) 
					weightedSubtopics = new HashMap();

				for(i = 0; i<l; i++) {
					candidateSubtopic = (String) suggestions.get(i);
					
//					candidateSubtopic = this.getLowerCaseOfSubtopic(candidateSubtopic);
//					candidateSubtopic = this.getSingularFormOfSubtopic(candidateSubtopic);					
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);

					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				queryTopicsSESubtopics.put(topicID, weightedSubtopics);
//				System.out.println(topicID +":"+ weightedSubtopics);
			}
		}

		for(Object topicID:this.googleCompletion.keySet()) {

			weightedSubtopics = (Map) queryTopicsSESubtopics.get(topicID);

			suggestions = (Vector) this.googleCompletion.get(topicID);

			if(suggestions != null) {
				l = suggestions.size();

				if(weightedSubtopics == null) 
					weightedSubtopics = new HashMap();

				for(i = 0; i<l; i++) {
					candidateSubtopic = (String) suggestions.get(i);
					
//					candidateSubtopic = this.getLowerCaseOfSubtopic(candidateSubtopic);
//					candidateSubtopic = this.getSingularFormOfSubtopic(candidateSubtopic);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				queryTopicsSESubtopics.put(topicID, weightedSubtopics);
//				System.out.println(topicID +":"+ weightedSubtopics);
			}
		}

		for(Object topicID:this.yahooCompletion.keySet()) {

			weightedSubtopics = (Map) queryTopicsSESubtopics.get(topicID);

			suggestions = (Vector) this.yahooCompletion.get(topicID);

			if(suggestions != null) {
				l = suggestions.size();

				if(weightedSubtopics == null) 
					weightedSubtopics = new HashMap();

				for(i = 0; i<l; i++) {
					candidateSubtopic = (String) suggestions.get(i);
					
//					candidateSubtopic = this.getLowerCaseOfSubtopic(candidateSubtopic);
//					candidateSubtopic = this.getSingularFormOfSubtopic(candidateSubtopic);
					
					ranks = (Vector) weightedSubtopics.get(candidateSubtopic);
					if(ranks == null) {
						ranks = new Vector();
					}
					ranks.add(i+1);
					weightedSubtopics.put(candidateSubtopic, ranks);
				}
				queryTopicsSESubtopics.put(topicID, weightedSubtopics);
//				System.out.println(topicID +":"+ weightedSubtopics);

			}
		}

		for(Object queryTopic:queryTopicsSESubtopics.keySet()) {
			weightedSubtopics = (Map) queryTopicsSESubtopics.get(queryTopic);

//			System.out.println(queryTopic + ":"+weightedSubtopics);
			
			subtopicFeature = new HashMap();
			
			for(Object subtopic:weightedSubtopics.keySet()) {
				ranks = (Vector) weightedSubtopics.get(subtopic);

				l = ranks.size();
				weight = 0.0;
				for(i = 0; i<l; i++) {
					rank = (Integer) ranks.get(i);
					weight += 1.0/(rank);
				}
		
				subtopicTermLength = subtopic.toString().split(" ").length;
//				subtopicTermLength = subtopic.toString().length();
				weight = weight * (l/3.0) + (1.0/subtopic.toString().length());
//				weight = weight * l + (1.0/subtopicTermLength);
//				weight = (1.0/subtopicTermLength);	
				
				subtopicFeature.put(subtopic, weight);
			}

			int topK = 30;
			this.sortMapDouble = new SortMapDouble();
			Map sortedSubtopic = this.sortMapDouble.getTopkeyValue(subtopicFeature, topK);
//			queryTopicsSortedSubtopic.put(queryTopic, subtopicFeature);
			queryTopicsSortedSubtopic.put(queryTopic, sortedSubtopic);
		}
		return queryTopicsSortedSubtopic;
	}
	
	public String getLowerCaseOfSubtopic(String subtopic) {
		
		return subtopic.toLowerCase();
		
	}
	
	public String getSingularFormOfSubtopic(String subtopic) {
		
		StringBuffer tempSingularSubtopic = new StringBuffer();
		
		String[] subtopicParts;
		String subtopicPart;
		String stemmedPart;
		String singularSubtopic;
		int i;
		int l;
		
		subtopicParts = subtopic.split(" ");
		l = subtopicParts.length;
		
		tempSingularSubtopic.delete(0,  tempSingularSubtopic.length());
		for(i = 0; i<l; i++) {
			subtopicPart = subtopicParts[i];
			stemmedPart = this.stemmer.stem(subtopicPart);
			tempSingularSubtopic.append(stemmedPart+" ");
		}
		singularSubtopic = tempSingularSubtopic.toString().trim();
		
		return singularSubtopic;
	}
	
	public Map getCombinedSubtopics(Map queryLogsSubtopics, Map querySuggestionSubtopics) {

		Map queryTopiccombinedSubtopics = new HashMap();

		Double weightQL;
		Double weightQS;


		Map weightedSubtopicsQL;
		Map weightedSubtopicsQS;
		Map combinedSubtopics;


		for(Object topicID:querySuggestionSubtopics.keySet()) {

			weightedSubtopicsQL = (Map) queryLogsSubtopics.get(topicID);
			weightedSubtopicsQS = (Map) querySuggestionSubtopics.get(topicID);
			
			if(weightedSubtopicsQL == null) {
				combinedSubtopics = weightedSubtopicsQS;
				queryTopiccombinedSubtopics.put(topicID, combinedSubtopics);
				continue;
			}

			if(weightedSubtopicsQS == null) {
				combinedSubtopics = weightedSubtopicsQL;
				queryTopiccombinedSubtopics.put(topicID, combinedSubtopics);
				continue;
			}


			combinedSubtopics = new HashMap();
			
			for(Object subtopic:weightedSubtopicsQS.keySet()) {
				weightQL = (Double) weightedSubtopicsQL.get(subtopic);

				if(weightQL == null)
					combinedSubtopics.put(subtopic, weightedSubtopicsQS.get(subtopic));
				else 
					combinedSubtopics.put(subtopic, weightQL + (Double) weightedSubtopicsQS.get(subtopic));
			}

			for(Object subtopic:weightedSubtopicsQL.keySet()) {
				weightQS = (Double) weightedSubtopicsQS.get(subtopic);

				if(weightQS == null)
					combinedSubtopics.put(subtopic, weightedSubtopicsQL.get(subtopic));
//				else 
//					combinedSubtopics.put(subtopic, weightQS + (Double) weightedSubtopicsQL.get(subtopic));
			}
			
			queryTopiccombinedSubtopics.put(topicID, combinedSubtopics);
		}
		return queryTopiccombinedSubtopics;
	}

	private Map getNormalizedMap(Map dataList) {

		Map normalizedData = new HashMap();

		Double weight;
		Double maximumWeight;
		Double minimumWeight;
		Double weightSum;
		Double normF1;
		Double normF2;
		int i;
		int N;

		N = dataList.size();
		maximumWeight = 0.0;
		weightSum = 0.0;
		minimumWeight = 10000.0;

		for(Object id:dataList.keySet()) {

			weight = (Double) dataList.get(id);
			if(maximumWeight<weight) maximumWeight = weight;
			if(minimumWeight>weight) minimumWeight = weight;
			
			weightSum += weight;
		}

		for(Object id:dataList.keySet()) {
			weight = (Double) dataList.get(id);

//			weight = weight.doubleValue()/weightSum.doubleValue();
			
			normF1 = (weight.doubleValue() - minimumWeight.doubleValue())/(maximumWeight.doubleValue() - minimumWeight.doubleValue());
			normF2 = normF1/(1.0 +normF1);
			normalizedData.put(id, normF2);
		}

		return normalizedData;
	}

	private Map getMeanVarianceBasedNormalization(Map dataList) {

		Map normalizedData = new HashMap();

		Double weight;
		Double mean;
		Double standardDeviation;
		Double sumWeight;
		Double meanNorm;
		Double sumNorm;
		Double meanDiff;
		Double meanVarianceNorm;
		int i;
		int N;

		N = dataList.size();
		mean = 0.0;
		sumWeight = 0.0;
		standardDeviation = 0.0;
		
		// Estimating mean
		for(Object id:dataList.keySet()) {

			weight = (Double) dataList.get(id);			
			sumWeight += weight;
		}

		mean = sumWeight/N;
		
		sumNorm = 0.0;
		//Estimating standard deviation
		for(Object id:dataList.keySet()) {
			weight = (Double) dataList.get(id);

			meanDiff = weight - mean;
			meanDiff = Math.pow(meanDiff,2);
			sumNorm += meanDiff;
		}		
			meanNorm = sumNorm/N;
			standardDeviation = Math.sqrt(meanNorm);
			
		//Normalization using mean and variance
			
		for(Object id:dataList.keySet()) {
			weight = (Double) dataList.get(id);
			
			meanVarianceNorm = (weight - mean)/standardDeviation;
			
			normalizedData.put(id, meanVarianceNorm);
		}
			
		return normalizedData;
	}
	
	public void makeTestRun(Map queryTopicsNormalizedSubtopics, String testRunFile) {

		int i;
		int j;
		int l;
		int N;
		int rank;

		Vector sortedSubtopicsKey;
		Map weightedSubtopics;
		String topicID;
		String topicString;
		String subtopic;
		Double prob;

		N = this.topicList.size();

		try{
			File file=new File(testRunFile);			
			PrintWriter runs = null;
			boolean append = false;
			runs = new PrintWriter(new FileWriter(file, append));

			for(i = 0; i<N; i++) {

				topicID = (String) this.topicList.get(i);
				topicString = (String) this.topicsIDName.get(topicID);

				weightedSubtopics = (Map) queryTopicsNormalizedSubtopics.get(topicID);

				this.sortMapDouble = new SortMapDouble();

				sortedSubtopicsKey = this.sortMapDouble.getTopKKey(weightedSubtopics, weightedSubtopics.size());


				if(i==0){
					runs.write("<SYSDESC>Query Logs, Query Dependent Features, Query Independent Features, Query Suggestion Ranklist</SYSDESC>"+"\n");
					System.out.println("<SYSDESC>Query Logs, Query Dependent Features, Query Independent Features, Query Suggestion Ranklist</SYSDESC>");
				}

				rank = 1;
				l = sortedSubtopicsKey.size();
				for(j = 0; j<l; j++){

					subtopic = (String) sortedSubtopicsKey.get(j);

					prob = (Double) weightedSubtopics.get(subtopic);

					runs.write(topicID+";"+0+";"+subtopic+";"+rank+";"+prob+";"+"TestRUN"+"\n");
					System.out.println(topicID+";"+0+";"+subtopic+";"+rank+";"+prob+";"+"TestRUN");
					rank++;
				}
			}	
			runs.close();
		}
		catch(Exception e){
			System.out.println(e);
		}

	}

	
	public void makeRun1A(int i, String TopicID, Map SubTopicData){

		int Rank;
		float Score;
		float RawScore;


		String Subtopic;

		try{
			File file=new File("./data/SEM12-S-E-1A.txt");			
			PrintWriter runs = null;
			boolean append = true;
			runs = new PrintWriter(new FileWriter(file, append));

			if(i==0){
				runs.write("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>"+"\n");
				System.out.println("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>");
			}

			Rank=1;
			for(Object id:SubTopicData.keySet()){
				Subtopic=(String) id;
				RawScore=(float) (1.0/Rank);
				Score=(float) (Math.floor(RawScore*100+0.5)/100);
				runs.write(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-1A"+"\n");
				System.out.println(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-1A");
				Rank++;
			}
			runs.close();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

	public void makeRun2A(int i, String TopicID, Map SubTopicData){

		int Rank;
		float Score;
		float RawScore;
		String Subtopic;


		//ValueComparator comparator =  new ValueComparator(SubTopicData);
		//TreeMap sortedSubTopicData = new TreeMap(comparator);
		//sortedSubTopicData.putAll(SubTopicData);
		Map sortedSubTopicData=this.sortByValue(SubTopicData);

		try{
			File file=new File("./data/SEM12-S-E-2A.txt");			
			PrintWriter runs = null;
			boolean append = true;
			runs = new PrintWriter(new FileWriter(file, append));

			if(i==0){
				runs.write("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>"+"\n");
				System.out.println("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>");
			}

			Rank=1;
			for(Object id:sortedSubTopicData.keySet()){
				Subtopic=(String) id;
				RawScore=(float) (1.0/Rank);
				Score=(float) (Math.floor(RawScore*100+0.5)/100);
				runs.write(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-2A"+"\n");
				System.out.println(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-2A");
				Rank++;
			}
			runs.close();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

	public void makeRun3A(int i, String TopicID, Map SubTopicData){

		int Rank;
		float Score;
		float RawScore;
		String Subtopic;
		Object[] subTopics;
		Object temp;
		Integer val1;
		Integer val2;
		String sub1;
		String sub2;

		int length;
		//ValueComparator comparator =  new ValueComparator(SubTopicData);
		//TreeMap sortedSubTopicData = new TreeMap(comparator);
		//sortedSubTopicData.putAll(SubTopicData);
		Map sortedSubTopicData=this.sortByValue(SubTopicData);

		subTopics=sortedSubTopicData.keySet().toArray();

		//Sort by co-occurance and ascii order

		length=subTopics.length;

		//		for(int k=0;k<length;k++){
		//			System.out.print(subTopics[k]+";");
		//		}
		//		 System.out.println();
		//		 
		for(int ii=0;ii<length;ii++)
			for(int j=0;j<(length-ii-1);j++)
			{
				sub1=subTopics[j].toString();
				sub2=subTopics[j+1].toString();
				val1=(Integer) sortedSubTopicData.get(subTopics[j]);
				val2=(Integer) sortedSubTopicData.get(subTopics[j+1]);
				if(val1==val2){
					if(sub1.compareTo(sub2)>0){
						temp=subTopics[j];
						subTopics[j]=subTopics[j+1];
						subTopics[j+1]=temp;
					}
				}
			}

		//		for(int k=0;k<length;k++){
		//			System.out.print(subTopics[k]+";");
		//		}
		//		 System.out.println();

		try{
			File file=new File("./data/SEM12-S-E-3A.txt");			
			PrintWriter runs = null;
			boolean append = true;
			runs = new PrintWriter(new FileWriter(file, append));

			if(i==0){
				runs.write("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>"+"\n");
				System.out.println("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>");
			}

			Rank=1;
			//for(Object id:sortedSubTopicData.keySet())
			for(int k=0;k<length;k++)
			{
				Subtopic=(String) subTopics[k];
				RawScore=(float) (1.0/Rank);
				Score=(float) (Math.floor(RawScore*100+0.5)/100);
				runs.write(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-3A"+"\n");
				System.out.println(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-3A");
				Rank++;
			}
			runs.close();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

	public void makeRun4A(int i, String Topic, String TopicID, Map SubTopicData){

		int Rank;
		float Score;
		float RawScore;
		String Subtopic;
		Object[] subTopics;
		Object temp;
		Integer val1;
		Integer val2;
		String sub1;
		String sub2;
		int distance;
		int length;

		Map sortedSubTopicData;

		//subTopics=sortedSubTopicData.keySet().toArray();
		Map SubTopicEditDistance=new HashMap();

		subTopics=SubTopicData.keySet().toArray();


		//Sort by co-occurance and ascii order

		length=subTopics.length;

		//		System.out.print(TopicID+":");
		//		for(int k=0;k<length;k++)
		//			System.out.print(subTopics[k]+";");
		//		System.out.println();

		for(int k=0;k<length;k++){
			//System.out.print(subTopics[k]+";");
			distance=this.editDistance(Topic, subTopics[k].toString());
			SubTopicEditDistance.put(subTopics[k], Integer.valueOf(distance));
		}
		sortedSubTopicData=this.sortByValue(SubTopicEditDistance);
		subTopics=sortedSubTopicData.keySet().toArray();

		//		System.out.print(TopicID+":");
		//		for(int k=0;k<length;k++)
		//			System.out.print(subTopics[k]+";");
		//		System.out.println();

		//		System.out.print(TopicID+":");
		//		for(Object id:sortedSubTopicData.keySet())
		//			System.out.print(id+":"+sortedSubTopicData.get(id)+";");
		//		System.out.println();

		//		 System.out.println();
		//		 
		//		for(int ii=0;ii<length;ii++)
		//			for(int j=0;j<(length-ii-1);j++)
		//			{
		//				sub1=subTopics[j].toString();
		//				sub2=subTopics[j+1].toString();
		//				val1=(Integer) SubTopicData.get(subTopics[j]);
		//				val2=(Integer) SubTopicData.get(subTopics[j+1]);
		//				if(val1==val2){
		//					if(this.editDistance(sub1, sub2)){
		//						temp=subTopics[j];
		//						subTopics[j]=subTopics[j+1];
		//						subTopics[j+1]=temp;
		//					}
		//				}
		//			}
		//		
		////		for(int k=0;k<length;k++){
		////			System.out.print(subTopics[k]+";");
		////		}
		////		 System.out.println();
		//		
		try{
			File file=new File("./data/SEM12-S-E-4A.txt");			
			PrintWriter runs = null;
			boolean append = true;
			runs = new PrintWriter(new FileWriter(file, append));

			if(i==0){
				runs.write("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>"+"\n");
				System.out.println("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>");
			}

			Rank=1;
			//for(Object id:sortedSubTopicData.keySet())
			for(int k=0;k<length;k++)
			{
				Subtopic=(String) subTopics[k];
				RawScore=(float) (1.0/Rank);
				Score=(float) (Math.floor(RawScore*100+0.5)/100);
				runs.write(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-4A"+"\n");
				System.out.println(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-4A");
				Rank++;
			}
			runs.close();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

	public void makeRun5A(int i, String Topic, String TopicID, Map SubTopicData){

		int Rank;
		float Score;
		float RawScore;
		String Subtopic;
		Object[] subTopics;
		Object temp;
		Integer val1;
		Integer val2;
		String sub1;
		String sub2;
		int distance;
		int length;
		Integer cooc1;
		Integer cooc2;

		Map sortedSubTopicData;

		//subTopics=sortedSubTopicData.keySet().toArray();
		Map SubTopicEditDistance=new HashMap();

		subTopics=SubTopicData.keySet().toArray();


		//Sort by co-occurance and ascii order

		length=subTopics.length;

		//		System.out.print(TopicID+":");
		//		for(int k=0;k<length;k++)
		//			System.out.print(subTopics[k]+";");
		//		System.out.println();

		for(int k=0;k<length;k++){
			//System.out.print(subTopics[k]+";");
			distance=this.editDistance(Topic, subTopics[k].toString());
			SubTopicEditDistance.put(subTopics[k], Integer.valueOf(distance));
		}
		sortedSubTopicData=this.sortByValue(SubTopicEditDistance);
		subTopics=sortedSubTopicData.keySet().toArray();

		//		System.out.print(TopicID+":");
		//		for(int k=0;k<length;k++)
		//			System.out.print(subTopics[k]+";");
		//		System.out.println();

		//		System.out.print(TopicID+":");
		//		for(Object id:sortedSubTopicData.keySet())
		//			System.out.print(id+":"+sortedSubTopicData.get(id)+";");
		//		System.out.println();

		//		 System.out.println();
		//		 
		for(int ii=0;ii<length;ii++)
			for(int j=0;j<(length-ii-1);j++)
			{
				sub1=subTopics[j].toString();
				sub2=subTopics[j+1].toString();
				val1=(Integer) SubTopicEditDistance.get(subTopics[j]);
				val2=(Integer) SubTopicEditDistance.get(subTopics[j+1]);

				if(val1==val2){
					cooc1=(Integer) SubTopicData.get(subTopics[j]);
					cooc2=(Integer) SubTopicData.get(subTopics[j+1]);

					if(cooc1<cooc2){
						temp=subTopics[j];
						subTopics[j]=subTopics[j+1];
						subTopics[j+1]=temp;
					}
				}
			}
		//		
		//		System.out.print(TopicID+":");
		//		for(int k=0;k<length;k++){
		//			System.out.print(subTopics[k]+";");
		//		}
		//		 System.out.println();
		//		
		try{
			File file=new File("./data/SEM12-S-E-5A.txt");			
			PrintWriter runs = null;
			boolean append = true;
			runs = new PrintWriter(new FileWriter(file, append));

			if(i==0){
				runs.write("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>"+"\n");
				System.out.println("<SYSDESC>English SubTopic Mining in Knowledge Data Engineering and Information Retrieval Lab</SYSDESC>");
			}

			Rank=1;
			//for(Object id:sortedSubTopicData.keySet())
			for(int k=0;k<length;k++)
			{
				Subtopic=(String) subTopics[k];
				RawScore=(float) (1.0/Rank);
				Score=(float) (Math.floor(RawScore*100+0.5)/100);
				runs.write(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-5A"+"\n");
				System.out.println(TopicID+";"+0+";"+Subtopic+";"+Rank+";"+Score+";"+"SEM12-S-E-5A");
				Rank++;
			}
			runs.close();
		}catch(Exception e){
			System.out.println(e);
		}

	}
	

	public static int editDistance(String s, String t){
		int m=s.length();
		int n=t.length();
		int[][]d=new int[m+1][n+1];
		for(int i=0;i<=m;i++){
			d[i][0]=i;
		}
		for(int j=0;j<=n;j++){
			d[0][j]=j;
		}
		for(int j=1;j<=n;j++){
			for(int i=1;i<=m;i++){
				if(s.charAt(i-1)==t.charAt(j-1)){
					d[i][j]=d[i-1][j-1];
				}
				else{
					d[i][j]=min((d[i-1][j]+1),(d[i][j-1]+1),(d[i-1][j-1]+1));
				}
			}
		}
		return(d[m][n]);
	}

	
	public static int min(int a,int b,int c){
		return(Math.min(Math.min(a,b),c));
	}
	

	static Map sortByValue(Map map) {
		LinkedList list = new LinkedList(map.entrySet());
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
						.compareTo(((Map.Entry) (o2)).getValue());
			}
		});

		Map result = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry)it.next();
			result.put(entry.getKey(), entry.getValue());
		}
		return result;
	} 

	public void showMap(Map data) {
		
		System.out.println("Total Items : "+ data.size());

		for(Object id:data.keySet()) {
			System.out.println(id +" : "+ data.get(id));
		}
	}
	
	public static void main(String arg[]){
		TrainingDataPath trainingDataPath = new TrainingDataPath();

		MapReduce mapReduce=new MapReduce(trainingDataPath.getIntent2TopicsPath(), trainingDataPath.getBingQuerySuggestionPath(), trainingDataPath.getBingQueryCompletionPath(), trainingDataPath.getGoogleQueryCompletionPath(), trainingDataPath.getYahooQueryCompletionPath(), trainingDataPath.getQueryLogsSubtopicsPath());
//		mapReduce.processSubtopics();
		mapReduce.processQueryTopicSubtopics(trainingDataPath.getTestRunFilePath());
	}
}
