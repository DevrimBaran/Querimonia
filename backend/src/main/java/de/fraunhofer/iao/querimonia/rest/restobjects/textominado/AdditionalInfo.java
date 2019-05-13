package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

public class AdditionalInfo implements Cloneable {
	
	// don't name it key: it will cause an error in mySQL
	private String infoKey = "";
	private String value = "";
	
	/**
	 * needed for jackson to read out of json in database
	 */
	@SuppressWarnings("unused")
	private AdditionalInfo() {
	}
	
	public AdditionalInfo(String infoKey, String value) {
		this.infoKey = infoKey;
		this.value = value;
	}
	
	public String getInfoKey() {
		return this.infoKey;
	}
	
	public String getValue() {
		return this.value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
	public AdditionalInfo clone() {
		return new AdditionalInfo(this.infoKey, this.value);
	}
}
