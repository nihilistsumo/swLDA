package trm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public class TermParserRunner {
	static String outputDir = "/home/sumanta/Documents/eric_codes/output/";//output directory of BabelnetProject
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File(outputDir+"temp");//output of BabelnetProject
		try {
			File xlsfile = new File(outputDir+"term_wt.xls");
			FileInputStream filestream = new FileInputStream(xlsfile);
			HSSFWorkbook wb = new HSSFWorkbook(filestream);
			HSSFSheet sheet = wb.getSheetAt(0);
			FileReader reader = new FileReader(file);
			TermParser parser = new TermParser();
			parser.parseTerms(sheet, reader);
			FileOutputStream out = new FileOutputStream(xlsfile);
			wb.write(out);
			wb.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
