package ca.enjoyit.aws.lambda.function.model;

import com.amazonaws.util.StringUtils;

public enum UpdateDomainOptionsEnum implements DomainOperationOptions {
	DOMAIN_NAME("domainName") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}
		
		@Override
		public String getOldValue(CustomResourceRequest request) {
			return getOldStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			String value = getValue(request);
			return (StringUtils.hasValue(value) && value.matches(DOMAIN_NAME_PATTERN));
		}
	},
	MASTER_NODE_COUNT("masterNodeCount") {
		@Override
		public Integer getValue(CustomResourceRequest request) {
			return getIntegerValue(request);
		}
		
		@Override
		public Integer getOldValue(CustomResourceRequest request) {
			return getOldIntegerValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			return true; // TODO: consider limit - optional
		}
	},
	MASTER_INSTANCE_TYPE("masterInstanceType") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}
		
		@Override
		public String getOldValue(CustomResourceRequest request) {
			return getOldStringValue(request);
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
		public Integer getOldValue(CustomResourceRequest request) {
			return getOldIntegerValue(request);
		}
		
		@Override
		public boolean validate(CustomResourceRequest request) {
			return true; // TODO: consider limit - optional
		}
	},
	DATA_INSTANCE_TYPE("dataInstanceType") {
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}
		
		@Override
		public String getOldValue(CustomResourceRequest request) {
			return getOldStringValue(request);
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
		public Integer getOldValue(CustomResourceRequest request) {
			return getOldIntegerValue(request);
		}
		
		@Override
		public boolean validate(CustomResourceRequest request) {
			return true; // TODO: consider limits - The minimum and maximum size of an EBS
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
		public String getOldValue(CustomResourceRequest request) {
			return getOldStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			String option = getValue(request);
			if (null == option) {
				return true; // optional
			} else {
				return !option.isEmpty();
			}
		}
	},
	MASTER_USER_PASSWORD("masterUserPassword") { // AdvancedSecurityOptions enabled
		@Override
		public String getValue(CustomResourceRequest request) {
			return getStringValue(request);
		}
		
		@Override
		public String getOldValue(CustomResourceRequest request) {
			return getOldStringValue(request);
		}

		@Override
		public boolean validate(CustomResourceRequest request) {
			String option = getValue(request);
			if (null == option) {
				return true; // optional
			} else {
				return !option.isEmpty(); // TODO: consider more limitations to have strong security
			}
		}
	};
	// TODO: more coming...

	UpdateDomainOptionsEnum(String optionName) {
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
	
	public abstract Object getOldValue(CustomResourceRequest request);
	
	protected String getOldStringValue(CustomResourceRequest request) {
		String value = null;
		Object optionValue = request.getOldResourceProperties().get(getOptionName());
		if (optionValue instanceof String) {
			value = (String) optionValue;
		}
		return value;
	}
	
	protected Integer getOldIntegerValue(CustomResourceRequest request) {
		Integer value = null;
		Object optionValue = request.getOldResourceProperties().get(getOptionName());
		if (optionValue instanceof Integer) {
			value = (Integer) optionValue;
		} else if (optionValue instanceof String) {
			try {
				value = Integer.valueOf((String)optionValue);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		return value;
	}
}
