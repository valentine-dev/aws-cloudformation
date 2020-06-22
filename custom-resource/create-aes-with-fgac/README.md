# Background
[Fine-grained access control (FGAC)](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/fgac.html) feature in Amazon Elasticsearch Service (AES) ties in with the [AdvancedSecurityOptions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-datatypes-advancedsec) and the [DomainEndpointOptions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-datatypes-domainendpointoptions) in [AES Configuration API](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-actions-createelasticsearchdomain). These properties are new and can only be used when creating the resource from AES console, AWS CLI or programatically via SDK.

For CloudFormation, it would take some time to integrate them for native support. There are open feature requests ([AdvancedSecurityOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/384), [DomainEndpointOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/201)) on the road map and you can vote there.

---

# Enable Amazon Elasticsearch Service Operations with Fine-Grained Access Control

## The Gradle projects contains Java source code to:
- implement a Lambda function;
- receive CloudFormation custom resource request for
        - Amazon Elasticsearch domain creation with fine-grained access control;
        - Amazon Elasticsearch domain update;
        - Amazon Elasticsearch domain deletion.
- send CloudFormation custom resource response;

## Build the Gradle Project
```
./gradlew clean
./gradlew build
```

## Java Artifact
After successful build, there is a zip file in ./build/distributions directory (for example, `ElasticsearchInPublicFineGrainedAccess-1.0.zip`) for CloudFormation custom resource to use. 


## Test
1. Upload the Java artifact to S3 folder (specified in the `template.yaml`);
2. Run the `startup.sh`;
3. Open a browser to the endpoint returned to access Elasticsearch with the provided username and password or access it through a terminal.

## Cleanup
Run `cleanup.sh` to release all AWS resources.

# References:
- https://docs.aws.amazon.com/lambda/latest/dg/services-cloudformation.html
- https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-samples.html
- https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-lambda-function-code-cfnresponsemodule.html
- https://hub.alfresco.com/t5/alfresco-platform-services-blog/how-a-lambda-backed-custom-resource-saved-the-day/ba-p/286986
- https://stackoverflow.com/questions/32811947/how-do-we-access-and-respond-to-cloudformation-custom-resources-using-an-aws-lam
