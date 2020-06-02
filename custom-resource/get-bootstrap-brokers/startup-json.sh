#!/bin/bash
aws cloudformation create-stack --stack-name MSK-Bootstrap-Stack --template-body file://3az-default-with-client-bootstrap-brokers.json --parameters ParameterKey="MSKClusterName",ParameterValue="MSK-Bootstrap" --capabilities CAPABILITY_IAM
