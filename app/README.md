# 1b – Lift & Shift

In this exercise we use EC2 to move our backend into the AWS Cloud in a lift & shift manner.

Note: Make sure you are on branch `1b_lift_and_shift`.

1. Login into the AWS Management Console

    - Go to https://console.aws.amazon.com/console/home
    - Select IAM User
    - Use the user information provided to you on paper

2. Create a launch template

    - Name it after your user account
    - Use the same settings as used in `1a_lift_and_shift`
    - That is, the Amazon Linux 2023 AMI (or newer), `t3a.nano`, `EC2` as security group and instance profile
    - As user data insert the following snippet:

```sh
#!/bin/bash

echo Update all packages
yum -y update

echo Install Java 17
yum -y install java-17-amazon-corretto-headless

echo Download app
wget https://github.com/openknowledge/workshop-cloud/releases/download/v2/v2.jar -O app.jar

echo Start app
java -jar app.jar --server.port=80
```

    - Leave everything else as is

3. Create a target group (Found under Load Balancing)

    - Name it after your user account
    - As health check use `/id`

4. Create a (application) load balancer

    - Name it after your user account
    - Choose all availability zones
    - Use `EC2` as security group
    - Listen on port 80 using your target group

5. Create an (EC2) autoscaling group

    - Name it after your user account
    - Use your launch template
    - Select all subnets
    - Attach your load balancer using your target group
    - Turn on Elastic Load Balancing health checks
    - Use 2 as desired capacity

6. Open the public domain of your loadbalancer (can be found in the Instance Summary view)

    - If everything is fine, the URL `http://$DOMAIN/id` (use HTTP!) should return some data
    - Reload a few time to check if all instances are hit eventually

7. Connect the frontend to the EC2 instance

    - NOTE: This might not work due to TLS in GitHub Codespaces
    - Adjust the showcase "1 – Lift & Shift" in showcases.ts'
    - Set the base URL using the domain of your EC2 instance (e.g. `http://$DOMAIN`)
    - Select showcase "1 – Lift & Shift" and check if the app works properly'

8. Feel free to delete all resources you've created (in reverse order)
