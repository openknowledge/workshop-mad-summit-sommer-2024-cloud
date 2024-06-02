locals {
    workshop_account_id = "339712796392"
}

variable "region" {
  default = "eu-central-1"  
}

variable "workshop_account_id" {
  default = "339712796392"
}

provider "aws" {
  region = "eu-central-1"
}

provider "aws" {
  alias = "workshop"

  region = var.region

  assume_role {
    role_arn = "arn:aws:iam::${local.workshop_account_id}:role/OrganizationAccountAccessRole"
  }
}

