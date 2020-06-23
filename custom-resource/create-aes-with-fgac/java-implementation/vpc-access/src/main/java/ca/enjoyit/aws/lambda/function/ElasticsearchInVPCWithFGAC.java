package ca.enjoyit.aws.lambda.function;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.EnumSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.elasticsearch.AWSElasticsearch;
import com.amazonaws.services.elasticsearch.AWSElasticsearchClientBuilder;
import com.amazonaws.services.elasticsearch.model.AWSElasticsearchException;
import com.amazonaws.services.elasticsearch.model.AdvancedSecurityOptionsInput;
import com.amazonaws.services.elasticsearch.model.CreateElasticsearchDomainRequest;
import com.amazonaws.services.elasticsearch.model.CreateElasticsearchDomainResult;
import com.amazonaws.services.elasticsearch.model.DeleteElasticsearchDomainRequest;
import com.amazonaws.services.elasticsearch.model.DeleteElasticsearchDomainResult;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainRequest;
import com.amazonaws.services.elasticsearch.model.DescribeElasticsearchDomainResult;
import com.amazonaws.services.elasticsearch.model.DomainEndpointOptions;
import com.amazonaws.services.elasticsearch.model.EBSOptions;
import com.amazonaws.services.elasticsearch.model.ElasticsearchClusterConfig;
import com.amazonaws.services.elasticsearch.model.EncryptionAtRestOptions;
import com.amazonaws.services.elasticsearch.model.MasterUserOptions;
import com.amazonaws.services.elasticsearch.model.NodeToNodeEncryptionOptions;
import com.amazonaws.services.elasticsearch.model.ResourceNotFoundException;
import com.amazonaws.services.elasticsearch.model.TLSSecurityPolicy;
import com.amazonaws.services.elasticsearch.model.UpdateElasticsearchDomainConfigRequest;
import com.amazonaws.services.elasticsearch.model.UpdateElasticsearchDomainConfigResult;
import com.amazonaws.services.elasticsearch.model.VolumeType;
import com.amazonaws.services.elasticsearch.model.ZoneAwarenessConfig;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import ca.enjoyit.aws.lambda.function.model.CreateDomainOptionsEnum;
import ca.enjoyit.aws.lambda.function.model.CustomResourceRequest;
import ca.enjoyit.aws.lambda.function.model.CustomResourceResponse;
import ca.enjoyit.aws.lambda.function.model.CustomResourceResponseData;
import ca.enjoyit.aws.lambda.function.model.DeleteDomainOptionsEnum;
import ca.enjoyit.aws.lambda.function.model.ResponseStatusEnum;
import ca.enjoyit.aws.lambda.function.model.UpdateDomainOptionsEnum;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Handler for requests to Lambda function.
 * 
 */
public class ElasticsearchInVPCWithFGAC implements RequestHandler<CustomResourceRequest, String> {

	final private static Log logger = LogFactory.getLog(ElasticsearchInVPCWithFGAC.class);
	final public static Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	@Override
	public String handleRequest(final CustomResourceRequest request, final Context context) {
		
		logger.info("Custom resource request: " + request.toString());

		final CustomResourceResponse response = new CustomResourceResponse();
		response.setLogicalResourceId(request.getLogicalResourceId());
		response.setRequestId(request.getRequestId());
		response.setStackId(request.getStackId());
		response.setStatus(ResponseStatusEnum.FAILED);
		response.setNoEcho(false);
		response.setReason("See the details in CloudWatch Log Stream: " + context.getLogStreamName());

		final AWSElasticsearch client = AWSElasticsearchClientBuilder.standard()
				.withCredentials(new DefaultAWSCredentialsProviderChain()).build();
		switch (request.getRequestType()) {
		case Create:
			response.setPhysicalResourceId(context.getLogStreamName());
			if (null != createDomain(client, request)) {
				String domainName = (String) CreateDomainOptionsEnum.DOMAIN_NAME.getValue(request);
				DescribeElasticsearchDomainResult completedResponse = waitForDomainProcessing(client, domainName);
				if (null != completedResponse) { // getDomainStatus().getEndpoint() cannot be null
					logger.info("Elasticsearch domain creation completed.");
					response.setStatus(ResponseStatusEnum.SUCCESS);
					final CustomResourceResponseData responseData = new CustomResourceResponseData();
					responseData.setDomainEndpoint(completedResponse.getDomainStatus().getEndpoint());
					logger.info("Response data: " + responseData);
					response.setData(responseData);
				} else {
					logger.error("Waiting for Elasticsearch domain processing failed.");
				}
			} else {
				logger.error("Create Elasticsearch domain failed.");
			}
			break;
		case Update:
			response.setPhysicalResourceId(request.getPhysicalResourceId());
			response.setData(null);
			if (null != updateDomain(client, request)) {
				String domainName = (String) UpdateDomainOptionsEnum.DOMAIN_NAME.getValue(request);
				DescribeElasticsearchDomainResult completedResponse = waitForDomainProcessing(client, domainName);
				if (null != completedResponse) {
					logger.info("Elasticsearch domain update completed.");
					response.setStatus(ResponseStatusEnum.SUCCESS);
					final CustomResourceResponseData responseData = new CustomResourceResponseData();
					responseData.setDomainEndpoint(completedResponse.getDomainStatus().getEndpoint());
					logger.info("Response data: " + responseData);
					response.setData(responseData);
				} else {
					logger.error("Waiting for Elasticsearch domain processing failed.");
				}
			} else {
				logger.error("Update Elasticsearch domain failed.");
			}
			break;
		case Delete:
			response.setPhysicalResourceId(request.getPhysicalResourceId());
			DeleteElasticsearchDomainResult deleteResponse = deleteDomain(client, request);
			if (null != deleteResponse) {
				logger.info("Elasticsearch domain deletion completed.");
				response.setStatus(ResponseStatusEnum.SUCCESS);
			} else {
				logger.error("Delete Elasticsearch domain failed.");
			}
			break;
		default:
			response.setPhysicalResourceId(context.getLogStreamName());
			logger.error("Invalid custom resource request type: " + request.getRequestType());
			break;
		}
		if (response.send(request, context)) {
			logger.info("Response sent successfully."); 
		} else {
			logger.info("Response sent unsuccessfully.");
		}
		return "Lambda function [ElasticsearchWithFGAC] finished execution at " + OffsetDateTime.now(ZoneOffset.UTC);
	}

	/**
	 * Creates an Amazon Elasticsearch Service domain with the specified options.
	 * Some options require other AWS resources, such as an Amazon Cognito user pool
	 * and identity pool, whereas others require just an instance type or instance
	 * count.
	 *
	 * @param client  The AWSElasticsearch client to use for the requests to Amazon
	 *                Elasticsearch Service
	 * @param request The custom resource request you receive
	 * @return CreateElasticsearchDomainResult
	 */
	private CreateElasticsearchDomainResult createDomain(final AWSElasticsearch client,
			final CustomResourceRequest request) {

		if (false == validateCreateRequest(request)) {
			return null;
		}

		CreateElasticsearchDomainResult createResponse = null;
		// 10 parameters
		final String domainName = (String) CreateDomainOptionsEnum.DOMAIN_NAME.getValue(request);
		final String elasticsearchVersion = (String) CreateDomainOptionsEnum.ELASTICSEARCH_VERSION.getValue(request);
		final Integer masterNodeCount = (Integer) CreateDomainOptionsEnum.MASTER_NODE_COUNT.getValue(request);
		final String masterInstanceType = (String) CreateDomainOptionsEnum.MASTER_INSTANCE_TYPE.getValue(request);
		final Integer dataNodeCount = (Integer) CreateDomainOptionsEnum.DATA_NODE_COUNT.getValue(request);
		final String dataInstanceType = (String) CreateDomainOptionsEnum.DATA_INSTANCE_TYPE.getValue(request);
		final Integer ebsVolumeSize = (Integer) CreateDomainOptionsEnum.EBS_VOLUME_SIZE.getValue(request);
		final String masterUserName = (String) CreateDomainOptionsEnum.MASTER_USER_NAME.getValue(request);
		final String masterUserPassword = (String) CreateDomainOptionsEnum.MASTER_USER_PASSWORD.getValue(request);
		final Integer availabilityZoneCount = (Integer) CreateDomainOptionsEnum.AVAILABILITY_ZONE_COUNT.getValue(request);

		try {

			// Create the request and set the desired configuration options
			final CreateElasticsearchDomainRequest createRequest = new CreateElasticsearchDomainRequest()
					.withDomainName(domainName).withElasticsearchVersion(elasticsearchVersion)
					.withEBSOptions(new EBSOptions()
							.withEBSEnabled(true).withVolumeSize(ebsVolumeSize).withVolumeType(VolumeType.Gp2))
					.withAdvancedSecurityOptions(
							new AdvancedSecurityOptionsInput().withEnabled(true).withInternalUserDatabaseEnabled(true)
									.withMasterUserOptions(new MasterUserOptions().withMasterUserName(masterUserName)
											.withMasterUserPassword(masterUserPassword)))
					// TODO: in another ticket after requirements are defined
					// .withLogPublishingOptions((Map<String, LogPublishingOption>) options
					// .get(CreateDomainOptionsEnum.LogPublishingOptions))
					.withAccessPolicies( // TODO: maybe domain specific
							"{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":\"*\"},\"Action\":[\"es:*\"],\"Resource\":\"*\"}]}")
					.withNodeToNodeEncryptionOptions(new NodeToNodeEncryptionOptions().withEnabled(true))
					.withDomainEndpointOptions(new DomainEndpointOptions().withEnforceHTTPS(true)
							.withTLSSecurityPolicy(TLSSecurityPolicy.PolicyMinTLS12201907))
					.withEncryptionAtRestOptions(new EncryptionAtRestOptions()
							.withEnabled(true))
							.withElasticsearchClusterConfig(new ElasticsearchClusterConfig()
							.withDedicatedMasterEnabled(true)
							.withDedicatedMasterCount(masterNodeCount)
							.withDedicatedMasterType(masterInstanceType)
							.withInstanceType(dataInstanceType)
							.withInstanceCount(dataNodeCount)
							.withZoneAwarenessEnabled(true)
							.withZoneAwarenessConfig(new ZoneAwarenessConfig()
									.withAvailabilityZoneCount(availabilityZoneCount)));

			// Make the request.
			logger.info("Sending domain creation request...");
			createResponse = client.createElasticsearchDomain(createRequest);
			logger.info("Domain creation response from Amazon Elasticsearch Service: " + createResponse);
		} catch (AWSElasticsearchException e) {
			logger.error("Domain [" + domainName + "] create failed.");
			e.printStackTrace();
		}
		return createResponse;
	}

	private boolean validateCreateRequest(CustomResourceRequest request) {
		return validateRequest(request, CreateDomainOptionsEnum.class);
	}

	/**
	 * Updates the configuration of an Amazon Elasticsearch Service domain with the
	 * specified options. Some options require other AWS resources, such as an
	 * Amazon Cognito user pool and identity pool, whereas others require just an
	 * instance type or instance count.
	 *
	 * @param client  The AWSElasticsearch client to use for the requests to Amazon
	 *                Elasticsearch Service
	 * @param request The custom resource request you receive
	 * @return UpdateElasticsearchDomainConfigResult
	 */
	private UpdateElasticsearchDomainConfigResult updateDomain(final AWSElasticsearch client,
			final CustomResourceRequest request) {
		if (false == validateUpdateRequest(request)) {
			return null;
		}
		
		UpdateElasticsearchDomainConfigResult updateResponse = null;
		
		// 8 parameters
		final String domainName = (String) UpdateDomainOptionsEnum.DOMAIN_NAME.getValue(request);
		final Integer masterNodeCount = (Integer) UpdateDomainOptionsEnum.MASTER_NODE_COUNT.getValue(request);
		final String masterInstanceType = (String) UpdateDomainOptionsEnum.MASTER_INSTANCE_TYPE.getValue(request);
		final Integer dataNodeCount = (Integer) UpdateDomainOptionsEnum.DATA_NODE_COUNT.getValue(request);
		final String dataInstanceType = (String) UpdateDomainOptionsEnum.DATA_INSTANCE_TYPE.getValue(request);
		final Integer ebsVolumeSize = (Integer) UpdateDomainOptionsEnum.EBS_VOLUME_SIZE.getValue(request);
		final String masterUserName = (String) UpdateDomainOptionsEnum.MASTER_USER_NAME.getValue(request);
		final String masterUserPassword = (String) UpdateDomainOptionsEnum.MASTER_USER_PASSWORD.getValue(request);
		
		try {
			final UpdateElasticsearchDomainConfigRequest updateRequest = new UpdateElasticsearchDomainConfigRequest()
					.withDomainName(domainName);
			if (isElasticsearchClusterConfigChanged(masterNodeCount, masterInstanceType, dataNodeCount, dataInstanceType, request)) {
				updateRequest.withElasticsearchClusterConfig(new ElasticsearchClusterConfig()
						.withDedicatedMasterEnabled(true)
						.withDedicatedMasterCount(masterNodeCount)
						.withDedicatedMasterType(masterInstanceType)
						.withInstanceType(dataInstanceType)
						.withInstanceCount(dataNodeCount));	
			}
			if (isEbsVolumeSizeChanged(ebsVolumeSize, request) != null) {
				updateRequest.withEBSOptions(new EBSOptions()
						.withEBSEnabled(true).withVolumeSize(ebsVolumeSize).withVolumeType(VolumeType.Gp2));
			}
			if (isMasterUserChanged(masterUserName, masterUserPassword, request)) {
				updateRequest.withAdvancedSecurityOptions(
						new AdvancedSecurityOptionsInput().withEnabled(true).withInternalUserDatabaseEnabled(true)
						.withMasterUserOptions(new MasterUserOptions().withMasterUserName(masterUserName)
								.withMasterUserPassword(masterUserPassword)));
			}
			logger.info("Sending domain update request...");
			updateResponse = client.updateElasticsearchDomainConfig(updateRequest);
			logger.info("Domain update response from Amazon Elasticsearch Service: " + updateResponse);
		} catch (AWSElasticsearchException e) {
			logger.error("Domain [" + request.getResourceProperties().get("domainName") + "] update failed.");
			e.printStackTrace();
		}

		return updateResponse;
	}
	
	private boolean isMasterUserChanged(final String masterUserName, final String masterUserPassword,
			final CustomResourceRequest request) {
		final String masterUserNameOld = (String) UpdateDomainOptionsEnum.MASTER_USER_NAME.getOldValue(request);
		final String masterUserPasswordOld = (String) UpdateDomainOptionsEnum.MASTER_USER_PASSWORD.getOldValue(request);
		return (!masterUserNameOld.equals(masterUserName) || !masterUserPasswordOld.equals(masterUserPassword));
	}
	private Object isEbsVolumeSizeChanged(final Integer ebsVolumeSize, final CustomResourceRequest request) {
		final Integer ebsVolumeSizeOld = (Integer) UpdateDomainOptionsEnum.EBS_VOLUME_SIZE.getOldValue(request);
		return !ebsVolumeSizeOld.equals(ebsVolumeSize);
	}
	private boolean isElasticsearchClusterConfigChanged(Integer masterNodeCount, String masterInstanceType,
			Integer dataNodeCount, String dataInstanceType, CustomResourceRequest request) {
		final Integer masterNodeCountOld = (Integer) UpdateDomainOptionsEnum.MASTER_NODE_COUNT.getOldValue(request);
		final String masterInstanceTypeOld = (String) UpdateDomainOptionsEnum.MASTER_INSTANCE_TYPE.getOldValue(request);
		final Integer dataNodeCountOld = (Integer) UpdateDomainOptionsEnum.DATA_NODE_COUNT.getOldValue(request);
		final String dataInstanceTypeOld = (String) UpdateDomainOptionsEnum.DATA_INSTANCE_TYPE.getOldValue(request);
		
		return (!masterNodeCountOld.equals(masterNodeCount) || !masterInstanceTypeOld.equals(masterInstanceType) 
				||!dataNodeCountOld.equals(dataNodeCount) || !dataInstanceTypeOld.equals(dataInstanceType));
	}

	private boolean validateUpdateRequest(CustomResourceRequest request) {
		return validateRequest(request, UpdateDomainOptionsEnum.class);
	}

	/**
	 * Deletes an Amazon Elasticsearch Service domain. Deleting a domain can take
	 * several minutes.
	 *
	 * @param client     The AWSElasticsearch client to use for the requests to
	 *                   Amazon Elasticsearch Service
	 * @param request The custom resource request you receive
	 * @return DeleteElasticsearchDomainResult
	 */
	private DeleteElasticsearchDomainResult deleteDomain(final AWSElasticsearch client,
			final CustomResourceRequest request) {
		
		if (false == validateDeleteRequest(request)) {
			return null;
		}
		
		DeleteElasticsearchDomainResult deleteResponse = null;
		String domainName = (String) DeleteDomainOptionsEnum.DOMAIN_NAME.getValue(request);
		try {
			final DeleteElasticsearchDomainRequest deleteRequest = new DeleteElasticsearchDomainRequest()
					.withDomainName(domainName);

			logger.info("Sending domain deletion request...");
			deleteResponse = client.deleteElasticsearchDomain(deleteRequest);
			logger.info("Domain deletion response from Amazon Elasticsearch Service: " + deleteResponse);
		} catch (ResourceNotFoundException e) {
			logger.info("Domain [" + domainName + "] delete not needed: No resources found.");
			e.printStackTrace();
			deleteResponse = new DeleteElasticsearchDomainResult().withDomainStatus(null);
		} catch (AWSElasticsearchException e) {
			logger.error("Domain [" + domainName + "] delete failed.");
			e.printStackTrace();
		}
		return deleteResponse;
	}

	private boolean validateDeleteRequest(CustomResourceRequest request) {
		return validateRequest(request, DeleteDomainOptionsEnum.class);
	}
	
	private <T extends Enum<T>> boolean validateRequest(CustomResourceRequest request, Class<T> requestType) {
		EnumSet<T> optionSet = EnumSet.allOf(requestType);

		boolean isValid = true;
		if (requestType == CreateDomainOptionsEnum.class) {
			for (T option : optionSet) {
				logger.info("Create Option - " + GSON.toJson(option));
				isValid = ((CreateDomainOptionsEnum) option).validate(request);
				logger.info("isValid: " + isValid);
				//isValid = ((T) option).validate(request);
				if (!isValid) {
					break;
				}
			}
		} else if (requestType == UpdateDomainOptionsEnum.class) {
			for (T option : optionSet) {
				logger.info("Update Option - " + GSON.toJson(option));
				isValid = ((UpdateDomainOptionsEnum) option).validate(request);
				logger.info("isValid: " + isValid);
				//isValid = ((T) option).validate(request);
				if (!isValid) {
					break;
				}
			}
		} else { // must be Delete
			for (T option : optionSet) {
				logger.info("Delete Option - " + GSON.toJson(option));
				isValid = ((DeleteDomainOptionsEnum) option).validate(request);
				logger.info("isValid: " + isValid);
				//isValid = ((T) option).validate(request);
				if (!isValid) {
					break;
				}
			}
		}
		return isValid;
	}

	/**
	 * Waits for the domain to finish processing changes. New domains typically take
	 * 15-30 minutes to initialize, but can take longer depending on the
	 * configuration. Most updates to existing domains take a similar amount of
	 * time. This method checks periodically and finishes only when the domain's
	 * processing status changes to false.
	 *
	 * @param client     The AWSElasticsearch client to use for the requests to
	 *                   Amazon Elasticsearch Service
	 * @param domainName The name of the domain that you want to check
	 * @param sleepTime  Sleep periodically in seconds to wait before checking until
	 *                   the domain finishes processing
	 * @return DescribeElasticsearchDomainResult
	 */
	private DescribeElasticsearchDomainResult waitForDomainProcessing(final AWSElasticsearch client,
			final String domainName, final long sleepTime) {
		// Create a new request to check the domain status.
		final DescribeElasticsearchDomainRequest describeRequest = new DescribeElasticsearchDomainRequest()
				.withDomainName(domainName);

		// Sleep for a while, then check whether the domain is processing.
		DescribeElasticsearchDomainResult describeResponse = null;
		try {
			describeResponse = client.describeElasticsearchDomain(describeRequest);
			while (describeResponse != null && describeResponse.getDomainStatus() != null
					&& (describeResponse.getDomainStatus().isProcessing() 
					    || describeResponse.getDomainStatus().getEndpoint() == null)) {
				try {
					logger.info("Domain [" + domainName + "] still processing...");
					TimeUnit.SECONDS.sleep(sleepTime);
					describeResponse = client.describeElasticsearchDomain(describeRequest);
				} catch (InterruptedException e) {
					logger.error("Interrupted while domain [" + domainName + "] is processing.");
					e.printStackTrace();
					describeResponse = null;
				}
			}
		} catch (AWSElasticsearchException e) {
			logger.error("Domain [" + domainName + "] describe failed.");
			e.printStackTrace();
		}

		// Once we exit that loop, the domain is available
		logger.info("Amazon Elasticsearch Service has finished processing changes for your domain.");
		logger.info("Domain description response from Amazon Elasticsearch Service:" + describeResponse);

		return describeResponse;
	}

	/**
	 * Waits for the domain to finish processing changes. New domains typically take
	 * 15-30 minutes to initialize, but can take longer depending on the
	 * configuration. Most updates to existing domains take a similar amount of
	 * time. This method checks every 30 seconds and finishes only when the domain's
	 * processing status changes to false.
	 *
	 * @param client     The AWSElasticsearch client to use for the requests to
	 *                   Amazon Elasticsearch Service
	 * @param domainName The name of the domain that you want to check
	 * 
	 * @return DescribeElasticsearchDomainResult
	 */
	private DescribeElasticsearchDomainResult waitForDomainProcessing(final AWSElasticsearch client,
			final String domainName) {
		return waitForDomainProcessing(client, domainName, 30L);
	}
}
