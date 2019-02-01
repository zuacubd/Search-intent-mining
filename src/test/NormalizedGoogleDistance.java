package test;

public class NormalizedGoogleDistance {

	private Double normalizedGoogleDistance;
	
	public Double getNormalizedGoogleDistance() {
		return this.normalizedGoogleDistance;
	}
	
	public NormalizedGoogleDistance(Integer X, Integer Y, Integer XY, Integer N) {
		
		this.normalizedGoogleDistance = this.estimateNormalizedGoogleDistance(X, Y, XY, N);
	}
	
	private Double estimateNormalizedGoogleDistance(Integer X, Integer Y, Integer XY, Integer N) {
		
		Double distance = new Double(0.0);
		Double logX = new Double(0.0);
		Double logY = new Double(0.0);
		Double logXY = new Double(0.0);
		Double logN = new Double(0.0);
		Double max;
		Double min;
		
		logX = Math.log(X.doubleValue());
		logY = Math.log(Y.doubleValue());
		logXY = Math.log(XY.doubleValue());
		logN = Math.log(N.doubleValue());
		
		max = Math.max(logX, logY);
		min = Math.min(logX, logY);
		
		
		distance = (max.doubleValue() - logXY.doubleValue())/(logN.doubleValue() - min);
		
		return distance;
	}
	
	public static void main(String[] args) {
		
		Integer X = new Integer(7040);
		Integer Y = new Integer(104000); 
		Integer XY = new Integer(754);
		Integer N = new Integer(34100000);
		
		NormalizedGoogleDistance normalizedBingDistance = new NormalizedGoogleDistance(X, Y, XY, N);
		
		System.out.println("NGD(bing) of pocono and resorts is "+normalizedBingDistance.getNormalizedGoogleDistance());
		
		X = 1430;
		Y = 86900; 
		XY = 2680;
		N = 9690000;
		
		NormalizedGoogleDistance normalizedGoogleDistance = new NormalizedGoogleDistance(X, Y, XY, N);
		
		System.out.println("NGD(google) of pocono and resorts is "+normalizedGoogleDistance.getNormalizedGoogleDistance());
		
		X = 7040;
		Y = 300000; 
		XY = 724;
		N = 34100000;
		
		NormalizedGoogleDistance normalizedGoogleDistance1 = new NormalizedGoogleDistance(X, Y, XY, N);
		
		System.out.println("NGD(bing) of pocono and record is "+normalizedGoogleDistance1.getNormalizedGoogleDistance());
	}
}
