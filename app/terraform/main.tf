variable "region" {
  default = "eu-central-1"
}

provider "aws" {
  region = var.region
}

data "aws_region" "current" {}

data "aws_caller_identity" "me" {

}

resource "random_id" "app" {
  byte_length = 4
}

locals {
  user_name = split("/", data.aws_caller_identity.me.arn,)[1]

  resource_id = "${local.user_name}-${random_id.app.hex}"
}