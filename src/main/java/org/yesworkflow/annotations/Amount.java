package org.yesworkflow.annotations;

import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords;
import org.yesworkflow.dataflow.Interpreter;
import org.yesworkflow.db.YesWorkflowDB;

/**
 * Annotationclass for the "@amount" tag
 * @author SebastianB
 *
 */
public class Amount extends Qualification {

	public Amount(YesWorkflowDB ywdb, Long id, Long sourceId, Long lineNumber, String comment,
			Annotation primaryAnnotation) throws Exception {
		super(id, sourceId, lineNumber, comment, YWKeywords.Tag.AMOUNT, primaryAnnotation);
		value = buildAmount(comment, ywdb);
		primaryAnnotation.qualifyWith(this);
	}

	public String amount() {
		return value;
	}


	/**
	 * functioncall for interpret the comment
	 * @param comment
	 * @param ywdb
	 * @return String with amount interpreted by the comment
	 */
	private String buildAmount(String comment, YesWorkflowDB ywdb) {
		return Interpreter.interpret(comment.substring(8), ywdb);
	}

	public String toString() {
		return value;
	}
}
