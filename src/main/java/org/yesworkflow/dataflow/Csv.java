package org.yesworkflow.dataflow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Csv {
	/**
	 * parse csv file
	 * @param file
	 * @return list of String[] - every String[] is one line splitted by ";"
	 */
	public static List<String[]> parse(File file) {
		File csvFile = file;
		String line = "";
		String splitter = ";";
		List<String[]> parsedFile = new ArrayList<String[]>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(csvFile));

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
	 * gets number of columns
	 * @param csvFile
	 * @return number of columns
	 */
	private static int numberColumn(List<String[]> csvFile) {
		int maxColumn = 0;
		for (String[] s : csvFile) {
			if (maxColumn <= s.length) {
				maxColumn = s.length;
			}
		}
		return maxColumn;
	}

	/**
	 * gets number of lines
	 * @param csvFile
	 * @return int number of lines
	 */
	private static int numberLine(List<String[]> csvFile) {
		return csvFile.size();
	}

	/**
	 * sums cells
	 * @param csvFile
	 * @return int summed cells
	 */
	private static int sumCells(List<String[]> csvFile) {
		int maxCells = 0;
		for (String[] s : csvFile) {
			maxCells = +s.length;

		}
		return maxCells;
	}

	public static boolean isNumeric(String str) {
		return str.matches("[0-9\\.]*");
	}

	public static boolean isModifier(String str) {
		return str.matches(".*[\\*+-/].*");
	}

	/**
	 * Filterfunction for csv files for greater, smaller, same than value
	 * @param csvFile
	 * @param value
	 * @param comperator
	 *            1 = smaller than Value, 2 = smaller/same than value, 3 = same than
	 *            value 4 = greater/same than value, 5 = greater than value
	 * @param cell
	 * @return int amount
	 */
	private static int filterInt(List<String[]> csvFile, String value, int comperator, String cell) {
		int numberFiltered = 0;
		//row
		if (isNumeric(cell)) {
			int index = Integer.parseInt(cell);
			if (csvFile.size() >= index - 1) {
				for (String s : csvFile.get(index - 1)) {
					if (s != null) {
						int compare = s.compareToIgnoreCase(value);

						switch (comperator) {
						case 1:
							if (compare < 0) {
								numberFiltered++;
							}
							break;
						case 2:
							if (compare <= 0) {
								numberFiltered++;
							}
							break;
						case 3:
							if (compare == 0) {
								numberFiltered++;
							}
							break;
						case 4:
							if (compare >= 0) {
								numberFiltered++;
							}
							break;
						case 5:
							if (compare > 0) {
								numberFiltered++;
							}
							break;
						}
					}
				}
			}
			return numberFiltered;
		//columns
		} else {
			cell = cell.toUpperCase();
			int offset = 64;
			int column = 0;
			for (int index = 0; index < cell.length(); index++) {
				int intValueOfChar = ((int) cell.charAt(cell.length() - index - 1)) - offset;
				column = column + (int) (intValueOfChar * Math.pow(26, index));
				System.out.println("Column" + column);
			}

			for (String[] s : csvFile) {
				if (s[column - 1] != null) {
					int compare = s[column - 1].compareToIgnoreCase(value);
					switch (comperator) {
					case 1:
						if (compare < 0) {
							numberFiltered++;
						}
						break;
					case 2:
						if (compare <= 0) {
							numberFiltered++;
						}
						break;
					case 3:
						if (compare == 0) {
							System.out.println(s[column - 1]);
							numberFiltered++;
						}
						break;
					case 4:
						if (compare >= 0) {
							numberFiltered++;
						}
						break;
					case 5:
						if (compare > 0) {
							numberFiltered++;
						}
						break;

					}
				}
			}
			return numberFiltered;
		}
	}

	
}
