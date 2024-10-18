const input = require("fs").readFileSync(0, "utf-8");

const users = JSON.parse(input);

for (const user of users) {
  console.log("<b>Cloud-Workshop</b><br/>");

  console.log("von Open Knowledge <u>https://www.openknowledge.de/</u><br/>");

  console.log("<br/>");
  console.log("Deine Zugangsdaten für AWS<br/>");

  console.log("<br/>");
  console.log("Account-ID: " + user.account_id + "<br/>");
  console.log("Region: " + "eu-central-1" + "<br/>");
  console.log("<br/>");

  console.log(
    "<b>AWS Management Console</b> (<u>https://console.aws.amazon.com/</u>)<br/>"
  );

  console.log("IAM-User: " + user.name + "<br/>");
  console.log("Passwort: " + user.password + "<br/>");

  console.log("<br/>");

  console.log("<b>AWS CLI</b><br/>");

  console.log("Access Key ID: " + user.access_key + "<br/>");
  console.log("Secret Access Key: " + user.secret_key + "<br/>");

  console.log("<br/>");

  console.log(
    "-----------------------------------------------------------<br/>"
  );

  console.log("<br/>");
  console.log("<br/>");
}
