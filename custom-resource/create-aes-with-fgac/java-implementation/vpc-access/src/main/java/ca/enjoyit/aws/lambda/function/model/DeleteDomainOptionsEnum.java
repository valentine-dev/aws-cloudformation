package ca.enjoyit.aws.lambda.function.model;

import com.amazonaws.util.StringUtils;

public enum DeleteDomainOptionsEnum implements DomainOperationOptions {
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
	};
	
	DeleteDomainOptionsEnum(String optionName) {
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
