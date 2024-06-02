variable "region" {
  default = "eu-central-1"
}

provider "aws" {
  region = var.region
}

data "aws_caller_identity" "me" {

}

resource "random_id" "app" {
  byte_length = 4
}

locals {
  user_name = split("/", data.aws_caller_identity.me.arn,)[1]

  resource_id = "${local.user_name}-${random_id.app.hex}"
}

locals {
  release_url = "https://github.com/openknowledge/workshop-cloudland-2023-cloud-muffel/releases/download/test/on-premises-0.0.1-SNAPSHOT.jar"
}

data "aws_ami" "app" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023*"]
  }
}

resource "aws_instance" "app" {
  tags = {
    Name = local.resource_id
  }

  ami = data.aws_ami.app.id

  instance_type = "t3a.nano"

  user_data = <<-EOF
  #!/bin/bash

  echo Update all packages
  yum -y update

  echo Install Java 17
  yum -y install java-17-amazon-corretto-headless

  echo Download app
  wget ${local.release_url} -O app.jar

  echo Start app
  java -jar app.jar --server.port=80
  EOF

  security_groups = [aws_security_group.app.name]

  iam_instance_profile = aws_iam_instance_profile.app.name
}

resource "aws_security_group" "app" {
  name = local.resource_id

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

data "aws_iam_role" "ec2" {
  name = "EC2"
}

resource "aws_iam_instance_profile" "app" {
  name = local.resource_id

  role = data.aws_iam_role.ec2.name
}

output "app_domain" {
  value = aws_instance.app.public_dns
}
