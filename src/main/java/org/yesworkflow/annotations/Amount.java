package org.yesworkflow.annotations;

import java.util.StringTokenizer;

import org.yesworkflow.YWKeywords;

public class Amount extends Qualification{
	public Amount(Long id, Long sourceId, Long lineNumber,String comment, Annotation primaryAnnotation) throws Exception {
        super(id, sourceId, lineNumber, comment, YWKeywords.Tag.AMOUNT, primaryAnnotation);
        StringTokenizer commentTokens = new StringTokenizer(comment);
        commentTokens.nextToken();
        value = buildAmount(commentTokens);
        primaryAnnotation.qualifyWith(this);
    }

    public String amount() {
        return value;
    }
    
    private String buildAmount(StringTokenizer commentTokens) {
        //TODO Add right builder
    	if (primaryAnnotation instanceof Flow) {
    		String URI = ((Flow) primaryAnnotation).uriAnnotation().value;
    	}
    	long id = primaryAnnotation.id;
    	
    	return "";
    }
    
    public String toString() {
        return value;
    }
}
