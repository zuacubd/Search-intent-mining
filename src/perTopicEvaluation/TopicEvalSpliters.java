package perTopicEvaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;

import dataPath.TestingPath;

public class TopicEvalSpliters {

	private Map topicEvalMeasures;

	public TopicEvalSpliters(String topicDnevPath, String iRec10FilePath, String nDCG10FilePath, String dnDCG10FilePath) {

		this.topicEvalMeasures = this.processTopicDnev(topicDnevPath);

		this.writeIRECMeasures(this.topicEvalMeasures, iRec10FilePath);
		
		this.writenDCGMeasures(this.topicEvalMeasures, nDCG10FilePath);
		
		this.writeDnDCG(this.topicEvalMeasures, dnDCG10FilePath);

	}

	private void writeIRECMeasures(Map data, String iRecFilePath) {

		Vector measures;
		int index;
		Double iRec;		

		index = 0;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(iRecFilePath), false));

			for(Object id:data.keySet()) {
				measures = (Vector) data.get(id);
				iRec = (Double) measures.get(index);

				writer.write(id+"\t"+iRec+"\n");
			}

			writer.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}

	private void writenDCGMeasures(Map data, String nDCGFilePath) {
		Vector measures;
		int index;
		Double nDCG;		

		index = 1;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(nDCGFilePath), false));

			for(Object id:data.keySet()) {
				measures = (Vector) data.get(id);
				nDCG = (Double) measures.get(index);

				writer.write(id+"\t"+nDCG+"\n");
			}

			writer.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void writeDnDCG(Map data, String dnDCGFilePath) {
		Vector measures;
		int index;
		Double dnDCG;		

		index = 2;

		try {
			PrintWriter writer = new PrintWriter(new FileWriter(new File(dnDCGFilePath), false));

			for(Object id:data.keySet()) {
				measures = (Vector) data.get(id);
				dnDCG = (Double) measures.get(index);

				writer.write(id+"\t"+dnDCG+"\n");
			}

			writer.close();
		}
		catch(Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public Map processTopicDnev(String filePath) {

		Map topicEvalMeasure = new TreeMap();
		Vector metric;

		String thisLine;
		String topicID;
		String[] lineParts;

		Double nDCG;
		Double iRec;
		Double d_nDCG; 

		int i;		
		int nDCGLine;
		int iRecLine;
		int d_nDCGLine;

		nDCGLine = 15;
		iRecLine = 23;
		d_nDCGLine = 25;

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath))));

			i = 1;	
			metric = new Vector();

			while((thisLine = reader.readLine())!= null) {

				if(i%nDCGLine == 0) {
					lineParts = thisLine.split(" ");
					if(lineParts.length>2) {
						//						System.out.println(lineParts[0]+"\t"+lineParts[1]+"\t"+lineParts[lineParts.length-1]);
						nDCG = Double.parseDouble(lineParts[lineParts.length-1]);
						metric.add(nDCG);
					}
				}

				if(i%iRecLine == 0) {
					lineParts = thisLine.split(" ");
					if(lineParts.length>2) {
						//						System.out.println(lineParts[0]+"\t"+lineParts[1]+"\t"+lineParts[lineParts.length-1]);
						iRec = Double.parseDouble(lineParts[lineParts.length-1]);
						metric.add(iRec);
					}
				}

				if(i%d_nDCGLine == 0) {
					lineParts = thisLine.split(" ");
					if(lineParts.length>2) {
						//						System.out.println(lineParts[0]+"\t"+lineParts[1]+"\t"+lineParts[lineParts.length-1]);
						d_nDCG = Double.parseDouble(lineParts[lineParts.length-1]);
						metric.add(d_nDCG);

						topicID = lineParts[0];
						topicEvalMeasure.put(topicID, metric);
					}
				}

				if(i%25 == 0) {
					i = 0;
					metric = new Vector();
				}
				i++;

			}
			reader.close();
		}catch(Exception e) {
			System.out.println("something is wrong"+ e.getMessage());
		}

		return topicEvalMeasure;
	}

	public static void main(String[] args) {

		TestingPath testingPath = new TestingPath();

		TopicEvalSpliters topicEvalSpliter = new TopicEvalSpliters(testingPath.getTopicDnevPath(), testingPath.getIRec10FilePath(), testingPath.getNDCG10FilePath(), testingPath.getDnDCG10FilePath());
	}
}
