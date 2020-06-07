# Set up Amazon Elasticsearch Service (AES) domain with a client instance 
1. `aes-3az.yaml`: CloudFormation template to declare AWS resources including a VPC with three private subnets in three availability zones and one public subnet, an AES domain in the VPC, a client (EC2 instance) in the public subnet of the VPC with Java 8 and AWS CLI installed. The client can access to the AES domain and can be accessed from Internet through SSH.
2. `startup.sh`: Script to provision the resources that are described in the template.
3. `cleanup.sh`: Script to release all the AWS resources.
4. `connect-to-client-instance.sh`: Script to connect to the client instance through SSH.

# Test
1. Make sure that there is no stack named "Valentine-Test-AES-Stack" and no AES domain named "valentine-test-aes" in your AWS account and region.
2. Run `startup.sh` to create a stack to set up the infrastructure with AES domain in it.
3. Wait until the status of stack **Valentine-Test-AES-Stack** is CREATE_COMPLETE (about 15 minutes).
4. Run `connect-to-client-instance.sh` to connect to the client instance through SSH.
5. Run `show-indices.sh` to show the current indices of the created AES domain.
6. Run `aws es describe-elasticsearch-domain --domain-name valentine-test-aes` to get domain configuration information about the created AES domain, including the domain ID, domain endpoint, and domain ARN.
7. Run `aws es describe-elasticsearch-domain-config --domain-name valentine-test-aes` to get cluster configuration information about the created AES domain, such as the state, creation date, update version, and update date for cluster options.
9. Run `exit` tp disconnect the SSH connection.
8. Run `cleanup.sh` to delete the stack.
