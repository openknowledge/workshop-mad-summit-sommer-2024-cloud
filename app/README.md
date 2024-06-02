# 1 – Lift & Shift

In this exercise we use EC2 to move our backend into the AWS Cloud in a lift & shift manner.

Note: Make sure you current directory is now `1_lift_and_shift`.

1. Login into the AWS Management Console

    - Go to https://console.aws.amazon.com/console/home
    - Select IAM User
    - Use the user information provided to you on paper

2. Launch an EC2 instance

    - Name it after your user account (e.g. apollo)
    - Use the most recent Amazon Linux as OS
    - Use a t3a.micro instance type
    - Proceed without key pair (select in the dropdown menu)
    - Create a new security group that allows SSH and HTTP traffic (or select an existing one if someone else created one already)
    - Leave everything else as is (but feel free to read all options)

3. After a while, try to "Connect" to the instance using EC2 Instance Connect

    - You should see a console where you are logged in as user ec2-user
    - Install Java 17 using `sudo yum install java-17-amazon-corretto-headless`
    - Download our app artifact using `wget https://github.com/openknowledge/workshop-cloudland-2023-cloud-muffel/releases/download/test/on-premises-0.0.1-SNAPSHOT.jar`
    - Start it using `sudo java -jar on-premises-0.0.1-SNAPSHOT.jar --server.port=80`

4. Open the public domain of your instance (can be found in the Instance Summary view)

    - If everything is fine, the URL `http://$DOMAIN/categories` should return some data

5. Connect the frontend to the EC2 instance

    - Adjust the showcase "0 – Lift & Shift" in showcases.ts
    - Set the base URL using the domain of your EC2 instance (e.g. `http://$DOMAIN`)
    - Select showcase "0 – Lift & Shift" and check if the app works properly

6. Terminate the EC2 instance
