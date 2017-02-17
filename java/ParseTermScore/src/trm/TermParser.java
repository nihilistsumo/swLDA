package trm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.ss.usermodel.Cell;

public class TermParser {
	public void parseTerms(HSSFSheet termsheet, FileReader outputfile){
		try(BufferedReader br = new BufferedReader(outputfile)){
			String line, term;
			Pattern pattern = Pattern.compile("(Term\\s=\\s)(\\w+)(.*)(Term\\sscore\\s=\\s)(.*\\b)");
			int rowCount = 0;
			double score = 0;
			while((line = br.readLine()) != null){
				Matcher matcher = pattern.matcher(line);
				if(matcher.find()){
					term = matcher.group(2);
					score = Double.parseDouble(matcher.group(5));
					System.out.println("Term is "+term+" score is "+score);
					HSSFRow row = termsheet.createRow(rowCount);
					Cell termcell = row.createCell(0);
					termcell.setCellValue(term);
					Cell scorecell = row.createCell(1);
					scorecell.setCellValue(score);
					rowCount++;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
