package algorithm;

import java.util.Vector;
import org.ejml.simple.SimpleMatrix;

public class PageRank {

	
	public PageRank(){
		
	}
	
	public void rowNormalization(SimpleMatrix A){
		
		double sum;
		double freq;
		double p;
		//Row normalization
		
		for(int i= 0; i<A.numRows(); i++){
			sum = 0;
			for(int j=0; j<A.numCols(); j++){
				sum += A.get(i, j); 
			}
			
			for(int j=0; j<A.numCols(); j++){
				freq = A.get(i,j);
				p = .90 * freq/sum + .10/A.numCols(); 
				A.set(i, j, p);
			}
		}
	}
	
	public void columnNormalization(SimpleMatrix A){
		
		double sum;
		int i;
		int j;
		double freq;
		double p;
		
		//Column Normalization
		for( i=0; i<A.numCols(); i++){
			
			sum = 0;
			for( j=0; j<A.numRows(); j++){
				sum += A.get(j, i);
			}
//			srSum = Math.sqrt(sum);
			
			for(j=0; j<A.numRows(); j++){
				freq = A.get(j,i);				
				p = .90 * freq/sum + .10/A.numRows(); 
//				A.set(j,i, A.get(j,i)/srSum);
				A.set(j, i, p);
			}
		}
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
	
	public Vector pageRank(SimpleMatrix A, SimpleMatrix V) {
		
		System.out.println("Running page rank");
		
		int i;
		double norm1;
		double norm2;
		double epsilon = 0.01;
		double sum = 0;

		SimpleMatrix S = A.mult(A.transpose());
		
		for(i = 0; i<S.numRows(); i++)
			S.set(i,i,0);
		
		SimpleMatrix D = this.getDiagonalMatrix(S);
		SimpleMatrix Dinv = this.getInverseSquareRootOFDiagonalMatrix(D);
		
		S = Dinv.mult(S.mult(Dinv));
		
		
		norm1 = V.normF();
	
		while(true) {			
			V = S.mult(V);	
			norm2 = V.normF();
	
			System.out.println("Convergence: 	norm1:"+ norm1 +" norm2:"+ norm2 +"\t Difference: "+ Math.abs(norm1-norm2) +" : "+ (Math.abs(norm1-norm2)<epsilon));
			if(Math.abs(norm1-norm2)<epsilon) 
			{
				System.out.println("PageRank Converged.");
				break;
			}
			norm1 = norm2;
		}
		
		Vector pageRank = new Vector();
		
		for(i = 0; i<V.numRows(); i++)
			pageRank.add(V.get(i,0));
		
		
		this.normalizedPageRankVector(pageRank);
		
		
		return pageRank;
	}
	
	private void normalizedPageRankVector(Vector pageRank) {
		
		Double weight;
		Double weightSum;
		int i;
		int N;
		
		N = pageRank.size();
		weightSum = 0.0;
		
		for(i = 0; i<N; i++) {
			weight = (Double) pageRank.get(i);
			weightSum += weight;
		}
		
		
		for(i = 0; i<N; i++) {
			weight = (Double) pageRank.get(i);
			weight = weight.doubleValue()/weightSum.doubleValue();
			pageRank.set(i, weight);
		}
	}

	public static void main(String[] args){

		String source ="./src/data/processed/user-item.txt";
		
	
	}
}
