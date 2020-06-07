#!/bin/bash
[ -f ./connect.sh ] && rm ./connect.sh
aws cloudformation delete-stack --stack-name Valentine-Test-AES-Stack
