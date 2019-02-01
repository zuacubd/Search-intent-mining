package test;

import utilityStemmer.PlingStemmer;

public class StemmerTest {

	public static void main(String[] args) {
		
		String term = "horses";
		
		PlingStemmer stemmer = new PlingStemmer();
		System.out.println("Stemmer form of "+term +" is "+ stemmer.stem(term));
//		System.out.println("Hello");
	}
}
