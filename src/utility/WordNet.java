package utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.smu.tspell.wordnet.NounSynset;
import edu.smu.tspell.wordnet.Synset;
import edu.smu.tspell.wordnet.SynsetType;
import edu.smu.tspell.wordnet.WordNetDatabase;
import edu.smu.tspell.wordnet.impl.file.ReferenceSynset;

public class WordNet{
	
	WordNetDatabase database;
	
	public WordNet(){
		
		System.setProperty("wordnet.database.dir", "/usr/local/WordNet-3.0/dict/");
		this.database = WordNetDatabase.getFileInstance();
	}
	
	public ArrayList getSynset(String term, SynsetType type){
		
		ArrayList synonymList = new ArrayList();
		ReferenceSynset refSynset;
		Synset[] synsets = database.getSynsets(term, type); 
		String[] synonym;
		
		for (int i = 0; i < synsets.length; i++) { 
			refSynset=(ReferenceSynset) synsets[i];

//			System.out.println(synsets[i]);
			synonym = refSynset.getWordForms();
			
			for(int j = 0; j<synonym.length; j++) {
//				System.out.println(synonym[j]);
				if(!synonymList.contains(synonym[j]))
					synonymList.add(synonym[j]);
			}
		}
		return synonymList;
	}
	
	
	public Set<String> getNounHypernym(String lemma) {
		Set<String> hypernyms = new HashSet<String>();
		int synsetNum = 5;
		Synset[] synsets = this.database.getSynsets(lemma, SynsetType.NOUN);
		
		
		for (Synset synset : synsets) {
			NounSynset nounSynset = (NounSynset) synset;
			NounSynset[] hypernymSynset = nounSynset.getHypernyms();
			for (NounSynset set : hypernymSynset) {
				System.out.println(set);
				hypernyms.addAll(Arrays.asList(set.getWordForms()));
			}
		}

		return hypernyms;
	}

	public static void main(String[] args) {
		
		WordNet wordNet = new WordNet();
		
		String term;
		SynsetType noun = SynsetType.NOUN;
		SynsetType adj = SynsetType.ADJECTIVE;
		SynsetType adjSatt = SynsetType.ADJECTIVE_SATELLITE;
		SynsetType verb = SynsetType.VERB;
		SynsetType adverb = SynsetType.ADVERB;
		
		term = "DOG";
//		System.out.println(wordNet.getSynset(term, noun));
//		wordNet.getSynset(term, adj);
//		wordNet.getSynset(term, adjSatt);
//		System.out.println(wordNet.getSynset(term, verb));
//		wordNet.getSynset(term, adverb);
		
		System.out.println(wordNet.getNounHypernym(term));
	}
}
