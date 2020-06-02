# Get Bootstrap Brokers for an Amazon MSK Cluster

## Can I get the Bootstrap Brokers for an Amazon MSK Cluster using CloudFormation?

Unfortunately no, bootstrap brokers information cannot be retrieved from cloud formation because it is not supported from the cloud formation side. I have checked the document associated with resource
[AWS::MSK::Cluster][1] and checked its return values, and I see that this resource returns only below value : “MSKClusterArn”, Since the bootstrap brokers is not present in the return values, it would not be possible to refer it directly. I have raised a ticket with cloud formation team to include this in the return values, however I do not have an ETA on when this would be available.

## Solution
Meanwhile, possible workaround here is to make use of CloudFormation Custom Resource with Lambda as back-end.

The custom resource created is the piece of code which will signal the lambda function with cluster ARN and output the bootstrap broker string. Please note that writing custom resource is out of scope of AWS Support, however I have outlined logic below for your reference:

First, you would need to get the cluster ARN using the Ref function on the Resource "AWS::MSK::Cluster" and then pass the MSK cluster ARN to the custom resource using the "Properties" parameter of the custom resource. The custom resource will invoke your Lambda function which performs the [BootstrapBrokers][2] API call on the provided ARN. After making the above api call store the output of brokers information in a variable   and send it to the CloudFormation stack using the responseData in the Lambda code.

Once the Lambda is invoked successfully and has returned the value “brokers” to the CloudFormation, use the "Fn:GetAtt" intrinsic function on the Custom resource to get the value returned by Lambda resource to the custom resource.

## Test
Be sure to have permissions to run AWS CLI to access AWS resources like AWS Lambda and Amazon MSK.

1. Set up AWS CLI at local.
2. Run start_up_json.sh or start_up_yaml.sh to creat the stack (make sure that the stack name and cluster name are unqiue in the region and account).
3. Go to the AWS Management Console to check that the MSK Cluster named in the start_up.sh is ready.
4. Compare the Bootstrap Brokers string from the output of the created MSK Cluster on CloudFormation Stacks console with the client information from that on Amazon MSK console - they should be the same.
5. Run clean_up.sh to delete the stack.

[1]: https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-msk-cluster.html
[2]: https://docs.aws.amazon.com/msk/1.0/apireference/clusters-clusterarn-bootstrap-brokers.html
