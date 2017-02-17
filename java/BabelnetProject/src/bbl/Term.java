package bbl;

import it.uniroma1.lcl.babelnet.BabelSynset;

import java.util.List;

public class Term {
	public String term;
	public List<BabelSynset> synsets;
	public double specificScore, genericScore, termScore;
	public String getTerm() {
		return term;
	}
	public void setTerm(String term) {
		this.term = term;
	}
	public List<BabelSynset> getSynsets() {
		return synsets;
	}
	public void setSynsets(List<BabelSynset> synsets) {
		this.synsets = synsets;
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

}
