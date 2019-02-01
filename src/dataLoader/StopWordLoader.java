package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import dataPath.TrainingDataPath;

public class StopWordLoader {

	private ArrayList stopwords = new ArrayList();
	
	public ArrayList getStopWords(){ return this.stopwords;}
	
	public StopWordLoader(String source){

		this.parseStopWord(source);
		
	}

	private void parseStopWord(String source){

		String thisLine;
		String stopword;
		boolean flag;
		
		try{
			File file = new File(source);
			FileInputStream fin = new FileInputStream(file);
			BufferedReader reader = new BufferedReader(new InputStreamReader(fin));

			while((thisLine = reader.readLine())!=null){

				stopword = thisLine.trim();
				flag = this.stopwords.contains(stopword);
				if(!flag){
					this.stopwords.add(stopword);
				}
		
			}
			reader.close();
			
		}catch(Exception e){

		}
	}

	public static void main(String[] args){

		TrainingDataPath paths = new TrainingDataPath();
		StopWordLoader 	stopwordparser = new StopWordLoader(paths.getStopWordDataPath());
		System.out.println(stopwordparser.stopwords.size());

	}
}
