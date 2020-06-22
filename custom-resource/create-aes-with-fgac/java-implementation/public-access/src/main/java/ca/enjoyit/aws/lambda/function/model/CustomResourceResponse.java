/**
 * 
 */
package ca.enjoyit.aws.lambda.function.model;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.lambda.runtime.Context;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Data;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomResourceResponse {
	
	final private static Log logger = LogFactory.getLog(CustomResourceResponse.class);
	final private static MediaType JSON = MediaType.get("application/json; charset=utf-8");
	final private static OkHttpClient HTTP_CLIENT = new OkHttpClient();
	
	@JsonProperty(value = "Status", required = true)
	private ResponseStatusEnum status;
	
	@JsonProperty(value = "StackId", required = true)
	private String stackId;
	
	@JsonProperty(value = "RequestId", required = true)
	private String requestId;
	
	@JsonProperty(value = "PhysicalResourceId", required = true)
	private String physicalResourceId;
	
	@JsonProperty(value = "LogicalResourceId", required = true)
	private String logicalResourceId;

	@JsonProperty("NoEcho")
	private Boolean noEcho;
	
	@JsonProperty("Reason")
	private String reason; // Required if Status is FAILED. It's optional otherwise.
	
	@JsonProperty("Data")
	private CustomResourceResponseData data;

	public boolean send(CustomResourceRequest request, Context context) {
		boolean sendSucceeded = true;
		ObjectMapper objectMapper = new ObjectMapper();
		String responseBody = null;;
		try {
			responseBody = objectMapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			logger.error("Error getting JSON string from CustomResourceResponse object");
			e.printStackTrace();
			sendSucceeded = false;
		}
		logger.info("Response body: " + responseBody);
		if (sendSucceeded) {
			RequestBody requestBody = RequestBody.create(responseBody, JSON);
			String sendResponse = "HTTP send failed";
			Request sendRequest = new Request.Builder()
				.url(request.getResponseURL())
				.put(requestBody)
				.build();
			try (Response response = HTTP_CLIENT.newCall(sendRequest).execute()) {
				if (response.isSuccessful()) {
					sendResponse = response.body().string();
					logger.info("HTTP sent successfully.");
				} else {
					sendResponse = "" + response.code() + " : "+ response.message();
				}
				logger.info("HTTP send response: [" + sendResponse + "].");
			} catch (IOException e) {
				logger.error("Error sending the request to " + request.getRequestId());
				e.printStackTrace();
				sendSucceeded = false;
			}
		}
		
		return sendSucceeded;
	}

}
