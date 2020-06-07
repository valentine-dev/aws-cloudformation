# Get Amazon Resource Name (ARN) of Custom Amazon Managed Streaming for Apache Kafka (Amazon MSK) Configuration

## Problem
1. Currently, AWS CloudFormation does not support the creation of custom MSK configuration. We do not have the functionality as seen in the document of [AWS::MSK::Cluster][1]. The only way to configure an MSK clutser with the custom configuration is to refer to the ARN in the [AWS::MSK::Cluster ConfigurationInfo][2] created manually via the CLI.
2. Given a custom MSK configuration properties file and kafka version, we need to find ARN of matching one from currently available custom MSK configurations or create a new custom MSK configuration and return the ARN.

## Solution Implemented in `get_custom_msk_configuration_test.yaml`
1. Read given custom MSK configuraiton file from S3;
2. Read the complete list of available custom MSK configurations from current AWS account and region;
3. Check whether the given custom MSK configuration is already available;
4. If not available, create the custom MSK configuration, and return the ARN;
5. If already available, return the ARN. 

## Test
Be sure to have permissions to run AWS CLI to access AWS resources like AWS S3, AWS Lambda and Amazon MSK.

1. Set up AWS CLI at local.
2. Run one of the following script files to creat the test stack (make sure that the stack name and cluster name are unqiue in the region and account):
    - `startup_yaml_matching.sh` for the case - Find matching custom MSK configuration;
    - `startup_yaml_not_matching_config.sh` for the case - Find no matching custom MSK configuration due to different configuration info;
    - `startup_yaml_not_matching_version.sh` for the case - Find no matching custom MSK configuration due to different kafka version.
3. Run `cleanup.sh` to delete the stack.

## Note
Since we cannot delete custom MSK configuration once created, I comment out the logic in the `get_custom_msk_configuration_test.yaml` to avoid creating. If you really need to test custom MSK configuration creation, you can remove the comments.

[1]: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-msk-cluster.html
[2]: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-properties-msk-cluster-configurationinfo.html
