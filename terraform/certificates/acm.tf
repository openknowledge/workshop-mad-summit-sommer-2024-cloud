resource "aws_acm_certificate" "workshop" {
  provider = aws.workshop

  domain_name       = aws_route53_zone.workshop.name
  validation_method = "DNS"

  subject_alternative_names = [ 
    "*.${aws_route53_zone.workshop.name}"
   ]
}

resource "aws_route53_record" "workshop_validation" {
  provider = aws.workshop

  for_each = {
    for dvo in aws_acm_certificate.workshop.domain_validation_options : dvo.domain_name => {
      name   = dvo.resource_record_name
      record = dvo.resource_record_value
      type   = dvo.resource_record_type
    }

     if length(regexall("\\*\\..+", dvo.domain_name)) > 0
  }

  allow_overwrite = true
  name            = each.value.name
  records         = [each.value.record]
  ttl             = 60
  type            = each.value.type
  zone_id         = aws_route53_zone.workshop.zone_id
}

resource "aws_acm_certificate_validation" "workshop" {
  provider = aws.workshop

  certificate_arn = aws_acm_certificate.workshop.arn

  validation_record_fqdns = [for record in aws_route53_record.workshop_validation : record.fqdn]
}
