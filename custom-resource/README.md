# What is Custom Resource?

Custom Resources are required in cases where you are trying to achieve a setup by utilizing Cloud Formation, but the resource/feature that you intend to provision is not supported yet. Please refer [1] for more information.

Also please refer [2 - 6] to read more about custom resources and to understand some best practices that should be taken into consideration while creating a custom resource.

## References
1. http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-custom-resources.html
2. AWS::CloudFormation::CustomResource : http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/aws-resource-cfn-customresource.html
3. AWS Lambda-backed Custom Resources: http://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/template-custom-resources-lambda.html
4. Best practices: https://aws.amazon.com/premiumsupport/knowledge-center/best-practices-custom-cf-lambda/
5. https://blog.jayway.com/2016/03/03/custom-resource-generating-lambdas/
6. Custom resources Example : https://github.com/aws-samples/aws-cfn-custom-resource-examples
