package dataPath;

public class TestingPath {
	
	private static String perTopicDnev = "./src/dataEvalMetric/testRun.txt.l10.Dnev";
	
	private static String iRec10 ="./src//dataEvalMetric/iRec_10";
	
	private static String nDCG10 ="./src//dataEvalMetric/nDCG_10";
	
	private static String d_nDCG10 ="./src//dataEvalMetric/d_nDCG_10";

	public String getTopicDnevPath() {
		return this.perTopicDnev;
	}
	
	public String getIRec10FilePath() {
		return this.iRec10;
	}
	
	public String getNDCG10FilePath() {
		return this.nDCG10;
	}
	
	public String getDnDCG10FilePath() {
		return this.d_nDCG10;
	}
}
