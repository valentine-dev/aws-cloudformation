# Background
[Fine-grained access control (FGAC)](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/fgac.html) feature in Amazon Elasticsearch Service (AES) ties in with the [AdvancedSecurityOptions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-datatypes-advancedsec) and the [DomainEndpointOptions](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-datatypes-domainendpointoptions) in [AES Configuration API](https://docs.aws.amazon.com/elasticsearch-service/latest/developerguide/es-configuration-api.html#es-configuration-api-actions-createelasticsearchdomain). These properties are new and can only be used when creating the resource from AES console, AWS CLI or programatically via SDK.

For CloudFormation, it would take some time to integrate them for native support. There are open feature requests ([AdvancedSecurityOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/384), [DomainEndpointOptions](https://github.com/aws-cloudformation/aws-cloudformation-coverage-roadmap/issues/201)) on the road map and you can vote there.

# Implementation using CloudFormation Custom Resource
Here I use Lambda backed custom resource in CloudFormation to create ElasticSearch domain and enable the missing options programatically.

1. `aes-fgac.yaml`: CloudFormation template to declare AWS resources including a VPC with three private subnets in three availability zones and one public subnet, an AES domain in the VPC, a client (EC2 instance) in the public subnet of the VPC with Java 8 and AWS CLI installed. The client can access to the AES domain and can be accessed from Internet through SSH.
2. `startup.sh`: Script to provision the resources that are described in the template.
3. `cleanup.sh`: Script to release all the AWS resources.
4. `connect-to-client-instance.sh`: Script to connect to the client instance through SSH.

# Verification
1. Make sure that there is no stack named **Valentine-Test-AES-FGAC-Stack** and no AES domain named **valentine-test-aes-fgac** in your AWS account and region.
2. Make sure that AWS CLI is set up at local.
3. Run `startup.sh` to create a stack to set up the infrastructure with AES domain in it.
4. Wait until the status of stack **Valentine-Test-AES-FGAC-Stack** is CREATE_COMPLETE (about 15 minutes).
5. Run `aws es describe-elasticsearch-domain --domain-name valentine-test-aes-fgac` to get domain configuration information about the created AES domain, which should include the domain ID, domain endpoint, and domain ARN.
6. Run `aws es describe-elasticsearch-domain-config --domain-name valentine-test-aes-fgac` to get cluster configuration information about the created AES domain, which should include the state, creation date, update version, and update date for cluster options.
7. Run `connect-to-client-instance.sh` to connect to the client instance through SSH (make sure the private key file is in the current directory).
8. Run `show-indices.sh` to show the current indices of the created AES domain, and verify that the output should look like:
```
health status index     uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .kibana_1 kHM1599OT72O-Esb9Mx-bQ   1   1          0            0       566b           283b
```
9. Run `exit` to disconnect the SSH connection.
10. Run `cleanup.sh` to delete the stack.

# Notes
1. The private key file used for SSH is **Valentine_Test.pem**. You have to set up your own private key file.
2. Stack name **Valentine-Test-AES-FGAC-Stack** is used in the `startup.sh`. You can use other names.
3. AES domain name **valentine-test-ae-fgacs** is used in the `startup.sh`, step 6 and 7. You can use other names.

# Limitation
1. Since the maximum timeout of the lambda function is 15 minutes, the duration of the AES domain creation has to be within 14 minutes for the custom resource to return the endpoint (you don't have to get the endpoint so that you don't have to wait for the creation to be completed).

