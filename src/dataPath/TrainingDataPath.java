package dataPath;

public class TrainingDataPath {

	private static String stopWordDataPath = "./src/data/stopword.txt";
	
	private String intent2TopicsPath = "./src/data/intent2_etopics_qs.txt";
	private String bingQuerySuggestion = "./src/data/bing_query_suggestion.txt";
	private String bingQueryCompletion = "./src/data/bing_query_completion.txt";
	private String qoogleQueryCompletion = "./src/data/google_query_completion.txt";
	private String yahooQueryCompletion = "./src/data/yahoo_query_completion.txt";
	
	private String queryLogsSubtopics = "./src/data/queryLogsSubtopics.txt";
	private String topicSubtopicStringList = "./src/data/topicSubtopicsString.txt";
	private String topicSubtopicSearchEngineResultPage = "./src/data/searchEngineResultPage.txt";
	
	//Learning to Rank datasets
	private String l2rTrainingDataPath ="./src/data/ltr/train.txt";
	private String l2rTestingDataPath ="./src/data/ltr/test.txt";
	private String l2rValidationDataPath ="./src/data/ltr/valid.txt";
	
	private String imineL2RTrainingDataPath ="./src/data/ltr/imine-training.txt";
	private String imineL2RTestingDataPath = "./src/data/ltr/imine-testing.txt";
	private String imineL2RModelDataPath = "./src/data/ltr/imine-ensemble.txt";

	private String imineRelevanceJudgementDataFilePath = "./src/data/INTENT-2SME.rev.Dqrels";
	
//	private String testRunFilePath = "./src/data/testRun.txt";
	private String testRunFilePath = "/Users/iarcubd/Dropbox/document/doctoral program/ntcir11imine/ntcir10intent2/sem12/SEM12-INTENT2/testRun.txt";
	
	public static String getStopWordDataPath() {
		return stopWordDataPath;
	}
	
	public String getTopicSubtopicSearchEngineResultPage() {
		return this.topicSubtopicSearchEngineResultPage;
	}
	
	public String getIntent2TopicsPath() {
		return this.intent2TopicsPath;
	}
	
	public String getBingQuerySuggestionPath() {
		return this.bingQuerySuggestion;
	}

	public String getBingQueryCompletionPath() {
		return this.bingQueryCompletion;
	}
	
	public String getGoogleQueryCompletionPath() {
		return this.qoogleQueryCompletion;
	}

	public String getYahooQueryCompletionPath() {
		return this.yahooQueryCompletion;
	}
	
	public String getQueryLogsSubtopicsPath() {
		return this.queryLogsSubtopics;
	}
	
	public String getTopicSubtopicsPath() {
		return this.topicSubtopicStringList;
	}
	
	public String getTestRunFilePath() {
		return this.testRunFilePath;
	}
	
	public String getLearningToRankTrainingDataPath() {
		return this.l2rTrainingDataPath;
	}

	public String getLearningToRankTestingDataPath() {
		return this.l2rTestingDataPath;
	}
	
	public String getLearningToRankValidationDataPath() {
		return this.l2rValidationDataPath;
	}
	
	public String getImineL2RTrainingDataPath() {
		return this.imineL2RTrainingDataPath;
	}
	
	public String getImineL2RTestingDataPath() {
		return this.imineL2RTestingDataPath;
	}
	
	public String getImineL2RModelDataPath() {
		return this.imineL2RModelDataPath;
	}
	
	public String getImineRelevanceJudgementDataPath() {
		return this.imineRelevanceJudgementDataFilePath;
	}
}
