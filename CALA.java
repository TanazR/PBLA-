package MyProject;


public class CALA {
    private double xProbability;
	private double meanProbability;
    private double standardDivProbability;

       
    
	public CALA(double xprobability, double meanProbability,
			double standardDivProbability) {
		this.xProbability = xprobability;
		this.meanProbability = meanProbability;
		this.standardDivProbability = standardDivProbability;
	}

	public double getMeanProbability() {
		return meanProbability;
	}

	public void setMeanProbability(double meanProbability) {
		this.meanProbability = meanProbability;
	}

	public double getStandardDivProbability() {
		return standardDivProbability;
	}

	public void setStandardDivProbability(double standardDivProbability) {
		this.standardDivProbability = standardDivProbability;
	}
	public double getXProbability() {
		return xProbability;
	}

	public void setXProbability(double probability) {
		xProbability = probability;
	}

	

	    }
