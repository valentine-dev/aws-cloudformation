#!/bin/bash

aws cloudformation create-stack --stack-name MSK-GetCustomConfiguration-Stack --template-body file://get-custom-msk-config.yaml --parameters ParameterKey="KafkaVersion",ParameterValue="2.5.1" ParameterKey="S3BucketName",ParameterValue="dev-valentine" ParameterKey="S3ObjectKeyName",ParameterValue="DataToShare/custom-config-info-matching" --capabilities CAPABILITY_IAM
echo " -- Start creating stack MSK-GetCustomConfiguration-Stack ..."
echo " -- Wait 2 minutes for the stack creation to complete ..."
sleep 60 
echo " -- 1 more minute ..."
sleep 30
echo " -- 30 more seconds ..."
sleep 30
echo " -- Thanks for waiting. Check the output ..."
if aws cloudformation describe-stacks --stack-name MSK-GetCustomConfiguration-Stack | grep -q 'No matching version.'; 
then
    echo " -- Output: No matching version!" 
else
    echo " -- It could be that the stack creation hasn't completed yet. Please wait for more time or go to AWS Management Console to check."
fi
