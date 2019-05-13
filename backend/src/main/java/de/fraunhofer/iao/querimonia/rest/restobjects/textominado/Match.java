package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

import java.util.ArrayList;
import java.util.List;

public final class Match implements Cloneable {
	
	private long matchID;
    private String token;
    private List<TextPosition> positions;
    private Tag tag;
    
    /**
	 * needed for jackson to read out of json in database
	 */
	@SuppressWarnings("unused")
    private Match() {
    	this.token = "";
    	this.positions = new ArrayList<>();
    	this.tag = null;
    }

    public Match(String token, TextPosition position, Tag tag) {
        this(token, new ArrayList<TextPosition>(), tag);
        this.positions.add(position);
    }

    public Match(String token, List<TextPosition> positions, Tag tag) {
        this.token = token;
        this.positions = positions;
        this.tag = tag;
    }
    
    public void setTag(Tag tag) {
    	this.tag = tag;
    }
    
    public String getToken() {
		return token;
	}
    
    public void setToken(String token) {
    	this.token = token;
    }

	public Tag getTag() {
		return this.tag;
	}
	
	public List<TextPosition> getPositions() {
		return this.positions;
	}
	
	public Match clone() {
		List<TextPosition> positions = new ArrayList<>();
		for (TextPosition pos : this.positions) {
			positions.add(pos.clone());
		}
		return new Match(this.token, positions, this.tag.clone());
	}
}

