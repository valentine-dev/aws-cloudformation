#!/bin/bash

aws cloudformation update-stack --stack-name Valentine-AES-PUBLIC-FGAC-Stack --use-previous-template --parameters ParameterKey="DomainName",ParameterValue="val-aes-public-fgac" ParameterKey="EBSVolumeSize",ParameterValue="30" --capabilities CAPABILITY_IAM
