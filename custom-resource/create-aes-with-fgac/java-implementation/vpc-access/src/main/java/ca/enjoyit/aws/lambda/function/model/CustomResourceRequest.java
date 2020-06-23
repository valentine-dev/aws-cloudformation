package ca.enjoyit.aws.lambda.function.model;

import java.util.Map;

import lombok.Data;

@Data
public class CustomResourceRequest {

	public RequestTypeEnum RequestType;

	public String StackId;

	public String RequestId;

	public String ResponseURL;

	public String ResourceType;

	public String LogicalResourceId;

	public Map<String, Object> ResourceProperties;

	public String PhysicalResourceId;

	public Map<String, Object> OldResourceProperties;
}
