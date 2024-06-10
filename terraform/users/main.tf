provider "aws" {
  region = "eu-central-1"
}

locals {
  all_names = [
    "dog",
    "cat",
    "elephant",
    "giraffe",
    "lion",
    "tiger",
    "bear",
    "wolf",
    "fox",
    "rabbit",
    "deer",
    "horse",
    "cow",
    "sheep",
    "chicken",
    "duck",
    "goose",
    "eagle",
    "owl",
    "penguin",
    "dolphin",
    "whale",
    "kangaroo",
    "zebra"
  ]

  user_count = length(local.all_names)

  names = slice(local.all_names, 0, local.user_count)

  expire_user_at = "2024-06-21T13:00:00.000Z"
}

resource "aws_iam_user" "users" {
  for_each = toset(local.names)

  name = each.value
}

resource "aws_iam_user_login_profile" "users" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = each.value
}

resource "aws_iam_access_key" "users" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = each.value
}

data "aws_caller_identity" "me" {

}

output "users" {
  sensitive = true

  value = [
    for user in local.names : {
      account_id = data.aws_caller_identity.me.account_id
      name       = aws_iam_user.users[user].name
      password   = aws_iam_user_login_profile.users[user].password
      access_key = aws_iam_access_key.users[user].id
      secret_key = aws_iam_access_key.users[user].secret
    }
  ]
}

resource "aws_iam_group" "users" {
  name = "users"
}

resource "aws_iam_user_group_membership" "users" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = each.value

  groups = [aws_iam_group.users.name]
}

data "aws_iam_policy" "view_only_access" {
  name = "ReadOnlyAccess"
}

resource "aws_iam_user_policy_attachment" "view_only_access" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = each.value

  policy_arn = data.aws_iam_policy.view_only_access.arn
}

resource "aws_iam_user_policy" "users" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = aws_iam_user.users[each.value].name

  name = "user-${each.value}-1"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      // disable permissions in all but the given region
      {
        Effect = "Deny"
        NotAction = [
          "cloudfront:*",
          "iam:*",
          "route53:*",
          "support:*"
        ]
        Resource = "*"
        Condition = {
          StringNotEquals = {
            "aws:RequestedRegion" = "eu-central-1"
          }
        }
      },

      // disable permissions after a certain time
      {
        Effect   = "Deny"
        Action   = "*"
        Resource = "*"
        Condition = {
          DateGreaterThan = {
            "aws:CurrentTime" = local.expire_user_at
          }
        }
      },

      // lift & shift hands-on
      // TODO: Add monitoring permissions
      {
        Effect = "Allow"
        Action = [
          "ec2-instance-connect:SendSSHPublicKey",
          "ec2:AuthorizeSecurityGroupIngress",
          "ec2:CreateKeyPair",
          "ec2:CreateSecurityGroup",
          "ec2:CreateTags",
          "ec2:DeleteSecurityGroup",
          "ec2:Describe*",
          "ec2:RunInstances",
          "ec2:StopInstances",
          "ec2:TerminateInstances",
          "ec2:DescribeImages",
          "iam:GetRole",
        ]
        Resource = "*"
      },

      {
        Effect   = "Deny"
        Action   = "ec2:RunInstances"
        Resource = "arn:aws:ec2:*:*:instance/*",
        Condition = {
          StringNotEquals = {
            "ec2:InstanceType" = "t3a.nano"
          }
        }
      },

      // managed services hands-on
      {
        Effect = "Allow"
        Action = [
          "apprunner:CreateService",
          "apprunner:DeleteService",
          "apprunner:DescribeService",
          "apprunner:List*",
          "apprunner:StartDeployment",
          "ecr:*",
          "iam:ListRoles",
          "logs:GetLogEvents",
        ],
        Resource = "*"
      },

      {
        Effect = "Allow"
        Action = [
          "iam:GetRole",
          "iam:PassRole",
        ]
        Resource = [
          "arn:aws:iam::${data.aws_caller_identity.me.account_id}:role/AppRunnerECRAccessRole",
          "arn:aws:iam::${data.aws_caller_identity.me.account_id}:role/AppRunner",
        ]
      },
    ]
  })
}

resource "aws_iam_user_policy" "users2" {
  for_each = toset([for user in aws_iam_user.users : user.name])

  user = aws_iam_user.users[each.value].name

  name = "user-${each.value}-2"

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      // paas hands-on  
      {
        Effect = "Allow"
        Action = [
          "dynamodb:CreateTable",
          "dynamodb:DeleteTable",
          "dynamodb:GetItem",
          "dynamodb:BatchGetItem",
          "dynamodb:Query",
          "dynamodb:PutItem",
          "dynamodb:UpdateItem",
          "dynamodb:DeleteItem",
          "dynamodb:BatchWriteItem",
          "dynamodb:Scan",
          "application-autoscaling:DeregisterScalableTarget",
          "apprunner:UpdateService"
        ]
        Resource = "*"
      },
      // lambda hands-on  
      {
        Effect = "Allow"
        Action = [
          "lambda:CreateFunction",
          "lambda:GetFunction",
          "dynamodb:UpdateTable",
          "lambda:GetPolicy",
          "lambda:*EventSourceMapping",
          "lambda:DeleteFunction",
          "lambda:UpdateFunctionCode",
          "lambda:UpdateFunctionConfiguration",
          "lambda:ListFunctions",
          "lambda:GetRuntimeManagementConfiguration",
          "lambda:GetAccountSettings",
        ]
        Resource = "*"
      },
      {
        Effect = "Allow"
        Action = [
          "iam:GetRole",
          "iam:PassRole",
        ]
        Resource = [
          "arn:aws:iam::${data.aws_caller_identity.me.account_id}:role/Lambda",
        ]
      },
    ]
  })
}

resource "aws_iam_group_policy" "users3" {
  group = aws_iam_group.users.name

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"
        Action = [
          "ec2:AuthorizeSecurityGroupEgress",
          "ec2:DescribeInstances",
          "ec2:RevokeSecurityGroupEgress",
          "iam:AddRoleToInstanceProfile",
          "iam:CreateInstanceProfile",
          "iam:DeleteInstanceProfile",
          "iam:GetInstanceProfile",
          "iam:PassRole",
          "iam:RemoveRoleFromInstanceProfile",
          "ssm:DescribeInstanceInformation",
          "ssm:GetConnectionStatus",
          "ssm:StartSession",
          "acm:DescribeCertificate",
          "route53:ListHostedZones",
          "acm:GetCertificate",
          "acm:ListTagsForCertificate",
          "elasticloadbalancing:CreateLoadBalancer",
          "elasticloadbalancing:CreateTargetGroup",
          "elasticloadbalancing:ModifyTargetGroupAttributes",
          "elasticloadbalancing:ModifyLoadBalancerAttributes",
          "elasticloadbalancing:DescribeTargetGroupAttributes",
          "elasticloadbalancing:DescribeTags",
          "elasticloadbalancing:DescribeLoadBalancerAttributes",
          "ec2:CreateLaunchTemplate",
          "elasticloadbalancing:DeleteTargetGroup",
          "autoscaling:CreateAutoScalingGroup",
          "elasticloadbalancing:DeleteLoadBalancer",
          "elasticloadbalancing:CreateListener",
          "route53:ChangeResourceRecordSets",
          "elasticloadbalancing:DeleteListener",
          "autoscaling:UpdateAutoScalingGroup",
          "autoscaling:DeleteAutoScalingGroup",
          "ec2:DeleteLaunchTemplate",
          "lambda:GetFunctionCodeSigningConfig",
          "logs:StartLiveTail",
          "ec2:CreateLaunchTemplateVersion",
          "autoscaling:StartInstanceRefresh"
        ],
        # Condition = {
        #     StringEquals = {
        #         "iam:RoleName": "EC2"
        #     }
        # }        
        Resource = "*"
      },
    ]
  })
}

resource "aws_security_group" "app" {
  name = "EC2"

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

resource "aws_iam_role" "ec2" {
  name = "EC2"

  assume_role_policy = jsonencode(
    {
      Version = "2012-10-17"
      Statement = [
        {
          Effect = "Allow"
          Principal = {
            Service = "ec2.amazonaws.com"
          }
          Action = "sts:AssumeRole"
        }
      ]
    }
  )

  managed_policy_arns = [
    "arn:aws:iam::aws:policy/AmazonSSMManagedInstanceCore"
  ]
}

resource "aws_iam_instance_profile" "app" {
  name = "EC2"

  role = aws_iam_role.ec2.name
}

resource "aws_iam_role" "app_runner_ecr_access" {
  name = "AppRunnerECRAccessRole"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "build.apprunner.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  managed_policy_arns = ["arn:aws:iam::aws:policy/service-role/AWSAppRunnerServicePolicyForECRAccess"]
}

resource "aws_iam_role_policy" "app_runner" {
  name = "AppRunner"

  role = aws_iam_role.app_runner.id

  policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"
        Action = [
          "dynamodb:GetItem",
          "dynamodb:Query",
          "dynamodb:PutItem",
        ]
        Resource = "*"
      }
    ]
  })
}

resource "aws_iam_role" "app_runner" {
  name = "AppRunner"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "tasks.apprunner.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })
}

resource "aws_iam_role" "lambda" {
  name = "Lambda"

  assume_role_policy = jsonencode({
    Version = "2012-10-17"

    Statement = [
      {
        Effect = "Allow"
        Principal = {
          Service = "lambda.amazonaws.com"
        }
        Action = "sts:AssumeRole"
      }
    ]
  })

  managed_policy_arns = ["arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole", "arn:aws:iam::aws:policy/service-role/AWSLambdaDynamoDBExecutionRole"]
}
