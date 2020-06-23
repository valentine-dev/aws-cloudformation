package ca.enjoyit.aws.lambda.function.model;

import lombok.Getter;

public enum VersionForElasticsearchWithFineGrainedAccessEnum {
	// >= 6.7 for FGAC
	SIX_DOT_SEVEN("6.7"), 
	SIX_DOT_EIGHT("6.8"),
	SEVEN_DOT_ONE("7.1"), 
	SEVEN_DOT_FOUR("7.4");
	
	VersionForElasticsearchWithFineGrainedAccessEnum (String version) {
        this.version = version;
    }
	
	@Getter
	private String version;
	
	@Override
	public String toString() {
        return version;
    }
	
	public static boolean contains(String option) {
        for(VersionForElasticsearchWithFineGrainedAccessEnum value : values()) {
        	if(value.toString().equals(option)) {
            	return true;
            }
        }   
        return false;
    }
}
