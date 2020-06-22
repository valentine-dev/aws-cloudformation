# Amazon Elasticsearch Service Custom Resource with Fine-Grained Access Control

This Gradle project contains Java source code to implement an AWS Lambda function for the following features:
- receive CloudFormation custom resource request for
    - Amazon Elasticsearch domain creation with fine-grained access control;
    - Amazon Elasticsearch domain update;
    - Amazon Elasticsearch domain deletion.
- send CloudFormation custom resource response.

# Build
```
./gradlew clean
./gradlew build
```

# Artifact
After successful build, there is a zip file in ./build/distributions directory (for example, `ElasticsearchFineGrainedAccess-1.0.zip`) for CloudFormation custom resource to use. 

# References:
- https://docs.aws.amazon.com/lambda/latest/dg/services-cloudformation.html
- https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-samples.html
- https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/cfn-lambda-function-code-cfnresponsemodule.html
- https://hub.alfresco.com/t5/alfresco-platform-services-blog/how-a-lambda-backed-custom-resource-saved-the-day/ba-p/286986
- https://stackoverflow.com/questions/32811947/how-do-we-access-and-respond-to-cloudformation-custom-resources-using-an-aws-lam
