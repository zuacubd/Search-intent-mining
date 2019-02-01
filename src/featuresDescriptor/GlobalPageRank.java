package featuresDescriptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

import algorithm.PageRank;

public class GlobalPageRank {

	private PageRank pageRank;
	
	GlobalPageRank(){
	
		this.pageRank = new PageRank();
	}
	
	public ArrayList getPageRankWeight(SimpleMatrix R, ArrayList candidateDocument) {
		
		ArrayList documentPageRankFeature = this.generateQueryPageRankFeature(R, candidateDocument);
		documentPageRankFeature = this.getNormalizedList(documentPageRankFeature);
		
		return documentPageRankFeature;
	}
	
	public ArrayList generateQueryPageRankFeature(SimpleMatrix R, ArrayList querySeeds) {
		
		ArrayList pageRankFeatureList = new ArrayList();
		
		int N;
		int i;
		
		System.out.println("Estimating PageRank Feature...");
		
		SimpleMatrix V = this.getQueryVector(querySeeds);
		Vector pageRankFeature = this.pageRank.pageRank(R, V);
		
		N = querySeeds.size();
		
		for( i = 0; i<N; i++) {
			pageRankFeatureList.add(pageRankFeature.get(i));
		}

		System.out.println("Finished PageRank Feature Estimation.");
		
		return pageRankFeatureList;
	}

	private SimpleMatrix getQueryVector(ArrayList querySeeds) {
		
		SimpleMatrix V = new SimpleMatrix(querySeeds.size(),1);
		V.set(1);	
		
		return V;
	}

	private ArrayList getNormalizedList(ArrayList dataList) {
		
		ArrayList listItems = new ArrayList();

		Double weight;
		Double maximumWeight;
		Double weightSum;
		Double minimumWeight;
		Double normF1;

		int i;
		int N;
		
		N = dataList.size();
		maximumWeight = 0.0;
		minimumWeight = 1000.0;
		weightSum = 0.0;
		
		for(i = 0; i<N; i++) {
			weight = (Double) dataList.get(i);
			if(maximumWeight < weight)
				maximumWeight = weight;
			if(minimumWeight>weight) minimumWeight = weight;
			
			weightSum += weight;
		}
		
//		System.out.println(maximumWeight);
//		System.out.println(weightSum);
		
		for(i = 0; i<N; i++) {
			weight = (Double) dataList.get(i);
//			weight = weight/maximumWeight;	
//			weight = weight.doubleValue()/weightSum.doubleValue();
			normF1 = (weight.doubleValue() - minimumWeight.doubleValue())/(maximumWeight.doubleValue() - minimumWeight.doubleValue());
			listItems.add(normF1);
		}
		
		return listItems;
	}
	
}
