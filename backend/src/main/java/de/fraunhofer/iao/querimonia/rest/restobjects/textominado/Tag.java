package de.fraunhofer.iao.querimonia.rest.restobjects.textominado;

import java.util.ArrayList;
import java.util.List;

public class Tag implements Cloneable {
	
	private long tagID;
    
    private boolean manualTagged = false;
	private String type = "";
	private String anonymizationType = null;
	
	
	private List<AdditionalInfo> additionalInfo = new ArrayList<>();
	
	/**
	 * needed for jackson to read out of json in database
	 */
	@SuppressWarnings("unused")
	private Tag() {}
	
	public Tag(String type) {
		this.type = type;
	}
	
	public Tag(String type, boolean manualTagged) {
		this.type = type;
		this.manualTagged = manualTagged;
	}
	
	public Tag(String nameOfTool, List<AdditionalInfo> extraInformation) {
		this(nameOfTool);
		this.additionalInfo = extraInformation;
	}
	
	public Tag(String type, boolean manualTagged, List<AdditionalInfo> extraInformation) {
		this(type, manualTagged);
		this.additionalInfo = extraInformation;
	}
	
	public Tag(String type, AdditionalInfo info) {
		this(type);
		List<AdditionalInfo> extraInformation = new ArrayList<>();
		extraInformation.add(info);
		this.additionalInfo = extraInformation;
	}
	
	public Tag(String type, boolean manualTagged, AdditionalInfo info) {
		this(type, info);
		this.manualTagged = manualTagged;
	}
	
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public List<AdditionalInfo> getAdditionalInfo() {
		return this.additionalInfo;
	}

	public boolean isManualTagged() {
		return this.manualTagged;
	}
	
	public String getAnonymizationType() {
		return this.anonymizationType;
	}
	
	public void setAnonymizationType(String anonymizationType) {
		this.anonymizationType = anonymizationType;
	}

	public Tag clone() {
		List<AdditionalInfo> additionalInfos = new ArrayList<>();
		for (AdditionalInfo additionalInfo : this.additionalInfo) {
			additionalInfos.add(additionalInfo.clone());
		}
		return new Tag(this.type, this.manualTagged, additionalInfos);
	}
	
	public String getAdditionalInfoOfKey(String key) {
		for (AdditionalInfo info : this.additionalInfo) {
			if (info.getInfoKey().equals(key)) {
				return info.getValue();
			}
		}
		return null;
	}
	
	public String toString() {
		String tag = this.manualTagged + this.type;
		for (AdditionalInfo info : this.additionalInfo) {
			tag += info.getInfoKey() + info.getValue();
		}
		return  tag;
	}
}
