locals {
  release_url = "https://github.com/openknowledge/workshop-mad-summit-sommer-2024-cloud/releases/download/v2/v2.jar"
}

data "aws_ami" "app" {
  most_recent = true

  owners = ["amazon"]

  filter {
    name   = "name"
    values = ["al2023-ami-2023.*-kernel-6.1-x86_64"]
  }
}

resource "aws_launch_template" "app" {
  name_prefix   = local.resource_id
  image_id      = data.aws_ami.app.id
  instance_type = "t3a.nano"

  iam_instance_profile {
    name = aws_iam_instance_profile.app.name
  }

  vpc_security_group_ids = [aws_security_group.app.id]

  metadata_options {
    http_tokens = "required"
  }

  user_data = base64encode(
    <<-EOF
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
  )
}

resource "aws_autoscaling_group" "app" {
  name = local.resource_id

  min_size            = 1
  desired_capacity    = 2
  max_size            = 3
  vpc_zone_identifier = data.aws_subnets.default.ids

  launch_template {
    id      = aws_launch_template.app.id
    version = aws_launch_template.app.latest_version
  }

  instance_refresh {
    strategy = "Rolling"
  }

  health_check_type = "ELB"

  target_group_arns = [aws_lb_target_group.app.arn]
}

data "aws_iam_role" "ec2" {
  name = "EC2"
}

resource "aws_iam_instance_profile" "app" {
  name = local.resource_id

  role = data.aws_iam_role.ec2.name
}
