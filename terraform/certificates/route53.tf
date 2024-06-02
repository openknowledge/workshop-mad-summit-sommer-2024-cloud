resource "aws_route53_zone" "workshop" {
  provider = aws.workshop

  name = "cloud.workshop.openknowledge.services"
}

data "aws_route53_zone" "main" {
  name = "openknowledge.services"
}

resource "aws_route53_record" "workshop" {
  zone_id = data.aws_route53_zone.main.id
  name    = aws_route53_zone.workshop.name
  type    = "NS"
  ttl     = "300"
  records = aws_route53_zone.workshop.name_servers
}

