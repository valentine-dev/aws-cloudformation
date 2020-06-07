#!/bin/bash
echo '#!/bin/bash' > ./connect.sh && echo -n 'ssh -i "Valentine_Test.pem" ec2-user@' >> ./connect.sh && aws cloudformation describe-stacks --stack-name Valentine-Test-AES-FGAC-Stack | sed -n 's/.*\"OutputValue\": //p' | tr -d ',' | grep  "ec2-" | tr -d '"' >> ./connect.sh
chmod 755 ./connect.sh
./connect.sh
