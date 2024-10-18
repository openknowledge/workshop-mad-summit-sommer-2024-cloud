# 0 – On-Premises

In this exercise we want to set up everything we need for the other exercises. Also we want to take a look at the example application (aka the OK Forum).

Note: Make sure you are on branch `0_on_premises`.

1. In a new terminal go to the `frontend` directory

    - Run `npm install` and `npm run dev`
    - A message should pop up which tells how to to reach the frontend
    - You only need to start the frontend once and leave it running for the duration of the workshop

2. In a new terminal go to the `app` directory

    - Run `mvn spring-boot:run` to start the backend
    - A message should pop up which tells us how to reach the backend
    - Make the backend port public (this can be done in the port tab via right click menu)

3. Connect the frontend to backend

    - Adjust the showcase "0 – On-Premises" in `frontend/src/showcases.ts`
    - Set the base URL to the URL of the backend
        - Reminders:
            - You will find the URLs in the VSCode Codespaces ports tab
            - The Backend Service runs on Port 8080
            - The Backend Port Visibility must be set to public
    - Open the App in the Browser and select the showcase "0 – On-Premises" in the dropdown
    - Check if the app works properly

4. Stop the backend by pressing Ctrl+C in the backend terminal
