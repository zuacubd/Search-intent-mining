package algorithm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Vector;

import org.ejml.simple.SimpleMatrix;

public class BiRandomWalk {

	int numRows;
	int numCols;
	SimpleMatrix R;

	public SimpleMatrix getConverseMatrix(){ return R;}

	public BiRandomWalk(){
		
	}
	
	public BiRandomWalk(SimpleMatrix A, double alpha){

		this.numRows = A.numRows();
		this.numCols = A.numCols();
		
		this.runBiRandomWalkBalance(A, alpha);
	
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
		double srSum;
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

	public void runBiRandomWalkBalance(SimpleMatrix A, double alpha){
		
		//Bi-Random walk equation
		// R = alpha * U * R * V + (1-alpha) * A
		
		SimpleMatrix U;
		SimpleMatrix V;
		SimpleMatrix At;
		
		double alphaInv;
		
//		System.out.println("Printing co-occurence matrix: A");
//		A.print();
		
		At = A.transpose();
		U = A.mult(At);
		V = At.mult(A);
		
		//Setting diagonal elements to zero so that self reinforcement is reduced.
		
		for(int i = 0; i<A.numRows(); i++)
			U.set(i,i,0);
		
		for(int j=0; j<A.numCols(); j++)
			V.set(j,j,0);
		
		//Column Normalize U		
		this.columnNormalization(U);
		
		//Row Normalize V
		this.rowNormalization(V);
		
		if(A.numRows()<=A.numCols())
			this.rowNormalization(A);		
		else
			this.columnNormalization(A);	

		this.R = A.copy();
//		System.out.println("Printing Transition Matrix R by column normalizing A");
//		this.R.print();
	
		alphaInv = 1.0 - alpha;
		
//		System.out.println("Query-Query Similarity Matrix, U" );
//		U.print();
		
//		System.out.println("URL-URL similarity Matrix, V" );
//		V.print();
		
		//Scaling
		U = U.scale(alpha);
		A = A.scale(alphaInv);

//		System.out.println("(1-alpha) scaled Matrix, A" );		
//		A.print();

//		System.out.println("(alpha scaled Query-Query Similarity Matrix, U" );
//		U.print();
		
		int iter = Math.max(A.numRows(), A.numCols());
		boolean convergence;
		
		
		double normT;
		double normT1;
		double diff;
		int step = 0;
		double epsilon = 0.001;
		
		normT = this.R.normF();
		
		iter = Math.min(iter, 10);	
		this.R = U.mult(this.R.mult(V));
		this.R = this.R.plus(A);
		
		normT1 = R.normF();
		
		step++;
		
		
		while(true){
		
			System.out.println("Norm T:" + normT);
			System.out.println("Norm T1:"+ normT1);
			
			diff = Math.abs(normT-normT1);
			
			System.out.println("Diff :"+ diff);		
			System.out.println("Convergence: 	normT:"+ normT +" normT1:"+ normT1 +"\t Difference: "+ Math.abs(normT1-normT) +" : "+ (Math.abs(normT1-normT)<epsilon));

			convergence = (Math.abs(normT1-normT)<epsilon);

			if(convergence) {System.out.println("Algorithm converged."); break;}
			if(step>iter) {System.out.println("Iterated the maximum times "); break;}
			
			normT = normT1;

			System.out.println("Step : "+ step);
			
			R = U.mult(R.mult(V));
			R = R.plus(A);
			normT1 = R.normF();
			
			step++;
	//		R.print();
		}
		
	}
	
	public void runBiRandomWalk(SimpleMatrix A){
		
		//Bi-Random walk equation
		// R = alpha * U * R * V + (1-alpha) * A
		
		SimpleMatrix U;
		SimpleMatrix V;
		SimpleMatrix AT;
		double alpha;
		double alphaInv;
		
		System.out.println("Printing co-occurence matrix: A");
		A.print();
		
		if(A.numRows()<=A.numCols())
			this.rowNormalization(A);		
		else
			this.columnNormalization(A);	

		this.R = A.copy();
		System.out.println("Printing Transition Matrix R by column normalizing A");
		this.R.print();
	
		alpha = 0.12;
		alphaInv = 1.0 - alpha;
		
		AT = A.transpose();
		
		U = A.mult(AT);
		V = AT.mult(A);
				
//		System.out.println("Query-Query Similarity Matrix, U" );
//		U.print();
		
//		System.out.println("URL-URL similarity Matrix, V" );
//		V.print();
		
		//Scaling
		U = U.scale(alpha);
		A = A.scale(alphaInv);

//		System.out.println("(1-alpha) scaled Matrix, A" );		
//		A.print();

//		System.out.println("(alpha scaled Query-Query Similarity Matrix, U" );
//		U.print();
		
		int iter = Math.max(A.numRows(), A.numCols());
		boolean convergence;
		double normT;
		double normT1;
		double diff;
		double epsilon = 0.001;
		int step = 0;
	
		iter = Math.max(iter, 100);
		normT = R.normF();
		R = U.mult(R.mult(V));
		R = R.plus(A);
		normT1 = R.normF();
		
		step++;
		
		
		while(true){
		
			System.out.println("Norm T:" + normT);
			System.out.println("Norm T1:"+ normT1);
			
			diff = Math.abs(normT-normT1);
			
			System.out.println("Diff :"+ diff);		
			System.out.println("Convergence: 	normT:"+ normT +" normT1:"+ normT1 +"\t Difference: "+ Math.abs(normT1-normT) +" : "+ (Math.abs(normT1-normT)<epsilon));

			convergence = (Math.abs(normT1-normT)<epsilon);

			if(convergence) {System.out.println("Algorithm converged."); break;}
			if(step>iter) {System.out.println("Iterated the maximum times "); break;}
			
			normT = normT1;

			System.out.println("Step : "+ step);
			
			R = U.mult(R.mult(V));
			R = R.plus(A);
			normT1 = R.normF();
			step++;
	//		R.print();
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
	
	public SimpleMatrix getR(String source) {
        String line;
        String[] lineParts;
        String[] featureParts;
        int numRows;
        int numCols;
        SimpleMatrix R = null;
        
        try{
        	File file = new File(source);
        	FileInputStream fin = new FileInputStream(file);
        	BufferedReader read = new BufferedReader(new InputStreamReader(fin));
        	line = read.readLine();
        	if(line==null) { System.out.println("Cann't read the source :" + source); return null;}

        	lineParts = line.split("\t");
        	if(lineParts.length>1){
        		numRows = Integer.parseInt(lineParts[0]);
        		numCols = Integer.parseInt(lineParts[1]);
        	}
        	else{
        		System.out.println("File is not in correct format."); return null;
        	}
        	
        	this.numRows = numRows;
        	this.numCols = numCols;
        	R = new SimpleMatrix(numRows, numCols);
        	
        	while((line = read.readLine())!=null){
        		lineParts = line.split("\t");
        		if(lineParts.length>1){
        			int nodeA = Integer.parseInt(lineParts[0]);
        			for(int i = 1; i<lineParts.length; i++){
        				featureParts = lineParts[i].split(":");
        				int nodeB = Integer.parseInt(featureParts[0].trim());
        				int freq = Integer.parseInt(featureParts[1].trim());
        				R.set(nodeA-1, nodeB-5, freq);
        			}
        		}
        	}
        }catch(Exception e){
        	System.out.println("Something goes wrong in getR" + e.getMessage());
        }
        return R;
    }
		
	public static void main(String[] args){

		String source ="./src/data/processed/user-item.txt";
		BiRandomWalk biRW = new BiRandomWalk();
		SimpleMatrix A;
		double alpha = 0.25;
		
		A = biRW.getR(source);
		biRW.runBiRandomWalkBalance(A, alpha);
		biRW.R.print();
		
//		SimpleMatrix v = new SimpleMatrix(biRW.numRows, 1);
////		v.set(1);
//		v.set(0, 1);
//		v.set(3,1);
//		v.print();
//		v = biRW.pageRank(biRW.R, v);
//		v.print();

	}
}
