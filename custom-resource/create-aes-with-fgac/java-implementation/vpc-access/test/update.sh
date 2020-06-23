#!/bin/bash

aws cloudformation update-stack --stack-name Valentine-Test-AES-FGAC-Stack --use-previous-template --parameters ParameterKey="DomainName",ParameterValue="valentine-test-aes-fgac" ParameterKey="EBSVolumeSize",ParameterValue="30" --capabilities CAPABILITY_IAM
