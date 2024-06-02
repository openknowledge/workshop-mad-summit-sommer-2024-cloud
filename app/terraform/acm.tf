data "aws_acm_certificate" "workshop" {
  domain   = "cloud.workshop.openknowledge.services"

  statuses = ["ISSUED"]
}