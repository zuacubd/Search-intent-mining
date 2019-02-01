package featureExtraction;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import utility.POSTaggerStanford;
import featuresDescriptor.AverageTermLength;
import featuresDescriptor.BM25;
import featuresDescriptor.CoordinationLevelMatching;
import featuresDescriptor.DFHDivergenceFromRandomness;
import featuresDescriptor.DirichletDocumentLikelihoodModelLanguageModel;
import featuresDescriptor.DirichletQueryLikelihoodModelLanguageModel;
import featuresDescriptor.EditDistance;
import featuresDescriptor.ElectionVoting;
import featuresDescriptor.JelinkMercerDocumentLikelihoodModelLanguageModel;
import featuresDescriptor.JelinkMercerQueryLikelihoodModelLanguageModel;
import featuresDescriptor.KulbackLaibler;
import featuresDescriptor.PL2;
import featuresDescriptor.RecipocalRank;
import featuresDescriptor.SearchEngineHitCount;
import featuresDescriptor.SearchEnginePMI;
import featuresDescriptor.SearchEngineWordCooccurance;
import featuresDescriptor.SubStringMatch;
import featuresDescriptor.TermDependencyMarkovRandomField;
import featuresDescriptor.TermDependencyTrigram;
import featuresDescriptor.TermLength;
import featuresDescriptor.TermOverlap;
import featuresDescriptor.TermSynonymOverlap;
import featuresDescriptor.TopicCohesiveness;
import featuresDescriptor.VectorSpaceModel;

public class TopicSubtopicFeatures {

	private Integer numFeatures;

	private Map topicCandidateSubtopics;
	private Map topicSubtopicHitCount;

	private Map termUnigramSubtopicPostingList;
	private Map termBigramSubtopicPostingList;
	private Map termTrigramSubtopicPostingList;
	private Integer numTermUnigramFreq;
	private Integer numTermBigramFreq;
	private Integer numTermTrigramFreq;

	//Query independent features
	private ElectionVoting electionVoting;
	private RecipocalRank recipocalRank;
	private AverageTermLength avgTermLength;
	private TopicCohesiveness topicCohesiveness;
	private TermLength termLength;

	//Query dependent features
	private EditDistance editDistance;
	private SubStringMatch subStringMatch;
	private TermOverlap termOverlap;
	private TermSynonymOverlap termSynonymOverlap;
	
	private VectorSpaceModel vectorSpaceModel;
	private CoordinationLevelMatching coordinationLevelMatch;
	
	private BM25 bm25;
	private DFHDivergenceFromRandomness dphDivergenceFromRandomness;
	private PL2 pl2DivergenceFromRandomess;
	private KulbackLaibler kulbackLaiblerDivergence;
	
	private JelinkMercerQueryLikelihoodModelLanguageModel topicLanguageModelJM;
	private JelinkMercerDocumentLikelihoodModelLanguageModel subtopicLanguageModelJM;
	private DirichletDocumentLikelihoodModelLanguageModel subtopicLanguageModelDirichlet;
	private DirichletQueryLikelihoodModelLanguageModel topicLanguageModelDirichlet;
	
	private TermDependencyMarkovRandomField termDependencyMRF;
	private TermDependencyTrigram termDependencyTrigram;
	
	private SearchEngineHitCount searchEngineHitCount;
	private SearchEnginePMI searchEnginePMI;
	private SearchEngineWordCooccurance searchEngineWordCooccurance;

	private POSTaggerStanford posTaggerStanford;

	public Integer getNumFeatures() {
		return this.numFeatures;
	}

	public TopicSubtopicFeatures(Map topicIDtopicString, Map topicCandidateSubtopics, Map termUnigramSubtopicPostingList, Map termBigramSubtopicPostingList, Map termTrigramSubtopicPostingList, Integer numTermUnigramFreq, Integer numTermBigramFreq, Integer numTermTrigramFreq, Map topicSubtopicHitCount, POSTaggerStanford posTaggerStanford){

		this.topicCandidateSubtopics = topicCandidateSubtopics;
		this.topicSubtopicHitCount = topicSubtopicHitCount;

		this.termUnigramSubtopicPostingList = termUnigramSubtopicPostingList;
		this.termBigramSubtopicPostingList = termBigramSubtopicPostingList;
		this.termTrigramSubtopicPostingList = termTrigramSubtopicPostingList;
		this.numTermUnigramFreq = numTermUnigramFreq;
		this.numTermBigramFreq = numTermBigramFreq;
		this.numTermTrigramFreq = numTermTrigramFreq;
		
		this.posTaggerStanford = posTaggerStanford;
	}

	private void initiateFeatureGenerator(String topicString, Map subtopics) {
		
		this.electionVoting = new ElectionVoting(subtopics);
		this.recipocalRank = new RecipocalRank(subtopics);
		this.topicCohesiveness = new TopicCohesiveness(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.avgTermLength = new AverageTermLength(topicString, subtopics);
		this.termLength = new TermLength(topicString, subtopics);
		
		this.editDistance = new EditDistance(topicString, subtopics);
		this.subStringMatch = new SubStringMatch(topicString, subtopics);
		this.termOverlap = new TermOverlap(topicString, subtopics);
		this.termSynonymOverlap = new TermSynonymOverlap(topicString, subtopics, this.posTaggerStanford);
		
		this.vectorSpaceModel = new VectorSpaceModel(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.coordinationLevelMatch = new CoordinationLevelMatching(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		
		this.topicLanguageModelJM = new JelinkMercerQueryLikelihoodModelLanguageModel(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.subtopicLanguageModelJM = new JelinkMercerDocumentLikelihoodModelLanguageModel(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.topicLanguageModelDirichlet = new DirichletQueryLikelihoodModelLanguageModel(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.subtopicLanguageModelDirichlet = new DirichletDocumentLikelihoodModelLanguageModel(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		
		this.bm25 = new BM25(this.topicCandidateSubtopics, this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.dphDivergenceFromRandomness = new DFHDivergenceFromRandomness(this.topicCandidateSubtopics, this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.pl2DivergenceFromRandomess = new PL2(this.topicCandidateSubtopics, this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		this.kulbackLaiblerDivergence = new KulbackLaibler(this.termUnigramSubtopicPostingList, this.numTermUnigramFreq, topicString, subtopics);
		
		this.termDependencyMRF = new TermDependencyMarkovRandomField(this.termUnigramSubtopicPostingList, this.termBigramSubtopicPostingList, this.numTermUnigramFreq, this.numTermBigramFreq, topicString, subtopics);
		this.termDependencyTrigram = new TermDependencyTrigram(this.termTrigramSubtopicPostingList, this.numTermTrigramFreq, topicString, subtopics);

		this.searchEngineHitCount = new SearchEngineHitCount(this.topicSubtopicHitCount, topicString, subtopics);
		this.searchEnginePMI = new SearchEnginePMI(this.topicSubtopicHitCount, topicString, subtopics);
		this.searchEngineWordCooccurance = new SearchEngineWordCooccurance(this.topicSubtopicHitCount, topicString, subtopics);
	}

	public Map getTopicSubtopicFeatureVectors(String topicID, String topicString, Map subtopics) {

		//Initiate Feature Extractors
		this.initiateFeatureGenerator(topicString, subtopics);
		this.numFeatures = new Integer(24);

		Map subtopicsFeaturesVector;
		Map subtopicQueryIndependentFeatures;
		Map subtopicQueryDependentFeatures;
		
		//Query independent features
		Map subtopicVotingFeature; 
		Map subtopicRecipocalRankFeature;
		Map subtopicAverageTermLengthFeature;
		Map subtopicTopicCohesivenessFeature;
		Map subtopicTermLengthFeature;
		
		//Query dependent features
		Map subtopicEditDistanceFeature;
		Map subtopicSubStringMatchFeature;
		Map subtopicTermOverlapFeature;
		Map subtopicTermSynonymOverlapFeature;
		
		Map subtopicVectorSpaceModelFeature;
		Map subtopicCoordinationLevelMatchingFeature;
		
		Map subtopicBM25Feature;
		Map subtopicDFHDFRFeature;
		Map subtopicPL2DFRFeature;
		Map subtopicKulbackLaiblerFeature;
		
		Map subtopicQLMJMFeature;
		Map subtopicDLMJMFeature;
		Map subtopicQLMDSFeature;
		Map subtopicDLMDSFeature;
		
		Map subtopicTermDependancyMRFFeature;
		Map subtopicTermDependancyTrigramFeature;
		
		Map subtopicSEHitCountFeature;
		Map subtopicSEPMIFeature;
		Map subtopicSEWCFeature;

//		System.out.println("Topic String: "+ topicString);
		//query independent features
		subtopicVotingFeature = this.electionVoting.getSubtopicVote(); 
//		this.info("voting",subtopicVotingFeature);
		subtopicVotingFeature = this.getNormalizedFeatures(subtopicVotingFeature);
//		this.info("voting",subtopicVotingFeature);
		
		subtopicRecipocalRankFeature = this.recipocalRank.getSubtopicRecipocalRank();
//		this.info("recipocal rank",subtopicRecipocalRankFeature);
		subtopicRecipocalRankFeature = this.getNormalizedFeatures(subtopicRecipocalRankFeature);
//		this.info("recipocal rank",subtopicRecipocalRankFeature);
		
		subtopicAverageTermLengthFeature = this.avgTermLength.getSubtopicATL();
//		this.info("avg term length",subtopicAverageTermLengthFeature);
		subtopicAverageTermLengthFeature = this.getNormalizedFeatures(subtopicAverageTermLengthFeature);
//		this.info("avg term length",subtopicAverageTermLengthFeature);
		
		subtopicTopicCohesivenessFeature = this.topicCohesiveness.getSubtopicTopicCohesiveness();
//		this.info("topic cohesiveness",subtopicTopicCohesivenessFeature);
		subtopicTopicCohesivenessFeature = this.getNormalizedFeatures(subtopicTopicCohesivenessFeature);
//		this.info("topic cohesiveness",subtopicTopicCohesivenessFeature);
		
		subtopicTermLengthFeature = this.termLength.getSubtopicLength();
//		this.info("term length",subtopicTermLengthFeature);
		subtopicTermLengthFeature = this.getNormalizedFeatures(subtopicTermLengthFeature);
//		this.info("term length",subtopicTermLengthFeature);
		
		//query dependent features
		subtopicEditDistanceFeature = this.editDistance.getSubtopicEditDistance();
//		this.info("edit distance",subtopicEditDistanceFeature);
		subtopicEditDistanceFeature = this.getNormalizedFeatures(subtopicEditDistanceFeature);
//		this.info("edit distance",subtopicEditDistanceFeature);

		subtopicSubStringMatchFeature = this.subStringMatch.getSubtopicSubStringMatch();
//		this.info("substring",subtopicSubStringMatchFeature);
		subtopicSubStringMatchFeature = this.getNormalizedFeatures(subtopicSubStringMatchFeature);
//		this.info("substring",subtopicSubStringMatchFeature);

		subtopicTermOverlapFeature = this.termOverlap.getSubtopicOverlap();
//		this.info("term overlap",subtopicTermOverlapFeature);
		subtopicTermOverlapFeature = this.getNormalizedFeatures(subtopicTermOverlapFeature);
//		this.info("term overlap",subtopicTermOverlapFeature);
		
		subtopicTermSynonymOverlapFeature = this.termSynonymOverlap.getSubtopicOverlapSyn();
//		this.info("term synonym overlap",subtopicTermSynonymOverlapFeature);
		subtopicTermSynonymOverlapFeature = this.getNormalizedFeatures(subtopicTermSynonymOverlapFeature);
//		this.info("term synonym overlap",subtopicTermSynonymOverlapFeature);

		subtopicVectorSpaceModelFeature = this.vectorSpaceModel.getSubtopicVSM();
//		this.info("vector space model",subtopicVectorSpaceModelFeature);
		subtopicVectorSpaceModelFeature = this.getNormalizedFeatures(subtopicVectorSpaceModelFeature);
//		this.info("vector space model",subtopicVectorSpaceModelFeature);
		
		subtopicCoordinationLevelMatchingFeature = this.coordinationLevelMatch.getSubtopicCLM();
//		this.info("coordination level matching",subtopicCoordinationLevelMatchingFeature);
		subtopicCoordinationLevelMatchingFeature = this.getNormalizedFeatures(subtopicCoordinationLevelMatchingFeature);
//		this.info("coordination level matching",subtopicCoordinationLevelMatchingFeature);
		
		subtopicBM25Feature = this.bm25.getSubtopicBM25();
//		this.info("bm25",subtopicBM25Feature);
		subtopicBM25Feature = this.getNormalizedFeatures(subtopicBM25Feature);
//		this.info("bm25",subtopicBM25Feature);

		subtopicDFHDFRFeature = this.dphDivergenceFromRandomness.getSubtopicDFHDFR();
//		this.info("dfh dfr",subtopicDFHDFRFeature);
		subtopicDFHDFRFeature = this.getNormalizedFeatures(subtopicDFHDFRFeature);
//		this.info("dfh dfr",subtopicDFHDFRFeature);

		subtopicPL2DFRFeature = this.pl2DivergenceFromRandomess.getSubtopicDFR();
//		this.info("pl2",subtopicPL2DFRFeature);
		subtopicPL2DFRFeature = this.getNormalizedFeatures(subtopicPL2DFRFeature);
//		this.info("pl2",subtopicPL2DFRFeature);
		
		subtopicKulbackLaiblerFeature = this.kulbackLaiblerDivergence.getSubtopicKL();
//		this.info("kulback laibler",subtopicKulbackLaiblerFeature);
		subtopicKulbackLaiblerFeature = this.getNormalizedFeatures(subtopicKulbackLaiblerFeature);
//		this.info("kulback laibler",subtopicKulbackLaiblerFeature);
		
		subtopicQLMJMFeature = this.topicLanguageModelJM.getSubtopicTLM();
//		this.info("jelink mercer qlm",subtopicQLMJMFeature);
		subtopicQLMJMFeature = this.getNormalizedFeatures(subtopicQLMJMFeature);
//		this.info("jelink mercer qlm",subtopicQLMJMFeature);
		
		subtopicDLMJMFeature = this.subtopicLanguageModelJM.getSubtopicDLM();
//		this.info("jelink mercer dlm",subtopicDLMJMFeature);
		subtopicDLMJMFeature = this.getNormalizedFeatures(subtopicDLMJMFeature);
//		this.info("jelink mercer dlm",subtopicDLMJMFeature);
		
		subtopicQLMDSFeature = this.topicLanguageModelDirichlet.getSubtopicTLMDS();
//		this.info("dirichlet qlm",subtopicQLMDSFeature);
		subtopicQLMDSFeature = this.getNormalizedFeatures(subtopicQLMDSFeature);
//		this.info("dirichlet qlm",subtopicQLMDSFeature);
		
		subtopicDLMDSFeature = this.subtopicLanguageModelDirichlet.getSubtopicDSDLMLM();
//		this.info("dirichlet dlm",subtopicDLMDSFeature);
		subtopicDLMDSFeature = this.getNormalizedFeatures(subtopicDLMDSFeature);
//		this.info("dirichlet dlm",subtopicDLMDSFeature);

		subtopicTermDependancyMRFFeature = this.termDependencyMRF.getSubtopicTDMRF();
//		this.info("td MRF",subtopicTermDependancyMRFFeature);
		subtopicTermDependancyMRFFeature = this.getNormalizedFeatures(subtopicTermDependancyMRFFeature);
//		this.info("td MRF",subtopicTermDependancyMRFFeature);
		
		subtopicTermDependancyTrigramFeature = this.termDependencyTrigram.getSubtopicTDTrigram();
//		this.info("td Trigram",subtopicTermDependancyTrigramFeature);
		subtopicTermDependancyTrigramFeature = this.getNormalizedFeatures(subtopicTermDependancyTrigramFeature);
//		this.info("td Trigram",subtopicTermDependancyTrigramFeature);

		subtopicSEHitCountFeature = this.searchEngineHitCount.getSubtopicHitCount();
//		this.info("se hit",subtopicSEHitCountFeature);
		subtopicSEHitCountFeature = this.getNormalizedFeatures(subtopicSEHitCountFeature);
//		this.info("se hit",subtopicSEHitCountFeature);

		subtopicSEPMIFeature = this.searchEnginePMI.getSubtopicPMI();
//		this.info("PMI",subtopicSEPMIFeature);
		subtopicSEPMIFeature = this.getNormalizedFeatures(subtopicSEPMIFeature);
//		this.info("PMI",subtopicSEPMIFeature);

		subtopicSEWCFeature = this.searchEngineWordCooccurance.getSubtopicWordCooccurance();
//		this.info("Word CO",subtopicSEWCFeature);
		subtopicSEWCFeature = this.getNormalizedFeatures(subtopicSEWCFeature);
//		this.info("Word CO",subtopicSEWCFeature);
		
		
		subtopicQueryIndependentFeatures = this.getQueryIndependentFeatures(subtopics, 
				subtopicVotingFeature, subtopicRecipocalRankFeature, subtopicAverageTermLengthFeature, subtopicTopicCohesivenessFeature, subtopicTermLengthFeature);

//		subtopicQueryIndependentFeatures = this.getQueryIndependentFeatures(subtopics, 
//				subtopicAverageTermLengthFeature, subtopicTopicCohesivenessFeature, subtopicTermLengthFeature);

		
		subtopicQueryDependentFeatures = this.getQueryDependentFeatures(subtopics, 
				subtopicEditDistanceFeature, subtopicSubStringMatchFeature, subtopicTermOverlapFeature, subtopicTermSynonymOverlapFeature, 
				subtopicVectorSpaceModelFeature,subtopicCoordinationLevelMatchingFeature, subtopicBM25Feature, subtopicDFHDFRFeature, 
				subtopicPL2DFRFeature, subtopicKulbackLaiblerFeature, subtopicQLMJMFeature, subtopicDLMJMFeature, 
				subtopicQLMDSFeature, subtopicDLMDSFeature, subtopicTermDependancyMRFFeature, subtopicTermDependancyTrigramFeature, 
				subtopicSEHitCountFeature, subtopicSEPMIFeature, subtopicSEWCFeature);

//		subtopicQueryDependentFeatures = this.getQueryDependentFeatures(subtopics, 
//				subtopicEditDistanceFeature, subtopicSubStringMatchFeature, subtopicTermOverlapFeature, subtopicTermSynonymOverlapFeature, 
//				subtopicVectorSpaceModelFeature,subtopicCoordinationLevelMatchingFeature, subtopicBM25Feature, subtopicDFHDFRFeature, 
//				subtopicPL2DFRFeature, subtopicKulbackLaiblerFeature, subtopicQLMJMFeature, subtopicDLMJMFeature, 
//				subtopicQLMDSFeature, subtopicDLMDSFeature, subtopicTermDependancyMRFFeature, subtopicTermDependancyTrigramFeature);
		
		subtopicsFeaturesVector = this.getCombinedQueryDependentAndIndependentFeaturesVector(subtopics,
				subtopicQueryDependentFeatures, subtopicQueryIndependentFeatures);
				

		return subtopicsFeaturesVector;
	}

	private Map getCombinedQueryDependentAndIndependentFeaturesVector( Map subtopics,
			Map subtopicQueryDependentFeatures,
			Map subtopicQueryIndependentFeatures) {
		
		Map subtopicsFeaturesVector = new HashMap();
		Vector features;
		Vector queryDependentFeatureVector;
		Vector queryIndependentFeatureVector;

		Double featureValue;
		int i;
		int l;
		
		for(Object subtopic:subtopics.keySet()) {
			
			queryDependentFeatureVector = (Vector) subtopicQueryDependentFeatures.get(subtopic);
			queryIndependentFeatureVector = (Vector) subtopicQueryIndependentFeatures.get(subtopic);
			
			features = new Vector();
			
			l = queryIndependentFeatureVector.size();
			for(i = 0; i<l; i++) {
				featureValue = (Double) queryIndependentFeatureVector.get(i);
				features.add(featureValue);
			}
			
			l = queryDependentFeatureVector.size();
			for(i = 0; i<l; i++) {
				featureValue = (Double) queryDependentFeatureVector.get(i);
				features.add(featureValue);
			}
			
			subtopicsFeaturesVector.put(subtopic, features);
		}
		
		return subtopicsFeaturesVector;
	}

	private Map getQueryDependentFeatures(Map subtopics,
			Map subtopicEditDistanceFeature, Map subtopicSubStringMatchFeature, Map subtopicTermOverlapFeature, 
			Map subtopicTermSynonymOverlapFeature, Map subtopicVectorSpaceModelFeature, Map subtopicCoordinationLevelMatchingFeature,
			Map subtopicBM25Feature, Map subtopicDFHDFRFeature, Map subtopicPL2DFRFeature, Map subtopicKullbackLaiblerFeature, 
			Map subtopicQLMJMFeature, Map subtopicDLMJMFeature, Map subtopicQLMDSFeature, Map subtopicDLMDSFeature, Map subtopicTermDependancyMRFFeature,
			Map subtopicTermDependancyTrigramFeature, Map subtopicSEHitCountFeature, Map subtopicSEPMIFeature, Map subtopicSEWCFeature) {
		
		Map subtopicQueryDependentFeaturesVector = new HashMap();
		
		Double editDistanceFeature;
		Double subStringMatchFeature;
		Double termOverlapFeature;
		Double termSynonymOverlapFeature;
		Double vectorSpaceModelFeature;
		Double coordinationLevelMatchingFeature;
		Double bm25Feature;
		Double dfhDFRFeature;
		Double pl2DFRFeature;
		Double klFeature;
		Double qlmJMFeature;
		Double dlmJMFeature;
		Double qlmDSFeature;
		Double dlmDSFeature;
		Double tdMRFFeature;
		Double tdTrigramFeature;
		Double seHitCountFeature;
		Double sePMIFeature;
		Double seWCFeature;
		
		Vector features;

		for(Object subtopic:subtopics.keySet()) {

			features = new Vector();

			editDistanceFeature = (Double) subtopicEditDistanceFeature.get(subtopic);
			subStringMatchFeature = (Double) subtopicSubStringMatchFeature.get(subtopic);
			termOverlapFeature = (Double) subtopicTermOverlapFeature.get(subtopic);
			termSynonymOverlapFeature = (Double) subtopicTermSynonymOverlapFeature.get(subtopic);
			vectorSpaceModelFeature = (Double) subtopicVectorSpaceModelFeature.get(subtopic);
			coordinationLevelMatchingFeature = (Double) subtopicCoordinationLevelMatchingFeature.get(subtopic);
			bm25Feature = (Double) subtopicBM25Feature.get(subtopic);
			dfhDFRFeature = (Double) subtopicDFHDFRFeature.get(subtopic);
			pl2DFRFeature = (Double) subtopicPL2DFRFeature.get(subtopic);
			klFeature = (Double) subtopicKullbackLaiblerFeature.get(subtopic);
			qlmJMFeature = (Double) subtopicQLMJMFeature.get(subtopic);
			dlmJMFeature = (Double) subtopicDLMJMFeature.get(subtopic);
			qlmDSFeature = (Double) subtopicQLMDSFeature.get(subtopic);
			dlmDSFeature = (Double) subtopicDLMDSFeature.get(subtopic);
			tdMRFFeature = (Double) subtopicTermDependancyMRFFeature.get(subtopic);
			tdTrigramFeature = (Double) subtopicTermDependancyTrigramFeature.get(subtopic);
			seHitCountFeature = (Double) subtopicSEHitCountFeature.get(subtopic);
			sePMIFeature = (Double) subtopicSEPMIFeature.get(subtopic);
			seWCFeature = (Double) subtopicSEWCFeature.get(subtopic);

			features.add(editDistanceFeature); 
			features.add(subStringMatchFeature); 
			features.add(termOverlapFeature); 
			features.add(termSynonymOverlapFeature);

			features.add(vectorSpaceModelFeature); 
			features.add(coordinationLevelMatchingFeature);
			
			features.add(bm25Feature); 
			features.add(dfhDFRFeature);
			features.add(pl2DFRFeature); 
			features.add(klFeature); 
			
			features.add(qlmJMFeature); 
			features.add(dlmJMFeature); 
			features.add(qlmDSFeature); 
			features.add(dlmDSFeature); 
			
			features.add(tdMRFFeature); 
			features.add(tdTrigramFeature); 
			
			features.add(seHitCountFeature);
			features.add(sePMIFeature); 
			features.add(seWCFeature);
			
			subtopicQueryDependentFeaturesVector.put(subtopic, features);
		}

		return subtopicQueryDependentFeaturesVector;
	}

	private Map getQueryIndependentFeatures(Map subtopics, Map subtopicVotingFeature, Map subtopicRecipocalRankFeature, Map subtopicAverageTermLengthFeature,
			Map subtopicTopicCohesivenessFeature, Map subtopicTermLengthFeature) {

		Map subtopicQueryIndependentFeaturesVector = new HashMap();

		Double votingFeature;
		Double recipocalRankFeature;
		Double avgTermLengthFeature;
		Double topicCohesivenessFeature;
		Double termLengthFeature;

		Vector features;

		for(Object subtopic:subtopics.keySet()) {

			features = new Vector();

			votingFeature = (Double) subtopicVotingFeature.get(subtopic);
			recipocalRankFeature = (Double) subtopicRecipocalRankFeature.get(subtopic);
			avgTermLengthFeature = (Double) subtopicAverageTermLengthFeature.get(subtopic);
			topicCohesivenessFeature = (Double) subtopicTopicCohesivenessFeature.get(subtopic);
			termLengthFeature = (Double) subtopicTermLengthFeature.get(subtopic);
			
			features.add(votingFeature);
			features.add(recipocalRankFeature);
			features.add(avgTermLengthFeature);
			features.add(topicCohesivenessFeature);
			features.add(termLengthFeature);
			
			subtopicQueryIndependentFeaturesVector.put(subtopic, features);
		}

		return subtopicQueryIndependentFeaturesVector;
	}

	private Map getCombinedMultipleFeatures(Map subtopics,
			Map subtopicVote, Map subtopicRecipocalRank, Map subtopicEditDistance, 
			Map subtopicVSM, Map subtopicCLM, Map subtopicTLM, Map subtopicDLM, Map subtopicKL, Map subtopicBM25, Map subtopicDFR, 
			Map subtopicTDMRF, Map subtopicExactMatch, Map subtopicATL, Map subtopicLength) 
	{
		Map subtopicFeaturesVector = new HashMap();

		Double voteFeature;
		Double recipocalRankFeature;
		Double semEditDistanceFeature;
		Double vsmFeature;
		Double clmFeature;
		Double tlmFeature;
		Double dlmFeature;
		Double klFeature;
		Double bm25Feature;
		Double dfrFeature;
		Double tdmrfFeature;
		Double emFeature;
		Double atlFeature;
		Double lengthFeature;

		Vector features;

		for(Object subtopic:subtopics.keySet()) {

			features = new Vector();

			voteFeature = (Double) subtopicVote.get(subtopic);
			recipocalRankFeature = (Double) subtopicRecipocalRank.get(subtopic);
			semEditDistanceFeature = (Double) subtopicEditDistance.get(subtopic);
			vsmFeature = (Double) subtopicVSM.get(subtopic);
			clmFeature = (Double) subtopicCLM.get(subtopic);
			tlmFeature = (Double) subtopicTLM.get(subtopic);
			dlmFeature = (Double) subtopicDLM.get(subtopic);
			klFeature = (Double) subtopicKL.get(subtopic);
			bm25Feature = (Double) subtopicBM25.get(subtopic);
			dfrFeature = (Double) subtopicDFR.get(subtopic);
			tdmrfFeature = (Double) subtopicTDMRF.get(subtopic);
			emFeature = (Double) subtopicExactMatch.get(subtopic);
			atlFeature = (Double) subtopicATL.get(subtopic);
			lengthFeature = (Double) subtopicLength.get(subtopic);

			features.add(voteFeature);
			features.add(recipocalRankFeature);
			features.add(semEditDistanceFeature);
			features.add(vsmFeature);
			features.add(clmFeature);
			features.add(tlmFeature);
			features.add(dlmFeature);
			features.add(klFeature);
			features.add(bm25Feature);
			features.add(dfrFeature);
			features.add(tdmrfFeature);
			features.add(emFeature);
			features.add(atlFeature);
			features.add(lengthFeature);

			subtopicFeaturesVector.put(subtopic, features);
		}

		return subtopicFeaturesVector;
	}

//	private Map getNormalizedFeatures(Map subtopicFeature) {
//
//		Map subtopicNormalizedFeature = new HashMap();
//
//		Double weight;
//		Double maximumWeight;
//		Double minimumWeight;
//		Double normF1;
//
//		subtopicFeature.size();
//		maximumWeight = 0.0;
//		minimumWeight = 1000.0;
//		for(Object subtopic:subtopicFeature.keySet()) {
//
//			weight = (Double) subtopicFeature.get(subtopic);
//
//			if(maximumWeight < weight)
//				maximumWeight = weight;
//			if(minimumWeight > weight) 
//				minimumWeight = weight;
//		}
//
//		for(Object subtopic:subtopicFeature.keySet()) {
//			weight = (Double) subtopicFeature.get(subtopic);
//			normF1 = (weight.doubleValue() - minimumWeight.doubleValue())/(maximumWeight.doubleValue() - minimumWeight.doubleValue());
//			subtopicNormalizedFeature.put(subtopic, normF1);
//		}
//
//		return subtopicNormalizedFeature;
//	}

	private Map getNormalizedFeatures(Map subtopicFeature) {

		Map subtopicNormalizedFeature = new HashMap();

		Double weight;
		Double sumWeight;
		Double normF1;

		subtopicFeature.size();
		sumWeight = 0.0;
		
		for(Object subtopic:subtopicFeature.keySet()) {

			weight = (Double) subtopicFeature.get(subtopic);

			sumWeight += weight;
		}

		for(Object subtopic:subtopicFeature.keySet()) {
			weight = (Double) subtopicFeature.get(subtopic);
			normF1 = weight/sumWeight;
			subtopicNormalizedFeature.put(subtopic, normF1);
		}

		return subtopicNormalizedFeature;
	}
	
	private void info(String msg, Map data) {
				System.out.println(msg +" : "+ data);
	}

}
