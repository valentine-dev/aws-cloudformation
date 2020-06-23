#!/bin/bash

aws cloudformation update-stack --stack-name Valentine-Test-AES-FGAC-Stack-Java --use-previous-template --parameters ParameterKey="DomainName",ParameterValue="valentine-test-aes-fgac-java" ParameterKey="EBSVolumeSize",ParameterValue="30" --capabilities CAPABILITY_IAM
