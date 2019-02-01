package utility;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class POSTaggerStanford {

	private MaxentTagger maxEntTagger;
	
	public POSTaggerStanford() {
		this.maxEntTagger = new MaxentTagger("./src/utility/taggerModel/wsj-0-18-bidirectional-nodistsim.tagger");
	}
	
	public String getPOSTag(String term) {

		String[] parts;
		String taggedTerm;
		String pos = null;
		String synsetType = null;

		try {
			taggedTerm = this.maxEntTagger.tagString(term);
			
			parts = taggedTerm.split("_");
			pos = parts[1].trim();
			
			if(pos.compareTo("JJ") == 0 || pos.compareTo("JJR")==0 || pos.compareTo("JJS")==0)
				synsetType = "adjective";
			else if(pos.compareTo("NN")==0 || pos.compareTo("NNS")==0 || pos.compareTo("NNP")==0 || pos.compareTo("NNPS")==0)
				synsetType = "noun";
			else if(pos.compareTo("RB")==0 || pos.compareTo("RBR")==0 || pos.compareTo("RBS")==0)
				synsetType = "adverb";
			else if(pos.compareTo("VB")==0 || pos.compareTo("VBD")==0 || pos.compareTo("VBG")==0 || pos.compareTo("VBN")==0 || pos.compareTo("VBP")==0|| pos.compareTo("VBZ")==0)
				synsetType = "verb";
			else 
				synsetType ="other";
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		
		return synsetType;
	}
	
	public static void main(String[] args) {

		POSTaggerStanford postagger= new POSTaggerStanford();
		System.out.println(postagger.getPOSTag("Arif"));
	}
}
