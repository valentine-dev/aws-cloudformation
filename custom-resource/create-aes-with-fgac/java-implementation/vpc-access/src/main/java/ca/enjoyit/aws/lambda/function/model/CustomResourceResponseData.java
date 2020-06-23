package ca.enjoyit.aws.lambda.function.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomResourceResponseData {
	
	@JsonProperty(value = "DomainEndpoint", required = true)
	private String domainEndpoint;

}
