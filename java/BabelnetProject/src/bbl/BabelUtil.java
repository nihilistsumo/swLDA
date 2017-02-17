package bbl;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetIDRelation;
import it.uniroma1.lcl.babelnet.BabelSynsetSource;
import it.uniroma1.lcl.babelnet.data.BabelPointer;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;

public class BabelUtil {
	//public final int STARTFROM = 3099;
	public final int STARTFROM = 0;//starting term index
	public Logger log = null;
	public Logger weightlog = null;
	//double totalHyperWeight, tempHyperWeight, totalHypoWeight, tempHypoWeight, totalMeroWeight, tempMeroWeight, totalHoloWeight, tempHoloWeight;
	//int hyperCount, hypoCount, meroCount, holoCount;
	double specificScore, genericScore, termScore;
	Object[] termarr;
	BabelNet bn = null;
	public ArrayList<SynsetInfo> termList = new ArrayList<SynsetInfo>();
	public void initLoggers(){
		log = Logger.getLogger(BabelnetMain.class.getName());
		weightlog = Logger.getLogger(BabelnetMain.class.getName());
	}
	public void initScores(){
		specificScore = 0;
		genericScore = 0;
		termScore = 0;
	}
	public Object[] loadTermArray(String fileName){
		JSONParser parser = new JSONParser();
		try{
			JSONArray termJSON = (JSONArray)parser.parse(new FileReader(fileName));
			termarr = termJSON.toArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return termarr;
	}
	public void initBabelNet(){
		try {
			bn = BabelNet.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void initAll(){
		this.initLoggers();
		this.initScores();
		this.initBabelNet();
	}
	public double calEdgeScore(BabelSynset synset, BabelPointer edgeType, SynsetInfo terminfo){
		double score = 0, tempWeight = 0, totalWeight = 0;
		int count = 0;
		for(BabelSynsetIDRelation edge : synset.getEdges(edgeType)) {
			count++;
			tempWeight = 1-edge.getNormalizedWeight();//this must be inversely proportional to it's specificity
			//we have to take care of it
			totalWeight += tempWeight;
			try{
				log.info(synset.getId()+"\t"+synset.getMainSense(Language.EN).getLemma()+" - "
						+ edge.getPointer()+" - "+ tempWeight +" - "
						+ edge.getBabelSynsetIDTarget().toBabelSynset().getMainSense(Language.EN).getLemma());
			}
			catch (NullPointerException e) {
				e.printStackTrace();
			}
		}
		if(count == 0)
			score = 0;
		else
			score = totalWeight/count;
		if (Double.isNaN(score))
			score = 0;
		if(edgeType == BabelPointer.ANY_HYPERNYM){
			terminfo.setHypernymCount(count);
		} else if(edgeType == BabelPointer.ANY_HYPONYM){
			terminfo.setHyponymCount(count);
		} else if(edgeType == BabelPointer.ANY_MERONYM){
			terminfo.setMeronymCount(count);
		} else if(edgeType == BabelPointer.ANY_HOLONYM){
			terminfo.setHolonymCount(count);
		} else{
			System.out.println("Something is wrong");
		}
		System.out.println(edgeType+" score calculated for "+synset.toString()+totalWeight+"/"+count+" = "+score);
		return score;
	}
	public void calScore(String term, Term terminfo, FileWriter w) throws IOException{
		List<BabelSynset> listSynset = bn.getSynsets(term, Language.EN);
		terminfo.setSynsets(listSynset);
		System.out.println(listSynset);
		w.append("\n"+listSynset.toString());
		int synsetCount = 0;
		BabelSynset synset = null;
		if (!listSynset.isEmpty()){
			Iterator<BabelSynset> synsetIt = listSynset.iterator();
			
			while(synsetIt.hasNext()){
				synset = synsetIt.next();
				if(synset.getSynsetSource()==BabelSynsetSource.WN || synset.getSynsetSource()==BabelSynsetSource.WIKIWN || synset.getSynsetSource()==BabelSynsetSource.BABELNET)
					break;
			}
			SynsetInfo synsetinfo = new SynsetInfo();
			synsetinfo.setMainSynset(synset);
			System.out.println("Main synset for "+term+" is "+synset.toString());
			w.append("\nMain synset for "+term+" is "+synset.toString());
			specificScore = (calEdgeScore(synset, BabelPointer.ANY_HYPERNYM, synsetinfo)+calEdgeScore(synset, BabelPointer.ANY_HOLONYM, synsetinfo))/2;
			genericScore = (calEdgeScore(synset, BabelPointer.ANY_HYPONYM, synsetinfo)+calEdgeScore(synset, BabelPointer.ANY_MERONYM, synsetinfo))/2;
			//synsetCount++;
			terminfo.setSpecificScore(specificScore);
			terminfo.setGenericScore(genericScore);
			terminfo.setTermScore((terminfo.getSpecificScore()-terminfo.getGenericScore())/2);
		}
	}
	public void iterateArray(Object[] termarr, File file){
		int i = STARTFROM;
		initBabelNet();
		while(i<termarr.length){
			try {
				initScores();
				FileWriter writer = new FileWriter(file, true);
				String currentTerm = new String(termarr[i].toString());
				Term terminfo = new Term();
				terminfo.setTerm(currentTerm);
				System.out.println("Term is "+currentTerm);
				writer.append("\nTerm is "+currentTerm);
				calScore(currentTerm, terminfo, writer);
				System.out.println("Term index i="+i+" "+currentTerm);
				writer.append("\nTerm index i="+i+" "+currentTerm);
				System.out.println("Term = "+currentTerm+" Specific score = "+terminfo.getSpecificScore()+" Generic score = "+terminfo.getGenericScore()+" Term score = "+terminfo.getTermScore());
				writer.append("\nTerm = "+currentTerm+" Specific score = "+terminfo.getSpecificScore()+" Generic score = "+terminfo.getGenericScore()+" Term score = "+terminfo.getTermScore());
				System.out.println("\n\n\n");
				writer.append("\n\n\n");
				writer.flush();
				writer.close();
				weightlog.warn(currentTerm+" index = "+i+" score = "+termScore);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			i++;
		}
	}
}
