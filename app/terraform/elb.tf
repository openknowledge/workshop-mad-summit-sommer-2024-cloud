resource "aws_lb" "app" {
  name = local.resource_id

  internal = false

  load_balancer_type = "application"

  security_groups = [aws_security_group.app.id]

  subnets = data.aws_subnets.default.ids
}

resource "aws_lb_listener" "http" {
  load_balancer_arn = aws_lb.app.arn

  port     = "443"
  protocol = "HTTPS"

  default_action {
    type = "forward"

    target_group_arn = aws_lb_target_group.app.arn
  }

  certificate_arn = data.aws_acm_certificate.workshop.arn
}

resource "aws_lb_target_group" "app" {
  name = local.resource_id

  port        = 80
  protocol    = "HTTP"

  target_type = "instance"

  vpc_id = data.aws_vpc.default.id

  health_check {
    path = "/categories"
  }
}