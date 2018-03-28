package org.yesworkflow.dataflow;

import static org.yesworkflow.db.Column.COMMENT_TEXT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jooq.Result;
import org.yesworkflow.db.Table;
import org.yesworkflow.db.YesWorkflowDB;
import org.jooq.Record;

/**
 * Interprets given comment and gives back the amount in numeric form
 * 
 * @author SebastianB
 *
 */
public class Interpreter {

	private YesWorkflowDB ywdb;
	public static boolean productionFlag = false;

	public static boolean isNumeric(String str) {
		return str.matches("[0-9]*");
	}

	/**
	 * override term in list with its solution.
	 * @param splittedList
	 * @param index
	 * @param solution
	 */
	public static void insertInto(List<String> splittedList, int index, int solution) {
		splittedList.remove(index + 1);
		splittedList.remove(index);
		splittedList.remove(index - 1);
		splittedList.add(index - 1, Integer.toString(solution));
	}

	/**
	 * looks is given part of list(index-1, given index, index +1) is a term when char at index is a mathsigns.
	 * @param splittedList
	 * @param index
	 * @return boolean if part of list is a term.
	 */
	public static boolean term(List<String> splittedList, int index) {
		return isNumeric(splittedList.get(index - 1)) && (isNumeric(splittedList.get(index + 1)));
	}

	public static boolean isModifier(String str) {
		return str.matches("[\\*+-/]");
	}

	/**
	 * Interprets given comment and gives back the amount in numeric form
	 * @param comment
	 * @param ywdb
	 * @return String with amount to store in db
	 */
	public static String interpret(String comment, YesWorkflowDB ywdb) {
		// If comment is only numeric
		if (isNumeric(comment)) {

			return comment;
		} else {
			String[] splittedComment = comment.split(" ");
			List<String> splittedList = new ArrayList<String>(Arrays.asList(splittedComment));
			// get the whole array to numbers and math signs
			int indexComment = 0;
			for (String str : splittedList) {
				if (isNumeric(str) || isModifier(str)) {
					// if number or mathsigns do nothing
				} else {
					// interpret the comment
					splittedComment = str.split("[.]");

					String annotationName = splittedComment[1];
					// only reference to other amount
					if (splittedComment.length == 2) {

						Result<Record> rows = ywdb.jooq().select(COMMENT_TEXT).from(Table.COMMENT).fetch();
						String value;
						String[] splittedValue;

						for (Record row : rows) {
							value = row.getValue(COMMENT_TEXT).toString();
							splittedValue = value.split(" ");
							value = "@" + splittedComment[0];
							if (value.equalsIgnoreCase(splittedValue[0])
									&& splittedComment[1].equalsIgnoreCase(splittedValue[1])) {
								int indexAmount = 0;
								for (String searchString : splittedValue) {

									if (searchString.equalsIgnoreCase("@amount")) {
										indexAmount++;
										break;
									}
									indexAmount++;
								}
								splittedList.set(indexComment, splittedValue[indexAmount]);
							}
						}

						// execute Functions
					} else if (splittedComment.length == 3) {
						Result<Record> rows = ywdb.jooq().select(COMMENT_TEXT).from(Table.COMMENT).fetch();
						String value;
						String[] splittedValue;

						for (Record row : rows) {
							value = row.getValue(COMMENT_TEXT).toString();
							splittedValue = value.split(" ");
							value = "@" + splittedComment[0];
							if (value.equalsIgnoreCase(splittedValue[0])
									&& splittedComment[1].equalsIgnoreCase(splittedValue[1])) {
								int indexAmount = 0;
								for (String searchString : splittedValue) {

									if (searchString.equalsIgnoreCase("@uri")) {
										indexAmount++;
										break;
									}
									indexAmount++;
								}
								String uriPath = splittedValue[indexAmount];
								String[] uriSplit = uriPath.split("\\.");
								String uriEnding = uriSplit[uriSplit.length - 1];
								String uriFunction = splittedComment[2];
								String solutionStr = null;
								// getting the right function calls for the file
								switch (uriEnding) {
								case "csv":
									if (uriFunction == "numRows()") {
										// functioncall

									}
									break;
								case "musc":
									if (uriFunction.equalsIgnoreCase("getSamples()")) {
										solutionStr = Integer.toString(PandaInput.getSamples(uriPath));
									}
									break;
								case "elems":
								case "surfs":
								case "nodes":
									if (uriFunction.equalsIgnoreCase("getNum()")) {
										solutionStr = PandaInput.getAmountEle(uriPath);
									}
									break;
								case "default":
									break;
								}
								splittedList.set(indexComment, solutionStr);
							}
						}
					} else {
						// TODO Exception
					}
				}
				indexComment++;
			}
			// going through list and make multiplications before additions. this is made
			// because it is
			// faster than some shorter versions
			boolean multiplyflag = false;
			boolean additionflag = false;
			int index = 0;
			int solution = 0;
			while (!multiplyflag) {
				if (splittedList.get(index).matches("[\\*]") && index < splittedList.size() - 1) {
					if (term(splittedList, index)) {
						solution = Integer.valueOf(splittedList.get(index - 1))
								* Integer.valueOf(splittedList.get(index + 1));
						insertInto(splittedList, index, solution);
					}
				} else if (splittedList.get(index).matches("[/]") && index != splittedList.size()) {
					if (term(splittedList, index)) {
						solution = Integer.valueOf(splittedList.get(index - 1))
								/ Integer.valueOf(splittedList.get(index + 1));
						insertInto(splittedList, index, solution);
					}
				}
				if (index >= splittedList.size() - 1) {
					multiplyflag = true;
				} else {
					index++;
				}
			}
			index = 0;
			while (!additionflag) {
				if (splittedList.get(index).matches("[+]") && index < splittedList.size() - 1) {
					if (term(splittedList, index)) {
						solution = Integer.valueOf(splittedList.get(index - 1))
								+ Integer.valueOf(splittedList.get(index + 1));
						insertInto(splittedList, index, solution);
					}
				} else if (splittedList.get(index).matches("[-]") && index != splittedList.size()) {
					if (term(splittedList, index)) {
						solution = Integer.valueOf(splittedList.get(index - 1))
								- Integer.valueOf(splittedList.get(index + 1));
						insertInto(splittedList, index, solution);
					}
				}
				if (index >= splittedList.size() - 1) {
					additionflag = true;
				} else {
					index++;
				}
			}
			return splittedList.get(0);

		}

	}
}
