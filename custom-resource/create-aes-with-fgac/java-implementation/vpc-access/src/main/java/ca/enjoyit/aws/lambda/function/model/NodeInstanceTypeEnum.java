package ca.enjoyit.aws.lambda.function.model;

import lombok.Getter;

public enum NodeInstanceTypeEnum {
	C5_LARGE("c5.large.elasticsearch"),
    C5_XLARGE("c5.xlarge.elasticsearch"),
    C5_2XLARGE("c5.2xlarge.elasticsearch"),
    C5_4XLARGE("c5.4xlarge.elasticsearch"),
    C5_9XLARGE("c5.9xlarge.elasticsearch"),
    C5_18XLARGE("c5.18xlarge.elasticsearch"),
    I3_LARGE("i3.large.elasticsearch"),
    I3_XLARGE("i3.xlarge.elasticsearch"),
    I3_2XLARGE("i3.2xlarge.elasticsearch"),
    I3_4XLARGE("i3.4xlarge.elasticsearch"),
    I3_8XLARGE("i3.8xlarge.elasticsearch"),
    I3_16XLARGE("i3.16xlarge.elasticsearch"),
    M5_LARGE("m5.large.elasticsearch"),
    M5_XLARGE("m5.xlarge.elasticsearch"),
    M5_2XLARGE("m5.2xlarge.elasticsearch"),
    M5_4XLARGE("m5.4xlarge.elasticsearch"),
    M5_12XLARGE("m5.12xlarge.elasticsearch"),
    R5_LARGE("r5.large.elasticsearch"),
    R5_XLARGE("r5.xlarge.elasticsearch"),
    R5_2XLARGE("r5.2xlarge.elasticsearch"),
    R5_4XLARGE("r5.4xlarge.elasticsearch"),
    R5_12XLARGE("r5.12xlarge.elasticsearch"),
    T2_SMALL("t2.small.elasticsearch"),
    T2_MEDIUM("t2.medium.elasticsearch"),
    C4_LARGE("c4.large.elasticsearch"),
    C4_XLARGE("c4.xlarge.elasticsearch"),
    C4_2XLARGE("c4.2xlarge.elasticsearch"),
    C4_4XLARGE("c4.4xlarge.elasticsearch"),
    C4_8XLARGE("c4.8xlarge.elasticsearch"),
    I2_XLARGE("i2.xlarge.elasticsearch"),
    I2_2XLARGE("i2.2xlarge.elasticsearch"),
    M4_LARGE("m4.large.elasticsearch"),
    M4_XLARGE("m4.xlarge.elasticsearch"),
    M4_2XLARGE("m4.2xlarge.elasticsearch"),
    M4_4XLARGE("m4.4xlarge.elasticsearch"),
    M4_10XLARGE("m4.10xlarge.elasticsearch"),
    R4_LARGE("r4.large.elasticsearch"),
    R4_XLARGE("r4.xlarge.elasticsearch"),
    R4_2XLARGE("r4.2xlarge.elasticsearch"),
    R4_4XLARGE("r4.4xlarge.elasticsearch"),
    R4_8XLARGE("r4.8xlarge.elasticsearch"),
    R4_16XLARGE("r4.16xlarge.elasticsearch"),
    M3_MEDIUM("m3.medium.elasticsearch"),
    M3_LARGE("m3.large.elasticsearch"),
    M3_XLARGE("m3.xlarge.elasticsearch"),
    M3_2XLARGE("m3.2xlarge.elasticsearch"),
    R3_LARGE("r3.large.elasticsearch"),
    R3_XLARGE("r3.xlarge.elasticsearch"),
    R3_2XLARGE("r3.2xlarge.elasticsearch"),
    R3_4XLARGE("r3.4xlarge.elasticsearch"),
    R3_8XLARGE("r3.8xlarge.elasticsearch");
	
	NodeInstanceTypeEnum (String nodeType) {
        this.nodeType = nodeType;
    }
	
	@Getter
	private String nodeType;
	
	@Override
	public String toString() {
        return nodeType;
    }
	
	public static boolean contains(String option) {
        for(NodeInstanceTypeEnum value : values()) {
        	if(value.toString().equals(option)) {
            	return true;
            }
        }   
        return false;
    }

}
