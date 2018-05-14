package org.yesworkflow.dataflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Functionclass for dataflowdeclaration for the bone simulation pandas script
 * @author SebastianB
 *
 */
public class PandaInput {
	
	/**
	 * parses file for .elems, .node and .surfs files with the normal splitter " "
	 * @param file file to be parsed
	 * @return list of String[] where every String[] contains on line splitted by space
	 */
	public static List<String[]> parseEle(File file) {
		return Csv.parse(file," ");
	}
	
	private static DecimalFormat df2 = new DecimalFormat(".##");
	public static String timeDelta(String path) {
		File file = new File(path);
		List<String[]> fileParsed = parseEle(file); 
		double timeTotal = Double.valueOf(fileParsed.get(fileParsed.size()-2)[0]) - Double.valueOf(fileParsed.get(1)[0]);
		String sTRtimeTotal = df2.format(timeTotal);
		timeTotal = Double.valueOf(sTRtimeTotal);
		return Double.toString(timeTotal / (fileParsed.size() - 2));
	}
	/**
	 * Returns amount of lines of the .surfs,.elems,.nodes file given by the path.
	 * These files have a total amount of lines in the header. So this is a less 
	 * computationheavy version.
	 * @param path
	 * @return amount of lines of given file
	 */
	public static String getAmountEle(String path) {
		File file = new File(path);
		List<String[]> fileParsed = parseEle(file); 
		return fileParsed.get(0)[1];
	}
	
	public static String getAmountEleGeneric(String path, int firstLine) {
		File file = new File(path);
		List<String[]> fileParsed = parseEle(file);
		int lines = fileParsed.size() - firstLine;
		return Integer.toString(lines);
	}
	/**
	 * searches for a parameter in a cmd file. For Dataanalysis the needed parameters
	 * are h0 for computiationinterval and hout for datacreationinterval
	 * @param param
	 * @param filePath
	 * @return The searched parameter from the cmd file
	 */
	public static String getParam(String param, String filePath) {
		File fileCmd = new File(filePath);
		List<String> fileCmdParsed = Csv.parseLines(fileCmd);
		for(String str : fileCmdParsed) {
			if ( str.contains(param)) {
				String[] lineSplitted = str.split(" ");
				if (lineSplitted[3].contains("#")){
					lineSplitted[3] = lineSplitted[3].substring(0, lineSplitted[3].lastIndexOf("#")-1);
				}
				return lineSplitted[3];
			}
		}
		return "0";
	}
	
	/**
	 * Created Dataamount in relation to computationinterval and datacreationintervall from samplesfile
	 * Calculation: Looking every calculation step if we have a new output step. If a new
	 * result is available for the new output step the amount counter raises, else we look at the next
	 * calculation step.
	 * @param path
	 * @return Dataamount in relation to computationinterval and datacreationintervall
	 */
	public static int getSamples(String path) {
		File fileSamples = new File(path);
		List<String> fileSamplesParsed = Csv.parseLines(fileSamples); 
		double berechnungsintervall = Double.valueOf(getParam("h0", "cmd"));
		double ausgabeintervall = Double.valueOf(getParam("hout", "cmd"));
		int samples = fileSamplesParsed.size() - 1;
		int ausgabeint = (int) (ausgabeintervall * 100);
		int berechnungsint= (int) (berechnungsintervall * 100);
		int next_ausgabe = 0;
		int ausgabemenge = 0;
		for (int index = 0; index <= samples; index = index + berechnungsint) {
			if (index >= next_ausgabe) {
				ausgabemenge++;
				next_ausgabe = Math.max(next_ausgabe+ausgabeint, index + 1);
			}
		}
		return ausgabemenge;
	}
}
