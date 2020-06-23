#!/bin/bash

aws cloudformation create-stack --stack-name Valentine-AES-PUBLIC-FGAC-Stack --template-body file://template.yaml --parameters ParameterKey="DomainName",ParameterValue="val-aes-public-fgac" --capabilities CAPABILITY_IAM
echo " -- Start creating stack Valentine-AES-PUBLIC-FGAC-Stack ..."
echo " -- Wait 16 minutes for the stack creation to complete ..."
sleep 60 
echo " -- 15 more minutes ..."
sleep 60 
echo " -- 14 more minutes ..."
sleep 60
echo " -- 13 more minutes ..."
sleep 60
echo " -- 12 more minutes ..."
sleep 60
echo " -- 11 more minutes ..."
sleep 60
echo " -- 10 more minutes ..."
sleep 60
echo " -- 9 more minutes ..."
sleep 60
echo " -- 8 more minutes ..."
sleep 60
echo " -- 7 more minutes ..."
sleep 60
echo " -- 6 more minutes ..."
sleep 60
echo " -- 5 more minutes ..."
sleep 60
echo " -- 4 more minutes ..."
sleep 60
echo " -- 3 more minutes ..."
sleep 60
echo " -- 2 more minutes ..."
sleep 60
echo " -- 1 more minute ..."
sleep 30
echo " -- 30 more seconds ..."
sleep 30
echo " -- Thanks for waiting. Check the status ..."
if aws cloudformation describe-stacks --stack-name Valentine-AES-PUBLIC-FGAC-Stack | sed -n 's/.*\"StackStatus\": //p' | tr -d ',' | grep -q 'CREATE_COMPLETE';
then
    echo " -- Stack created successfully. Please move to next step."
else
    echo " -- It could be that the stack creation hasn't completed yet. Please wait for more time or go to AWS Management Console to check."
fi
