package ca.enjoyit.aws.lambda.function.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResourceTag {

	@JsonProperty(value = "Key", required = true)
	private String key;
	
	@JsonProperty(value = "Value", required = true)
	private String value;
	
}
