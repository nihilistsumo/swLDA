package tst;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.babelnet.BabelSynsetComparator;
import it.uniroma1.lcl.babelnet.BabelSynsetSource;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

public class AccuracyTests {
	final int documentSheet = 0;
	final int topicSheet = 1;
	HSSFRow docrow, topicrow;
	static String fileName;
	BabelSynset actualTopicSynset;
	int accuracyScore;
	BabelNet bn = BabelNet.getInstance();
	public void runAccuracyTest(String resultFile, Map<String,BabelSynset> topicByFilename){
		accuracyScore = 0;
		//Map topicByFilename will map first char of filename to topic name
		//For ex. p103 is related to politics
		//So Map will contain p -> Politics
		try {
			FileInputStream fis = new FileInputStream(new File(resultFile));
			HSSFWorkbook workbook = new HSSFWorkbook(fis);
		    HSSFSheet docsheet = workbook.getSheetAt(documentSheet);
		    HSSFSheet topics = workbook.getSheetAt(topicSheet);
		    Iterator < Row > docRowIterator = docsheet.iterator();
		    BabelSynsetComparator bc = new BabelSynsetComparator();
		    //String[] topicWords;
		    List<BabelSynset> topicSynsets;
		    float accuracyScore = 0;
		    while(docRowIterator.hasNext()){
		    	docrow = (HSSFRow)docRowIterator.next();
		    	Iterator<Cell> docCellIt = docrow.cellIterator();
		    	fileName = docCellIt.next().getStringCellValue();
		    	System.out.println("Examining "+fileName);
		    	actualTopicSynset = topicByFilename.get(String.valueOf(fileName.charAt(0)));
		    	System.out.println("Actual topic "+actualTopicSynset);
		    	topicSynsets = this.getTopicWords(docCellIt, topics);
		    	Iterator<BabelSynset> synsetIt = topicSynsets.iterator();
		    	int synsetListSize = topicSynsets.size();
		    	int compareScore = 0;
		    	while(synsetIt.hasNext()){
		    		try {
						BabelSynset currentSynset = synsetIt.next();
						//System.out.println("Synset calc "+currentSynset);
						compareScore = bc.compare(actualTopicSynset, currentSynset);
						accuracyScore += Math.abs(compareScore);
					} catch (NullPointerException e) {
						// TODO Auto-generated catch block
						synsetListSize--;
						e.printStackTrace();
					}
		    	}
		    	accuracyScore = accuracyScore/synsetListSize;
		    	System.out.println(fileName+" accuracy = "+accuracyScore+"\n\n\n");
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private List<BabelSynset> getTopicWords(Iterator<Cell> cellIt, HSSFSheet topics){
		List<BabelSynset> topicSynsetList = new ArrayList<BabelSynset>();
		float topicProb = 0, tempval = 0;
		int topicCount = 0;
		while(cellIt.hasNext()){
			Cell cellval = cellIt.next();
			if(cellval.getCellType()==0){
				tempval = (float)cellval.getNumericCellValue();
				if(tempval>topicProb){
					topicProb = tempval;
					topicCount = cellval.getColumnIndex();
				}
			}
		}
		System.out.println("Highest topic #"+topicCount);
		HSSFRow topicRow = topics.getRow(topicCount-1);
		Iterator<Cell> topicCellIt = topicRow.iterator();
		while(topicCellIt.hasNext()){
			String topicWord = topicCellIt.next().getStringCellValue();
			//System.out.println("Word "+topicWord);
			List<BabelSynset> wordSynsets = bn.getSynsets(Language.EN, topicWord);
			//System.out.println("Word synsets "+wordSynsets);
			if(!wordSynsets.isEmpty()){
				Iterator<BabelSynset> synsetIt = wordSynsets.iterator();
				while(synsetIt.hasNext()){
					BabelSynset synset = synsetIt.next();
					BabelSynsetSource source = synset.getSynsetSource();
					if(source==BabelSynsetSource.WN || source==BabelSynsetSource.WIKIWN){
						topicSynsetList.add(synset);
						System.out.println("Choosing synset "+synset);
						break;
					}
				}
			}
		}
		//System.out.println(topicSynsetList);
		return topicSynsetList;
	}

}
