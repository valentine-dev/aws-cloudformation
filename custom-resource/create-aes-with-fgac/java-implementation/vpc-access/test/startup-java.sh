#!/bin/bash

aws cloudformation create-stack --stack-name Valentine-Test-AES-FGAC-Stack-Java --template-body file://aes_fgac_3az_java.yaml --parameters ParameterKey="DomainName",ParameterValue="valentine-test-aes-fgac-java" --capabilities CAPABILITY_IAM
