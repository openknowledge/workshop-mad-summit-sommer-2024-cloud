resource "aws_dynamodb_table" "app" {
  name = local.resource_id

  billing_mode = "PROVISIONED"

  read_capacity  = 5
  write_capacity = 5

  hash_key  = "pk"
  range_key = "sk"

  attribute {
    name = "pk"
    type = "S"
  }

  attribute {
    name = "sk"
    type = "S"
  }
}