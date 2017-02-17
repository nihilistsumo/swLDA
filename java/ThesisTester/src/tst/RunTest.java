package tst;

import it.uniroma1.lcl.babelnet.BabelNet;
import it.uniroma1.lcl.babelnet.BabelSynset;
import it.uniroma1.lcl.jlt.util.Language;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
public class RunTest 
{
   static HSSFRow row;
   public static void main(String[] args) throws Exception 
   {
	   BabelNet bnet = BabelNet.getInstance();
	   Map<String, BabelSynset> topicMap = new HashMap();
	   //Put correct synsets here against which we have to compare
	   topicMap.put("b", bnet.getSynsets(Language.EN, "business").get(6));
	   topicMap.put("e", bnet.getSynsets(Language.EN, "entertainment").get(0));
	   topicMap.put("p", bnet.getSynsets(Language.EN, "politics").get(4));
	   topicMap.put("s", bnet.getSynsets(Language.EN, "sport").get(1));
	   topicMap.put("t", bnet.getSynsets(Language.EN, "technology").get(0));
	   AccuracyTests ac = new AccuracyTests();
	   ac.runAccuracyTest("/home/sumanta/Documents/eric_codes/processed_dtmat.xls", topicMap);//output of lda_docs.py
   }
}
