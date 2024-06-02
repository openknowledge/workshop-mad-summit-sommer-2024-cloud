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

  stream_enabled = true

  stream_view_type = "NEW_AND_OLD_IMAGES"
}