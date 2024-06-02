resource "aws_apprunner_service" "app" {  
  depends_on = [ terraform_data.app_dummy_image ]

  service_name = local.resource_id

  source_configuration {
    authentication_configuration {
      access_role_arn = data.aws_iam_role.app_runner_ecr_access.arn
    }

    image_repository {
      image_configuration {
        port = 8080

        runtime_environment_variables = {
          DYNAMODB_TABLE = aws_dynamodb_table.app.name
        }
      }

      image_repository_type = "ECR"
      image_identifier      = "${aws_ecr_repository.app.repository_url}:latest"
    }


    auto_deployments_enabled = true
  }

  instance_configuration {
    instance_role_arn = data.aws_iam_role.app_runner.arn
  }
}

data "aws_iam_role" "app_runner_ecr_access" {
  name = "AppRunnerECRAccessRole"
}


data "aws_iam_role" "app_runner" {
  name = "AppRunner"
}

output "app_domain" {
  value = aws_apprunner_service.app.service_url
}

output "ecr_repository_url" {
  value = aws_ecr_repository.app.repository_url
}