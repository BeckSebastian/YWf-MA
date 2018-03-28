package org.yesworkflow.dataflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Functionclass for dataflowdeclaration for the bone simulation pandas script
 * @author SebastianB
 *
 */
public class PandaInput {
	
	/**
	 * parses file for .elems, .node and .surfs
	 * @param file
	 * @return list of String[] where every String[] contains on line splitted by space
	 */
	public static List<String[]> parseEle(File file) {
		File pandaInput = file;
		String line = "";
		String splitter = " ";
		List<String[]> parsedFile = new ArrayList<String[]>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pandaInput));

			while ((line = br.readLine()) != null) {

				// spliting the line
				String[] values = line.split(splitter, -1);
				parsedFile.add(values);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parsedFile;
	}
	
	/**
	 * Gives linerepresantation of given file
	 * @param file
	 * @return List of Strings. Every String is a single line of the file.
	 */
	public static List<String> parseLines(File file) {
		File pandaInput = file;
		String line = "";
		List<String> parsedFile = new ArrayList<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(pandaInput));

			while ((line = br.readLine()) != null) {

				// spliting the line
				parsedFile.add(line);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return parsedFile;
	}
	/**
	 * Returns amount of lines of the .surfs,.elems,.nodes file given by the path
	 * @param path
	 * @return amount of lines of given file
	 */
	public static String getAmountEle(String path) {
		File file = new File(path);
		List<String[]> fileParsed = parseEle(file); 
		return fileParsed.get(0)[1];
	}
	
	/**
	 * searches for a parameter in a cmd file. For Dataanalysis the needed parameters
	 * are h0 for computiationinterval and hout for datacreationinterval
	 * @param param
	 * @param fileCmdParsed
	 * @return The searched parameter from the cmd file
	 */
	public static String getParam(String param, List<String> fileCmdParsed) {
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
	 * @param path
	 * @return Dataamount in relation to computationinterval and datacreationintervall
	 */
	public static int getSamples(String path) {
		File fileSamples = new File(path);
		File fileCmd = new File("cmd");
		List<String> fileSamplesParsed = parseLines(fileSamples); 
		List<String> fileCmdParsed = parseLines(fileCmd);
		double berechnungsintervall = Double.valueOf(getParam("h0", fileCmdParsed));
		double ausgabeintervall = Double.valueOf(getParam("hout", fileCmdParsed));
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
