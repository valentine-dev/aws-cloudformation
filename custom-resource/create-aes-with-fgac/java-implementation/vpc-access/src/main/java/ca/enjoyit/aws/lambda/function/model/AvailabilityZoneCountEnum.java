package ca.enjoyit.aws.lambda.function.model;

import lombok.Getter;

public enum AvailabilityZoneCountEnum {
	TWO(2), 
	THREE(3);
	
	AvailabilityZoneCountEnum (Integer count) {
        this.count = count;
    }
	
	@Getter
	private Integer count;
	
	@Override
	public String toString() {
        return count.toString();
    }
	
	public static boolean contains(Integer option) {
        for(AvailabilityZoneCountEnum value : values()) {
        	if(value.getCount().equals(option)) {
            	return true;
            }
        }   
        return false;
    }
}
