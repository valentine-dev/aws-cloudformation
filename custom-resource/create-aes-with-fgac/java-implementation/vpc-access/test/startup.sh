#!/bin/bash

aws cloudformation create-stack --stack-name Valentine-Test-AES-FGAC-Stack --template-body file://aes-fgac-3az.yaml --parameters ParameterKey="DomainName",ParameterValue="valentine-test-aes-fgac" --capabilities CAPABILITY_IAM
