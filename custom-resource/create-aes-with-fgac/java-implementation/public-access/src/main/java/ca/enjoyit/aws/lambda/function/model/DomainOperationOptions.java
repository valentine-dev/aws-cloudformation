package ca.enjoyit.aws.lambda.function.model;

public interface DomainOperationOptions {
	
	String DOMAIN_NAME_PATTERN = "^([a-z])[a-z0-9-]{2,27}$";

	String getOptionName();
	boolean validate(CustomResourceRequest request);
	Object getValue(CustomResourceRequest request);

	
	default String getStringValue(CustomResourceRequest request) {
		String value = null;
		Object optionValue = request.getResourceProperties().get(getOptionName());
		if (optionValue instanceof String) {
			value = (String) optionValue;
		}
		return value;
	}
	
	default Integer getIntegerValue(CustomResourceRequest request) {
		Integer value = null;
		Object optionValue = request.getResourceProperties().get(getOptionName());
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
