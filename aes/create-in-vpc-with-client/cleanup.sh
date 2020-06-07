#!/bin/bash
rm ./connect.sh
aws cloudformation delete-stack --stack-name Valentine-Test-AES-Stack
