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
health status index                uuid                   pri rep docs.count docs.deleted store.size pri.store.size
green  open   .opendistro_security MPTQOVGnTTyOPt3pwvtToQ   1   2          7            4    128.8kb         34.4kb
green  open   .kibana_1            oGZgWvoUR6SnDVCcqPT2UQ   1   1          0            0       566b           283b
```
9. Run `exit` to disconnect the SSH connection.
10. Run `cleanup.sh` to delete the stack.
11. Verify that AES domain **valentine-test-aes-fgac** is deleted as well.

# Notes
1. The private key file used for SSH is **Valentine_Test.pem**. You have to set up your own private key file.
2. Stack name **Valentine-Test-AES-FGAC-Stack** is used in the `startup.sh`. You can use other names.
3. AES domain name **valentine-test-ae-fgac** is used in the `startup.sh`, step 6 and 7. You can use other names.

# Limitation
1. Since the maximum timeout of the lambda function is 15 minutes, the duration of the AES domain creation has to be within 14 minutes for the custom resource to return the endpoint (you don't have to get the endpoint so that you don't have to wait for the creation to be completed).
2. The Update event is NOT supported in the AWS CloudFormation custom resource, in which case SUCCESS is passed for simplicity.
