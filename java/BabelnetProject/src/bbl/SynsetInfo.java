package bbl;

import it.uniroma1.lcl.babelnet.BabelSynset;

public class SynsetInfo {
	String term;
	BabelSynset mainSynset;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public BabelSynset getMainSynset() {
		return mainSynset;
	}
	public void setMainSynset(BabelSynset mainSynset) {
		this.mainSynset = mainSynset;
	}
	public int getHypernymCount() {
		return hypernymCount;
	}
	public void setHypernymCount(int hypernymCount) {
		this.hypernymCount = hypernymCount;
	}
	public int getHyponymCount() {
		return hyponymCount;
	}
	public void setHyponymCount(int hyponymCount) {
		this.hyponymCount = hyponymCount;
	}
	public int getMeronymCount() {
		return meronymCount;
	}
	public void setMeronymCount(int meronymCount) {
		this.meronymCount = meronymCount;
	}
	public int getHolonymCount() {
		return holonymCount;
	}
	public void setHolonymCount(int holonymCount) {
		this.holonymCount = holonymCount;
	}
	public double getSpecificScore() {
		return specificScore;
	}
	public void setSpecificScore(double specificScore) {
		this.specificScore = specificScore;
	}
	public double getGenericScore() {
		return genericScore;
	}
	public void setGenericScore(double genericScore) {
		this.genericScore = genericScore;
	}
	public double getTermScore() {
		return termScore;
	}
	public void setTermScore(double termScore) {
		this.termScore = termScore;
	}
	int hypernymCount, hyponymCount, meronymCount, holonymCount;
	double totalHyperWeight, totalHypoWeight, totalMeroWeight, totalHoloWeight;
	double specificScore, genericScore, termScore;

}
