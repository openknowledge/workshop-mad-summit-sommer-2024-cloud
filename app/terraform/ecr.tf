locals {
  app_image = "renke/ok-hello-world-backend:latest"
}

resource "aws_ecr_repository" "app" {
  name = local.resource_id

  force_delete = true
}

data "aws_ecr_authorization_token" "token" {

}

resource "terraform_data" "app_dummy_image" {
  depends_on = [aws_ecr_repository.app]

  triggers_replace = [
    # timestamp(),
    local.app_image
  ]

  provisioner "local-exec" {
    environment = {
      DOCKER_USERNAME = data.aws_ecr_authorization_token.token.user_name
      DOCKER_PASSWORD = nonsensitive(data.aws_ecr_authorization_token.token.password)
      DOCKER_REGISTRY = aws_ecr_repository.app.repository_url
    }

    command = <<-EOT
      docker login --username "$DOCKER_USERNAME" \
                   --password "$DOCKER_PASSWORD" \
                   "$DOCKER_REGISTRY"
                  
      docker manifest inspect ${aws_ecr_repository.app.repository_url} > /dev/null 2>&1

      if [ $? -eq 0 ]; then
        exit
      fi

      docker buildx imagetools create --tag ${aws_ecr_repository.app.repository_url}:latest ${local.app_image}
    EOT
  }
}