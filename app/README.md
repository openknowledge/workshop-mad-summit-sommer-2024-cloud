# 2 – Managed Services

In this exercise we use AppRunner instead of EC2 to deploy our backend. To do this, a Dockerfile was added that creates a Docker image running our backend.

Note: Make sure you are on branch `2_managed_services`.

1. Run `aws configure` and use the user information provided to you on paper

    - Use the region of your user
    - As default output use the json or yaml (you may also just press enter)
    - If everything worked, you can now use the AWS CLI from your console

2. Create an image registry (known as repository) in AWS using ECR. This allows to actually push our newly build Docker image.

    - Name it after your user and leave everything else as is.

3. Build a new version of the backend
    - Make sure you're in the app folder
    - Run `mvn clean package`

4. Build Docker image and push to our new ECR repository

    - Open the new ECR repository and click on "View push commands"
    - Follow the instructions there (login, build, tag and push)

5. Create a new app runner service

    - Go to the AppRunner page and start to create a new service
    - Select container registry and the latest image in our ECR repository
    - Set deployment trigger to automatic (we will benefit from this later on)
    - As service role use the existing "AppRunnerECRAccessRole" role
    - Name the service after your user name
    - As Instance Role use the existing "AppRunner" role
    - Leave everything else as is
    - Create and deploy the service

6. Connect the frontend to AppRunner service

    - Adjust the showcase "2 – Managed Services" in showcases.ts
    - Set the base URL using the default domain of your app runner service
    - Select showcase "2 – Managed Services" and check if the app works properly
