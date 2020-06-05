# Background
[Fine-grained access control (FGAC)](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/fgac.html) feature in Amazon Elasticsearch Service (AES) ties in with the AdvancedSecurityOptions and the DomainEndpointOptions in [AES Configuration API](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-actions-createelasticsearchdomain). These properties are new and can only be used when creating the resource from AES console, AWS CLI or programatically via SDK.

For CloudFormation, it would take some time to integrate them for native support. There are open feature requests ([AdvancedSecurityOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/384), [DomainEndpointOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/201)) on the road map and you can vote there.

# Implementation using CloudFormation Custom Resource
Here I use Lambda backed custom resource in CloudFormation to create ElasticSearch domain and enable the missing options programatically.
