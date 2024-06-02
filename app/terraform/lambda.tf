data "aws_iam_role" "lambda" {
  name = "Lambda"
}

data "archive_file" "lambda" {
  type        = "zip"
  output_path = "${path.module}/lambda.zip"
  source {
    content  = <<EOF
export const handler = async (event) => {
  console.log(JSON.stringify(event, null, 2));
};
EOF
    filename = "index.mjs"
  }
}

resource "aws_lambda_function" "app" {
  filename = "${path.module}/lambda.zip"

  function_name = local.resource_id

  role = data.aws_iam_role.lambda.arn

  handler = "index.handler"

  source_code_hash = data.archive_file.lambda.output_base64sha256

  runtime = "nodejs20.x"
}

resource "aws_lambda_event_source_mapping" "example" {
  event_source_arn = aws_dynamodb_table.app.stream_arn

  function_name = aws_lambda_function.app.arn

  starting_position = "LATEST"
}