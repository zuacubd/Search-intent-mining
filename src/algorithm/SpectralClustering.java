package algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.ejml.data.Complex64F;
import org.ejml.data.Matrix64F;
import org.ejml.factory.EigenDecomposition;
import org.ejml.simple.SimpleMatrix;

import utility.SortMapDouble;

public class SpectralClustering {

	public SpectralClustering() {
		
		
	}
	
	public SimpleMatrix getSimpleMatrix(double[][] data) {
		
		int rows = data.length;
		int cols = data[0].length;
		
		SimpleMatrix M = new SimpleMatrix(rows, cols);

		for(int i = 0; i<rows; i++)
			for(int j = 0; j<cols; j++)
				M.set(i, j, data[i][j]);
	
		return M;
	}
	
	public double[][] getDoubleMatrix(SimpleMatrix M) {
		
		int rows = M.numRows();
		int cols = M.numCols();
		
		double[][] data = new double[rows][cols];

		for(int i = 0; i<rows; i++)
			for(int j = 0; j<cols; j++)
				data[i][j] = M.get(i,j);
	
		return data;
	}
	
	public SimpleMatrix getAffinityMatrix(SimpleMatrix R) {
		
		int rows = R.numRows();
		double sigma;
		double sim;
		SimpleMatrix W = new SimpleMatrix(rows, rows);
		
		sigma = 0.75;
		sim  = 0;
		
		for(int i = 0; i<rows; i++)
		{
			for(int j = i; j<rows; j++)
			{	
				if(i == j) 
				{
					W.set(i, j, 0); 
					continue;
				}
				
				sim = this.getAffinity(R, i, j, sigma);
				W.set(i,j,sim);
				W.set(j,i,sim);
			}
		}
		return W;
	}

	private double getAffinity(SimpleMatrix A, int i, int j, double sigma) {
			
		double gamma;		
		double l2Norm = 0;
		double d;
		double xi;
		double xj;
		double sim;
		
		int cols = A.numCols();

		for(int k = 0; k<cols; k++) {
			
			xi = A.get(i, k);
			xj = A.get(j,k);
			d = (xi - xj);
			d = this.getSquare(d);
			l2Norm += d;
		}
		
		gamma = -0.5;
		gamma = gamma/this.getSquare(sigma);
		
		sim = this.getExponential(l2Norm, gamma);
		return sim;
	}

	public double getSquare(double w){
			
		return w*w;
	}
	
	public double getExponential(double x, double gamma){
		
		double exp;
	
		exp = x*gamma;
		
		exp = Math.exp(exp);
		
		return exp;
	}
	
	public SimpleMatrix getDiagonalMatrix(SimpleMatrix W) {
		
		SimpleMatrix D = new SimpleMatrix(W.numRows(), W.numCols());
		
		int rows = W.numRows();
		int cols = W.numCols();
		double sum;
		
		for(int i = 0; i<rows; i++) {
			sum = 0;
			for(int j=0; j<cols; j++)
				sum += W.get(i, j);
			D.set(i,i, sum);
		}
		return D;
	}
	
	public SimpleMatrix getInverseSquareRootOFDiagonalMatrix(SimpleMatrix D) {
			
		SimpleMatrix Dinv2 = new SimpleMatrix(D.numRows(), D.numCols());
		double sum;
		int rows = D.numRows();

		for(int i = 0; i<rows; i++) {
			sum = D.get(i,i);
			sum = Math.sqrt(sum);
			Dinv2.set(i,i, 1.0/sum);
		}
		return Dinv2;
	}
	
	public SimpleMatrix getUnnormalizedLaplacian(SimpleMatrix W) {

		SimpleMatrix L;
		SimpleMatrix D = this.getDiagonalMatrix(W);
		
		L = D.minus(W);
		
		return L;
	}
	
	public SimpleMatrix getNormalizedLaplacian(SimpleMatrix W) {
		
		SimpleMatrix D = this.getDiagonalMatrix(W);
		SimpleMatrix L;
		SimpleMatrix Dinv = this.getInverseSquareRootOFDiagonalMatrix(D);
		SimpleMatrix Lsys;
		
		L = D.minus(W);
		Lsys = Dinv.mult(L);
		Lsys = Lsys.mult(Dinv);

		return Lsys;
	}
	
	public SimpleMatrix rowNormalization(SimpleMatrix A){
		
		double sum;
		double freq;
		int rows = A.numRows();
		int cols = A.numCols();
		SimpleMatrix T = new SimpleMatrix(rows, cols);
		
		for(int i= 0; i<rows; i++){
			sum = 0;
			for(int j=0; j<cols; j++){
				freq = A.get(i, j); 
				sum += freq*freq;
			}
			sum = Math.sqrt(sum);

			for(int j=0; j<cols; j++){
				freq = A.get(i,j);
				T.set(i, j, freq/sum);
			}
		}
		return T;
	}

	public void spectralClustering(double[][] data, int numClus) {

		SimpleMatrix W = this.getSimpleMatrix(data);
		SimpleMatrix Lsys = this.getNormalizedLaplacian(W);
		int numEigenVectors = 2;
		
		double[][] LsysDouble = this.getDoubleMatrix(Lsys);
		
//		SimpleMatrix Unk = this.getFirstLargestEigenVectors(LsysDouble, numEigenVectors);
		SimpleMatrix Unk = this.getFirstSmallestEigenVectors(LsysDouble, numEigenVectors);
		
		SimpleMatrix T = this.rowNormalization(Unk);
		
		//KMeans Clustering
		double[][] Td = this.getDoubleMatrix(T.transpose());
		Map cluster = this.KMeansClustering(Td, numClus);
		this.showCluster(cluster, numClus);
	}
	
	public Map spectralClustering(SimpleMatrix R, int numClus) {

		SimpleMatrix W = this.getAffinityMatrix(R);
		SimpleMatrix Lsys = this.getNormalizedLaplacian(W);
		int numEigenVectors = numClus; //first k-largest eigen values
		
		double[][] LsysDouble = this.getDoubleMatrix(Lsys);
		
//		SimpleMatrix Unk = this.getFirstLargestEigenVectors(LsysDouble, numEigenVectors);
		SimpleMatrix Unk = this.getFirstSmallestEigenVectors(LsysDouble, numEigenVectors);
		
		SimpleMatrix T = this.rowNormalization(Unk);
		
		//KMeans Clustering
		double[][] Td = this.getDoubleMatrix(T.transpose());
		Map cluster = this.KMeansClustering(Td, numClus);
		
		return cluster;
	}
	
	public Map KMeansClustering(double[][] data, int numClus) {
		
//		KMeansOptions options = new KMeansOptions();
//		options.nClus = numClus;
//		options.verbose = true;
//		options.maxIter = 100;
//
//		KMeans KMeans= new KMeans(options);
//
//		KMeans.feedData(data);
//		KMeans.clustering(); // Use null for random initialization
//
////		System.out.println("Indicator Matrix:");
////		Matlab.printMatrix(Matlab.full(KMeans.getIndicatorMatrix()));
//		RealMatrix clusters = KMeans.getIndicatorMatrix();
//		
//		Map cluster = new HashMap();
//		
//		int numData = clusters.getRowDimension();
//		double value; 
//		
//		for(int i = 0; i<numClus; i++) {
//		
//			Vector clusterMembers = new Vector();
//			
//			for(int j = 0; j<numData; j++) {
//				value = clusters.getEntry(j, i);
//			
//				if((value-1.0)>=0) {
//					clusterMembers.add(j);
//				}
//			}
//			cluster.put(i, clusterMembers);
//		}
		
		Map cluster = null;
		return cluster;
	}

	public void showCluster(Map cluster, int numClus) {
		
		for(int i = 0; i<numClus; i++) {
			System.out.println(cluster.get(i));
		}
	}
	
	public EigenDecomposition eigenDecomposition(double[][] data) {
		
		SimpleMatrix W = this.getSimpleMatrix(data);
		
		EigenDecomposition evd = W.eig().getEVD();
		
		return evd;
	}
	
	public SimpleMatrix getFirstLargestEigenVectors(double[][] data, int numEigenVectors) {

		SortMapDouble sortMapDouble;
		SimpleMatrix largestEigenVectors;
		Vector sortedEigenValueKeys;
		Map sortedEigenValues;
		double eigValue;
		int numEigenValue;
		int key;
		
		sortedEigenValues = new HashMap();
		sortMapDouble = new SortMapDouble();
		
		EigenDecomposition evd = this.eigenDecomposition(data);
		numEigenValue = evd.getNumberOfEigenvalues();		
		largestEigenVectors = new SimpleMatrix(numEigenValue, numEigenVectors);
				
		for(int i = 0; i<numEigenValue; i++) {
				eigValue = evd.getEigenvalue(i).getMagnitude();
				sortedEigenValues.put(i, eigValue);
		}
	
		sortedEigenValueKeys = sortMapDouble.getTopKKey(sortedEigenValues, sortedEigenValues.size());
		
		for(int i = 0; i<numEigenVectors; i++) 
		{
			key = Integer.parseInt(sortedEigenValueKeys.get(i).toString());	
			Matrix64F eigenVector = evd.getEigenVector(key);

			for(int j=0; j<numEigenValue; j++) 
			{
				largestEigenVectors.set(j, i, eigenVector.get(j, 0));
			}	
		}
		System.out.println("First largest "+numEigenVectors+" eigen vectors...");
		largestEigenVectors.print();
		
		return largestEigenVectors;
	}
	
	public SimpleMatrix getFirstSmallestEigenVectors(double[][] data, int numEigenVectors) {
		
		SortMapDouble sortMapDouble;
		SimpleMatrix smallestEigenVectors;
		Vector sortedEigenValueKeys;
		Map eigenValues;
		double eigValue;
		int numEigenValue;
		int key;
		int range;
		
		eigenValues = new HashMap();
		sortMapDouble = new SortMapDouble();
		
		EigenDecomposition evd = this.eigenDecomposition(data);
		numEigenValue = evd.getNumberOfEigenvalues();		
		smallestEigenVectors = new SimpleMatrix(numEigenValue, numEigenVectors);
		
		for(int i = 0; i<numEigenValue; i++) {
				eigValue = evd.getEigenvalue(i).getMagnitude();
				eigenValues.put(i, eigValue);
		}
	
		//Sorting in descending order
		sortedEigenValueKeys = sortMapDouble.getTopKKey(eigenValues, eigenValues.size());
		
		range = numEigenValue - numEigenVectors;
		
		for(int i = numEigenValue-1, k = 0; i>= range; i--, k++) 
		{
			key = Integer.parseInt(sortedEigenValueKeys.get(i).toString());	
			Matrix64F eigenVector = evd.getEigenVector(key);

			for(int j=0; j<numEigenValue; j++) 
			{
				smallestEigenVectors.set(j, k, eigenVector.get(j, 0));
			}	
		}
//		System.out.println("First smallest "+numEigenVectors+" eigen vectors...");
//		smallestEigenVectors.print();
		
		return smallestEigenVectors;
	}

	public SimpleMatrix getEigenVectors(double[][] data) {
		
		SimpleMatrix eigenVectors;
		int numEigenValues;

		EigenDecomposition evd = this.eigenDecomposition(data);
		numEigenValues = evd.getNumberOfEigenvalues();
		
		eigenVectors = new SimpleMatrix(numEigenValues, numEigenValues);
		
		for(int i = 0; i<numEigenValues; i++) {
			Matrix64F eigVector = evd.getEigenVector(i);
			
			for(int j = 0; j<numEigenValues; j++) {
				eigenVectors.set(j,i, eigVector.get(j, 0));
			}
		}
		System.out.println("All eigen vectors ...");
		eigenVectors.print();
		
		return eigenVectors;
	}
	
	public SimpleMatrix getEigenValues(double[][] data) {
		SimpleMatrix eigenValues;
		int numEigenValues;

		EigenDecomposition evd = this.eigenDecomposition(data);
		numEigenValues = evd.getNumberOfEigenvalues();
		
		eigenValues = new SimpleMatrix(numEigenValues, 1);
		
		for(int i = 0; i<numEigenValues; i++) 
		{
			Complex64F eigValue = evd.getEigenvalue(i);
			eigenValues.set(i,0, eigValue.getMagnitude());
		}
		System.out.println("All eigen values ...");
		eigenValues.print();
		return eigenValues;
	}
	
	public static void main(String[] args) {
		
		double[][] data = {
				{0, 	0.8, 	0.6, 	0, 		0.1, 	0},
				{0.8, 	0, 		0.8, 	0, 		0, 		0},
				{0.6, 	0.8, 	0,		0.2,	0, 		0},
				{0, 	0, 		0.2, 	0, 		0.8, 	0.7},
				{0.1, 	0, 		0,	 	0.8, 	0, 		0.8},
				{0, 	0, 		0, 		0.7, 	0.8, 	0},
			};
		
		SpectralClustering matrixOperation = new SpectralClustering();
		int numClus = 2;
		
		matrixOperation.spectralClustering(data, numClus);
	}
}
