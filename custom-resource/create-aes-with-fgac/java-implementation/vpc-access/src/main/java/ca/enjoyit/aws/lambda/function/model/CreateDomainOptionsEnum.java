package ca.enjoyit.aws.lambda.function.model;

import com.amazonaws.services.elasticsearch.model.VPCOptions;
import com.amazonaws.util.CollectionUtils;
import com.amazonaws.util.StringUtils;
import ca.enjoyit.aws.lambda.function.ElasticsearchInVPCWithFGAC;

/**
 * Here are the requirements for enabling fine-grained access control: -
 * Elasticsearch 6.7 or later - Encryption of data at rest enabled -
 * Node-to-node encryption enabled - Require HTTPS for all traffic to the domain
 * enabled
 * 
 * You can't enable fine-grained access control on existing domains, only new
 * ones. After you enable fine-grained access control, you can't disable it.
 * 
 * Source:
 * https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/fgac.html
 *
 */
public enum CreateDomainOptionsEnum implements DomainOperationOptions {

	DOMAIN_NAME("domainName") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			String value = getValue(request);
			return (StringUtils.hasValue(value) && value.matches(DOMAIN_NAME_PATTERN));
		}
	},
	ELASTICSEARCH_VERSION("elasticsearchVersion") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return VersionForElasticsearchWithFineGrainedAccessEnum.contains(getValue(request));
		}
	},
	MASTER_NODE_COUNT("masterNodeCount") {
		@Override
		public Integer getValue(CustomResourceRequest request) {
			return getIntegerValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			Integer value = getValue(request);
			return (null != value && value > 0); // TODO: consider limit
		}
	},
	MASTER_INSTANCE_TYPE("masterInstanceType") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return NodeInstanceTypeEnum.contains(getValue(request));
		}
	},
	DATA_NODE_COUNT("dataNodeCount") {
		@Override
		public Integer getValue(CustomResourceRequest request) {
			return getIntegerValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			Integer value = getValue(request);
			return (null != value && value > 0); // TODO: consider limit
		}
	},
	DATA_INSTANCE_TYPE("dataInstanceType") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return NodeInstanceTypeEnum.contains(getValue(request));
		}
	},
	EBS_VOLUME_SIZE("ebsVolumeSize") {
		@Override
		public Integer getValue(CustomResourceRequest request) {
			return getIntegerValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			Integer value = getValue(request);
			return (null != value && value > 0); // TODO: consider limits - The minimum and maximum size of an EBS
														// volume depends on the EBS volume type and the instance type
														// to which it is attached
		}
	},
	MASTER_USER_NAME("masterUserName") { // AdvancedSecurityOptions enabled
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return (StringUtils.hasValue(getValue(request))); 
		}
	},
	MASTER_USER_PASSWORD("masterUserPassword") { // AdvancedSecurityOptions enabled
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return (StringUtils.hasValue(getValue(request))); // TODO: consider more limitations to have strong security
		}
	},
	// TODO: in another ticket after requirements are defined
	/*
	 * LOG_PUBLISHING_OPTIONS("logPublishingOptions") {
	 * 
	 * @Override public Map<String, LogPublishingOption>
	 * getValue(CustomResourceRequest request) { return (String)
	 * request.getResourceProperties().get(this.getOptionName()); } },
	 */
	VPC_OPTIONS("vpcOptions") {
		@Override
		public VPCOptions getValue(CustomResourceRequest request) {
			VPCOptions value = null;
			Object optionValue = request.getResourceProperties().get(getOptionName());
			String jsonString = ElasticsearchInVPCWithFGAC.GSON.toJson(optionValue);
			try {
				value = ElasticsearchInVPCWithFGAC.GSON.fromJson(jsonString, VPCOptions.class);
			} catch(JsonSyntaxException e) {
				e.printStackTrace();
			}
			
			return value;
		}
		
		@Override
		public boolean validate(CustomResourceRequest request) {
			VPCOptions options = getValue(request);
			return (null!= options && !CollectionUtils.isNullOrEmpty(options.getSecurityGroupIds())
					&& !CollectionUtils.isNullOrEmpty(options.getSubnetIds()));
		}
	};

	CreateDomainOptionsEnum(String optionName) {
		this.optionName = optionName;
	}

	private String optionName;

	@Override
	public String getOptionName() {
		return optionName;
	}

	@Override
	public String toString() {
		return optionName;
	}	
	
}
