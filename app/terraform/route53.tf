data "aws_route53_zone" "workshop" {
  name = "cloud.workshop.openknowledge.services"
}

resource "aws_route53_record" "app" {
  zone_id = data.aws_route53_zone.workshop.id
  name = "${local.resource_id}.${data.aws_route53_zone.workshop.name}"
  type    = "A"

  alias {
    name                   = aws_lb.app.dns_name
    zone_id                = aws_lb.app.zone_id
    evaluate_target_health = true    
  }
}

output "app_domain" {
  value = aws_route53_record.app.name
}