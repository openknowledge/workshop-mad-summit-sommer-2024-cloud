# 0 – On-Premises

In this exercise we want to set up everything we need for the other exercises. Also we want to take a look at the example application (aka the OK Forum).

Note: Make sure you current directory is now `0_on_premises`.

1. Go to the `frontend` directory

    - Run `npm install` and `npm run dev`
    - A message should pop up which tells how to to reach the frontend

2. Now go back to `0_on_premises` directory in a new terminal

    - Run `sdk install java 17.0.0-tem` (and possibly `sdk default java 17.0.0-tem` and `sdk use java 17.0.0-tem`)
    - Check if you are using Java 17 by running `java -version`
    - Run `mvn spring-boot:run` to start the backend
    - A message should pop up which tells us how to reach the backend
    - Make the backend port public (this can be done in the port tab via right click menu)

3. Connect the frontend to backend

    - Adjust the showcase "0 – On-Premises" in showcases.ts
    - Set the base URL to the URL of the backend
    - Select showcase "0 – On-Premises" and check if the app works properly

4. Stop the backend by pressing Ctrl+C in the backend terminal
