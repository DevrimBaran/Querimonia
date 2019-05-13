package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

public class TextPosition {
	
	private final int startPos;
	private final int endPos;
    
    /**
	 * needed for jackson to read out of json in database
	 */
	@SuppressWarnings("unused")
    private TextPosition() {
    	this.startPos = 0;
    	this.endPos = 0;
    }

    public TextPosition(int startPos, int endPos) {
        this.startPos = startPos;
        this.endPos = endPos;
    }
    
    @Override
    public boolean equals(Object pos) {
    	if (pos instanceof TextPosition) {
    		return (((TextPosition) pos).startPos == this.startPos && ((TextPosition) pos).endPos == this.endPos);
    	} else {
    		return false;
    	}
    }
    
    public int getEndPos() {
    	return this.endPos;
    }
    
    public int getStartPos() {
    	return this.startPos;
    }
    
    public TextPosition clone() {
    	return new TextPosition(this.startPos, this.endPos);
    }
}
