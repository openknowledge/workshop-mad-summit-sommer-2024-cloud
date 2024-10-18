const input = require("fs").readFileSync(0, "utf-8");

const users = JSON.parse(input);

console.log("IAM-User,Account-ID,Passwort,Access Key ID,Secret Access Key");

for (const user of users) {
  console.log(
    [
      user.name,
      user.account_id,
      user.password,
      user.access_key,
      user.secret_key,
    ]
      .map((value) => {
        return `"${value}"`;
      })
      .join(",")
  );
}
